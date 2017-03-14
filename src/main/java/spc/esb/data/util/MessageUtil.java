package spc.esb.data.util;

import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.util.KMP;
import spc.webos.util.StringX;

/**
 * 报文二进制操作工具类， 主要用于加入签名字段和提取签名字段
 * 
 * @author spc
 * 
 */
public class MessageUtil
{
	public static ICompositeNode getCNodeHeader(byte[] msg) throws Exception
	{
		return SOAPConverter.getInstance().deserialize2composite(getHeader(msg));
	}

	/**
	 * 从报文中获得头header标签(含header标签)
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] getHeader(byte[] msg)
	{
		// 先查找body标签的尾部
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_BODY));
		int start = indexOf(msg, end, startTag(IMessage.TAG_HEADER));
		// header标签没有出现在body标签后面
		if (start < 0) start = indexOf(msg, startTag(IMessage.TAG_HEADER));
		end = indexOf(msg, start, endTag(IMessage.TAG_HEADER));

		return getContent(msg, start, end, IMessage.TAG_HEADER.length(), true);
	}

	public static byte[] getSOAPHeader(byte[] msg)
	{ // soap报文header必须出现在body前面
		byte[] startHeader = ":Header>".getBytes();
		int start = indexOf(msg, startHeader);
		StringBuffer ns = new StringBuffer(); // 查找当前namespace
		for (int i = start - 1; i > 0; i--)
		{
			if (msg[i] == '<') break;
			ns.append((char) msg[i]);
		}
		ns.reverse();
		String tag = ns + ":Header";
		start -= ns.length() + 1;

		return getContent(msg, start, indexOf(msg, start, endTag(tag)), tag.length(), true);
	}

	/**
	 * 由于报文中签名字段为body(含body标签)
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] getBody(byte[] msg)
	{
		int start = indexOf(msg, 0, startTag(IMessage.TAG_BODY));
		if (start < 0) return null;
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_BODY));
		if (start >= end) return null;
		return getContent(msg, start, end, IMessage.TAG_BODY.length(), true);
	}

	public static byte[] getOriginalBytes(byte[] msg)
	{
		int start = lastIndexOf(msg, msg.length, startTag(IMessage.TAG_ORIGINALBYTES));
		if (start < 0) return null;
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_ORIGINALBYTES));
		if (end < 0) return null;
		return getContent(msg, start, end, IMessage.TAG_ORIGINALBYTES.length(), false);
	}

	/**
	 * 从报文中获取签名信息 signature标签必须出现在body后面，在整个xml报文的尾部
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] getSignature(byte[] msg)
	{
		int start = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_BODY)); // </body>标签的位置
		start = indexOf(msg, start + 7, startTag(IMessage.TAG_SIGNATURE)); // 从body标签往后搜索<signature>标签
		if (start < 0) return null;
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_SIGNATURE));
		if (end < 0) return null;
		return getContent(msg, start, end, IMessage.TAG_SIGNATURE.length(), false);
	}

	// 从header下获取signature标签
	public static byte[] getSignature2(byte[] msg)
	{
		int start = lastIndexOf(msg, msg.length, startTag(IMessage.TAG_HEADER)); // </header>标签的位置
		start = indexOf(msg, start + 7, startTag(IMessage.TAG_SIGNATURE)); // 从header标签往后搜索<signature>标签
		if (start < 0) return null;
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_SIGNATURE));
		if (end < 0) return null;
		return getContent(msg, start, end, IMessage.TAG_SIGNATURE.length(), false);
	}

	/**
	 * 从报文块中获得一块信息
	 * 
	 * @param msg
	 *            报文
	 * @param start
	 *            指定标签的起始位置
	 * @param end
	 *            指定标签的截止位置
	 * @param tagLen
	 *            标签长度
	 * @param includeTag
	 *            是否包含标签(待签名的信息域需要包含标签，签名信息域时只需要提取里面的内容块)
	 * @return
	 */
	public static byte[] getContent(byte[] msg, int start, int end, int tagLen, boolean includeTag)
	{
		if (start >= end) return null;
		byte[] body = new byte[includeTag ? (end - start + tagLen + 3) : (end - start - tagLen - 2)];
		System.arraycopy(msg, includeTag ? start : (start + tagLen + 2), body, 0, body.length);
		return body;
	}

	/**
	 * 删除报文中的签名部分
	 * 
	 * @param msg
	 * @return
	 */
	public static byte[] removeSignature(byte[] msg)
	{
		return remove(msg, lastIndexOf(msg, msg.length, startTag(IMessage.TAG_SIGNATURE)),
				lastIndexOf(msg, msg.length, endTag(IMessage.TAG_SIGNATURE)),
				IMessage.TAG_SIGNATURE.length());
	}

	public static byte[] removeSignature2(byte[] msg)
	{
		return remove(msg, lastIndexOf(msg, msg.length, startTag(IMessage.TAG_SIGNATURE)),
				lastIndexOf(msg, msg.length, endTag(IMessage.TAG_SIGNATURE)),
				IMessage.TAG_SIGNATURE.length());
	}

	public static byte[] removeOriginalBytes(byte[] msg)
	{
		return remove(msg, lastIndexOf(msg, msg.length, startTag(IMessage.TAG_ORIGINALBYTES)),
				lastIndexOf(msg, msg.length, endTag(IMessage.TAG_ORIGINALBYTES)),
				IMessage.TAG_ORIGINALBYTES.length());
	}

	public static byte[] removeHeader(byte[] msg)
	{
		// 先查找body标签的尾部
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_BODY));
		int start = indexOf(msg, end, startTag(IMessage.TAG_HEADER));
		// header标签没有出现在body标签后面
		if (start < 0) start = indexOf(msg, startTag(IMessage.TAG_HEADER));
		end = indexOf(msg, start, endTag(IMessage.TAG_HEADER));

		return remove(msg, start, end, IMessage.TAG_HEADER.length());
	}

	public static byte[] removeBody(byte[] msg)
	{
		int start = indexOf(msg, 0, startTag(IMessage.TAG_BODY));
		if (start < 0) return msg;
		int end = lastIndexOf(msg, msg.length, endTag(IMessage.TAG_BODY));
		if (start >= end) return msg;

		return remove(msg, start, end, IMessage.TAG_BODY.length());
	}

	public static byte[] remove(byte[] msg, int start, int end, int tagLen)
	{
		if (start >= end) return msg;
		byte[] xml = new byte[msg.length - (end - start + tagLen + 3)];
		System.arraycopy(msg, 0, xml, 0, start);
		System.arraycopy(msg, end + tagLen + 3, xml, start, xml.length - start);
		return xml;
	}

	/**
	 * 将签名加入报文体中
	 * 
	 * @param msg
	 * @param sig
	 * @return
	 */
	public static byte[] addSignature(byte[] msg, byte[] sig)
	{
		msg = removeSignature(msg);
		return add(msg, lastIndexOf(msg, msg.length, endTag(IMessage.TAG_ROOT)), sig,
				IMessage.TAG_SIGNATURE);
	}

	public static byte[] addSignature2(byte[] msg, byte[] sig)
	{
		msg = removeSignature(msg);
		return add(msg, lastIndexOf(msg, msg.length, endTag(IMessage.TAG_HEADER)), sig,
				IMessage.TAG_SIGNATURE);
	}

	public static byte[] addOriginalBytes(byte[] msg, byte[] originalBytes)
	{
		msg = removeOriginalBytes(msg);
		return add(msg, lastIndexOf(msg, msg.length, endTag(IMessage.TAG_ROOT)), originalBytes,
				IMessage.TAG_ORIGINALBYTES);
	}

	public static byte[] addHeader(byte[] msg, byte[] header)
	{
		msg = removeHeader(msg);
		byte[] rootStartTag = startTag(IMessage.TAG_ROOT);
		return add(msg, indexOf(msg, rootStartTag) + rootStartTag.length, header, null);
	}

	public static byte[] addBody(byte[] msg, byte[] body)
	{
		msg = removeBody(msg);
		byte[] hdrEndTag = endTag(IMessage.TAG_HEADER);
		return add(msg, indexOf(msg, hdrEndTag) + hdrEndTag.length, body, null);
	}

	/**
	 * 将某一二进制块以在原报文中的某一起始位置放入到原报文
	 * 
	 * @param msg
	 *            原报文
	 * @param offset
	 *            插入到原报文的起始位置
	 * @param content
	 *            插入字节数组
	 * @param tag
	 *            插入内容标签
	 * @return
	 */
	public static byte[] add(byte[] msg, int offset, byte[] content, String tag)
	{
		byte[] xml = new byte[msg.length + content.length
				+ (StringX.nullity(tag) ? 0 : (2 * tag.length() + 5))];
		System.arraycopy(msg, 0, xml, 0, offset);
		byte[] startTag = StringX.nullity(tag) ? new byte[0] : startTag(tag);
		byte[] endTag = StringX.nullity(tag) ? new byte[0] : endTag(tag);
		System.arraycopy(startTag, 0, xml, offset, startTag.length);
		System.arraycopy(content, 0, xml, offset + startTag.length, content.length);
		System.arraycopy(endTag, 0, xml, offset + startTag.length + content.length, endTag.length);
		System.arraycopy(msg, offset, xml, xml.length - msg.length + offset, msg.length - offset);
		return xml;
	}

	public static byte[] startTag(String tag)
	{
		return ('<' + tag + '>').getBytes();
	}

	public static byte[] endTag(String tag)
	{
		return ("</" + tag + '>').getBytes();
	}

	public static int lastIndexOf(byte[] src, byte[] target)
	{
		return lastIndexOf(src, src.length, target);
	}

	public static int lastIndexOf(byte[] src, int offset, byte[] target)
	{
		// src.length - target.length;
		int index = (offset > src.length - target.length) ? src.length - target.length : offset;
		while (index >= 0)
		{
			if (match(src, index, target)) return index;
			index--;
		}
		return -1;
	}

	public static int indexOf(byte[] src, byte[] target)
	{
		return indexOf(src, 0, target);
	}

	public static int indexOf(byte[] src, int offset, byte[] target)
	{
		// 700 2013-08-01 使用KMP算法
		return KMP.indexOf(src, offset, target);
		
//		int index = offset < 0 ? 0 : offset;
//		while (index < src.length - target.length + 1)
//		{
//			if (match(src, index, target)) return index;
//			index++;
//		}
//		return -1;
	}

	public static boolean match(byte[] src, int start, byte[] target)
	{
		int j = 0;
		for (int i = start; i < src.length && j < target.length; i++, j++)
			if (src[i] != target[j]) return false;
		return j >= target.length;
	}
	
	// 设置报文应答信息，将请求报文变为应答报文
	
}
