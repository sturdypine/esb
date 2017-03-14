package spc.esb.data.iso8583;

import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * @author chenjs
 * 
 *         本类完成字符串类型的解析，它将作为一个基类供其它类进行处理
 */
public class Field implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	public String value = StringX.EMPTY_STRING; // 字段值
	public byte[] buf; // 对应8583报文中的二进制内容
	public int len = 0; // 字段长度
	public int decimal = 2; // 浮点数的小数点位数
	public int vlen = 0; // 可变长度 2L 3L, LEN_LLVAR, LEN_LLLVAR
	public int type = 0; // 字段类型
	public int offset = 0; // 数据包中的偏移位置
	public int no; // 位图位置
	public boolean enabled = false;
	public MsgSchemaPO schema;
	public TreeNode tree; // 751

	public static final int TYPE_A = 1; // 字母字符
	public static final int TYPE_N = 2; // 数字
	public static final int TYPE_S = 3; // 特殊字符
	public static final int TYPE_AN = 4; // 字母和数字
	public static final int TYPE_AS = 5; // 字母和特殊数字
	public static final int TYPE_NS = 6; // 数字和特殊字符
	public static final int TYPE_B = 7; // 二进制
	public static final int TYPE_Z = 8; // 磁道
	public static final int TYPE_X = 9; // 'C''D'分别表示贷借
	public static final int TYPE_MONEY = 10; // 金额
	public static final int TYPE_LL = 11; // 利率
	public static final int TYPE_CMONEY = 12; // 带借贷的金额
	public static final int TYPE_NN = 13; // 数字，但不用去掉前导0

	public static final int LEN_FIX = 0; // 固定长
	public static final int LEN_LLVAR = 2; // 两字节变长
	public static final int LEN_LLLVAR = 3; // 三字节变长

	public Field()
	{
	}

	public Field(TreeNode tree, MsgSchemaPO schema)
	{
		this.tree = tree;
		this.schema = schema;
		if (schema.getDeci() != null) decimal = schema.getDeci().intValue();
		int[] iso8583 = StringX.split2ints(schema.getIso8583(), "|");
		// modified by chenjs 2011-11-10
		no =  iso8583[0]; // Integer.parseInt(schema.getRcvName());
		type = iso8583[1];
		vlen = iso8583[2];
		len = iso8583[3];
	}

	public Field(int fno, int ftype, int flen, int vflen)
	{
		this(fno, ftype, flen, vflen, 2, null);
	}

	public Field(int fno, int ftype, int flen, int vflen, int decimal)
	{
		this(fno, ftype, flen, vflen, decimal, null);
	}

	public Field(int fno, int ftype, int flen, int vflen, String fv)
	{
		this(fno, ftype, flen, vflen, 2, fv);
	}

	public Field(int fno, int ftype, int flen, int vflen, int decimal, String fv)
	{
		no = fno;
		len = flen;
		type = ftype;
		vlen = vflen;
		value = fv;
		this.decimal = decimal;
		if (!StringX.nullity(value)) enabled = true;
	}

	public String toString()
	{
		return "{no:" + no + ", len:" + len + ", vlen:" + vlen + ", type:" + type + ", offset:"
				+ offset + ", enabled:" + enabled + ", value:'" + value + "'}";
	}

	public int getNo()
	{
		return no;
	}

	public void setNo(int no)
	{
		this.no = no;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean isFixedLen()
	{
		return vlen == 0;
	}

	public byte[] getBuf()
	{
		return buf;
	}

	public void setBuf(byte[] buf)
	{
		this.buf = buf;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
		if (!StringX.nullity(value)) enabled = true;
	}

	/**
	 * 返回是否有效
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * 清除
	 * 
	 */
	public void clear()
	{
		enabled = false;
		value = StringX.EMPTY_STRING;
	}

	/**
	 * 返回值长＋变长
	 */
	public int length()
	{
		return len + vlen;
	}

	public int getLen()
	{
		return len;
	}

	public void setLen(int len)
	{
		this.len = len;
	}

	public int getVlen()
	{
		return vlen;
	}

	public void setVlen(int vlen)
	{
		this.vlen = vlen;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public int getDecimal()
	{
		return decimal;
	}

	public void setDecimal(int decimal)
	{
		this.decimal = decimal;
	}
}
