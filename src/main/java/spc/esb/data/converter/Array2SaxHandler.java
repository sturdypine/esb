package spc.esb.data.converter;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import spc.esb.data.ArrayNode;
import spc.esb.data.AtomNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.INode;
import spc.webos.util.StringX;

/**
 * 主要针对数组表示是 <array>a</array><array>b</array>类型的xml报文
 * 但如果array只出现一次，这种情况需要报文规范的支持才能知道是array还是map结构
 * 
 * @author spc
 * 
 */
public class Array2SaxHandler extends SaxHandler
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
	}

	static final ThreadLocal STATUS = new ThreadLocal();
	static final ThreadLocal ROOT = new ThreadLocal();
	static final Logger log = LoggerFactory.getLogger(Array2SaxHandler.class);
	static final Array2SaxHandler handler = new Array2SaxHandler();

	private Array2SaxHandler()
	{
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
		ROOT.set(null);
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
		String s = new String(v, start, length);
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

	public void startElement(String uri, String localname, String qName, Attributes attr)
			throws SAXException
	{
		Status status = (Status) STATUS.get();
		if (status.isFirst)
		{
			status.isFirst = false;
			return;
		}
		INode node = null;
		String type = attr.getValue(INode.TYPE_TAG);
		if (type == null || type.length() == 0) type = String.valueOf(INode.TYPE_STRING);
		status.t = (byte) type.charAt(0);
		if (status.t == '8') status.t = INode.TYPE_STRING;
		if (status.t == INode.TYPE_MAP) node = new CompositeNode();
		else if (status.t == INode.TYPE_ARRAY) node = new ArrayNode();
		else node = new AtomNode(StringX.EMPTY_STRING); // 默认为空标签的字符串

		for (int i = 0; i < attr.getLength(); i++)
		{
			String key = attr.getQName(i);
			if (key.equals(INode.TYPE_TAG)) continue; // t,x
			// if (key.equals(INode.TYPE_TAG) || key.equals(INode.TYPE_XTAG)
			// || key.equals(INode.SIZE_TAG)) continue; // t,x,size
			node.setExt(key, attr.getValue(i));
		}
		// if (qName.equals("name")) System.out.println(current.getClass() + ","
		// + node.getClass() + "," + qName + "," + type + "," + (char) t);
		String name = qName;
		String x = attr.getValue(INode.TYPE_XTAG); // 标签是否base64转换
		byte[] buf = name.getBytes();
		if (x != null) name = new String(buf, 1, buf.length - 1);
		if (status.current instanceof ICompositeNode)
		{
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
		{
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

			pnode = new CompositeNode();
			((CompositeNode) pnode).set(name, node); // 修改一下原子节点所在父节点信息

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
		else ((IArrayNode) status.parent).set(status.index, node);
	}

	// 把一个节点放入到父节点中，如果某个节点已经放入到父节点中时，采用把原节点合并数组处理
	// 返回当前节点此时的真正parent
	INode add2compositenode(ICompositeNode parent, String name, INode current)
	{
		INode first = parent.getNode(name);
		System.out.println(name + ":" + (first == null));
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
		log.error("Line:" + e.getLineNumber() + ",Column:" + e.getColumnNumber(), e);
	}
}
