package spc.esb.data.validator;

import spc.esb.data.IMessage;

/**
 * 根据数据库的配置信息验证报文中的各字段, 并生成Errors信息， 是全文结构的验证
 * 
 * @author spc
 * 
 */
public class DefaultMessageValidator extends AbstractMessageValidator
{
	String[] validators;

	public String[] getValidators(IMessage msg)
	{
		return validators;
	}

	public void setValidators(String[] validators)
	{
		this.validators = validators;
	}
}
