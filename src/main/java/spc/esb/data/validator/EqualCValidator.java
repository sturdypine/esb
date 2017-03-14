package spc.esb.data.validator;

import org.springframework.validation.Errors;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.IMessage;
import spc.esb.model.MsgValidatorPO;
import spc.webos.util.StringX;

/**
 * 判断报文中两个字段应该相等
 * 
 * @author spc
 * 
 */
public class EqualCValidator extends AbstractCompositeValidator
{
	public void validate(IMessage msg, MsgValidatorPO msgValidatorVO, Errors errors)
	{
		String val1 = StringX.null2emptystr(getNode(msg, msgValidatorVO.getPath1()));
		String val2 = StringX.null2emptystr(getNode(msg, msgValidatorVO.getPath2()));
		// System.out.println(msgValidatorVO.getPath1()+"x "+val1+" - "+val2);
		if (!val1.equals(val2)) reject(
				errors,
				msgValidatorVO.getPath1(),
				new Object[] {
						StringX.nullity(msgValidatorVO.getName1()) ? msgValidatorVO.getPath1()
								: msgValidatorVO.getName1(),
						val1,
						StringX.nullity(msgValidatorVO.getName2()) ? msgValidatorVO.getPath2()
								: msgValidatorVO.getName2(), val2 }, msgValidatorVO);
	}

	public EqualCValidator()
	{
		name = "equal";
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
		msgFormat = StringX.utf82str("{0}({1}) \u5FC5\u987B\u7B49\u4E8E {2}({3})");
	}
}