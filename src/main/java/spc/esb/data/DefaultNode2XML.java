package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.webos.util.StringX;

public class DefaultNode2XML implements INode2XML
{
	public final static Logger log = LoggerFactory.getLogger(DefaultNode2XML.class);
	protected String tagType;
	protected String tagTypeMap;
	protected String tagTypeList;
	protected String tagList = INode.ARRAY_TAG;

	public DefaultNode2XML()
	{
	}

	public DefaultNode2XML(String tagList)
	{
		this.tagList = tagList;
	}

	public DefaultNode2XML(String tagType, String tagTypeMap, String tagTypeList, String tagList)
	{
		this.tagType = tagType;
		this.tagTypeMap = tagTypeMap;
		this.tagTypeList = tagTypeList;
		this.tagList = tagList;
	}

	public void array(OutputStream os, IArrayNode anode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
					throws IOException
	{
		// if (anode.size() == 0) addAttr(os, (byte) 't', INode.TYPE_ARRAY);
		addAttr(anode, os, (byte) 't', INode.TYPE_ARRAY, index);
		ext2XML(os, anode, parent, tag, anode.getExt(), attribute);
		os.write('>');
		for (int i = 0; i < anode.size(); i++)
		{
			INode node = (INode) anode.getNode(i);
			if (node == null) continue;
			if (!attribute.containsKey(ATTR_KEY_NO_EMPTY_TAG)
					&& ((node instanceof ICompositeNode && ((ICompositeNode) node).size() == 0)
							|| (node instanceof IArrayNode && ((IArrayNode) node).size() == 0)))
				continue; // 如果节点是map节点，但是节点没有内容则不需要序列话
			if (pretty)
			{ // for pretty
				os.write('\n');
				for (int j = 0; j < path.size(); j++)
					os.write('\t');
			}
			node2xml(os, node, ns, tagList, pretty, root, parent, path, attribute, i);
		}
	}

	public void map(OutputStream os, ICompositeNode cnode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
					throws IOException
	{
		addAttr(cnode, os, (byte) 't', INode.TYPE_MAP, index);

		ext2XML(os, cnode, parent, tag, cnode.getExt(), attribute);
		if (!StringX.nullity(tag)) os.write('>');

		// 序列化成员元素
		Iterator keys = cnode.keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();
			INode node = (INode) cnode.getNode(key);
			if (node == null || (attribute.get(ATTR_KEY_NO_NULL_TAG) != null
					&& node.toString().length() == 0))
				continue;
			if (!attribute.containsKey(ATTR_KEY_NO_EMPTY_TAG)
					&& (node.getExt() == null || node.getExt().size() == 0)
					&& ((node instanceof ICompositeNode && ((ICompositeNode) node).size() == 0)
							|| (node instanceof IArrayNode && ((IArrayNode) node).size() == 0)))
				continue; // 如果节点是map节点，但是节点没有内容则不需要序列话
			if (pretty)
			{ // for pretty
				os.write('\n');
				for (int j = 0; j < path.size() - 1; j++) // 增加-1
					os.write('\t');
			}
			path.add(key); // 增加路径
			node2xml(os, node, ns, key, pretty, root, cnode, path, attribute, -1);
			path.remove(path.size() - 1); // 推出路径
		}
	}

	public void atom(OutputStream os, IAtomNode anode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
					throws IOException
	{
		addAttr(anode, os, (byte) 't', INode.TYPE_STRING, index);
		if (anode.type() != INode.TYPE_STRING && attribute.get(ATTR_KEY_TYPE_TAG) != null)
			addAttr(anode, os, (byte) 't', (byte) anode.type(), index);
		ext2XML(os, anode, parent, tag, anode.getExt(), attribute);
		os.write('>');
		Object value = atom2obj(anode, ns, tag, pretty, root, parent, path, attribute, index);
		try
		{
			if (value instanceof byte[]) os.write(StringX.encodeBase64((byte[]) value));
			else if (value instanceof Boolean) os.write(value.toString().getBytes());
			// modified by chenjs 2012-01-25 boolean类型使用true/false表示
			// os.write(((Boolean) value).booleanValue() ? '1' : '0');
			else if (value instanceof String)
			{
				String v = (String) value;
				if (v.length() == 0) return;
				v = StringX.str2xml(v, !attribute.containsKey(ATTR_KEY_NO_CDATA));
				os.write(string2bytes(v, attribute));
			}
			else os.write(string2bytes(anode.toString(), attribute));
		}
		catch (Exception e)
		{
			log.error("atom", e);
			throw new RuntimeException(e);
		}
	}

	protected Object atom2obj(IAtomNode anode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
	{
		return anode.getValue();
	}

	// public static String str2xml(String v)
	// {
	// if (v.indexOf('&') >= 0) v = StringX.replaceAll(v, "&", "&amp;");
	// if (v.indexOf('<') >= 0) v = StringX.replaceAll(v, "<", "&lt;");
	// if (v.indexOf('>') >= 0) v = StringX.replaceAll(v, ">", "&gt;");
	// // if (v.indexOf('<') >= 0 || v.indexOf('>') >= 0 ||
	// // v.indexOf('&') >= 0) v = "<![CDATA["
	// // + value + "]]>";
	// return v;
	// }

	// modified by chenjs, 增加一个index参数，用于表示在数组的位置,
	public void node2xml(OutputStream os, INode node, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
					throws IOException
	{
		if (node.isNull()) return;
		startElement(os, node, ns, tag, pretty, root, parent, path, attribute);

		if (node instanceof IArrayNode)
			array(os, (IArrayNode) node, ns, tag, pretty, root, parent, path, attribute, index);
		else if (node instanceof ICompositeNode)
			map(os, (ICompositeNode) node, ns, tag, pretty, root, parent, path, attribute, index);
		else atom(os, (IAtomNode) node, ns, tag, pretty, root, parent, path, attribute, index);

		if (pretty && !(node instanceof IAtomNode))
		{ // for pretty
			os.write('\n');
			for (int j = 0; j < (path.size() - (tag.equals(tagList) ? 0 : 1)); j++)
				os.write('\t');
		}
		endElement(os, node, ns, tag, pretty, root, path, attribute);
	}

	protected void startElement(OutputStream os, INode node, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attr) throws IOException
	{
		if (StringX.nullity(tag)) return; // chenjs 2012-11-19支持不带标签情况
		if (tag.indexOf('.') >= 0) tag = tag.replace('.', '_'); // tag标签中不容许出现.
		String namespace = null;
		// added by chenjs 2011-12-20,如果某个节点指定了特殊ns，则使用特殊ns
		if (attr.containsKey(INode2XML.USING_NODE_NS)) namespace = node.getNs();
		if (StringX.nullity(namespace)) namespace = ns; // 否则使用默认ns

		// 一旦出现则用下划线记录
		byte[] t = StringX.nullity(namespace) ? tag.getBytes() : (namespace + ':' + tag).getBytes();
		os.write('<');
		byte[] tt = t;
		os.write(tt);
	}

	protected void endElement(OutputStream os, INode node, String ns, String tag, boolean pretty,
			ICompositeNode root, List path, Map attr) throws IOException
	{
		if (StringX.nullity(tag)) return; // chenjs 2012-11-19支持不带标签情况
		if (tag.indexOf('.') >= 0) tag = tag.replace('.', '_'); // tag标签中不容许出现.
		// 一旦出现则用下划线记录
		String namespace = null;
		// added by chenjs 2011-12-20,如果某个节点指定了特殊ns，则使用特殊ns
		if (attr.containsKey(INode2XML.USING_NODE_NS)) namespace = node.getNs();
		if (StringX.nullity(namespace)) namespace = ns; // 否则使用默认ns

		// 一旦出现则用下划线记录
		byte[] t = StringX.nullity(namespace) ? tag.getBytes() : (namespace + ':' + tag).getBytes();
		os.write('<');
		os.write('/');
		os.write(t);
		os.write('>');
	}

	public static boolean isUnvalidTag(String tag)
	{
		char first = tag.charAt(0);
		if ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) return false;
		return true;
	}

	public void addAttr(INode node, OutputStream os, byte k, byte v, int index) throws IOException
	{
		// if (StringX.nullity(tagType)) return;
		// os.write(' ');
		// os.write(tagType.getBytes());
		// os.write('=');
		// os.write('"');
		// if (v == INode.TYPE_MAP) os.write(tagTypeMap.getBytes());
		// else if (v == INode.TYPE_ARRAY)
		// {
		// os.write(tagTypeList.getBytes());
		// os.write('"');
		// os.write(' ');
		// os.write(tagType.getBytes());
		// os.write('=');
		// }
		// os.write('"');
	}

	public static void addAttr(OutputStream os, byte[] k, byte[] v) throws IOException
	{
		os.write(' ');
		os.write(k);
		os.write('=');
		os.write('"');
		os.write(v);
		os.write('"');
	}

	protected void ext2XML(OutputStream os, INode value, ICompositeNode parent, String name,
			Map ext, Map attribute) throws IOException
	{
		if (ext == null || ext.size() == 0) return;
		Iterator keys = ext.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			Object v = ext.get(key);
			if (v == null) continue;
			String str = v.toString(); // 700 2013-07-16 属性中出现'"两个字符需要转义
			if (str.indexOf('&') >= 0) str = StringX.replaceAll(str, "&", "&amp;");
			if (str.indexOf('\'') >= 0) str = StringX.replaceAll(str, "'", "&apos;");
			if (str.indexOf('"') >= 0) str = StringX.replaceAll(str, "\"", "&quot;");
			addAttr(os, key.getBytes(), string2bytes(str, attribute));
		}
	}

	// 根据定义的报文编码集，把string 变为 bytes
	public static byte[] string2bytes(String str, Map attribute)
	{
		if (str == null) return null;
		boolean cn2utf8 = attribute.get(ATTR_KEY_CN2UTF8) != null; // 是否中文转为utf8格式传输
		if (cn2utf8) return StringX.str2utf8(str).getBytes();
		String charset = (String) attribute.get(ATTR_KEY_CHARSET);
		if (charset == null) charset = DEFAULT_CHARSET;
		try
		{
			return str.getBytes(charset);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void setTagList(String tagList)
	{
		this.tagList = tagList;
	}

	final static DefaultNode2XML DN2X = new DefaultNode2XML();

	public static INode2XML getInstance()
	{
		return DN2X;
	}
}
