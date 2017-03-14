package spc.esb.data.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.Errors;

import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 一组验证器，主要是把零散的验证器可以命名为一个组，然后引用这个组，在配置上简介化
 * 
 * @author spc
 * 
 */
public class GroupNodeValidator extends AbstractNodeValidator
{
	String validatorNames;
	List validators;

	public void validate(IMessage msg, String field, INode node, TreeNode tnode, Errors errors)
	{
		if (validators == null) return;
		for (int i = 0; i < validators.size(); i++)
		{
			INodeValidator validator = (INodeValidator) validators.get(i);
			validator.validate(msg, field, node, tnode, errors);
		}
	}

	public void init() throws Exception
	{
		super.init();
		if (validators != null || StringX.nullity(validatorNames)) return;
		validators = new ArrayList();
		String[] val = StringX.split(validatorNames, StringX.COMMA);
		for (int i = 0; i < val.length; i++)
		{
			INodeValidator validator = (INodeValidator) VALIDATOR.get(val[i]);
			if (validator != null) validators.add(validator);
			else log.warn("cannot find validator:" + val[i]);
		}
	}

	public void setValidatorNames(String validatorNames)
	{
		this.validatorNames = validatorNames;
	}

	public void addValidator(INodeValidator validator)
	{
		validators.add(validator);
	}

	public List getValidators()
	{
		return validators;
	}

	public void setValidators(List validators)
	{
		this.validators = validators;
	}
}
