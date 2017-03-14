package spc.esb.data.util;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.model.MsgSchemaPO;

public interface IAtomConverter
{
	IAtomNode converter(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception;

	final static Map CONVERTERS = new HashMap();
}
