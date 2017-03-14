package spc.esb.converter;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.IMessage;

/**
 * 专供后台系统是tcp/http， 但报文规范却是ESB xml, ESB soap类型使用
 * 
 * @author chenjs
 * 
 */
public class BAESBXMLMsgConverter extends AbstractMsgConverter
{
	public IMessage deserialize(byte[] buf, IMessage reqmsg) throws Exception
	{ // 将服务放返回的esb xml or esb soap放入到local环境中
		reqmsg.setInLocal(ESBMsgLocalKey.LOCAL_REP_BYTES, buf);
		return reqmsg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{ // 直接发给服务方从MQ队列中获取的esb xml or esb soap报文内容发给后台系统
		return (byte[]) msg.getInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES);
	}
}
