package spc.esb.data.iso8583;

/**
 * 8583报文的field表示形式
 * 
 * @author spc
 * 
 */
public class ISO8583Message
{
	protected BitMap bitmap;
	protected Field[] fields;

	public ISO8583Message()
	{
		this(128);
	}

	public ISO8583Message(int bit)
	{
		if (bit != 64 && bit != 128) throw new RuntimeException("bit should be 64 or 128!!!");
		bitmap = new BitMap(bit / 8);
		if (bit == 128) bitmap.setValid(0);
		fields = new Field[bit];
	}

	public void setField(Field f)
	{
		fields[f.no] = f;
		if (f.enabled) bitmap.setValid(f.no);
		else bitmap.setUnvalid(f.no);
	}

	public void removeField(int no)
	{
		fields[no] = null;
		bitmap.setUnvalid(no);
	}

	public Field getField(int no)
	{
		return fields[no];
	}

	public boolean isValid(int no)
	{
		return fields[no] != null && fields[no].enabled;
	}

	public BitMap getBitmap()
	{
		return bitmap;
	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("ISO8583 BitMap:" + bitmap + ":[\n");
		for (int i = 1; i < bitmap.size(); i++)
		{
			if (!isValid(i)) continue;
			buf.append(getField(i));
			buf.append('\n');
		}
		buf.append(']');
		return buf.toString();
	}
}
