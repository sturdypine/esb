package spc.esb.data.iso8583;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISO8583MessageConverter implements IISO8583MessageConverter
{
	public byte[] serialize(ISO8583Message iso8583msg) throws Exception
	{
		// added by chenjs 2011-12-20 调整最后的位图长度
		int bitLen = iso8583msg.getBitmap().getBit().length;
		iso8583msg.getBitmap().removeExtBitMap();
		if (log.isDebugEnabled()) log.debug("serialize2iso8583: bit len before removeExtBit:"
				+ bitLen + ", len after:" + iso8583msg.getBitmap().getBit().length + ":"
				+ iso8583msg);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(iso8583msg.bitmap.getBit()); // 放位图
		// 8583第一个字段为扩展字段，此字段序列化在位图中实现而不是采用字段,so 下标从1开始
		for (int i = 1; i < iso8583msg.fields.length; i++)
		{
			if (!iso8583msg.isValid(i)) continue;
			Field f = iso8583msg.fields[i];
			converter.pack(f);
			if (f.vlen > 0) baos.write(converter.vlen(f));
			baos.write(f.buf);
		}
		return baos.toByteArray();
	}

	public void deserialize(ISO8583Message iso8583msg, byte[] buf8583, int offset) throws Exception
	{
		BitMap bitmap = new BitMap(buf8583);
		if (offset > 0)
		{ // 如果有偏移量则重新构造bitmap位图信息
			byte[] hdr = new byte[8];
			System.arraycopy(buf8583, offset, hdr, 0, 8);
			bitmap = new BitMap(hdr);
		}
		byte[] bits = bitmap.isValid(0) ? new byte[16] : new byte[8];
		System.arraycopy(buf8583, offset, bits, 0, bits.length);
		iso8583msg.bitmap = new BitMap(bits);
		offset += bits.length;

		// 下标从1开始，忽略0
		for (int i = 1; i < iso8583msg.bitmap.size(); i++)
		{
			if (!iso8583msg.bitmap.isValid(i)) continue;
			Field f = iso8583msg.getField(i);
			if (f == null)
			{
				log.warn("cannot find field(" + i + ") definition!!!");
				continue;
			}
			offset = converter.unpack(buf8583, offset, f);
		}
	}

	public void setConverter(IFieldConverter converter)
	{
		this.converter = converter;
	}

	protected IFieldConverter converter = new DefaultFieldConverter();
	protected Logger log = LoggerFactory.getLogger(getClass());

	public ISO8583MessageConverter()
	{
	}

	public ISO8583MessageConverter(IFieldConverter converter)
	{
		this.converter = converter;
	}
}
