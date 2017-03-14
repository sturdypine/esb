package spc.esb.common.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.BytesMessage;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import spc.esb.common.service.JournalService;
import spc.esb.common.service.MsgDefService;
import spc.esb.constant.ESBCommon;
import spc.esb.constant.ESBConfig;
import spc.esb.constant.ESBMsgCode;
import spc.esb.core.TagAttr;
import spc.esb.core.service.CoreService;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.Message;
import spc.esb.data.MessageAttr;
import spc.esb.data.converter.MessageConverter;
import spc.esb.data.converter.SOAPConverter;
import spc.esb.data.util.INodeVisitor;
import spc.esb.data.util.MessageTraversal;
import spc.esb.model.AlarmLogPO;
import spc.esb.model.LogDetailPO;
import spc.esb.model.LogPO;
import spc.esb.model.MsgSchemaPO;
import spc.esb.util.LogUtil;
import spc.webos.config.AppConfig;
import spc.webos.constant.Common;
import spc.webos.exception.Status;
import spc.webos.persistence.IPersistence;
import spc.webos.persistence.jdbc.blob.ByteArrayBlob;
import spc.webos.service.BaseService;
import spc.webos.service.seq.UUID;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 将报文登记入数据库，根据日志点
 * 
 * @author chenjs
 * 
 */
@Service("esbJournalService")
public class JournalServiceImpl extends BaseService implements JournalService
{
	protected MessageConverter converter = new SOAPConverter(false);
	@Resource
	protected CoreService coreService;
	@Resource
	protected MsgDefService msgDefService;
	@Resource
	protected UUID uuid;
	@Resource
	@Qualifier("esbLogJmsTemplate")
	protected JmsTemplate jms;

	public void sendLog(IMessage msg, String logPoint)
	{
		if (!logPoint(msg, logPoint)) return;
		jms.send(ESBCommon.DEFAULT_ESBLOG, (s) -> {
			BytesMessage bmsg = s.createBytesMessage();
			bmsg.setStringProperty(Common.JMS_TRACE_NO, spc.webos.util.LogUtil.getTraceNo());
			bmsg.setStringProperty(ESBCommon.JMS_LOGPOINT, logPoint);
			SOAPConverter converter = new SOAPConverter(false, null, true);
			try
			{
				byte[] xml = converter.serialize(msg);
				if (log.isDebugEnabled())
					log.debug("esb.log xml:{}", new String(xml, converter.getCharset()));
				bmsg.writeBytes(xml);
			}
			catch (Exception e)
			{
				log.warn("fail to getBytes", e);
				return null;
			}
			return bmsg;
		});
	}

	public void sendAlarm(IMessage msg)
	{
		jms.send(ESBCommon.DEFAULT_ESBALARM, (s) -> {
			BytesMessage bmsg = s.createBytesMessage();
			bmsg.setStringProperty(Common.JMS_TRACE_NO, spc.webos.util.LogUtil.getTraceNo());
			SOAPConverter converter = new SOAPConverter(false, null, true);
			try
			{
				byte[] xml = converter.serialize(msg);
				if (log.isDebugEnabled())
					log.debug("esb.alarm xml:{}", new String(xml, converter.getCharset()));
				bmsg.writeBytes(xml);
			}
			catch (Exception e)
			{
				log.warn("fail to getBytes", e);
				return null;
			}
			return bmsg;
		});
	}

	public IMessage fetchJournal(LogPO logVO) throws Exception
	{
		if (StringX.nullity(logVO.getLogPoint())) logVO.setLogPoint("0"); // 如果没指定日志点，则默认为0日志点
		Map params = new HashMap();
		// added by chenjs 2011-04-22, 在查询到多条记录时, 用时间最新的一条
		params.put(IPersistence.SELECT_ATTACH_TAIL_KEY, " order by TMSTAMP desc");
		LogPO nlogVO = (LogPO) persistence.find(logVO, params);
		if (nlogVO == null)
		{
			log.warn("log cannot be found by : " + logVO);
			return null;
		}
		return LogUtil.log2msg(nlogVO, new Message());
	}

	public void doJournal(IMessage msg, String logPoint) throws Exception
	{
		doJournal(msg, logPoint, FastDateFormat.getInstance("yyyyMMddHHmmssSSS").format(new Date()),
				null, null);
	}

	public void doJournal(IMessage msg, String logPoint, String logDt) throws Exception
	{
		doJournal(msg, logPoint, logDt, null, null);
	}

	public boolean logPoint(IMessage msg, String logPoint)
	{
		if (!config.isProduct()) return true;
		MessageAttr attr = msgDefService.getMsgAttr(msg.getMsgCd());
		// 403_20140315 容许产品在测试模式下记录全报文
		if (attr != null && !attr.isLog(logPoint))
		{ // 为了减少日志量，对某些不重要报文只记录2个点日志
			log.debug("no point log");
			return false;
		}
		return true;
	}

	public void doJournal(IMessage msg, String logPoint, String logDt, String broker, String eg)
			throws Exception
	{
		boolean trace = config.getProperty(ESBConfig.JOURNAL_traceDetail, false);
		int maxMsgSize = config.getProperty(ESBConfig.JOURNAL_maxMsgSize, ESBCommon.DBMSG9K);
		int maxOrigBytesSize = config.getProperty(ESBConfig.JOURNAL_maxOrigBytesSize, 3000);
		String charset = config.getProperty(ESBConfig.JOURNAL_charset, Common.CHARSET_UTF8)
				.toString();
		// int descLen =config.getProperty(ESBConfig.JOURNAL_maxStatusDescSize,
		// 900);
		// String gateway =
		// StringX.null2emptystr(msg.getInLocal(ESBBPLVars.ENV_VARIABLE_GATEWAY));
		if (msg.getMsgCd() == null || msg.getMsgCd().trim().length() <= 0)
			msg.setMsgCd(ESBMsgCode.MSGCD_NOMSGCD_ERR);
		if (log.isInfoEnabled()) log.info(
				"sn: " + msg.getMsgSn() + ", logPoint: " + logPoint + ", msgCd: " + msg.getMsgCd());
		if (!logPoint(msg, logPoint)) return;

		byte[] originalBytes = msg.getOriginalBytes();
		String msgSn = msg.isRequestMsg() ? msg.getMsgSn() : msg.getRefMsgSn();
		LogPO logVO = LogUtil.msg2log(msg, new LogPO());
		logVO.setSeq(uuid.uuid());
		// modified by chenjs 2011-03-28 使用新的工具类完成

		if (StringX.nullity(logDt) || logDt.length() < 14)
		{
			log.warn("logdt is unvalid(" + logDt + "), use current system date!!!");
			logDt = FastDateFormat.getInstance("yyyyMMddHHmmssSSS").format(new Date());
		}
		logVO.setTmStamp(logDt); // 接收到报文的时间
		// chenjs 2012-08-15 将 tmstamp 字段分为dt, tm两个字段存放，便于索引，有利于统计
		logVO.setTdt(logDt.substring(0, 8));
		logVO.setPdd(logDt.substring(6, 8)); // 452, 设置分区日信息
		logVO.setTtm(logDt.substring(8, 14));
		logVO.setTtm10s(logDt.substring(8, 13)); // 每10秒统计
		logVO.setTtm1m(logDt.substring(8, 12)); // 每1分钟统计
		logVO.setTtm10m(logDt.substring(8, 11)); // 每10分钟统计
		logVO.setTtm1h(logDt.substring(8, 10)); // 每小时统计
		// chenjs 2012-08-15 end
		logVO.setOrignalLen(originalBytes == null ? 0 : originalBytes.length);
		logVO.setMsgSn(msgSn);
		logVO.setLogPoint(logPoint);
		logVO.setBroker(StringX.nullity(broker) ? SpringUtil.LOCAL_HOST_IP : broker);
		logVO.setEg(StringX.nullity(eg) ? SpringUtil.JVM : eg);
		if (logVO.getIp() != null && logVO.getIp().length() > 15)
			logVO.setIp(logVO.getIp().substring(0, 15)); // 无线网卡情况下可能获取不到准确IP，且字符串过长导致插入DB问题

		fillBiz(msg, logVO); // 填充业务信息
		persistence.insert(logVO);

		// 填写全报文信息
		if (!isBookInFullMsg(msg, logPoint)) return;
		byte[] xml = converter.serialize(msg);
		log.info("xml:{}, orignalCnt:{}", xml.length,
				(originalBytes == null ? 0 : originalBytes.length));
		LogDetailPO detailVO = new LogDetailPO();
		detailVO.setMsgSn(msgSn);
		detailVO.setEsbXML(new ByteArrayBlob(xml));
		if (originalBytes != null) detailVO.setOrigBytes(new ByteArrayBlob(originalBytes));
		detailVO.setSignature(msg.getSignature()); // 2012-01-20
		detailVO.setTmStamp(logDt);
		detailVO.setSeq(logVO.getSeq());
		try
		{
			persistence.insert(detailVO);
		}
		catch (Exception e)
		{
			log.warn("fail to detail...", e);
		}
	}

	protected boolean isBookInFullMsg(IMessage msg, String logPoint)
	{
		if (config.getProperty(ESBConfig.JOURNAL_traceDetail, false) || !config.isProduct())
			return true;
		MessageAttr attr = msgDefService.getMsgAttr(msg.getMsgCd());
		if (ESBCommon.REP_OUT_POINT.equals(logPoint))
		{
			Status status = msg.getStatus();
			if (status != null && !status.success()) return true;
		}
		if (attr == null)
		{
			log.warn("msgCd: " + msg.getMsgCd() + " 's attr is null!!!");
			return false;
		}
		return attr.isLog(logPoint) && attr.isFullLog();
	}

	public void doAlarm(IMessage msg, byte[] xml, String broker, String eg) throws Exception
	{
		if (xml == null) xml = SOAPConverter.getInstance().serialize(msg);
		if (msg == null) msg = SOAPConverter.getInstance().deserialize(xml);
		int descLen = (Integer) AppConfig.getInstance()
				.getProperty(ESBConfig.JOURNAL_maxStatusDescSize, 900);

		String tm = FastDateFormat.getInstance("yyyyMMddHHmmssSSS").format(new Date());
		AlarmLogPO alarmLogVO = new AlarmLogPO();
		alarmLogVO.setSeq(uuid.uuid());
		msg.getHeader().toObject(alarmLogVO);
		Status status = msg.getStatus();
		if (status != null)
		{
			alarmLogVO.setRetCd(status.getRetCd());
			alarmLogVO.setIp(status.getIp());
			alarmLogVO.setAppCd(status.getAppCd());
			alarmLogVO.setRetDesc(status.getDesc());
			alarmLogVO.setTraceNo(status.traceNo);
		}
		alarmLogVO.setTmStamp(tm); // 接收到报文的时间
		alarmLogVO.setMsgSn(msg.getRefMsgSn());
		alarmLogVO.setBroker(StringX.nullity(broker) ? SpringUtil.LOCAL_HOST_IP : broker);
		alarmLogVO.setEg(StringX.nullity(eg) ? SpringUtil.JVM : eg);
		// 错误描述截取200字符
		String desc = (status.getDesc() != null && status.getDesc().length() > descLen)
				? status.getDesc().substring(0, descLen) : status.getDesc();
		alarmLogVO.setRetDesc(desc);
		persistence.insert(alarmLogVO);
	}

	// added by chenjs 2012-05-12 填充biz1 - biz9
	protected void fillBiz(IMessage msg, LogPO logVO) throws Exception
	{
		TreeNode schema = msgDefService.getMsgSchema(msg.getMsgCd());
		if (schema == null)
		{
			log.info("schema is null by:{}", msg.getMsgCd());
			return;
		}
		JournalBizNodeVisitor jbnv = new JournalBizNodeVisitor(); // 业务日志信息
		MessageTraversal traversal = new MessageTraversal(
				msg.isRequestMsg() ? msg.getRequest() : msg.getResponse(), schema); // 消息遍历类
		traversal.dfs(jbnv);
		Map bizs = jbnv.getBizs();
		if (log.isDebugEnabled()) log.debug("bizs: " + bizs);
		logVO.setBiz1(StringX.null2emptystr(bizs.get("1")));
		logVO.setBiz2(StringX.null2emptystr(bizs.get("2")));
		logVO.setBiz3(StringX.null2emptystr(bizs.get("3")));
		logVO.setBiz4(StringX.null2emptystr(bizs.get("4")));
		logVO.setBiz5(StringX.null2emptystr(bizs.get("5")));
		logVO.setBiz6(StringX.null2emptystr(bizs.get("6")));
		logVO.setBiz7(StringX.null2emptystr(bizs.get("7")));
		logVO.setBiz8(StringX.null2emptystr(bizs.get("8")));
		logVO.setBiz9(StringX.null2emptystr(bizs.get("9")));
	}

	public void setConverter(MessageConverter converter)
	{
		this.converter = converter;
	}
}

class JournalBizNodeVisitor implements INodeVisitor
{
	protected Map<String, String> bizs = new HashMap<>();

	public boolean start(INode node, TreeNode nodeSchema) throws Exception
	{
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();
		TagAttr tagAttr = new TagAttr(schema.getTagAttr());
		if (!tagAttr.getBizNo().equals("0"))
			bizs.put(tagAttr.getBizNo(), StringX.null2emptystr(node));
		return true;
	}

	public boolean end(INode node, TreeNode nodeSchema) throws Exception
	{
		return true;
	}

	public Map<String, String> getBizs()
	{
		return bizs;
	}
}
