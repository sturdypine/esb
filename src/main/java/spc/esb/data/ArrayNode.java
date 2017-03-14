package spc.esb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spc.esb.data.converter.NodeConverterFactory;
import spc.webos.util.StringX;

public class ArrayNode extends ArrayList implements IArrayNode
{
	private static final long serialVersionUID = 1L;

	public ArrayNode()
	{
	}

	public ArrayNode(int size)
	{
		super(size);
	}

	public ArrayNode(List value)
	{
		super(value);
	}

	public IArrayNode clone()
	{
		IArrayNode anode = new ArrayNode();
		for (int i = 0; i < size(); i++)
		{
			INode node = getNode(i);
			if (node instanceof IArrayNode) anode.add(((IArrayNode) node).clone());
			else if (node instanceof ICompositeNode) anode.add(((ICompositeNode) node).clone());
			else anode.add(node);
		}
		return anode;
	}

	public Object getValue()
	{
		return this;
	}

	public List listValue()
	{
		return this;
	}

	public boolean add(Object o)
	{
		if (o == null) return false;
		return super.add(NodeConverterFactory.getInstance().unpack(o, null));
	}

	public void addAll(IArrayNode an)
	{
		addAll(an.listValue());
	}

	public boolean contains(Object o)
	{
		if (o == null) return false;
		INode n = NodeConverterFactory.getInstance().unpack(o, null);
		for (int i = 0; i < size(); i++)
			if (n.equals(get(i))) return true;
		return false;
	}

	public INode getNode(int index)
	{
		Object v = super.get(index);
		if (v == null || v instanceof INode) return (INode) v;
		INode n = NodeConverterFactory.getInstance().unpack(v, null);
		set(index, n);
		return n;
	}

	public INode findIgnoreCase(String path)
	{
		path = stdPath(path);
		int idx = path.indexOf('/');
		int index = Integer.parseInt(idx > 0 ? path.substring(0, idx) : path);
		if (index >= size()) return null;
		INode node = getNode(index);
		if (idx > 0)
		{
			path = path.substring(idx + 1);
			if (node instanceof IArrayNode) return ((IArrayNode) node).findIgnoreCase(path);
			return ((ICompositeNode) node).findIgnoreCase(path);
		}
		return node;
	}

	public INode find(String path)
	{
		path = stdPath(path);
		int idx = path.indexOf('/');
		int index = Integer.parseInt(idx > 0 ? path.substring(0, idx) : path);
		if (index >= size()) return null;
		INode node = getNode(index);
		if (idx > 0)
		{
			path = path.substring(idx + 1);
			if (node instanceof IArrayNode) return ((IArrayNode) node).find(path);
			return ((ICompositeNode) node).find(path);
		}
		return node;
	}

	protected String stdPath(String path)
	{
		if (path.indexOf('.') >= 0) return path.replace('.', '/');
		return path;
	}

	public Object removeNode(Object o)
	{
		if (o == null) return null;
		INode n = NodeConverterFactory.getInstance().unpack(o, null);
		for (int i = 0; i < super.size(); i++)
			if (n.equals(get(i))) return super.remove(i);
		return null;
	}

	public byte type()
	{
		return INode.TYPE_ARRAY;
	}

	public void setValue(List value)
	{
		super.clear();
		super.addAll(value);
	}

	public boolean isNull()
	{
		return false;
	}

	protected Map ext = new HashMap();

	public Map getExt()
	{
		return ext;
	}

	public Object getExt(String key)
	{
		return ext == null ? null : ext.get(key);
	}

	public Object removeExt(String key)
	{
		return ext == null ? null : ext.remove(key);
	}

	public void setExt(String key, Object value)
	{
		if (ext == null) ext = new HashMap();
		ext.put(key, value);
	}

	/*
	 * public void toXml(OutputStream os, int level) throws IOException { for
	 * (int i = 0; i < super.size(); i++) { INode node = (INode) getNode(i); if
	 * (node == null) continue; if (level > 0) { // for pretty os.write('\n');
	 * for (int j = 0; j < level * 4; j++) os.write(' '); } node.toXml(os, "v",
	 * level + 1); } }
	 * 
	 * public void toXml(OutputStream os, String tag, int level) throws
	 * IOException { if (isNull()) return; byte[] t = tag.getBytes(); boolean
	 * unvalid = isUnvalidTag(tag); os.write('<'); byte[] tt = t; if (unvalid)
	 * os.write('X'); // 在前面加一个固定的字母 os.write(tt); // 如果标签不合法，则base64转换，并作标记 if
	 * (unvalid) { os.write(' '); os.write('x'); os.write('='); os.write('"');
	 * os.write('X'); os.write('"'); } // os.write(' '); // os.write('s'); //
	 * os.write('i'); // os.write('z'); // os.write('e'); // os.write('='); //
	 * os.write('"'); // os.write(String.valueOf(size()).getBytes()); //
	 * os.write('"'); if (size() == 0 && INode.NO_TYPE_TAG.get() == null)
	 * addAttr(os, (byte) 't', (byte) type()); ext2XML(os); os.write('>');
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
	public void setExt(Map ext)
	{
		this.ext = ext;
	}

	public List plainListValue()
	{
		ArrayList list = new ArrayList();
		for (int i = 0; i < size(); i++)
		{
			INode node = getNode(i);
			if (node == null) continue;
			if (node instanceof ICompositeNode) list.add(((ICompositeNode) node).plainMapValue());
			else if (node instanceof IArrayNode) list.add(((IArrayNode) node).plainListValue());
			else list.add(((IAtomNode) node).stringValue());
		}
		return list;
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
}
