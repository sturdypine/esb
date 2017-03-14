package spc.esb.data.converter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.ArrayNode;
import spc.esb.data.AtomNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.INode;

public class ArrayNodeConverter implements INodeConverter
{
	static ArrayNodeConverter anc = new ArrayNodeConverter();
	static Logger log = LoggerFactory.getLogger(ArrayNodeConverter.class);

	private ArrayNodeConverter()
	{
	}

	public static ArrayNodeConverter getInstance()
	{
		return anc;
	}

	public boolean supportNode(INode node)
	{
		return (node instanceof IArrayNode);
	}

	public boolean support(Object obj)
	{
		return obj instanceof List || obj instanceof Iterator
				|| (obj.getClass() != byte[].class && obj.getClass().isArray());
	}

	public INode unpack(Object obj, Map attribute)
	{
		if (obj instanceof List) return new ArrayNode(new ArrayList((List) obj));
		if (obj instanceof Iterator)
		{
			Iterator iter = (Iterator) obj;
			List value = new ArrayList();
			while (iter.hasNext())
				value.add(iter.next());
			return new ArrayNode(value);
		}
		if (obj.getClass().isArray())
		{ // 当前对象是数组类型，可以转化为数组节点
			List value = toArrayIfNeeded(obj);
			return new ArrayNode(value);
		}
		return null;
	}

	static List toArrayIfNeeded(Object value)
	{
		List v = null;
		if (value.getClass().equals(int[].class))
		{
			int[] values = (int[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new Integer(values[i])));

			return v;
		}
		if (value.getClass().equals(Integer[].class))
		{
			Integer[] values = (Integer[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(values[i]));

			return v;
		}
		if (value.getClass().equals(short[].class))
		{
			short[] values = (short[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new Integer(values[i])));

			return v;
		}
		if (value.getClass().equals(Short[].class))
		{
			Short[] values = (Short[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new Integer(values[i].shortValue())));

			return v;
		}
		if (value.getClass().equals(long[].class))
		{
			long[] values = (long[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new Long(values[i])));

			return v;
		}
		if (value.getClass().equals(Long[].class))
		{
			Long[] values = (Long[]) value;
			v = new ArrayNode(values.length);

			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(values[i]));

			return v;
		}
		if (value.getClass().equals(float[].class))
		{
			float[] values = (float[]) value;
			v = new ArrayNode(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new BigDecimal(String.valueOf(values[i]))));
			return v;
		}
		if (value.getClass().equals(Float[].class))
		{
			Float[] values = (Float[]) value;
			v = new ArrayNode(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new BigDecimal(String.valueOf(values[i]))));
			return v;
		}
		if (value.getClass().equals(double[].class))
		{
			double[] values = (double[]) value;
			v = new ArrayList(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new BigDecimal(String.valueOf(values[i]))));
			return v;
		}
		if (value.getClass().equals(Double[].class))
		{
			Double[] values = (Double[]) value;
			v = new ArrayList(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new BigDecimal(String.valueOf(values[i]))));
			return v;
		}

		if (value.getClass().equals(boolean[].class))
		{
			boolean[] values = (boolean[]) value;
			v = new ArrayNode(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(new Boolean(values[i])));
			return v;
		}
		if (value.getClass().equals(Boolean[].class))
		{
			Boolean[] values = (Boolean[]) value;
			v = new ArrayNode(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(new AtomNode(values[i]));
			return v;
		}
		if (value.getClass().equals(String[].class))
		{
			String[] values = (String[]) value;
			v = new ArrayList(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(values[i]);
			return v;
		}
		if (value.getClass().equals(Object[].class))
		{
			Object[] values = (Object[]) value;
			v = new ArrayNode(values.length);
			for (int i = 0; i < values.length; i++)
				v.add(values[i]);
			return v;
		}
		v = new ArrayNode(Array.getLength(value));
		for (int i = 0; i < Array.getLength(value); i++)
			v.add(Array.get(value, i));
		return v;
	}

	public Object pack(INode node, Object target, Map attribute)
	{
		IArrayNode array = (IArrayNode) node;
		Class clazz = null;
		if (target instanceof Class) clazz = (Class) target;
		else clazz = target.getClass();
		if (clazz == String[].class)
		{
			String[] v = new String[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = ((IAtomNode) array.getNode(i)).stringValue();
			return v;
		}
		if (clazz == int[].class)
		{
			int[] v = new int[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = ((IAtomNode) array.getNode(i)).intValue();
			return v;
		}
		if (clazz == short[].class)
		{
			short[] v = new short[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = (short) ((IAtomNode) array.getNode(i)).intValue();
			return v;
		}
		if (clazz == long[].class)
		{
			long[] v = new long[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = ((IAtomNode) array.getNode(i)).longValue();
			return v;
		}
		if (clazz == float[].class)
		{
			float[] v = new float[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = Float.parseFloat(((IAtomNode) array.getNode(i))
						.stringValue());
			return v;
		}
		if (clazz == double[].class)
		{
			double[] v = new double[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = ((IAtomNode) array.getNode(i)).doubleValue();
			return v;
		}
		if (clazz == boolean[].class)
		{
			boolean[] v = new boolean[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = ((IAtomNode) array.getNode(i)).booleanValue();
			return v;
		}
		// for object
		if (clazz == Object[].class)
		{
			Object[] v = new Object[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = array.getNode(i);
			return v;
		}
		// 如果是数组。并且数组的元素是自定义类型
		Class compType = clazz.getComponentType();
		if (compType != null)
		{
			Object[] v = new Object[array.size()];
			for (int i = 0; i < v.length; i++)
				v[i] = NodeConverterFactory.getInstance().pack(
						array.getNode(i), compType, attribute);
			Object arr = Array.newInstance(compType, array.size());
			System.arraycopy(v, 0, arr, 0, array.size());
			return arr;
		}

		log.warn("array class is undefined in arraynodeconverter:"
				+ clazz.getName());
		return array;
	}
}
