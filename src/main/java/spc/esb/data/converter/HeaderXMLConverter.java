package spc.esb.data.converter;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.Array2Node2XML;
import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.esb.data.util.MessageUtil;
import spc.webos.util.StringX;

/**
 * 日志模板解析XML报文消息，为了提高ESB的性能，不需要解析xml报文的body部分，只解析头部分
 * 
 * @author spc
 * 
 */
public class HeaderXMLConverter extends SOAPConverter
{
	public HeaderXMLConverter()
	{
		node2xml = Array2Node2XML.getInstance();
	}

	public IMessage deserialize(byte[] buf, IMessage reqmsg) throws Exception
	{
		IMessage msg = new Message();
		// 711_20140725 增加前端对json格式处理
		if (isJSON(buf)) msg = deserializeJSON(buf, reqmsg);
		else msg.setTransaction(deserialize2composite(MessageUtil.removeBody(buf)));
		msg.setInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES, buf);
		return msg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		String originalBytes = msg.getOriginalBytesPlainStr();
		if (!StringX.nullity(originalBytes))
		{ // 当前端是fixmsg报文时，AsynESBCall节点使用此序列化器进行序列化时需要把全报文放入到REQ队列
			log.info("originalBytes.len=" + originalBytes.length());
			return super.serialize(msg);
		}
		byte[] xml = (byte[]) msg.getInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES);
		if (xml != null) return xml;
		log.info("cannot find LOCAL_ORIGINAL_REQ_BYTES !!!");
		return super.serialize(msg);
	}

	static HeaderXMLConverter HXC = new HeaderXMLConverter();

	public static SOAPConverter getInstance()
	{
		return HXC;
	}
}
