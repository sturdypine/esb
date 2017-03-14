package spc.esb.common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spc.esb.common.service.ESBInfoService;
import spc.esb.common.service.MsgDefService;
import spc.esb.common.service.MsgRoute;
import spc.esb.common.service.Route;
import spc.esb.common.service.RouteService;
import spc.esb.constant.ESBCommon;
import spc.esb.constant.ESBConfig;
import spc.esb.constant.ESBMsgCode;
import spc.esb.data.IMessage;
import spc.esb.data.MessageAttr;
import spc.esb.data.util.MsgFTLUtil;
import spc.esb.model.MessagePO;
import spc.esb.model.NodePO;
import spc.esb.model.NodeServicePO;
import spc.webos.config.AppConfig;
import spc.webos.service.BaseService;
import spc.webos.util.FTLUtil;
import spc.webos.util.StringX;

/**
 * 2011-09-29 取消广播交易表的特殊配置，使用同一的ESB_MSG or ESB_SERVICE表的qname & routerule属性
 * 
 * @author
 * 
 */
@Service("esbRouteService")
public class RouteServiceImpl extends BaseService implements RouteService
{
	// 400 2012-05-09 废除 specialRoute表使用
	@Autowired(required = false)
	protected MsgDefService msgDefService;
	@Autowired(required = false)
	protected ESBInfoService esbInfoService;
	protected String reqQueueFix = ESBCommon.REQFIX; // 请求队列名前缀REQ.
	protected String repQueueFix = ESBCommon.REPFIX; // 应答队列名前缀REP.
	protected String broadcastQueueFix = ESBCommon.REQFIX; // chenjs 2013-04-18
															// 容许广播交易以不同队列前缀
	protected String mbrCdCfgKey; // 在esb_config表中配置的行号映射mdl值。推荐为mbrCd

	// 根据成员编号和报文编号获取目的队列：ESB_MBRDISTRIB表中有配置的话读取其中的QNAME值，没有的使用ESB_MSG表中的QNAME值
	public String getQname(IMessage msg) throws Exception
	{
		if (!msg.isRequestMsg()) return getRepQname(msg);
		String rcvMbrCd = mappingMbrCd(msg.getRcvNode()); // 多分行队列集中映射
		String rcvAppCd = msg.getRcvApp();
		String msgCd = msg.getMsgCd();
		MessageAttr msgAttr = msgDefService.getMsgAttr(msgCd);

		// 进行普通模式路由，依据静态或者动态规则
		String qname = route(getRoute(msg), msg);
		// modified by chenjs 2011-10-02 如果路由信息为空，则表示用rcvappcd作为路由队列
		// ncc报文规范是不依赖服务定义，而是发起方指定，因为同一个报文编号可以路由给不同人
		if (StringX.nullity(qname))
		{
			if (log.isInfoEnabled()) log.info(
					"qname is null, route by rcvAppCd: " + rcvAppCd + ", rcvMbrCd: " + rcvMbrCd);
			return genReqQname(rcvAppCd, rcvMbrCd, rcvAppCd);
		}

		// 非广播交易报文, 如果接收成员不为空则自动填充接收成员编号
		if (!msgAttr.isBroadcast()) return genReqQname(qname, rcvMbrCd, rcvAppCd);

		// 广播交易报文使用qname or routerule结果, 但不自动补充接收成员
		if (StringX.nullity(broadcastQueueFix)) return qname; // 没有固定前缀则直接返回,
		// 2011-09-30 chenjs
		StringBuffer broadCastQ = new StringBuffer();
		List broadCastQs = StringX.split2list(qname, StringX.COMMA);
		for (int j = 0; j < broadCastQs.size(); j++)
		{
			if (broadCastQ.length() > 0) broadCastQ.append(StringX.COMMA);
			String q = broadCastQs.get(j).toString(); // 如果配置路由中包含了前缀，则不增加前缀
			broadCastQ.append(q.startsWith(broadcastQueueFix) ? q : broadcastQueueFix + q);
		}
		return broadCastQ.toString();
	}

	/**
	 * 根据路由规则，报文，接受成员进行请求路由
	 * 
	 * @param route
	 * @param msg
	 * @param mbrCd
	 * @return
	 * @throws Exception
	 */
	protected String route(Route route, IMessage msg) throws Exception
	{
		if (!StringX.nullity(route.getRouteBeanName()))
		{ // adde chenjs 2011-12-20 如果qname & ftl均无法解决则可使用注入java接口方式
			if (log.isDebugEnabled()) log.debug("routeBeanName: " + route.getRouteBeanName());
			MsgRoute msgRoute = (MsgRoute) MsgRoute.ROUTES.get(route.getRouteBeanName());
			if (msgRoute != null) return msgRoute.route(route, msg);
			log.warn("IMsgRoute is null by: " + route.getRouteBeanName());
		}

		if (!StringX.nullity(route.getFtlRule()))
		{ // 动态根据报文内容进行路由
			Map root = new HashMap();
			root.put("route", route);
			MsgFTLUtil.model(root, msg);
			String qname = StringX.trim(FTLUtil.freemarker(route.getFtlRule(), root));
			if (!StringX.nullity(qname))
			{
				if (log.isInfoEnabled()) log.info("route by rule, des. queue: " + qname);
				return qname;
			}
		}
		return route.getQname();
	}

	protected Route getRoute(IMessage msg)
	{
		String node = msg.getSndNode();
		String msgCd = msg.getMsgCd();

		// 1. 按渠道服务关系表优先选择路由, 400 2013-05-09
		NodeServicePO nodeServiceVO = esbInfoService.getNodeService(node, msgCd);
		if (nodeServiceVO != null && nodeServiceVO.isValidRoute()) return nodeServiceVO;

		// 2. 按service表路由规则进行路由，如果没有则 不再按msg表路由规则进行
		return msgDefService.getService(msgCd);
	}

	/**
	 * 应答报文路由应答
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public String getRepQname(IMessage msg) throws Exception
	{
		// 401_20130925 chenjs 对于异步应答情况，如果渠道没有设置使用replyToQ属性，则使用传统的rep.nbs.asyn
		// 404_20140525 chenjs 如果replyToQ前端填写错误，则视为replyToQ属性无效没填
		String replyToQ = msg.getReplyToQ();
		if (!isValidReplyToQ(msg, replyToQ))
		{
			log.warn("replyToQ: " + replyToQ + " is unvalid!!!");
			replyToQ = null;
		}
		if (!StringX.nullity(replyToQ)) return replyToQ;

		boolean syn = true; // 默认同步应答
		String callType = msg.getCallType(); // 400 2013-06-01 使用callType字段
		if (IMessage.CALLTYP_SYN.equalsIgnoreCase(callType)
				|| ESBMsgCode.MSGCD_COMM_RECEIPT().equals(msg.getMsgCd()))
			syn = true;
		// modified by guodd 20110622
		else if (IMessage.CALLTYP_ASYN.equalsIgnoreCase(callType)) syn = false;
		else
		{ // 如果返回方没有填写参考调用方式或者填写非SYN和ASYN采用原报文的默认调用类型
			String refMsgCd = msg.getRefMsgCd();
			MessagePO msgVO = msgDefService.getMessage(refMsgCd);
			if (msgVO == null) log.warn("cannot find MessageVO by " + refMsgCd);
			else syn = !new MessageAttr(msgVO.getMsgAttr()).isAsyn();
			if (log.isInfoEnabled()) log.info("callType is not SYN/ASYN, using refmsgcd attr: "
					+ syn + ", refMsgCd:" + refMsgCd);
		}

		if (!syn)
		{ // 401_20131022 chenjs 异步应答如果有了指定队列则优先使用
			NodeServicePO nodeServiceVO = esbInfoService.getNodeService(msg.getRefSndNodeApp(),
					msg.getRefMsgCd());
			if (nodeServiceVO != null && !StringX.nullity(nodeServiceVO.getAsynRepQName()))
				return nodeServiceVO.getAsynRepQName();
		}

		String refSndNode = mappingMbrCd(msg.getRefSndNode());
		String refSndAppCd = msg.getRefSndApp();

		return repQueueFix + refSndAppCd
				+ (StringX.nullity(refSndNode) ? StringX.EMPTY_STRING
						: ESBCommon.QSPLIT + refSndNode)
				+ (syn ? StringX.EMPTY_STRING : ESBCommon.QSPLIT + IMessage.CALLTYP_ASYN);
	}

	// 判断是否是非法的replyToQ属性
	public boolean isValidReplyToQ(IMessage msg, String replyToQ)
	{
		if (StringX.nullity(replyToQ)) return true;

		String replyToQPrefix = (String) AppConfig.getInstance()
				.getProperty(ESBConfig.MB_replyToQPrefix, "REP.");
		if (log.isDebugEnabled())
			log.debug("replyToQPrefix:" + replyToQPrefix + ", replyToQ:" + replyToQ);
		if (replyToQ.startsWith(replyToQPrefix)) return true;

		NodePO node = esbInfoService
				.getNode(msg.isRequestMsg() ? msg.getSndNodeApp() : msg.getRefSndNodeApp());
		if (node != null && !StringX.nullity(node.getReplyToQ()))
		{ // 渠道指定了应答返回队列的有效集合
			return StringX.contain(StringX.split(node.getReplyToQ(), StringX.COMMA), replyToQ,
					true);
		}
		return false;
	}

	// added by chenjs 2011-08-20, 人行12行号可能要映射为4位地区码进行路由队列计算
	protected String mappingMbrCd(String mbrCd)
	{
		if (StringX.nullity(mbrCdCfgKey) || StringX.nullity(mbrCd)) return mbrCd;
		return (String) AppConfig.getInstance().getProperty(mbrCdCfgKey + '.' + mbrCd, mbrCd);
	}

	protected String genReqQname(String qname, String rcvMbrCd, String rcvAppCd)
	{
		// modified by chenjs 2011-08-20.
		// 考虑到rcvMbrCd可能使用人行行号，但可能一批人行行号的业务发往同一个队列。
		// Note: 以前路由队列为REQ.2400.CBS, 现在变更为REQ.CBS.2400 or CBS.2400, 将成员信息后置
		return reqQueueFix
				+ (StringX.nullity(rcvMbrCd) ? qname : qname + ESBCommon.QSPLIT + rcvMbrCd);
	}

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}

	public void setMsgDefService(MsgDefService msgDefService)
	{
		this.msgDefService = msgDefService;
	}

	public void setReqQueueFix(String reqQueueFix)
	{
		this.reqQueueFix = reqQueueFix;
	}

	public void setRepQueueFix(String repQueueFix)
	{
		this.repQueueFix = repQueueFix;
	}

	public void setBroadcastQueueFix(String broadcastQueueFix)
	{
		this.broadcastQueueFix = broadcastQueueFix;
	}
}
