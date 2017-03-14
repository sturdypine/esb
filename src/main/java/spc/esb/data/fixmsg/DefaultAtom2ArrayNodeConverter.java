package spc.esb.data.fixmsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;

public class DefaultAtom2ArrayNodeConverter implements IAtom2ArrayNodeConverter
{
	protected Logger log = LoggerFactory.getLogger(getClass());
	public final static String DEFAULT_NUM_VALUE = "0";
	protected boolean floatWithDot = true;

	public void pack(String[] array, int index, IAtomNode value, MsgSchemaPO schema)
			throws Exception
	{
		if (value == null) array[index] = StringX.EMPTY_STRING;
		else array[index] = value.stringValue();
	}

	public INode unpack(String[] array, int index, MsgSchemaPO schema) throws Exception
	{
		return index < array.length ? new AtomNode(array[index]) : null;
	}
}
