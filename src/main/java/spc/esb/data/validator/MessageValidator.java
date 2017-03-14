package spc.esb.data.validator;

import spc.esb.data.IMessage;

/**
 * 根据报文编号获取验证源，MessageValidator 只校验request 部分和 response部分
 * 
 * @author spc
 * 
 */
public class MessageValidator extends AbstractMessageValidator
{
	public String[] getValidators(IMessage msg)
	{
		return new String[] { msg.getMsgCd() };
	}

	public MessageValidator()
	{
		name = "MSGCD";
	}
}
