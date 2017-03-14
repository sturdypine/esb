package spc.esb.converter;

import spc.esb.data.IMessage;
import spc.esb.data.Message;

public class CustomMsgConverter extends AbstractMsgConverter
{
	public IMessage deserialize(byte[] buf, IMessage reqmsg) throws Exception
	{
		IMessage msg = new Message();
		msg.setOriginalBytes(buf);
		return msg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		return msg.getOriginalBytes();
	}
}
