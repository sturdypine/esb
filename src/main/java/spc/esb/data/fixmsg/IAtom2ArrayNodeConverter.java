package spc.esb.data.fixmsg;

import spc.esb.data.IAtomNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;

public interface IAtom2ArrayNodeConverter
{
	void pack(String[] array, int index, IAtomNode value, MsgSchemaPO schema) throws Exception;

	INode unpack(String[] array, int index, MsgSchemaPO schema) throws Exception;
}
