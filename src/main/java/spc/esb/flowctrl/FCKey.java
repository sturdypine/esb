package spc.esb.flowctrl;

import spc.esb.data.IMessage;
import spc.esb.model.MessagePO;

public interface FCKey
{
	String key(IMessage msg, MessagePO msgVO, int priority) throws Exception;
}
