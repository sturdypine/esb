package spc.esb.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spc.esb.common.service.AuthService;
import spc.esb.common.service.ESBInfoService;
import spc.esb.common.service.MsgDefService;
import spc.esb.constant.ESBConfig;
import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.constant.ESBRetCode;
import spc.esb.core.NodeAttr;
import spc.esb.core.NodeServiceAttr;
import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.esb.data.util.MsgFTLUtil;
import spc.esb.model.NodePO;
import spc.esb.model.NodeServicePO;
import spc.esb.model.ServicePO;
import spc.webos.config.AppConfig;
import spc.webos.exception.AppException;
import spc.webos.service.BaseService;
import spc.webos.util.FTLUtil;
import spc.webos.util.JsonUtil;
import spc.webos.util.RegExp;
import spc.webos.util.StringX;;

/**
 * 交易授权服务
 * 
 * @author sunqian at 2010-05-24
 */
@Service("esbAuthService")
public class AuthServiceImpl extends BaseService implements AuthService
{
	protected List<String> defValidServiceStatus = (List<String>) JsonUtil.json2obj("['2','5']");
	@Autowired(required = false)
	protected ESBInfoService esbInfoService;
	@Autowired(required = false)
	protected MsgDefService msgDefService;

	// 返回普通报文的授权状态
	public boolean isAuth(IMessage msg) throws Exception
	{
		if (!checkAuth(msg)) throw new AppException(ESBRetCode.SERVICE_UNAUTH,
				new String[] { msg.getMsgCd(), msg.getSndNodeApp() });
		checkChannel(msg); // 检查渠道/服务系统是否合法状态和指定时间窗口
		checkService(msg); // 检查服务是否合法状态和指定时间窗口
		return true;
	}

	// 判断渠道时间窗口和状态
	protected boolean checkChannel(IMessage msg)
	{
		// 1. 检查发起方渠道的状态.
		NodePO sndNodeVO = esbInfoService.getNode(msg.getSndNodeApp());
		NodeAttr sndNodeattr = new NodeAttr(sndNodeVO.getAppAttr());
		if (sndNodeattr.isUnvalidChannel())
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "channel is unvalid!!!");

		// 2. 检查发起方渠道的时间窗口
		String currentDt = new SimpleDateFormat("yyyyMMdd").format(new Date());
		if ((!StringX.nullity(sndNodeVO.getPublishDt())
				&& currentDt.compareTo(sndNodeVO.getPublishDt()) < 0)
				|| (!StringX.nullity(sndNodeVO.getEffectDt())
						&& currentDt.compareTo(sndNodeVO.getEffectDt()) > 0))
		{
			log.warn("currentDt: " + currentDt + ", publishDt:" + sndNodeVO.getPublishDt()
					+ ", effectDt" + sndNodeVO.getEffectDt());
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "channel not in valid date!!!");
		}

		// 3. 检查服务方系统状态
		NodePO rcvNodeVO = esbInfoService.getNode(msg.getRcvNodeApp());
		// 503_20150214, 广播交易检查授权时还没有填写报文的rcv信息，所以如果检查不到rcv信息则通过
		if (rcvNodeVO == null)
		{
			log.debug("No RcvNode:" + msg.getRcvNodeApp());
			return true;
		}
		// if (rcvNodeVO == null) throw new
		// AppException(ESBRetCode.FLOW_UNAUTH(),
		// "RcvNode inexistence(" + msg.getRcvNodeApp() + ")");
		NodeAttr rcvNodeattr = new NodeAttr(rcvNodeVO.getAppAttr());
		if (rcvNodeattr.isUnvalidServer())
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "server is unvalid!!!");

		return true;
	}

	// 判断服务时间窗口和状态
	protected boolean checkService(IMessage msg) throws Exception
	{
		String msgCd = msg.getMsgCd();
		ServicePO serviceVO = msgDefService.getService(msgCd);
		if (serviceVO == null)
		{
			log.warn("NO Service by " + msgCd);
			return true;
		}

		// 1. 判断服务状态是否可用 T表测试正常状态，O表生产正常状态
		// 读取esb_config表配置, 默认为2，5有效
		ArrayList validServiceStatusList = (ArrayList) AppConfig.getInstance()
				.getProperty(ESBConfig.MB_validServiceStatus, defValidServiceStatus);
		if (!StringX.nullity(serviceVO.getStatus()) && !StringX.contain(
				(String[]) validServiceStatusList
						.toArray(new String[validServiceStatusList.size()]),
				serviceVO.getStatus(), true))
		{
			log.warn("service status:" + serviceVO.getStatus() + " not in "
					+ validServiceStatusList);
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "status of service is unvalid!!!");
		}

		// 2. 判断服务时间窗口，如果没指定时间窗口则认为不限制
		String currentDt = new SimpleDateFormat("yyyyMMdd").format(new Date());
		if ((!StringX.nullity(serviceVO.getPublishDt())
				&& currentDt.compareTo(serviceVO.getPublishDt()) < 0)
				|| (!StringX.nullity(serviceVO.getEffectDt())
						&& currentDt.compareTo(serviceVO.getEffectDt()) > 0))
		{
			log.warn("currentDt: " + currentDt + ", publishDt:" + serviceVO.getPublishDt()
					+ ", effectDt" + serviceVO.getEffectDt());
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "service not in valid date!!!");
		}

		// 3. 如果服务配置了规则校验则使用规则判断
		if (!StringX.nullity(serviceVO.getValidRule()))
		{
			Map root = new HashMap();
			Message hmsg = new Message();
			hmsg.setHeader(msg.getHeader()); // 只根据报文头判断
			MsgFTLUtil.model(root, hmsg);
			String errMsg = StringX.trim(FTLUtil.freemarker(serviceVO.getValidRule(), root));
			if (!StringX.nullity(errMsg)) throw new AppException(ESBRetCode.SERVICE_UNAUTH, errMsg);
		}

		// 4. 检查服务是否超过规定的最大报文长度
		String msgLen = StringX.null2emptystr(msg.getInLocal(ESBMsgLocalKey.MSG_LENGTH));
		if (!StringX.nullity(msgLen) && serviceVO.getMaxLen() != null && serviceVO.getMaxLen() >= 0
				&& (Integer.parseInt(msgLen) > serviceVO.getMaxLen()))
		{
			log.warn("msgLen: " + msgLen + ", maxLen:" + serviceVO.getMaxLen());
			throw new AppException(ESBRetCode.SERVICE_UNAUTH, "msg is too long!!!");
		}
		return true;
	}

	// 判断服务授权状态
	protected boolean checkAuth(IMessage msg)
	{
		String mbrCd = StringX.null2emptystr(msg.getSndNode()).trim();
		String appCd = msg.getSndApp();
		String msgCd = msg.getMsgCd();

		// 40 2013-05-09 chenjs 增加了Node表批量正则表达式授权
		NodePO node = esbInfoService.getNode(mbrCd + appCd);
		if (node != null && !StringX.nullity(node.getAuthMsgCd())
				&& ("*".equals(node.getAuthMsgCd())
						|| RegExp.match(msgCd, Pattern.compile(node.getAuthMsgCd()))))
			return true;

		// 2. 400版本后如果存在esb_nodeservice, 则优先使用此数据信息
		NodeServicePO nodeServiceVO = esbInfoService.getNodeService(mbrCd + appCd, msgCd);
		if (nodeServiceVO != null) return new NodeServiceAttr(nodeServiceVO.getAttr()).isAuth();
		return false;
	}

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}

	public void setMsgDefService(MsgDefService msgDefService)
	{
		this.msgDefService = msgDefService;
	}

	public void setDefValidServiceStatus(String[] defValidServiceStatus)
	{
		this.defValidServiceStatus = new ArrayList<>();
		for (String s : defValidServiceStatus)
			this.defValidServiceStatus.add(s);
	}
}
