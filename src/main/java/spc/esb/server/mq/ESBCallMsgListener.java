package spc.esb.server.mq;

import javax.annotation.Resource;
import javax.jms.BytesMessage;
import javax.jms.Message;

import spc.esb.core.service.ESBService;
import spc.esb.data.IMessage;
import spc.esb.data.converter.JSONConverter;
import spc.esb.data.converter.MessageConverter;
import spc.webos.constant.Common;
import spc.webos.mq.MQ;
import spc.webos.mq.jms.AbstractBytesMessageListener;
import spc.webos.util.LogUtil;
import spc.webos.util.StringX;;

/**
 * 使用esb xml通过MQ调用ESB
 * 
 * @author chenjs
 *
 */
public class ESBCallMsgListener extends AbstractBytesMessageListener
{
	protected void onMessage(Message qmsg, String queue, String corId, byte[] buf)
	{
		try
		{
			IMessage msg = converter.deserialize(buf);
			String replyToQ = msg.getReplyToQ();
			String replyMsgCd = msg.getReplyMsgCd();
			log.info("MQ request: sn:{}, msgCd:{}, replyMsgCd:{}, corId:{},  replyToQ:{}",
					msg.getMsgSn(), msg.getMsgCd(), replyMsgCd, qmsg.getJMSCorrelationID(),
					replyToQ);
			msg.setOriginalBytes(buf);
			esbService.sync(msg);
			if (StringX.nullity(replyToQ)) return; // 通知报文
			// 优先使用前端适配器的输出内容
			final byte[] response = msg.getOriginalBytes() == null ? converter.serialize(msg)
					: msg.getOriginalBytes();
			log.info("MQ response: {}, replyMsgCd:{}, corId:{},  len:{}", replyToQ, replyMsgCd,
					qmsg.getJMSCorrelationID(), response.length);
			mq.jms().send(replyToQ, (s) -> {
				BytesMessage m = s.createBytesMessage();
				m.setStringProperty(Common.JMS_TRACE_NO, LogUtil.getTraceNo());
				m.setJMSCorrelationID(qmsg.getJMSCorrelationID());
				m.writeBytes(response);
				return m;
			});
		}
		catch (Exception e)
		{
			log.warn("ex:: corId:" + corId + ", buf:" + buf == null ? "" : new String(buf), e);
		}
	}

	@Resource
	protected MQ mq;
	@Resource
	protected ESBService esbService;
	MessageConverter converter = new JSONConverter(); // ESB json

	public void setMq(MQ mq)
	{
		this.mq = mq;
	}

	public void setEsbService(ESBService esbService)
	{
		this.esbService = esbService;
	}

	public void setConverter(MessageConverter converter)
	{
		this.converter = converter;
	}
}
