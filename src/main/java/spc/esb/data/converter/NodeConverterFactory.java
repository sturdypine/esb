package spc.esb.data.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spc.esb.data.INode;

public class NodeConverterFactory
{
	static NodeConverterFactory cf = new NodeConverterFactory();
	List converters = new ArrayList();

	public void setConverters(List converters)
	{
		this.converters = converters;
	}

	private NodeConverterFactory()
	{
		converters.add(AtomNodeConverter.getInstance());
		converters.add(ArrayNodeConverter.getInstance());
		converters.add(CompositeNodeConverter.getInstance());
	}

	public static NodeConverterFactory getInstance()
	{
		return cf;
	}

	public INode unpack(Object obj, Map attribute)
	{
		if (obj == null) return null;
		if (obj instanceof INode) return (INode) obj;
		return lookup(obj).unpack(obj, attribute);
	}

	public INodeConverter lookup(Object obj)
	{
		for (int i = 0; i < converters.size(); i++)
		{
			INodeConverter converter = (INodeConverter) converters.get(i);
			if (converter.support(obj)) return converter;
		}
		return null;
	}

	// public ICompositeNode unpack(byte[] xml, int start, int len, boolean
	// gzip)
	// {
	// InputStream is = new ByteArrayInputStream(xml, start, len);
	// try
	// {
	// if (gzip) is = new GZIPInputStream(is);
	// }
	// catch (Exception e)
	// {
	// throw new RuntimeException(e);
	// }
	// return (ICompositeNode) XMLConverter.getInstance().unpack(is);
	// }

	public Object pack(INode node, Object target, Map attribute)
	{
		for (int i = 0; i < converters.size(); i++)
		{
			INodeConverter converter = (INodeConverter) converters.get(i);
			if (converter.supportNode(node)) return converter.pack(node, target, attribute);
		}
		return target instanceof Class ? null : target;
	}
}
