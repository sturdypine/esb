package spc.esb.data.iso8583;

import spc.webos.util.StringX;

/**
 * 8583位图操作
 * 
 * @author spc
 * 
 */
public class BitMap
{
	protected byte[] bit; // 位图

	public BitMap(byte[] bit)
	{
		this.bit = bit;
	}

	// added by chenjs 2012-01-01 支持偏移量的模式输入位图进行解析
	public BitMap(byte[] bit, int offset)
	{
		if (offset == 0) this.bit = bit;
		else
		{
			byte[] nbit = new byte[16]; // 最多16个字节
			System.arraycopy(bit, offset, nbit, 0, 16);
			this.bit = nbit;
		}
	}

	public BitMap(int len)
	{
		this.bit = new byte[len];
	}

	public int size()
	{
		return isValid(0) ? 128 : 64;
	}

	/**
	 * no 从1开始
	 * 
	 * @param no
	 * @return
	 */
	public boolean isValid(int no)
	{
		int b = bit[no / 8];
		int index = no % 8;
		int mode = (int) (1 << (7 - index));
		return (b & mode) > 0;
	}

	public void setValid(int no)
	{
		int b = bit[no / 8];
		int index = no % 8;
		int mode = (1 << (7 - index));
		bit[no / 8] = (byte) (b | mode);
	}

	public void setValid(int[] nos)
	{
		for (int i = 0; i < nos.length; i++)
			setValid(nos[i]);
	}

	public void setUnvalid(int no)
	{
		int b = bit[no / 8];
		int index = no % 8;
		int mode = (1 << (7 - index));
		mode = ~mode;
		bit[no / 8] = (byte) (b & mode);
	}

	public void setUnvalid(int[] nos)
	{
		for (int i = 0; i < nos.length; i++)
			setUnvalid(nos[i]);
	}

	public byte[] getBit()
	{
		return bit;
	}

	public String getValidFields()
	{
		StringBuffer buf = new StringBuffer();
		int fieldNum = isValid(0) ? 128 : 64;
		for (int i = 0; i < fieldNum; i++)
		{
			if (!isValid(i)) continue;
			if (buf.length() > 0) buf.append(',');
			buf.append(i);
		}
		return buf.toString();
	}

	// added by chenjs 2011-12-20 在序列化成iso8583报文时默认都变成了128位图, 需要最后调整
	public void removeExtBitMap()
	{
		// 如果从65位起都没有有效位，则修改bit长度为64字节.
		for (int i = 64; i < bit.length * 8; i++)
			if (isValid(i)) return;
		setUnvalid(0); // 将0位设置无效
		byte[] nbit = new byte[8];
		System.arraycopy(bit, 0, nbit, 0, 8);
		bit = nbit;
	}

	public String toString()
	{
		return StringX.bytes2binary(bit, " ");
	}
}
