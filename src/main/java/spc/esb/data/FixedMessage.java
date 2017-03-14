package spc.esb.data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import spc.webos.constant.Common;
import spc.webos.util.NumberX;
import spc.webos.util.StringX;

public abstract class FixedMessage implements Serializable
{
	private static final long serialVersionUID = 1;
	protected byte[] msg;
	protected int offset;
	protected String charset = Common.CHARSET_UTF8; // 字符集, 默认是utf8
	protected boolean floatWithDot = true; // 表示浮点数时带.分隔符
	protected boolean as400 = false; // 是否为AS400
	public static final byte BLANK = ' ';

	public static final int TYPE_S = 0;
	public static final int TYPE_C = 1;
	public static final int TYPE_I = 2; // 字符串整数0436
	public static final int TYPE_L = 3; // 字符串长整数00008938
	public static final int TYPE_BI = 4; // 二进制整数， 占用4个字节
	public static final int TYPE_BL = 5; // 二进制长整数， 占用8个字节
	public static final int TYPE_BYTE = 6; // 二进制数据

	// type >= 100 &&type<200 表示double, 用十位和各位来表示小数点位数

	public abstract int length();

	public abstract String[] fields();

	public abstract int[] offset();

	public abstract int[] len();

	public abstract int[] type();

	public void init()
	{
		write(offset, length(), (byte[]) null);
	}

	public byte[] toBytes()
	{
		toMsg();
		if (offset == 0 && msg.length == length()) return msg;
		byte[] buf = new byte[msg.length];
		System.arraycopy(msg, offset, buf, 0, buf.length);
		return buf;
	}

	// 把一个定长字符串变成一个复杂节点
	public ICompositeNode toCNode(ICompositeNode cnode)
	{
		if (cnode == null) cnode = new CompositeNode();
		String[] fields = fields();
		int[] len = len();
		int[] offset = offset();
		int[] type = type();
		for (int i = 0; i < fields.length; i++)
		{
			if (type[i] >= 100 && type[i] < 200)
			{ // 浮点带有.
				String v = readStr(offset[i], len[i]);
				if (StringX.nullity(v)) continue;
				if (!floatWithDot)
				{
					int dotIndex = v.length() - type[i] % 100;
					v = v.substring(0, dotIndex) + '.' + v.substring(dotIndex);
				}
				cnode.set(fields[i], new BigDecimal(v));
				continue;
			}
			String v = null;
			switch (type[i])
			{
				case TYPE_S:
					v = readStr(offset[i], len[i]);
					if (!StringX.nullity(v)) cnode.set(fields[i], v);
					break;
				case TYPE_C:
					v = bytes2cn(read(offset[i], len[i]));
					if (!StringX.nullity(v)) cnode.set(fields[i], v);
					break;
				case TYPE_I:
					v = readStr(offset[i], len[i]);
					if (!StringX.nullity(v)) cnode.set(fields[i], new Integer(v));
					break;
				case TYPE_BI:
					cnode.set(
							fields[i],
							new Integer(NumberX
									.bytes2int(read(msg, this.offset, offset[i], len[i]))));
					break;
				case TYPE_L:
					v = readStr(offset[i], len[i]);
					if (!StringX.nullity(v)) cnode.set(fields[i], new Long(v));
					break;
				case TYPE_BL:
					cnode.set(fields[i],
							new Long(NumberX.bytes2long(read(msg, this.offset, offset[i], len[i]))));
					break;
			}
		}
		return cnode;
	}

	/**
	 * 字节数组变成中文字符串, 可能是不同的编码模式，比如ebcd编码
	 * 
	 * @param buf
	 * @return
	 */
	public String bytes2cn(byte[] buf)
	{
		try
		{
			return new String(buf, charset).trim();
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将数字类型变为定常字符串，decimal为0则表示为整数
	 * 
	 * @param number
	 * @param len
	 * @param decimal
	 * @return
	 */
	public String number2str(String number, int len, int decimal)
	{
		if (decimal <= 0) return StringX.int2str(number, len);
		return StringX.float2str(number, len, decimal, floatWithDot);
	}

	public void set(ICompositeNode cnode)
	{
		String[] fields = fields();
		int[] len = len();
		int[] offset = offset();
		int[] type = type();
		for (int i = 0; i < fields.length; i++)
		{
			IAtomNode v = cnode.findAtom(fields[i], null);
			if (v == null) continue;
			// if (v == null && (type[i] == TYPE_S || type[i] == TYPE_C))
			// continue;
			if (type[i] >= 100 && type[i] < 200)
			{ // 如果是浮点类型, 则用十位和各位来表示小数点位数
				write(offset[i], len[i], number2str(v.toString(), len[i], type[i] % 100).getBytes());
				continue;
			}
			switch (type[i])
			{
				case TYPE_S:
				case TYPE_C:
					try
					{
						write(offset[i], len[i], v.toString().getBytes(charset));
					}
					catch (UnsupportedEncodingException e)
					{
						throw new RuntimeException(e);
					}
					break;
				case TYPE_I:
					break;
				case TYPE_L:
					write(offset[i], len[i], StringX.int2str(v.toString(), len[i]).getBytes());
					break;
				case TYPE_BI:
					write(offset[i], len[i], NumberX.int2bytes(v.intValue()));
					break;
				case TYPE_BL:
					write(offset[i], len[i], NumberX.long2bytes(v.longValue()));
					break;
			}
		}
	}

	public byte[] toMsg()
	{
		return msg;
	}

	public String toString()
	{
		return toCNode(null).toString();
	}

	public void writeStr(int start, int len, String v)
	{
		try
		{
			if (v != null) write(msg, offset, start, len, v.getBytes(charset));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void write(int start, int len, byte[] v)
	{
		write(msg, offset, start, len, v);
	}

	public static void write(byte[] msg, int offset, int start, int len, byte[] v)
	{
		write(msg, offset, start, len, v, true, BLANK);
	}

	public static void write(byte[] msg, int offset, int start, int len, byte[] v, boolean toLeft,
			byte blank)
	{
		if (v == null) v = new byte[0];
		if (toLeft)
		{
			for (int j = 0, i = start; j < len; i++, j++)
			{
				byte b = blank;
				if (v != null && j < v.length) b = v[j];
				msg[offset + i] = b;
			}
		}
		else
		{
			for (int j = v.length - 1, i = start + len - 1; i >= start; i--, j--)
			{
				byte b = blank;
				if (v != null && j >= 0) b = v[j];
				msg[offset + i] = b;
			}
		}
	}

	public static void replace(byte[] msg, int offset, int len, byte o, byte t)
	{
		for (int i = offset; i < offset + len; i++)
			if (msg[i] == o) msg[i] = t;
	}

	public String readStr(int start, int len)
	{
		try
		{
			byte[] buf = read(msg, offset, start, len);
			return buf == null ? StringX.EMPTY_STRING : new String(buf, charset).trim();
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
	}

	public byte[] read(int start, int len)
	{
		return read(msg, offset, start, len);
	}

	public static byte[] read(byte[] msg, int offset, int start, int len)
	{
		// modified by spc, AS400下传定长报文时，如果后面的字段没有，则不下传，导致字节数组长度不够
		if (offset + start + len > msg.length) len = msg.length - offset - start;
		if (len <= 0) return null;
		byte[] v = new byte[len];
		for (int j = 0, i = start; j < len; i++, j++)
			v[j] = msg[offset + i];
		return v;
	}

	public static void fillBlank(byte[] msg, int offset, int len)
	{
		write(msg, offset, 0, len, null);
	}

	public boolean isFloatWithDot()
	{
		return floatWithDot;
	}

	public void setFloatWithDot(boolean floatWithDot)
	{
		this.floatWithDot = floatWithDot;
	}

	public String getCharset()
	{
		return charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}
}
