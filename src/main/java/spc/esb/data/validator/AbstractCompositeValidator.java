package spc.esb.data.validator;

import java.text.MessageFormat;

import org.springframework.validation.Errors;

import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgValidatorPO;
import spc.webos.util.StringX;

/**
 * 为复杂多节点之间的验证提供从报文中获取待验值的模板
 * 
 * @author spc
 * 
 */
public abstract class AbstractCompositeValidator implements ICompositeValidator
{
	public void init() throws Exception
	{
		if (name != null) VALIDATOR.put(name, this);
	}

	/**
	 * 考虑到生成错误消息的多样性, 单独封装一个方法来生产错误信息描述
	 * 
	 * @param errors
	 * @param fieldName
	 * @param args
	 */
	public void reject(Errors errors, String fieldName, Object[] args, MsgValidatorPO msgValidatorVO)
	{
		fieldName = fieldName.replace('/', '.');
		// 如果当前验证器没有配置了私有错误码和错误信息描述
		if (StringX.nullity(msgValidatorVO.getMsgFormat())) errors.rejectValue(fieldName, errCd,
				args, msgFormat == null ? null : new MessageFormat(msgFormat).format(args));
		else errors.rejectValue(fieldName, msgValidatorVO.getErrCd(), args, msgValidatorVO
				.getMsgFormat() == null ? null : new MessageFormat(msgValidatorVO.getMsgFormat())
				.format(args));
	}

	public INode getNode(IMessage msg, String path)
	{
		if (msg.isRequestMsg()) return msg.findInRequest(path);
		return msg.findInResponse(path);
	}

	protected String name;
	protected String errCd; // 验证的错误码
	protected String msgFormat; // 验证的错误模板

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
