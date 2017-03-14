package spc.esb.data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import spc.esb.data.converter.NodeConverterFactory;
import spc.webos.util.JsonUtil;
import spc.webos.util.StringX;

public final class AtomNode implements IAtomNode
{
	static
	{
		JsonUtil.registerStrSerializer(AtomNode.class);
	}
	
	private static final long serialVersionUID = 1L;
	// modified by chenjs 2011-12-20, xml 中bool不再使用"1";
	public final static String BOOL_TRUE = "true";
	public final static String BOOL_FALSE = "false"; // "0";

	public static final IAtomNode ZERO = new AtomNode(0);
	public static final IAtomNode TRUE = new AtomNode(true);
	public static final IAtomNode FALSE = new AtomNode(false);
	public static final IAtomNode EMPTY_STRING = new AtomNode(StringX.EMPTY_STRING);

	protected Object value;
	protected byte type;

	protected String ns;

	public String getNs()
	{
		return StringX.null2emptystr(ns);
	}

	public void setNs(String ns)
	{
		this.ns = ns;
	}

	// protected INode parent;
	//
	// public INode parent()
	// {
	// return this.parent;
	// }
	//
	// public void setParent(INode parent)
	// {
	// this.parent = parent;
	// }

	public AtomNode()
	{
	}

	public AtomNode(IAtomNode anode)
	{
		value = anode.stringValue();
		setExt(anode.getExt());
	}

	public AtomNode(Object value)
	{
		this.value = value;
	}

	public AtomNode(Object value, Map ext)
	{
		this.value = value;
		this.ext = ext;
	}

	public AtomNode(String v, byte type)
	{
		set(v, type);
	}

	public void set(String v, byte type)
	{
		try
		{
			v = StringX.xml2str(v);
			// v = StringX.replaceAll(v, "&lt;", "<");
			// v = StringX.replaceAll(v, "&gt;", ">");
			// v = StringX.replaceAll(v, "&apos;", "'");
			// v = StringX.replaceAll(v, "&quot;", "\"");
			// v = StringX.replaceAll(v, "&amp;", "&");
			this.type = type;
			if (type == INode.TYPE_STRING) this.value = StringX.utf82str(v);
			else if (type == INode.TYPE_BOOL) this.value = BOOL_TRUE.equalsIgnoreCase(v) ? Boolean.TRUE
					: Boolean.FALSE;
			else if (type == INode.TYPE_DOUBLE) this.value = new BigDecimal(v);
			else if (type == INode.TYPE_INT) this.value = new Integer(v);
			else if (type == INode.TYPE_LONG) this.value = new Long(v);
			else if (type == INode.TYPE_BYTE) this.value = StringX.decodeBase64(v.getBytes());
			else this.value = StringX.utf82str(v);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public AtomNode(String value)
	{
		this.value = StringX.utf82str(value);
	}

	public AtomNode(int value)
	{
		this.value = new Integer(value);
	}

	public AtomNode(Integer value)
	{
		this.value = value;
	}

	public AtomNode(long value)
	{
		this.value = new Long(value);
	}

	public AtomNode(Long value)
	{
		this.value = value;
	}

	public AtomNode(double value)
	{
		this.value = BigDecimal.valueOf(value);
		// this.value = new BigDecimal(value); // chenjs 2012-09-09
	}

	public AtomNode(BigDecimal value)
	{
		this.value = value;
	}

	public AtomNode(Double value)
	{
		this.value = BigDecimal.valueOf(value);
		// this.value = new BigDecimal(value.doubleValue());
	}

	public AtomNode(byte[] value)
	{
		this.value = value;
	}

	public AtomNode(Boolean value)
	{
		this.value = value;
	}

	public AtomNode(boolean value)
	{
		this.value = new Boolean(value);
	}

	public void setValue(Object value)
	{
		if (this.value instanceof Double) this.value = new BigDecimal(
				((Double) value).doubleValue());
		else if (this.value instanceof Float) this.value = new BigDecimal(value.toString());
		else this.value = value;
	}

	public Number toNumber()
	{
		if (value instanceof String) return (Number) (new BigDecimal((String) value));
		return (Number) value;
	}

	public Object getValue()
	{
		return value;
	}

	// public void toXml(OutputStream os) throws IOException
	// {
	// if (value == null) return;
	// try
	// {
	// if (value instanceof byte[]) os.write(StringX.encodeBase64((byte[])
	// value));
	// else if (value instanceof Boolean) os.write(((Boolean)
	// value).booleanValue() ? '1'
	// : '0');
	// else if (value instanceof String)
	// {
	// String v = (String) value;
	// if (v.length() == 0) return;
	// v = StringX.str2xml(v);
	// // if (v.indexOf('<') >= 0) v = StringX.replaceAll(v, "<",
	// // "&lt;");
	// // if (v.indexOf('>') >= 0) v = StringX.replaceAll(v, ">",
	// // "&gt;");
	// // if (v.indexOf('&') >= 0) v = StringX.replaceAll(v, "&",
	// // "&amp;");
	// // if (v.indexOf('<') >= 0 || v.indexOf('>') >= 0 ||
	// // v.indexOf('&') >= 0) v = "<![CDATA["
	// // + value + "]]>";
	// os.write(v.getBytes(Common.CHARSET_UTF8));
	// }
	// else os.write(value.toString().getBytes(Common.CHARSET_UTF8));
	// }
	// catch (Exception e)
	// {
	// }
	// }

	public byte type()
	{
		if (type != 0) return type;
		if (value instanceof byte[]) return INode.TYPE_BYTE;
		if (value instanceof Boolean) return INode.TYPE_BOOL;
		if (value instanceof Integer) return INode.TYPE_INT;
		if (value instanceof Long) return INode.TYPE_LONG;
		if (value instanceof BigDecimal || value instanceof Double) return INode.TYPE_DOUBLE;
		return INode.TYPE_STRING;
	}

	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AtomNode)) obj = NodeConverterFactory.getInstance().unpack(obj, null);
		if (!(obj instanceof AtomNode)) return false;
		if (value == null) return ((AtomNode) obj).value == null;
		return value.equals(((AtomNode) obj).value);
	}

	public String toString()
	{
		if (value instanceof BigDecimal) return ((BigDecimal) value).toPlainString();
		if (value instanceof byte[]) return StringX.base64((byte[]) value);
		return value == null ? StringX.EMPTY_STRING : value.toString();
	}

	public boolean booleanValue()
	{
		return (value instanceof String) ? BOOL_TRUE.equalsIgnoreCase((String) value)
				: ((Boolean) value).booleanValue();
	}

	public byte[] byteValue()
	{
		if (value instanceof String)
		{
			if (StringX.nullity((String) value)) return null;
			return StringX.decodeBase64(((String) value).getBytes());
		}
		return (byte[]) value;
	}

	public double doubleValue()
	{
		return toNumber().doubleValue();
	}

	public int intValue()
	{
		return toNumber().intValue();
	}

	public long longValue()
	{
		return toNumber().longValue();
	}

	public String stringValue()
	{
		return toString();
	}

	public void set(int v)
	{
		value = new Integer(v);
	}

	public void set(long v)
	{
		value = new Long(v);
	}

	public void set(byte[] v)
	{
		value = v;
	}

	public void set(double v)
	{
		value = new BigDecimal(v);
	}

	public void set(String v)
	{
		value = v;
	}

	public void set(boolean v)
	{
		value = new Boolean(v);
	}

	public void set(short v)
	{
		value = new Integer(v);
	}

	public void set(float v)
	{
		value = new BigDecimal(v);
	}

	public boolean isNull()
	{
		return value == null;
	}

	public boolean isBoolean()
	{
		return value instanceof Boolean;
	}

	public boolean isNumber()
	{
		return value instanceof Number;
	}

	public boolean isDouble()
	{
		return value instanceof BigDecimal;
	}

	public boolean isInteger()
	{
		return value instanceof Integer;
	}

	public boolean isLong()
	{
		return value instanceof Long || isInteger();
	}

	public boolean isBytes()
	{
		return value instanceof byte[];
	}

	public boolean isString()
	{
		return value instanceof String;
	}

	// from AbstractNode
	protected Map ext = new HashMap();

	public Map getExt()
	{
		return ext;
	}

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
	 * public void toXml(OutputStream os, String tag, int level) throws
	 * IOException { if (isNull()) return; byte[] t = tag.getBytes(); boolean
	 * unvalid = isUnvalidTag(tag); os.write('<'); byte[] tt = t; if (unvalid)
	 * os.write('X'); // 在前面加一个固定的字母 os.write(tt); // 如果标签不合法，则base64转换，并作标记 if
	 * (unvalid) { os.write(' '); os.write('x'); os.write('='); os.write('"');
	 * os.write('X'); os.write('"'); } if (type() != INode.TYPE_STRING &&
	 * INode.NO_TYPE_TAG.get() == null) addAttr( os, (byte) 't', (byte) type());
	 * ext2XML(os); os.write('>');
	 * 
	 * toXml(os);
	 * 
	 * os.write('<'); os.write('/'); if (unvalid) os.write('X'); // 在前面加一个固定的字母
	 * os.write(tt); os.write('>'); }
	 * 
	 * public boolean isUnvalidTag(String tag) { char first = tag.charAt(0); if
	 * ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) return
	 * false; return true; }
	 * 
	 * protected void ext2XML(OutputStream os) throws IOException { if (ext ==
	 * null || ext.size() == 0) return; Iterator keys = ext.keySet().iterator();
	 * while (keys.hasNext()) { String key = keys.next().toString(); addAttr(os,
	 * key.getBytes(), ext.get(key).toString().getBytes( "UTF-8")); } }
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
}
