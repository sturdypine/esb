package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractNode implements INode
{
	private static final long serialVersionUID = 1L;
	protected Map ext;

	protected String stdPath(String path)
	{
		if (path.indexOf('.') >= 0) return path.replace('.', '/');
		return path;
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

	public void toXml(OutputStream os, String tag) throws IOException
	{
		if (isNull()) return;
		byte[] t = tag.getBytes();
		boolean unvalid = isUnvalidTag(tag);
		os.write('<');
		byte[] tt = t;
		if (unvalid) os.write('X'); // 在前面加一个固定的字母
		os.write(tt);
		// 如果标签不合法，则base64转换，并作标记
		if (unvalid)
		{
			os.write(' ');
			os.write('x');
			os.write('=');
			os.write('"');
			os.write('X');
			os.write('"');
		}
		if (type() != INode.TYPE_STRING) addAttr(os, (byte) 't', (byte) type());
		ext2XML(os);
		os.write('>');

		toXml(os);

		os.write('<');
		os.write('/');
		if (unvalid) os.write('X'); // 在前面加一个固定的字母
		os.write(tt);
		os.write('>');
	}

	public boolean isUnvalidTag(String tag)
	{
		char first = tag.charAt(0);
		if ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) return false;
		return true;
	}

	public abstract void toXml(OutputStream os) throws IOException;

	public abstract byte type();

	protected void ext2XML(OutputStream os) throws IOException
	{
		if (ext == null || ext.size() == 0) return;
		Iterator keys = ext.keySet().iterator();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			addAttr(os, key.getBytes(), ext.get(key).toString().getBytes());
		}
	}

	protected void addAttr(OutputStream os, byte k, byte v) throws IOException
	{
		os.write(' ');
		os.write(k);
		os.write('=');
		os.write('"');
		os.write(v);
		os.write('"');
	}

	protected void addAttr(OutputStream os, byte[] k, byte[] v) throws IOException
	{
		os.write(' ');
		os.write(k);
		os.write('=');
		os.write('"');
		os.write(v);
		os.write('"');
	}
}
