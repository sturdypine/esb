package spc.esb.data.converter;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import spc.esb.data.ArrayNode;
import spc.esb.data.AtomNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.INode;
import spc.webos.util.StringX;

/**
 * esb报文的sax handler， 为了多线程安全，必须把解析某一xml报文的状态放入当前线程环境中
 * 
 * @author spc
 * 
 */
public class DefaultSaxHandler extends SaxHandler
{
	static final class Status
	{
		public Stack stack = new Stack();
		public byte t; // 类型
		// public byte x; // 标签是否base64
		public boolean isFirst = true;
		public INode current;
		public INode parent; // 父节点
		public String name; // 当前节点在父节点中名字
		public int index; // 如果父节点是ArrayNode节点.则记录在ArrayNode中的位置.
		// public long start;
	}

	static final ThreadLocal STATUS = new ThreadLocal();
	static final ThreadLocal ROOT = new ThreadLocal();
	static final Logger log = LoggerFactory.getLogger(DefaultSaxHandler.class);
	static final DefaultSaxHandler handler = new DefaultSaxHandler();
	protected String tagType = INode.TYPE_TAG;
	protected String tagTypeMap = String.valueOf(INode.TYPE_MAP);
	protected String tagTypeList = String.valueOf(INode.TYPE_ARRAY);
	protected String tagList = INode.ARRAY_TAG;
	protected boolean ignoreAttr; // ignore所有属性

	public DefaultSaxHandler()
	{
	}

	public DefaultSaxHandler(String tagType, String tagTypeMap, String tagTypeList, String tagList,
			boolean ignoreAttr)
	{
		this.tagType = tagType;
		this.tagTypeMap = tagTypeMap;
		this.tagTypeList = tagTypeList;
		this.tagList = tagList;
		this.ignoreAttr = ignoreAttr;
	}

	public static SaxHandler getInstance()
	{
		return handler;
	}

	public void setRoot(ICompositeNode root)
	{
		ROOT.set(root);
	}

	public ICompositeNode root()
	{
		return (ICompositeNode) ROOT.get();
	}

	public void startDocument() throws SAXException
	{
		Status status = new Status();
		status.current = (ICompositeNode) ROOT.get();
		status.stack.clear();
		status.stack.push(status.current);
		status.isFirst = true;
		STATUS.set(status);
		// status.start = System.currentTimeMillis();
	}

	public void endDocument() throws SAXException
	{
		// Status status = (Status) STATUS.get();
		// System.out.println((System.currentTimeMillis() - status.start));
		STATUS.set(null);
	}

	public void endElement(String uri, String localname, String qName) throws SAXException
	{
		Status status = (Status) STATUS.get();
		if (status.stack.empty()) return;
		status.stack.pop();
		if (status.stack.empty()) return;
		status.current = (INode) status.stack.peek();
	}

	public void characters(char[] v, int start, int length)
	{
		if (length == 0) return;
		Status status = (Status) STATUS.get();

		String s = StringX.utf82str(new String(v, start, length));
		// StringX.utf82str(StringX.trim(new String(v, start, length),
		// CHAR_ARRAY));

		// if (s.trim().length() == 0) return; // 无效空格不予以处理
		INode node = (INode) status.current;
		// System.out.print("\nchars:" + s + "," + node.getClass());
		if (node == null || !(node instanceof AtomNode)) return;
		// System.out.print(" ..ok: ");
		Object o = ((AtomNode) node).getValue();
		if (o != null) s = o.toString() + s; // 对于文本中存在回车, 解析器会把每行作为一个事件进行触发
		((AtomNode) node).set(s, status.t);
	}

	// public void ignorableWhitespace(char[] v, int start, int length)
	// {
	// String s = new String(v, start, length);
	// System.out.println("ignorableWhitespace:" + s + ",");
	// }

	protected void setAttrs(INode node, Attributes attr)
	{
		if (ignoreAttr) return;
		for (int i = 0; i < attr.getLength(); i++)
		{
			String key = attr.getQName(i);
//			System.out.println("k: " + key);
			if (key.equals(tagType) || key.equals(INode.TYPE_XTAG)) continue; // t,x
			// if (key.equals(tagType) || key.equals(INode.TYPE_XTAG)
			// || key.equals(INode.SIZE_TAG)) continue; // t,x,size
			node.setExt(key, attr.getValue(i));
		}
	}

	public void startElement(String uri, String localname, String qName, Attributes attr)
			throws SAXException
	{
		Status status = (Status) STATUS.get();
		if (status.isFirst)
		{
			status.isFirst = false;
			setAttrs((INode) ROOT.get(), attr);
			return;
		}
		INode node = null;
		String type = attr.getValue(tagType);
		if (StringX.nullity(type)) type = String.valueOf(INode.TYPE_STRING);

		// status.t = (byte) type.charAt(0);
		// modified by chenjs, 增加适应类型，以适应trc的xml规范 2010-10-20
		if (type.equalsIgnoreCase(tagTypeMap)) status.t = INode.TYPE_MAP;
		else if (type.equalsIgnoreCase(tagTypeList)) status.t = INode.TYPE_ARRAY;
		else status.t = (byte) type.charAt(0);

		if (status.t == '8') status.t = INode.TYPE_STRING;
		// modifed by chenjs 2011-12-02 容许产生不同实例的cnode
		if (status.t == INode.TYPE_MAP) node = root().newInstance(); // new
																		// CompositeNode();
		else if (status.t == INode.TYPE_ARRAY) node = new ArrayNode();
		else node = new AtomNode(StringX.EMPTY_STRING); // 默认为空标签的字符串

		setAttrs(node, attr);
		// if (!ignoreAttr)
		// {
		// for (int i = 0; i < attr.getLength(); i++)
		// {
		// String key = attr.getQName(i);
		// System.out.println("k: " + key);
		// if (key.equals(tagType) || key.equals(INode.TYPE_XTAG)) continue; //
		// t,x
		// // if (key.equals(tagType) || key.equals(INode.TYPE_XTAG)
		// // || key.equals(INode.SIZE_TAG)) continue; // t,x,size
		// node.setExt(key, attr.getValue(i));
		// }
		// }
		// if (qName.equals("name")) System.out.println(current.getClass() + ","
		// + node.getClass() + "," + qName + "," + type + "," + (char) t);
		node.setNs(ns(qName)); // 如果当前标签有ns则记录
		String name = ignoreNS(qName);
		String x = attr.getValue(INode.TYPE_XTAG); // 标签是否base64转换
		byte[] buf = name.getBytes();
		if (x != null) name = new String(buf, 1, buf.length - 1);
		if (status.current instanceof ICompositeNode)
		{ // 如果当前父亲节点是结构类型
			// 需要对非ESB数组表示方式的报文，增加数组支持。 modified by spc 20090601
			/*
			 * esb: <array> <v>aaa</v> <v>bbb</v> </array> 不含有v， 重复标签格式为：
			 * <array>aaa</array> <array>bbb</array>
			 */
			ICompositeNode parent = (ICompositeNode) status.current;
			add2compositenode(parent, name, node);

			status.parent = status.current;
			status.name = name;
			status.current = node;
		}
		else if (status.current instanceof IArrayNode)
		{ // 如果当前父亲节点是数组节点
			((IArrayNode) status.current).add(node);
			status.parent = status.current;
			status.index = ((IArrayNode) status.current).size() - 1;
			status.current = node;
		}
		else
		{ // 修改父节点为compositenode节点
			// System.out.println(name + ":" +
			// status.name+":"+status.stack.peek().getClass());
			INode pnode = null;
			if (name.equalsIgnoreCase(tagList))
			{ // 如果当前是数组节点下的子元素
				pnode = new ArrayNode(); // 修改父节点为数组节点
				((ArrayNode) pnode).add(node); // 修改一下原子节点所在父节点信息
				status.index = 0;
			}
			else
			{ // 如果当前是符合节点下的子元素
				// modifed by chenjs 2011-12-02 容许产生不同实例的cnode
				pnode = root().newInstance(); // new CompositeNode();
				// System.out.println("pnode: "+pnode.getClass());
				((ICompositeNode) pnode).set(name, node); // 修改一下原子节点所在父节点信息
			}
			pnode.setExt(status.current.getExt());
			pnode.setNs(status.current.getNs()); // added by chenjs 2011-12-20
													// 命名空间也增加进来
			changeParent(status, pnode);
			// if (status.parent instanceof ICompositeNode) ((ICompositeNode)
			// status.parent)
			// .set(status.name, pnode);
			// else ((IArrayNode) status.parent).set(status.index, pnode);
			// System.out.println("p: "+status.parent);

			status.stack.pop();
			status.stack.push(pnode);

			status.name = name;
			status.current = node;
			status.parent = pnode;
		}
		status.stack.push(node);
	}

	// 忽略每个标签的namespace
	String ignoreNS(String name)
	{
		int index = name.lastIndexOf(':');
		return index >= 0 ? name.substring(index + 1) : name;
	}

	String ns(String name)
	{
		int index = name.lastIndexOf(':');
		return index >= 0 ? name.substring(0, index) : null;
	}

	// 改变父节点类型, 由原子类型改为compositenode or arraynode
	void changeParent(Status status, INode node)
	{
		if (status.parent instanceof ICompositeNode)
		{
			INode first = ((ICompositeNode) status.parent).getNode(status.name);
			if (first instanceof IAtomNode) ((ICompositeNode) status.parent).set(status.name, node);
			else if (first instanceof IArrayNode) ((IArrayNode) first).set(
					((IArrayNode) first).size() - 1, node);
			else System.err
					.println("cannot change parent node, cos parent in parent is valid node.."
							+ first.getClass());
		}
		else
		{
			IArrayNode pnode = (IArrayNode) status.parent;
			// System.out.println(pnode.size() + ":" + node);
			// if (anode.size()==0)anode.add(node);
			pnode.set(status.index, node);
		}
	}

	// 把一个节点放入到父节点中，如果某个节点已经放入到父节点中时，采用把原节点合并数组处理
	// 返回当前节点此时的真正parent
	INode add2compositenode(ICompositeNode parent, String name, INode current)
	{
		INode first = parent.getNode(name);
		// System.out.println(name + ":" + (first == null));
		if (first != null)
		{ // 表面xml报文中在同一层级中已经包含了一个同名标签， 此时应该理解为数组
			if (first instanceof IArrayNode)
			{
				((IArrayNode) first).add(current);
				return first;
			}
			// 修改父亲节点中的元素，使之变为一个数组类型，
			// Note: 此时还没考虑元素类型的第一个类型就是数组类型，此时逻辑会出问题，对于数组嵌套比较少
			IArrayNode arr = new ArrayNode();
			arr.add(first);
			arr.add(current);
			parent.set(name, arr);
			return arr;
		}
		parent.set(name, current);
		return parent;
	}

	public void error(SAXParseException e) throws SAXException
	{
		log.error("Line:" + e.getLineNumber() + ",Column:" + e.getColumnNumber(), e);
	}

	public void fatalError(SAXParseException e) throws SAXException
	{
		log.error("Line:" + e.getLineNumber() + ",Column:" + e.getColumnNumber(), e);
	}

	public void warning(SAXParseException e) throws SAXException
	{
		log.warn("Line:" + e.getLineNumber() + ",Column:" + e.getColumnNumber(), e);
	}

	public String getTagType()
	{
		return tagType;
	}

	public void setTagType(String tagType)
	{
		this.tagType = tagType;
	}

	public String getTagTypeMap()
	{
		return tagTypeMap;
	}

	public void setTagTypeMap(String tagTypeMap)
	{
		this.tagTypeMap = tagTypeMap;
	}

	public String getTagTypeList()
	{
		return tagTypeList;
	}

	public void setTagTypeList(String tagTypeList)
	{
		this.tagTypeList = tagTypeList;
	}

	public String getTagList()
	{
		return tagList;
	}

	public void setTagList(String tagList)
	{
		this.tagList = tagList;
	}

	public void setIgnoreAttr(boolean ignoreAttr)
	{
		this.ignoreAttr = ignoreAttr;
	}
}
