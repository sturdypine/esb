package spc.esb.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.converter.CompositeNodeConverter;
import spc.esb.data.converter.NodeConverterFactory;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.constant.Common;
import spc.webos.exception.AppException;
import spc.webos.util.StringX;

public class CompositeNode extends HashMap implements ICompositeNode
{
	private static final long serialVersionUID = 1L;

	public ICompositeNode clone()
	{
		CompositeNode cnode = new CompositeNode();
		Iterator keys = keys();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			INode node = getNode(key);
			if (node instanceof IArrayNode) cnode.put(key, ((IArrayNode) node).clone());
			else if (node instanceof ICompositeNode)
				cnode.put(key, ((ICompositeNode) node).clone());
			else cnode.put(key, node);
		}
		return cnode;
	}

	protected String ns;

	public String getNs()
	{
		return StringX.null2emptystr(ns);
	}

	public void setNs(String ns)
	{
		this.ns = ns;
	}

	public CompositeNode()
	{
	}

	public CompositeNode(ICompositeNode cnode)
	{
		set(cnode);
		Map ext = cnode.getExt();
		if (ext != null && ext.size() > 0) this.ext = new HashMap(ext);
	}

	public CompositeNode(int size)
	{
		super(size);
	}

	public CompositeNode(Map value)
	{
		set((ICompositeNode) NodeConverterFactory.getInstance().unpack(value, null));
		// modified by chenjs 2011-10-28
		// super(value);
	}

	public Object putAsFirstChild(String key, Object o)
	{
		return set(key, o);
	}

	public Object putAsLastChild(String key, Object o)
	{
		return set(key, o);
	}

	public void remove(String key)
	{
		if (key == null) return;
		int index = key.lastIndexOf('/');
		if (index < 0)
		{
			super.remove(key);
			return;
		}
		ICompositeNode parent = findComposite(key.substring(0, index), null);
		if (parent != null) parent.remove(key.substring(index + 1));
	}

	public void remove(String[] keys)
	{
		if (keys == null) return;
		for (int i = 0; i < keys.length; i++)
			remove(keys[i]);
	}

	public void removeNotIn(String[] keys)
	{
		removeNotIn(keys, false);
	}

	public void removeNotIn(String[] keys, boolean ignoreCase)
	{
		List delKeys = new ArrayList();
		Iterator ks = keys();
		while (ks.hasNext())
		{
			String key = ks.next().toString();
			if (!StringX.contain(keys, key, ignoreCase)) delKeys.add(key);
		}
		for (int i = 0; i < delKeys.size(); i++)
			remove(delKeys.get(i));
	}

	public Map mapValue()
	{
		return this;
	}

	public Iterator keys()
	{
		return super.keySet().iterator();
	}

	public INode find(String curPath, String path, byte type, INode defaultValue)
	{
		INode node = find(curPath, path, type, true);
		return node == null ? defaultValue : node;
	}

	public boolean dfs(INodeVisitor visitor)
	{
		return dfs(visitor, null, null);
	}

	public boolean dfs(INodeVisitor visitor, INode parent, String tag)
	{
		if (!visitor.visitor(this, parent, tag)) return false;
		Iterator keys = keys();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			INode node = getNode(key);
			if (!visitor.visitor(node, this, key)) return false;
			if (node instanceof ICompositeNode)
			{
				if (!((ICompositeNode) node).dfs(visitor, this, key)) return false;
			}
			else if (node instanceof IArrayNode)
			{
				IArrayNode array = (IArrayNode) node;
				for (int i = 0; i < array.size(); i++)
				{
					INode n = array.getNode(i);
					if (!visitor.visitor(n, array, String.valueOf(i))) return false;
					if (n instanceof ICompositeNode)
					{
						if (!((ICompositeNode) n).dfs(visitor, array, String.valueOf(i)))
							return false;
					}
				}
			}
		}
		return true;
	}

	public INode find(String curPath, String path, byte type, boolean canNull)
	{
		// 如果目标数据类型是字符串,实际类型是I,L,D,S,C,O五种类型为同一类型
		type = compatibleType(type);
		// cn_string
		INode node = find(path);
		if (!canNull
				&& (node == null || (node instanceof IAtomNode && ((IAtomNode) node).isNull())))
			throw new AppException(ESBRetCode.MSG_UNDEF_TAG,
					new Object[] { curPath + IMessage.PATH_DELIM + path }, this);
		if (type == INode.TYPE_UNDEFINED) return node;
		// byte xType = node.type();
		// if (xType == INode.TYPE_CNSTR) xType = INode.TYPE_STRING;
		// 如果目标数据类型是字符串,实际类型是I,L,D
		if (node != null && type != compatibleType(node.type()))
			throw new AppException(ESBRetCode.MSG_UNMATCH_TYPE,
					new Object[] { curPath + IMessage.PATH_DELIM + path,
							String.valueOf((char) node.type()), String.valueOf((char) type) },
					this);
		return node;
	}

	byte compatibleType(byte type)
	{
		if (type == INode.TYPE_STRING || type == INode.TYPE_CNSTR || type == INode.TYPE_INT
				|| type == INode.TYPE_LONG || type == INode.TYPE_DOUBLE || type == INode.TYPE_BOOL
				|| type == INode.TYPE_BYTE)
			return INode.TYPE_STRING;
		return type;
	}

	public Object lookup(String path, Object defaultValue)
	{
		Object v = find(path);
		return v == null ? defaultValue : v;
	}

	public INode findIgnoreCase(String path)
	{
		// if (path.indexOf('.') >= 0) path = path.replace('.', '/');
		path = stdPath(path);
		String key = path;
		int index = path.indexOf('/');
		if (index > 0) key = path.substring(0, index);

		INode node = getNodeIgnoreCase(key);
		if (node == null) return null;
		if (index > 0)
		{
			path = path.substring(index + 1);
			if (node instanceof IArrayNode) return ((IArrayNode) node).findIgnoreCase(path);
			return ((ICompositeNode) node).findIgnoreCase(path);
		}
		return node;
	}

	public ICompositeNode findComposite(String path, ICompositeNode def)
	{
		if (path == null) return null;
		ICompositeNode cnode = (ICompositeNode) find(path, CompositeNode.class);
		return cnode == null ? def : cnode;
	}

	public IArrayNode findArray(String path, IArrayNode def)
	{
		if (path == null) return null;
		IArrayNode anode = (IArrayNode) find(path, ArrayNode.class);
		return anode == null ? def : anode;
	}

	public IAtomNode findAtom(String path, IAtomNode def)
	{
		if (path == null) return null;
		IAtomNode anode = (IAtomNode) find(path, AtomNode.class);
		return anode == null ? def : anode;
	}

	public INode find(String path)
	{
		if (path == null) return null;
		// if (path.indexOf('.') >= 0) path = path.replace('.', '/');
		path = stdPath(path);
		String key = path;
		int index = path.indexOf('/');
		if (index > 0) key = path.substring(0, index);

		INode node = getNode(key);
		if (node == null) return null;
		if (index > 0)
		{
			path = path.substring(index + 1);
			// modified by spc 2010.1.22 增强空标签报文的处理能力
			if (StringX.startsWithNumber(path))
			{
				IArrayNode an = findArray(key, null);
				return an == null ? null : an.find(path); // 如果路径以数字开头，则说明需要一个数组节点
			}
			ICompositeNode cn = findComposite(key, null);
			return cn == null ? null : cn.find(path); // 否则需要一个复杂节点，因为后面还有路径。此时可能将一个原子节点变为复杂节点
			// if (node instanceof IArrayNode) return ((IArrayNode)
			// node).find(path);
			// if (node instanceof IAtomNode) return null; //
			// 如果是原子节点，则说明当期原子节点表达了一个空复杂节点
			// return ((ICompositeNode) node).find(path);
		}
		return node;
	}

	public Object find(String path, Object target)
	{
		if (path == null) return null;
		path = stdPath(path);
		INode value = find(path);
		if (target == null || value == null) return value;
		// if (path.indexOf('.') >= 0) path = path.replace('.', '/');
		Class clazz = null;
		if (target instanceof Class) clazz = (Class) target;
		else clazz = target.getClass();

		if (clazz.isAssignableFrom(value.getClass())) return value;
		if ((clazz == ArrayNode.class || clazz == List.class)
				&& (!(value instanceof List) || !(value instanceof IArrayNode)))
		{ // 如果需要的类型是AtomNode 或者是List类型， 而当前节点又不是，则把当前节点封装为AtomNode类型
			IArrayNode an = new ArrayNode();
			an.add(value);
			return an;
		}
		return NodeConverterFactory.getInstance().pack(value, target, null);
	}

	public Object toObject(Object target)
	{
		return NodeConverterFactory.getInstance().pack(this, target, null);
	}

	public Object toObject(Object target, Map attr)
	{
		return NodeConverterFactory.getInstance().pack(this, target, attr);
	}

	public ICompositeNode create(String path)
	{
		return create(path, null);
	}

	public ICompositeNode create(String path, ICompositeNode data)
	{
		path = stdPath(path);
		String key = path;
		int index = path.indexOf('/');
		if (index > 0) key = path.substring(0, index);
		INode o = getNode(key);
		if (o == null || !(o instanceof ICompositeNode))
		{
			o = data == null ? new CompositeNode() : data.newInstance();

			// modifed by chenjs 2011-12-26 如果名字中有:，表示有命名空间
			int idx = key.indexOf(":");
			if (idx > 0)
			{
				o.setNs(key.substring(0, idx));
				key = key.substring(idx + 1);
			}

			put(key, o);
		}
		if (index > 0) return ((ICompositeNode) o).create(path.substring(index + 1), data);

		return (ICompositeNode) o;
	}

	public INode getNodeIgnoreCase(String name)
	{
		Iterator keys = keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();
			if (key.equalsIgnoreCase(name)) return getNode(key);
		}
		return null;
	}

	public INode getNode(String name)
	{
		Object o = super.get(name);
		if (o == null || o instanceof INode) return (INode) o;
		INode node = NodeConverterFactory.getInstance().unpack(o, null);
		// set(name, node); // 不做写回动作,
		// 在hashmap结构中，可能产生java.util.ConcurrentModificationException 异常
		return node;
	}

	public Object set(String xml)
	{
		try
		{
			setAll(SOAPConverter.getInstance().deserialize2composite(xml.getBytes()));
			return this;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public ICompositeNode setAll(ICompositeNode cn)
	{
		if (cn == null) return this;
		super.putAll(cn.mapValue());
		return this;
	}

	public ICompositeNode apply(ICompositeNode cn)
	{
		if (cn == null || cn.size() == 0) return this;
		Iterator keys = cn.keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();
			INode node = getNode(key);
			INode nnode = cn.getNode(key);
			if (node instanceof ICompositeNode && nnode instanceof ICompositeNode)
				((ICompositeNode) node).apply((ICompositeNode) nnode);
			else set(key, nnode);
		}
		return this;
	}

	public ICompositeNode apply(ICompositeNode cn, String[] keys)
	{
		return apply(cn, keys, null);
	}

	public ICompositeNode apply(ICompositeNode cn, String[] keys, String[] names)
	{
		if (cn == null || cn.size() == 0) return this;
		if (keys == null) return apply(cn);
		for (int i = 0; i < keys.length; i++)
		{
			INode nnode = cn.getNode(keys[i]);
			if (nnode == null) continue;
			INode node = getNode(names == null ? keys[i] : names[i]);
			if (node instanceof ICompositeNode && nnode instanceof ICompositeNode)
				((ICompositeNode) node).apply((ICompositeNode) nnode);
			else set(names == null ? keys[i] : names[i], nnode);
		}
		return this;
	}

	public ICompositeNode applyIf(ICompositeNode cn)
	{
		if (cn == null || cn.size() == 0) return this;
		Iterator keys = cn.keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();
			INode node = getNode(key);
			INode nnode = cn.getNode(key);
			if (node instanceof ICompositeNode && nnode instanceof ICompositeNode)
				((ICompositeNode) node).applyIf((ICompositeNode) nnode);
			else if (node == null) set(key, nnode);
		}
		return this;
	}

	public ICompositeNode applyIf(ICompositeNode cn, String[] keys)
	{
		return applyIf(cn, keys, null);
	}

	public ICompositeNode applyIf(ICompositeNode cn, String[] keys, String[] names)
	{
		if (cn == null || cn.size() == 0) return this;
		if (keys == null) return applyIf(cn);
		for (int i = 0; i < keys.length; i++)
		{
			INode nnode = cn.getNode(keys[i]);
			if (nnode == null) continue;
			INode node = getNode(names == null ? keys[i] : names[i]);
			if (node instanceof ICompositeNode && nnode instanceof ICompositeNode)
				((ICompositeNode) node).applyIf((ICompositeNode) nnode);
			else if (node == null) set(names == null ? keys[i] : names[i], nnode);
		}
		return this;
	}

	public Object set(String name, Object value)
	{
		if (value == null)
		{
			remove(name); // added by chenjs 2011-06-02, 如果输入值为null表示删除此标签
			return null;
		}
		name = stdPath(name);
		// if (name.indexOf('.') >= 0) name = name.replace('.', '/');
		int index = name.lastIndexOf('/');
		CompositeNode parent = this;
		if (index >= 0)
		{
			parent = (CompositeNode) create(name.substring(0, index));
			name = name.substring(index + 1);
		}
		INode node = NodeConverterFactory.getInstance().unpack(value, null);
		// modifed by chenjs 2011-12-26 如果名字中有:，表示有命名空间
		int idx = name.indexOf(":");
		if (idx > 0)
		{
			node.setNs(name.substring(0, idx));
			name = name.substring(idx + 1);
		}
		return parent.put(name, node);
	}

	public Object add(String name, Object value)
	{
		INode node = getNode(name);
		if (node == null) return set(name, value);
		if (node instanceof IArrayNode)
		{
			((IArrayNode) node).add(value);
			return node;
		}
		ArrayNode anode = new ArrayNode();
		anode.add(node);
		anode.add(value);
		set(name, anode);
		return anode;
	}

	public ICompositeNode set(Object obj)
	{
		return set((ICompositeNode) CompositeNodeConverter.getInstance().unpack(obj, null));
	}

	public ICompositeNode setByMapping(Object obj, Object mapping)
	{
		Map attr = new HashMap();
		attr.put(CompositeNodeConverter.ATTR_MAPPING, mapping);
		return set((ICompositeNode) CompositeNodeConverter.getInstance().unpack(obj, attr));
	}

	public ICompositeNode setByAttr(Object obj, Map attr)
	{
		return set((ICompositeNode) CompositeNodeConverter.getInstance().unpack(obj, attr));
	}

	public ICompositeNode set(ICompositeNode cn)
	{
		if (cn == null) return this;
		// modified by chenjs 2011-10-28
		super.putAll((CompositeNode) CompositeNodeConverter.getInstance().unpack(cn, null));
		// super.putAll(cn.mapValue());
		return this;
	}

	public Object getValue()
	{
		return this;
	}

	public ICompositeNode newInstance()
	{
		return new CompositeNode();
	}

	public String toXml(String tag, boolean pretty) throws IOException
	{
		return toXml(tag, pretty, DefaultNode2XML.getInstance());
	}

	public String toXml(String tag, boolean pretty, INode2XML node2xml) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		toXml(baos, tag, pretty, node2xml);
		try
		{
			return new String(baos.toByteArray(), Common.CHARSET_UTF8);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void toXml(OutputStream os, String tag, boolean pretty, INode2XML node2xml)
			throws IOException
	{
		toXml(os, tag, pretty, node2xml, new HashMap());
	}

	public void toXml(OutputStream os, String tag, boolean pretty, INode2XML node2xml,
			Map attribute) throws IOException
	{
		toXml(os, null, tag, pretty, node2xml, attribute);
	}

	public byte[] toXml(String ns, String tag, boolean pretty, INode2XML node2xml, Map attribute)
			throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		toXml(baos, ns, tag, pretty, node2xml, attribute);
		return baos.toByteArray();
	}

	public void toXml(OutputStream os, String ns, String tag, boolean pretty, INode2XML node2xml,
			Map attribute) throws IOException
	{
		List path = new ArrayList();
		path.add(tag);
		node2xml.node2xml(os, this, ns, tag, pretty, null, this, path, attribute, -1);
	}

	/*
	 * public void toXml(OutputStream os, int level) throws IOException {
	 * Iterator keys = super.keySet().iterator(); while (keys.hasNext()) {
	 * String key = (String) keys.next(); INode node = (INode) getNode(key); if
	 * (node == null || (INode.NO_NULL_TAG.get() != null && node.toString()
	 * .length() == 0)) continue; if (level > 0) { // for pretty os.write('\n');
	 * for (int j = 0; j < level * 4; j++) os.write(' '); } node.toXml(os, key,
	 * level + 1); } }
	 */
	public boolean equals(Object o)
	{
		if (o == this) return true;
		o = NodeConverterFactory.getInstance().unpack(o, null);
		if (!(o instanceof CompositeNode)) return false;
		CompositeNode vv = (CompositeNode) o;
		// if (value == null) return vv.value == null;
		return super.equals(vv);
	}

	public byte type()
	{
		return INode.TYPE_MAP;
	}

	public void setValue(Map value)
	{
		super.clear();
		super.putAll(value);
	}

	public boolean isNull()
	{
		return false;
	}

	protected String stdPath(String path)
	{
		if (path.indexOf('.') >= 0) path = path.replace('.', '/');
		if (path.indexOf('[') >= 0) path = path.replace('[', '/').replaceAll("]", "");
		return path;
	}

	protected Map ext = new HashMap();

	public Object getExt(String key)
	{
		return ext == null ? null : ext.get(key);
	}

	public void setExt(String key, Object value)
	{
		if (ext == null) ext = new HashMap();
		ext.put(key, value);
	}

	public Object removeExt(String key)
	{
		return ext == null ? null : ext.remove(key);
	}

	/*
	 * public String toXml(String tag, boolean pretty) throws IOException {
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream(); toXml(baos,
	 * tag, pretty ? 0 : -100000000); return baos.toString(); }
	 * 
	 * public void toXml(OutputStream os, String tag, int level) throws
	 * IOException { if (isNull()) return; byte[] t = tag.getBytes(); boolean
	 * unvalid = isUnvalidTag(tag); os.write('<'); byte[] tt = t; if (unvalid)
	 * os.write('X'); // 在前面加一个固定的字母 os.write(tt); // 如果标签不合法，则base64转换，并作标记 if
	 * (unvalid) { os.write(' '); os.write('x'); os.write('='); os.write('"');
	 * os.write('X'); os.write('"'); } // os.write(' '); // os.write('s'); //
	 * os.write('i'); // os.write('z'); // os.write('e'); // os.write('='); //
	 * os.write('"'); // os.write(String.valueOf(size()).getBytes()); //
	 * os.write('"'); // 如果CompositeNode节点包含元素则不需要输出t="M". if (size() == 0 &&
	 * INode.NO_TYPE_TAG.get() == null) addAttr(os, (byte) 't', (byte) type());
	 * ext2XML(os); os.write('>');
	 * 
	 * toXml(os, level);
	 * 
	 * if (level >= 0) { // for pretty os.write('\n'); for (int j = 0; j <
	 * (level - 1) * 4; j++) os.write(' '); } os.write('<'); os.write('/'); if
	 * (unvalid) os.write('X'); // 在前面加一个固定的字母 os.write(tt); os.write('>'); }
	 * 
	 * public boolean isUnvalidTag(String tag) { char first = tag.charAt(0); if
	 * ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) return
	 * false; return true; }
	 * 
	 * protected void ext2XML(OutputStream os) throws IOException { if (ext ==
	 * null || ext.size() == 0) return; Iterator keys = ext.keySet().iterator();
	 * while (keys.hasNext()) { String key = keys.next().toString(); addAttr(os,
	 * key.getBytes(), ext.get(key).toString().getBytes()); } }
	 * 
	 * protected void addAttr(OutputStream os, byte k, byte v) throws
	 * IOException { os.write(' '); os.write(k); os.write('='); os.write('"');
	 * os.write(v); os.write('"'); }
	 * 
	 * protected void addAttr(OutputStream os, byte[] k, byte[] v) throws
	 * IOException { os.write(' '); os.write(k); os.write('='); os.write('"');
	 * os.write(v); os.write('"'); }
	 */
	public Map getExt()
	{
		return ext;
	}

	public void setExt(Map ext)
	{
		this.ext = ext;
	}

	public String toString()
	{
		try
		{
			return toXml("root", true);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Map plainMapValue()
	{
		HashMap map = new HashMap();
		Iterator keys = keys();
		while (keys.hasNext())
		{
			String key = (String) keys.next();
			INode node = find(key);
			if (node == null) continue;
			if (node instanceof ICompositeNode)
				map.put(key, ((ICompositeNode) node).plainMapValue());
			else if (node instanceof IArrayNode) map.put(key, ((IArrayNode) node).plainListValue());
			else map.put(key, ((IAtomNode) node).stringValue());
		}
		return map;
	}
}
