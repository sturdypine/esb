package spc.esb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.constant.ESBConfig;
import spc.esb.constant.ESBMsgKey;
import spc.esb.data.Array2Node2XML;
import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.converter.SOAPConverter;
import spc.esb.model.LogPO;
import spc.webos.config.AppConfig;
import spc.webos.constant.Common;
import spc.webos.exception.Status;
import spc.webos.util.StringX;

/**
 * 流水日志工具类
 * 
 * @author spc
 * 
 */
public class LogUtil
{
	protected final static Logger LOG = LoggerFactory.getLogger(LogUtil.class);

	/**
	 * 用一行日志记录回复报文头, 考虑到恢复请求报文，还是应答报文
	 * 
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public static IMessage log2msg(LogPO log, IMessage msg) throws Exception
	{
		// 填充原信息
		msg.setMsgCd(log.getMsgCd());
		msg.setSndDt(log.getSndDt());
		msg.setSndTm(log.getSndTm());
		if (!StringX.nullity(log.getSndMbrCd())) msg.setSndNode(log.getSndMbrCd());
		msg.setSeqNb(log.getSeqNb());
		msg.setCallType(log.getCallTyp());
		msg.setSndAppCd(log.getSndAppCd());
		msg.setRcvAppCd(log.getRcvAppCd());
		if (!StringX.nullity(log.getRcvAppSn())) msg.setRcvAppSN(log.getRcvAppSn());
		if (!msg.isRequestMsg())
		{
			msg.setRefMsgCd(log.getRefMsgCd());
			msg.setRefSndApp(log.getRefSndAppCd());
			msg.setRefSndDt(log.getRefSndDt());
			if (!StringX.nullity(log.getCallTyp())) msg.setCallType(log.getCallTyp());
			// if (!StringX.nullity(log.getRefCallTyp()))
			// msg.setRefCallType(log.getRefCallTyp());
			if (!StringX.nullity(log.getRefSndMbrCd())) msg.setRefSndNode(log.getRefSndMbrCd());
			msg.setRefSeqNb(log.getRefSeqNb());
			Status status = new Status();
			status.setRetCd(log.getRetCd());
			status.setDesc(log.getRetDesc());
			status.setIp(log.getIp());
			status.setLocation(log.getLocation());
			status.setAppCd(log.getAppCd());
			msg.setStatus(status);
		}

		// ext处理
		if (!StringX.nullity(log.getExt()))
		{
			ICompositeNode ext = new SOAPConverter()
					.deserialize2composite(log.getExt().getBytes(Common.CHARSET_UTF8));
			msg.setHeaderExt(ext); // 原样返回请求端的ext
		}
		return msg;
	}

	/**
	 * 将msg变成logvo
	 * 
	 * @param msg
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public static LogPO msg2log(IMessage msg, LogPO log) throws Exception
	{
		msg.getHeader().toObject(log);
		log.setMsgSn(msg.getMsgSn());
		// 411_20141001 将文件传输信息加入到fts字段中
		ICompositeNode fts = msg.isRequestMsg() ? msg.findCompositeInRequest("FTS", null)
				: msg.findCompositeInResponse("FTS", null);
		if (fts != null) log.setFts(fts.toXml("FTS", false, Array2Node2XML.getInstance()));

		ICompositeNode hdrExt = msg.getHeaderExt();
		if (hdrExt != null && hdrExt.size() > 0)
			log.setExt(hdrExt.toXml(IMessage.TAG_HEADER_EXT, false, Array2Node2XML.getInstance()));
		// 452, 容许header/ext字段为任意字符串
		else if (!StringX.nullity(msg.getHeaderExtStr())) log.setExt(msg.getHeaderExtStr());
		if (!msg.isRequestMsg())
		{
			Status status = msg.getStatus();
			if (status != null)
			{
				ICompositeNode statusNode = new CompositeNode();
				statusNode.set(status);
				statusNode.toObject(log);
				int descLen = (Integer) AppConfig.getInstance()
						.getProperty(ESBConfig.JOURNAL_maxStatusDescSize, 900);
				String desc = (status.getDesc() != null && status.getDesc().length() > descLen)
						? status.getDesc().substring(0, descLen) : status.getDesc();
				log.setRetDesc(desc);
			}
			else LOG.info("response msg's status is null!!!");
		}
		// 2012-06-12 将ext/bpl信息写入logvo
		if (hdrExt != null)
		{
			ICompositeNode bpl = hdrExt.findComposite(ESBMsgKey.EXT_BPL, null);
			if (bpl == null) return log;
			log.setBplPSN(StringX.null2emptystr(bpl.get(ESBMsgKey.BPL_PMSGSN)));
			log.setBplNode(StringX.null2emptystr(bpl.get(ESBMsgKey.BPL_NODE_NAME)));
			log.setBplPSndMbrCd(StringX.null2emptystr(bpl.get(ESBMsgKey.BPL_PSNDMBRCD)));
			log.setBplPSndAppCd(StringX.null2emptystr(bpl.get(ESBMsgKey.BPL_PSNDAPPCD)));
			log.setBplFailAbort(StringX.null2emptystr(bpl.get(ESBMsgKey.BPL_FAIL_ABORT)));
		}
		return log;
	}
}
