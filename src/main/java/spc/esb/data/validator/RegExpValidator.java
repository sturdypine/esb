package spc.esb.data.validator;

import java.util.regex.Pattern;

import org.springframework.validation.Errors;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 通用正则表达式验证
 * 
 * @author spc
 * 
 */
public class RegExpValidator extends AbstractNodeValidator
{
	protected Pattern pattern;

	public RegExpValidator()
	{
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
		msgFormat = StringX.utf82str("{0}({1})\u4E0D\u7B26\u5408\u6B63\u5219\u8868\u8FBE\u5F0F{2}");
	}

	public void validate(IMessage msg, String field, INode node, TreeNode tnode, Errors errors)
	{
		if (node == null) return;
		String value = node.toString();
		if (pattern.matcher(value).matches()) return;
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
		reject(msg, field, node, tnode, errors,
				new Object[] { fieldName, value, pattern.pattern() });
	}

	public Pattern getPattern()
	{
		return pattern;
	}

	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = Pattern.compile(pattern);
	}
}
