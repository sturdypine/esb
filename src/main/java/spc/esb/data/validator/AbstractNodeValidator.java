package spc.esb.data.validator;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

public abstract class AbstractNodeValidator implements INodeValidator
{
	public Logger log = LoggerFactory.getLogger(getClass());
	protected String name;
	protected String errCd; // 错误码
	protected String msgFormat; // 返回消息模板

	public void reject(IMessage msg, String field, INode node, TreeNode tnode, Errors errors,
			Object[] args)
	{
		MsgSchemaPO struct = (MsgSchemaPO) tnode.getTreeNodeValue();
		String fieldName = StringX.nullity(struct.getFdesc()) ? field : struct.getFdesc();
		if (StringX.nullity(errCd))
			errors.rejectValue(field, ESBRetCode.MSG_FIELD_VALIDATOR, args, null);
		else errors.rejectValue(fieldName, errCd, args,
				msgFormat == null ? null : new MessageFormat(msgFormat).format(args));
	}

	public void init() throws Exception
	{
		if (name != null) VALIDATOR.put(name, this);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getErrCd()
	{
		return errCd;
	}

	public void setErrCd(String errCd)
	{
		this.errCd = errCd;
	}

	public String getMsgFormat()
	{
		return msgFormat;
	}

	public void setMsgFormat(String msgFormat)
	{
		this.msgFormat = msgFormat;
	}
}
