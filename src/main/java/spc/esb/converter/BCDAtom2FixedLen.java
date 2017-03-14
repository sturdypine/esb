package spc.esb.converter;

import spc.esb.core.TagAttr;
import spc.esb.data.AtomNode;
import spc.esb.data.FixedMessage;
import spc.esb.data.IAtomNode;
import spc.esb.data.INode;
import spc.esb.data.fixmsg.DefaultAtom2FixedLen;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.charset.EBCDUtil;

/**
 * 解决特殊含有P类型的BCD转码
 * 
 * @author chenjs
 * 
 */
public class BCDAtom2FixedLen extends DefaultAtom2FixedLen
{
	protected void pack(byte[] fixedLen, int offset, int start, int len, byte type,
			IAtomNode value, String strValue, MsgSchemaPO schema, String charset) throws Exception
	{
		TagAttr attr = new TagAttr(schema.getTagAttr());
		if (!attr.isBcdPack()) super.pack(fixedLen, offset, start, len, type, value, strValue,
				schema, charset);
		else
		{
			byte[] bcdp = EBCDUtil.gbk2bcdp(strValue);
			if (log.isDebugEnabled()) log.debug("it is a EBCDP: bcdLen:" + bcdp.length + ", s:"
					+ start + ",l:" + len);
			FixedMessage.write(fixedLen, offset, start, len, bcdp);
		}
	}

	protected INode unpack(byte[] fixedLen, int offset, int start, int len, byte type,
			MsgSchemaPO schema, String charset) throws Exception
	{
		TagAttr attr = new TagAttr(schema.getTagAttr());
		if (!attr.isBcdPack()) return super.unpack(fixedLen, offset, start, len, type, schema,
				charset);
		String[] fix = StringX.split(getFixmsg(schema), "|");
		int ascLen = Integer.parseInt(fix[2]); // 将asc字节长度放在第三栏位
		byte[] bcdp = FixedMessage.read(fixedLen, offset, start, len);
		String strValue = EBCDUtil.bcdp2gbk(bcdp, ascLen);
		if (!StringX.nullity(schema.getFixmsg()) && schema.getFixmsg().indexOf(TRIM) >= 0) strValue = strValue
				.trim();
		if (log.isDebugEnabled()) log.debug("EBCD-P: fixmsg:[" + schema.getFixmsg() + "], bcdLen:"
				+ bcdp.length + ", ascLen:" + ascLen + ", strvalue:[[" + strValue + "]]");
		return new AtomNode(strValue);
	}
}
