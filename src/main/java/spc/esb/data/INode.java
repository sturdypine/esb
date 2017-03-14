package spc.esb.data;

import java.util.Map;

/**
 * ICompsiteData数据组成中的非简单类型节点，String,int,long,float,double,byte[]为简单类型
 * IArray,ICompsiteData为非简单类型
 * 
 * @author spc
 * 
 */
public interface INode extends java.io.Serializable
{
	// INode parent(); // 父亲节点。可能是Map类型，也可能是Array类型

	// Object getValue();
	byte type();

	boolean isNull();

	Object getExt(String key);

	Map getExt();

	void setExt(Map ext);

	void setExt(String key, Object value);
	
	Object removeExt(String key);

	// added by chenjs 2011-12-20 为每个节点增加ns属性
	void setNs(String ns);

	String getNs();

	// void toXml(OutputStream os, String tag, int level) throws IOException;

	public static final byte TYPE_STRING = 'S'; // 英文字符串
	public static final byte TYPE_CNSTR = 'C'; // 可能含有中文的字符串
	public static final byte TYPE_BOOL = 'b'; // boolean
	public static final byte TYPE_INT = 'I'; // 整型
	public static final byte TYPE_LONG = 'L'; // 长整数
	public static final byte TYPE_DOUBLE = 'D'; // 浮点
	public static final byte TYPE_BYTE = 'B'; // bytes
	public static final byte TYPE_ARRAY = 'A'; // 数组
	public static final byte TYPE_MAP = 'M'; // map类型
	public static final byte TYPE_UNDEFINED = 'U'; // 未定义的类型

	public static final String TYPE_TAG = "t"; // 节点数据类型
	public static final String TYPE_XTAG = "x"; // 标签是否经过特殊处理，增加了X字符头
	public static final String ARRAY_TAG = "v"; // 数组表示
//	public static final String SIZE_TAG = "size"; // 数组长度 // modified by chenjs 2012-01-10 取消此属性为默认属性
	public static final String NULL_TAG = "null"; // null标签
}
