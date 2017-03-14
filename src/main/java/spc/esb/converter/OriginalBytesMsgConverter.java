package spc.esb.converter;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.IMessage;
import spc.esb.data.util.MessageUtil;
import spc.webos.util.StringX;

/**
 * 在核心做了报文转换的情况下，后端服务方适配器的报文转换模式，此类适合同步请求后端服务的场景
 * 
 * @author chenjs
 * 
 */
public class OriginalBytesMsgConverter extends AbstractMsgConverter
{
	public IMessage deserialize(byte[] buf, IMessage reqmsg) throws Exception
	{
		reqmsg = getRequestMsg(buf, reqmsg);
		reqmsg.setOriginalBytes(buf);
		return reqmsg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		byte[] originalBytes = msg.getOriginalBytes();
		if (originalBytes != null) return originalBytes;
		// 400 chenjs 2013-07-29
		// 如果使用的是HeaderXMLConverter解析器，就只解析了header部分，需要从原始报文中抽取
		originalBytes = MessageUtil.getOriginalBytes((byte[]) msg
				.getInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES)); // 如果originalBytes存在，则抽取
		return StringX.decodeBase64(originalBytes);
	}
}
