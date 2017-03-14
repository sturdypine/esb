package spc.esb.data.validator;

import org.springframework.validation.Errors;

import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.webos.util.tree.TreeNode;

public class LenValidator extends AbstractNodeValidator
{
	int min = 0;
	int max = 10000000;

	public void validate(IMessage msg, String field, INode node, TreeNode tnode, Errors errors)
	{
		Utils.length(msg, field, node, tnode, errors, min, max);
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public void setMax(int max)
	{
		this.max = max;
	}
}
