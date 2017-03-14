package spc.esb.data.validator;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.message.DictMessageSource;
import spc.webos.service.common.DictDesc;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 通用验证工具类
 * 
 * @author spc
 * 
 */
public class Utils
{
	static Logger log = LoggerFactory.getLogger(Utils.class);

	// 数字类型范围
	public static void number(IMessage msg, String field, INode node, TreeNode tnode,
			Errors errors, BigDecimal min, BigDecimal max, int decimal)
	{
		if (node == null) return;
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();

		// chenjs 2012-09-06 如果数字类型节点传入<num></num>容许
		if (StringX.nullity(node.toString()))
		{
			if (log.isDebugEnabled()) log.debug("tag(" + field
					+ ") is empty string for field type(" + String.valueOf(struct.getFtyp())
					+ ")!!!");
			return;
		}
		BigDecimal value = null;
		try
		{
			value = new BigDecimal(((IAtomNode) node).stringValue());
		}
		catch (NumberFormatException e)
		{ // 不是数字类型
			errors.rejectValue(fieldName, ESBRetCode.MSG_FIELD_NOTNUMBER, new Object[] {
					fieldName, node.toString() }, null);
			return;
		}
		if (min.subtract(value).doubleValue() > 0 || value.subtract(max).doubleValue() > 0) errors
				.rejectValue(fieldName, ESBRetCode.MSG_FIELD_VAL_RANGE, new Object[] { fieldName,
						String.valueOf(value), String.valueOf(min), String.valueOf(max) }, null);
		// 检查小数点位数
		String val = node.toString();
		int index = val.indexOf('.');
		if (index >= 0 && val.length() - index - 1 > decimal) errors.rejectValue(fieldName,
				ESBRetCode.MSG_FIELD_DECIMAL,
				new Object[] { field, String.valueOf(value), String.valueOf(decimal) }, null);
	}

	// 长度验证，支持数组，复杂节点，字符串类型
	public static void length(IMessage msg, String field, INode node, TreeNode tnode,
			Errors errors, int min, int max)
	{
		if (node == null) return;
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
		if (node instanceof IArrayNode)
		{
			IArrayNode anode = (IArrayNode) node;
			if (anode.size() < min || anode.size() > max) errors.rejectValue(field,
					ESBRetCode.MSG_FIELD_LEN,
					new Object[] { fieldName, String.valueOf(anode.size()), String.valueOf(min),
							String.valueOf(max) }, null);
		}
		else if (node instanceof ICompositeNode)
		{
			ICompositeNode cnode = (ICompositeNode) node;
			if (cnode.size() < min || cnode.size() > max) errors.rejectValue(field,
					ESBRetCode.MSG_FIELD_LEN,
					new Object[] { fieldName, String.valueOf(cnode.size()), String.valueOf(min),
							String.valueOf(max) }, null);
		}
		else
		{
			String value = node.toString();
			if (value.length() < min || value.length() > max) errors.rejectValue(field,
					ESBRetCode.MSG_FIELD_LEN,
					new Object[] { fieldName, value, String.valueOf(min), String.valueOf(max) },
					null);
		}
	}

	// 正则表达式验证
	public static void regex(IMessage msg, String field, INode node, TreeNode tnode, Errors errors,
			Pattern pattern)
	{
		if (node == null) return;
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
		String str = node.toString(); // 2012-05-29 空字符串不校验正则表达式
		if (!StringX.nullity(str) && !pattern.matcher(str).matches()) errors.rejectValue(fieldName,
				ESBRetCode.MSG_FIELD_REG,
				new Object[] { field, node.toString(), pattern.pattern() }, null);
	}

	// 是否必输验证
	public static void require(String field, INode node, TreeNode tnode, Errors errors)
	{
		if (node != null) return;
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
		errors.rejectValue(field, ESBRetCode.MUST_OPTIONAL, new Object[] { fieldName }, null);
	}

	// 是否符合数据字典定义
	public static void dict(IMessage msg, String field, INode node, TreeNode tnode, Errors errors,
			String dict)
	{
		if (node == null) return;
		DictDesc ddesc = (DictDesc) DictMessageSource.getInstance().getDict().get(dict);
		if (ddesc == null)
		{
			if (log.isInfoEnabled()) log.info("cannot find dict desc for " + dict);
			return;
		}
		String value = ((IAtomNode) node).stringValue();
		if (ddesc.dfs(value, null) == null)
		{
			MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
			String fieldNm = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
			Object[] args = new Object[] { fieldNm, value };
			MessageFormat mf = new MessageFormat(
					StringX.utf82str("{0}({1}) \u4E0D\u7B26\u5408\u6570\u636E\u5B57\u5178\u5B9A\u4E49("
							+ dict + ")"));
			errors.rejectValue(field, ESBRetCode.MSG_FIELD_VALIDATOR, args, mf.format(args));
		}
	}
}
