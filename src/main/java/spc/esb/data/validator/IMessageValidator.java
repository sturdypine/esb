package spc.esb.data.validator;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IMessage;

public interface IMessageValidator
{
	MessageErrors validate(IMessage msg, MessageErrors errors);

	final Map VALIDATOR = new HashMap();
}
