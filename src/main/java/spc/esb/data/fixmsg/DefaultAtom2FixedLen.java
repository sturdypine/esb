package spc.esb.data.fixmsg;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.AtomNode;
import spc.esb.data.FixedMessage;
import spc.esb.data.IAtomNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.charset.EBCDUtil;

/**
 * 默认原子数据放入定长报文的字段要求
 * 
 * @author spc
 * 
 */
public class DefaultAtom2FixedLen implements IAtom2FixedLenConverter
{
	static DefaultAtom2FixedLen da2fl = new DefaultAtom2FixedLen();
	protected Logger log = LoggerFactory.getLogger(getClass());
	protected boolean bcd;
	protected boolean bcdAdjustBlank = true; // 自动调整BCD空格
	protected boolean floatWithDot = true;
	protected boolean fixmsgOrRcvName = true;

	public DefaultAtom2FixedLen()
	{
	}

	public DefaultAtom2FixedLen(boolean bcd, boolean floatWithDot, boolean fixmsgOrRcvName)
	{
		this.bcd = bcd;
		this.floatWithDot = floatWithDot;
		this.fixmsgOrRcvName = fixmsgOrRcvName;
	}

	public void pack(byte[] fixedLen, int offset, IAtomNode value, MsgSchemaPO schema,
			String charset) throws Exception
	{
		if (value == null) value = new AtomNode(
				StringX.nullity(schema.getDefValue()) ? StringX.EMPTY_STRING : schema.getDefValue());
		String strValue = value.toString();
		String fixmsg = getFixmsg(schema);
		int index = fixmsg.indexOf('|');
		if (index < 0)
		{
			if (log.isDebugEnabled()) log.debug("fixmsg unvalid for " + schema.getEsbName() + ", "
					+ fixmsg);
			return;
		}
		String[] fix = StringX.split(fixmsg, "|");
		int start = Integer.parseInt(fix[0]);
		int len = Integer.parseInt(fix[1]);
		// int start = Integer.parseInt(fixmsg.substring(0, index));
		// int len = Integer.parseInt(fixmsg.substring(index + 1));
		byte type = (byte) schema.getFtyp().charAt(0);
		pack(fixedLen, offset, start, len, type, value, strValue, schema, charset);
	}

	protected String getFixmsg(MsgSchemaPO schema)
	{
		String fixmsg = schema.getFixmsg(); // getRcvName(); modifed by chenjs
		// 2011-11-10
		if (fixmsgOrRcvName && StringX.nullity(fixmsg)) fixmsg = schema.getRcvName();
		if (StringX.nullity(fixmsg))
		{ // added by spc 2011-03-25, 如果配置为空字符串则认为不参与定长转换的标签
			if (log.isDebugEnabled()) log.debug("fixmsg is null for " + schema.getEsbName());
			return StringX.EMPTY_STRING;
		}
		// 定长报文将接受字段名定义为在定常报文中的起始位置 和长度，中间用.分割
		return fixmsg.replace('-', '|').replace('.', '|');
	}

	protected void pack(byte[] fixedLen, int offset, int start, int len, byte type,
			IAtomNode value, String strValue, MsgSchemaPO schema, String charset) throws Exception
	{
		if (type == INode.TYPE_STRING || type == INode.TYPE_CNSTR)
		{ // 如果是字符串
			writeString(fixedLen, offset, start, len, schema, charset, strValue,
					StringX.nullity(schema.getFixmsg()) || schema.getFixmsg().indexOf(TO_RIGHT) < 0);
		}
		else if (type == INode.TYPE_INT || type == INode.TYPE_LONG)
		{ // 如果是整数则前面补齐00000
			writeInt(fixedLen, offset, start, len, StringX.nullity(strValue) ? DEFAULT_NUM_VALUE
					: strValue, schema);
		}
		else if (type == INode.TYPE_DOUBLE)
		{ // 如果是浮点类型则标准化小数点位数，前面也补齐0000， 数字中间含有.来分割整数部分和小数部分
			writeFloat(fixedLen, offset, start, len, schema.getDeci().intValue(),
					StringX.nullity(strValue) ? DEFAULT_NUM_VALUE : strValue, schema);
		}
		else if (type == INode.TYPE_BYTE)
		{ // 如果是二进制类型
			// added by chenjs 2011-05-06 针对400核心,可能前端系统的规范是二进制，但我们需要进行码字转换
			// byte[] v = value.byteValue();
			// if (bcd) v = EBCDUtil.gbk2bcd(new String(v,
			// Common.CHARSET_UTF8));
			writeBytes(fixedLen, offset, start, len, value, schema);
		}
		else log.warn("Atom node(" + schema.getEsbName() + ") type(" + type + ") dont match!!!");
	}

	protected void writeBytes(byte[] fixedLen, int offset, int start, int len, IAtomNode value,
			MsgSchemaPO schema)
	{
		writeBytes(fixedLen, offset, start, len, value);
	}

	protected void writeBytes(byte[] fixedLen, int offset, int start, int len, IAtomNode value)
	{
		FixedMessage.write(fixedLen, offset, start, len, value.byteValue());
	}

	protected void writeString(byte[] fixedLen, int offset, int start, int len, MsgSchemaPO schema,
			String charset, String value, boolean toLeft) throws UnsupportedEncodingException
	{
		// added by chenjs 2011-11-22 考虑到业务字段中可能有空格，容许配置ext1里面K属性去掉空格
		if (!StringX.nullity(schema.getFixmsg()) && schema.getFixmsg().indexOf(KICK) >= 0) value = value
				.replaceAll(" ", StringX.EMPTY_STRING);
		writeString(fixedLen, offset, start, len, charset, value, toLeft);
	}

	protected void writeString(byte[] fixedLen, int offset, int start, int len, String charset,
			String value, boolean toLeft) throws UnsupportedEncodingException
	{
		// modified by chenjs 2011-05-09 考虑到东莞的业务场景需要某些情况下左补空格和右补空格
		byte[] v = bcd ? (StringX.nullity(value) ? null : EBCDUtil.gbk2bcd(value, bcdAdjustBlank))
				: value.getBytes(charset);
		FixedMessage
				.write(fixedLen, offset, start, len, v, toLeft, bcd ? 0x40 : FixedMessage.BLANK);
		// if (!bcd) FixedMessage.write(fixedLen, offset, start, len,
		// value.getBytes(charset));
		// else
		// {
		// byte[] v = StringX.nullity(value) ? null : EBCDUtil.gbk2bcd(value);
		// for (int j = 0, i = start; j < len; i++, j++)
		// {
		// byte b = 0x40;
		// if (v != null && j < v.length) b = v[j];
		// fixedLen[offset + i] = b;
		// }
		// }
	}

	protected void writeInt(byte[] fixedLen, int offset, int start, int len, String value,
			MsgSchemaPO schema)
	{
		writeInt(fixedLen, offset, start, len, value);
	}

	protected void writeInt(byte[] fixedLen, int offset, int start, int len, String value)
	{
		if (bcd) FixedMessage.write(fixedLen, offset, start, len,
				EBCDUtil.gbk2bcd(StringX.int2str(value, len)));
		else FixedMessage.write(fixedLen, offset, start, len, StringX.int2str(value, len)
				.getBytes());
	}

	protected void writeFloat(byte[] fixedLen, int offset, int start, int len, int deci,
			String value, MsgSchemaPO schema)
	{
		writeFloat(fixedLen, offset, start, len, deci, value);
	}

	protected void writeFloat(byte[] fixedLen, int offset, int start, int len, int deci,
			String value)
	{
		if (bcd) FixedMessage.write(fixedLen, offset, start, len,
				EBCDUtil.gbk2bcd(StringX.float2str(value, len, deci, floatWithDot)));
		else FixedMessage.write(fixedLen, offset, start, len,
				StringX.float2str(value, len, deci, floatWithDot).getBytes());
	}

	public INode unpack(byte[] fixedLen, int offset, MsgSchemaPO schema, String charset)
			throws Exception
	{
		// String fixmsg = schema.getFixmsg(); // getRcvName(); modifed by
		// chenjs
		// // 2011-11-10
		// if (fixmsgOrRcvName && StringX.nullity(fixmsg)) fixmsg =
		// schema.getRcvName();
		// if (StringX.nullity(fixmsg))
		// { // added by spc 2011-03-25, 如果配置为空字符串则认为不参与定长转换的标签
		// if (log.isDebugEnabled()) log.debug("fixmsg is null for " +
		// schema.getEsbName());
		// return null;
		// }
		// // 定长报文将接受字段名定义为在定常报文中的起始位置 和长度，中间用.分割
		// fixmsg = fixmsg.replace('-', '|').replace('.', '|');
		String fixmsg = getFixmsg(schema);
		int index = fixmsg.indexOf('|');
		if (index < 0)
		{
			if (log.isDebugEnabled()) log.debug("fixmsg unvalid for " + schema.getEsbName() + ", "
					+ fixmsg);
			return null;
		}
		String[] fix = StringX.split(fixmsg, "|");
		int start = Integer.parseInt(fix[0]);
		int len = Integer.parseInt(fix[1]);
		byte type = (byte) schema.getFtyp().charAt(0);
		return unpack(fixedLen, offset, start, len, type, schema, charset);
	}

	protected INode unpack(byte[] fixedLen, int offset, int start, int len, byte type,
			MsgSchemaPO schema, String charset) throws Exception
	{
		if (type == INode.TYPE_STRING || type == INode.TYPE_CNSTR)
		{ // 如果是字符串
			return readString(fixedLen, offset, start, len, charset, schema);
		}
		else if (type == INode.TYPE_INT || type == INode.TYPE_LONG)
		{ // 如果是整数则前面补齐00000
			return readInt(fixedLen, offset, start, len, schema);
		}
		else if (type == INode.TYPE_DOUBLE)
		{ // 如果是浮点类型则标准化小数点位数，前面也补齐0000， 数字中间含有.来分割整数部分和小数部分
			return readFloat(fixedLen, offset, start, len, schema.getDeci().intValue(), schema);
		}
		else if (type == INode.TYPE_BYTE)
		{ // 如果是二进制类型
			// added by chenjs 2011-05-06 针对400核心,可能前端系统的规范是二进制，但我们需要进行码字转换
			byte[] v = FixedMessage.read(fixedLen, offset, start, len);
			// if (bcd) v = EBCDUtil.bcd2gbk(v).getBytes(Common.CHARSET_UTF8);
			return new AtomNode(v);
		}
		else
		{
			log.warn("atom node(" + schema.getEsbName() + ") type(" + type + ") dont match!!!");
			return null;
		}
	}

	protected AtomNode readString(byte[] fixedLen, int offset, int start, int len, String charset,
			MsgSchemaPO schema) throws UnsupportedEncodingException
	{
		if (fixedLen == null) return null;
		if (fixedLen.length < offset + start + len) len = fixedLen.length - offset - start;
		if (len <= 0) return null;
		// modified by chenjs 2011-05-08 定长不trim掉空格
		if (bcd) return new AtomNode(readStr(
				EBCDUtil.bcd2gbk(fixedLen, offset + start, len, bcdAdjustBlank), schema));
		byte[] buf = FixedMessage.read(fixedLen, offset, start, len);
		if (buf == null) return null;
		String strValue = readStr(new String(buf, charset), schema);
		return new AtomNode(strValue);
	}

	protected String readStr(String str, MsgSchemaPO schema)
	{
		if (str == null) return null;
		if (!StringX.nullity(schema.getFixmsg()) && schema.getFixmsg().indexOf(TRIM) >= 0) str = str
				.trim();
		return str;
	}

	protected AtomNode readInt(byte[] fixedLen, int offset, int start, int len, MsgSchemaPO schema)
	{
		if (fixedLen == null) return null;
		if (fixedLen.length < offset + start + len) len = fixedLen.length - offset - start;
		if (len <= 0) return null;
		if (bcd) return new AtomNode(new Long(EBCDUtil.bcd2gbk(fixedLen, offset + start, len)
				.trim()));
		byte[] buf = FixedMessage.read(fixedLen, offset, start, len);
		return buf == null ? null : new AtomNode(new Long(new String(buf).trim()));
	}

	protected AtomNode readFloat(byte[] fixedLen, int offset, int start, int len, int deci,
			MsgSchemaPO schema)
	{
		if (fixedLen == null) return null;
		if (fixedLen.length < offset + start + len) len = fixedLen.length - offset - start;
		if (len <= 0) return null;
		String strValue = null;
		if (bcd) strValue = EBCDUtil.bcd2gbk(fixedLen, offset + start, len).trim();
		else strValue = new String(FixedMessage.read(fixedLen, offset, start, len)).trim();
		if (floatWithDot || deci <= 0) return new AtomNode(StringX.float2str(new BigDecimal(
				strValue).toPlainString(), deci));
		return new AtomNode(
				StringX.float2str(
						new BigDecimal(strValue).divide(new BigDecimal(Math.pow(10, deci)))
								.toPlainString(), deci));
	}

	public void setBcd(boolean bcd)
	{
		this.bcd = bcd;
	}

	public void setFloatWithDot(boolean floatWithDot)
	{
		this.floatWithDot = floatWithDot;
	}

	public static IAtom2FixedLenConverter getInstance()
	{
		return da2fl;
	}

	public boolean isFixmsgOrRcvName()
	{
		return fixmsgOrRcvName;
	}

	public void setFixmsgOrRcvName(boolean fixmsgOrRcvName)
	{
		this.fixmsgOrRcvName = fixmsgOrRcvName;
	}

	public boolean isBcd()
	{
		return bcd;
	}

	public boolean isFloatWithDot()
	{
		return floatWithDot;
	}

	public boolean isBcdAdjustBlank()
	{
		return bcdAdjustBlank;
	}

	public void setBcdAdjustBlank(boolean bcdAdjustBlank)
	{
		this.bcdAdjustBlank = bcdAdjustBlank;
	}
}
