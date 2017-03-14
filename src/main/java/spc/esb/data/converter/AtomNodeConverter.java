package spc.esb.data.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.INode;

public class AtomNodeConverter implements INodeConverter
{
	static AtomNodeConverter ANC = new AtomNodeConverter();
	protected Logger log = LoggerFactory.getLogger(getClass());

	private AtomNodeConverter()
	{
	}

	public static AtomNodeConverter getInstance()
	{
		return ANC;
	}

	public boolean support(Object obj)
	{
		Class clazz = obj.getClass();
		return (clazz == String.class || clazz == Short.class || clazz == short.class
				|| clazz == Integer.class || clazz == int.class || clazz == Long.class
				|| clazz == long.class || clazz == byte[].class || clazz == Boolean.class
				|| clazz == boolean.class || clazz == Float.class || clazz == float.class
				|| clazz == Double.class || clazz == double.class || clazz == Character.class
				|| clazz == char.class || clazz == BigDecimal.class || clazz == BigInteger.class);
	}

	public INode unpack(Object obj, Map attribute)
	{
		Class clazz = obj.getClass();
		try
		{
			// System.out.println("unpack:"+obj.getClass()+","+obj);
			if (clazz == String.class) return new AtomNode((String) obj);
			else if (clazz == Character.class || clazz == char.class) return new AtomNode(
					(obj.toString()));
			else if (clazz == Short.class || clazz == short.class || clazz == int.class) return new AtomNode(
					(new Integer(obj.toString())));
			else if (clazz == Float.class || clazz == float.class) return new AtomNode(
					(new BigDecimal(String.valueOf(obj))));
			else if (clazz == double.class || clazz == Double.class) return new AtomNode(
					(Double) obj);
			else if (clazz == long.class || clazz == BigInteger.class) return new AtomNode(
					(new Long(obj.toString())));
			else if (clazz == boolean.class) return new AtomNode((new Boolean(obj.toString())));

			return new AtomNode(obj);
		}
		catch (RuntimeException e)
		{
			log.warn("clzz: " + clazz + ", obj:[" + obj + "]");
			throw e;
		}
	}

	public Object pack(INode node, Object target, Map attribute)
	{
		IAtomNode atom = (IAtomNode) node;
		Class clazz = null;
		if (target instanceof Class) clazz = (Class) target;
		else clazz = target.getClass();
		if (clazz == String.class) return atom.stringValue();
		if (atom.stringValue().length() == 0) return target instanceof Class ? null : target;
		if (clazz == Character.class || clazz == char.class) return new Character(atom
				.stringValue().charAt(0));
		if (clazz == Short.class || clazz == short.class) return new Short(atom.stringValue());
		if (clazz == Integer.class || clazz == int.class) return new Integer(atom.stringValue());
		if (clazz == Float.class || clazz == float.class) return new Float(atom.stringValue());
		if (clazz == double.class || clazz == Double.class) return new Double(atom.stringValue());
		if (clazz == long.class || clazz == Long.class) return new Long(atom.stringValue());
		if (clazz == boolean.class || clazz == Boolean.class) return new Boolean(
				atom.booleanValue());
		if (clazz == BigDecimal.class) return new BigDecimal(atom.toString());
		if (clazz == BigInteger.class) return new BigInteger(atom.toString());
		return target instanceof Class ? null : target;
	}

	public boolean supportNode(INode node)
	{
		return (node instanceof IAtomNode);
	}
}
