package spc.esb.data.converter;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IAtomNode;

public interface IAtomConverter
{
	IAtomNode converter(IAtomNode src, boolean cnode2obj, Object attribute);

	final static Map CONVERTERS = new HashMap();
}
