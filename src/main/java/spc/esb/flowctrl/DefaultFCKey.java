package spc.esb.flowctrl;

import spc.esb.data.IMessage;
import spc.esb.model.MessagePO;

public class DefaultFCKey implements FCKey
{
	public String key(IMessage msg, MessagePO msgVO, int priority) throws Exception
	{
		return msg.isRequestMsg() ? request(msg, msgVO, priority) : response(msg, msgVO, priority);
	}

	protected String request(IMessage msg, MessagePO msgVO, int priority) throws Exception
	{
		if (msgVO == null) return null;
		String sndNodeAppcd = msg.getSndNodeApp();
		String rcvNodeAppcd = msg.getRcvNodeApp();
		String msgCd = msg.getMsgCd();
		String callType = msg.getCallType();
		String msgAttr = msgVO.getMsgAttr();
		String key = sndNodeAppcd + "#" + rcvNodeAppcd + "#" + msgCd + "#" + callType + "#"
				+ msgAttr + "#" + priority;
		return key;
	}

	protected String response(IMessage msg, MessagePO msgVO, int priority) throws Exception
	{
		if (msgVO == null) return null;
		String sndNodeAppcd = msg.getRefSndNodeApp();
		String rcvNodeAppcd = msg.getSndNodeApp();
		String msgCd = msg.getRefMsgCd();
		String callType = msg.getCallType();
		String msgAttr = msgVO.getMsgAttr();
		String key = sndNodeAppcd + "#" + rcvNodeAppcd + "#" + msgCd + "#" + callType + "#"
				+ msgAttr + "#" + priority;
		return key;
	}
}
