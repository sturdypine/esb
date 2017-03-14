package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import spc.webos.constant.Common;

/**
 * 节点的xml序列化接口，
 * 
 * @author spc
 * 
 */
public interface INode2XML
{
	public final static String ATTR_KEY_NO_CDATA = "NO_CDATA"; // 特殊xml字符是否采用cdata
	public final static String ATTR_KEY_NO_EMPTY_TAG = "NO_EMPTY_TAG"; // 是否针对空的arraynode和mapnode保留标签

	public final static String ATTR_KEY_NO_NULL_TAG = "NO_NULL_TAG";
	// 如果里面有对象, 则在compositenode生产结构元素时候不生产空字符串标签
	// final static ThreadLocal NO_NULL_TAG = new ThreadLocal();
	// 如果里面有对象表示toxml时,则生成t类型属性标签, 默认不生成t属性标签
	public final static String ATTR_KEY_TYPE_TAG = "TYPE_TAG";
	// added by chenjs 2011-12-20 序列化时使用节点自带的命名空间优先
	public final static String USING_NODE_NS = "USING_NODE_NS";
	// final static ThreadLocal NO_TYPE_TAG = new ThreadLocal();
	// 在序列化的时候希望xml报文中esb的数组类型不是标签v类型

	// final static ThreadLocal CHARSET = new ThreadLocal(); // 字符集
	public final static String ATTR_KEY_CHARSET = "CHARSET";

	public final static String ATTR_KEY_CN2UTF8 = "CN2UTF8";
	// final static ThreadLocal CN2UTF8 = new ThreadLocal(); // 是否要求CN 2 UTF8

	public final static String DEFAULT_CHARSET = Common.CHARSET_UTF8; // 默认编码集

	/*
	 * esb: <array> <v>aaa</v> <v>bbb</v> </array> 不含有v， 重复标签格式为：
	 * <array>aaa</array> <array>bbb</array>
	 */
	// final static ThreadLocal ARRAY_REPEAT = new ThreadLocal();
	void node2xml(OutputStream os, INode node, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException;

	// 结构类型xml序列化
	/**
	 * 报文和当前节点在报文中的路径
	 */
	void map(OutputStream os, ICompositeNode cnode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException;

	// 数组类型xml序列化
	void array(OutputStream os, IArrayNode anode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException;

	// 原子类型序列化
	void atom(OutputStream os, IAtomNode node, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException;
}
