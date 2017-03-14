package spc.esb.data.validator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.Errors;

import spc.esb.data.IMessage;
import spc.esb.model.MsgValidatorPO;

/**
 * 组合验证，根据给定的多个字段，验证多个字段之间的关系
 * 
 * @author spc
 * 
 */
public interface ICompositeValidator
{
	void validate(IMessage msg, MsgValidatorPO msgValidatorVO, Errors errors);

	final static Map VALIDATOR = new HashMap();
}
