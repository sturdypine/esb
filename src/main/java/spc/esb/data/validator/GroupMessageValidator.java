package spc.esb.data.validator;

import java.util.ArrayList;
import java.util.List;

import spc.esb.data.IMessage;
import spc.webos.util.StringX;

public class GroupMessageValidator extends AbstractMessageValidator
{
	String validatorNames;
	List validators;

	public MessageErrors validate(IMessage msg, MessageErrors errors)
	{
		if (validators == null) return errors;
		for (int i = 0; i < validators.size(); i++)
		{
			IMessageValidator validator = (IMessageValidator) validators.get(i);
			validator.validate(msg, errors);
		}
		return errors;
	}

	public String[] getValidators(IMessage msg)
	{
		return null;
	}

	public void init() throws Exception
	{
		super.init();
		if (validators != null || StringX.nullity(validatorNames)) return;
		validators = new ArrayList();
		String[] val = StringX.split(validatorNames, StringX.COMMA);
		for (int i = 0; i < val.length; i++)
		{
			IMessageValidator validator = (IMessageValidator) VALIDATOR.get(val[i]);
			if (validator != null) validators.add(validator);
			else log.warn("canot find validator: " + val[i]);
		}
	}

	public void setValidatorNames(String validatorNames)
	{
		this.validatorNames = validatorNames;
	}

	public void setValidators(List validators)
	{
		this.validators = validators;
	}
}
