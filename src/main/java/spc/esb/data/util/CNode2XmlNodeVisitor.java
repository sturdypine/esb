package spc.esb.data.util;

import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 将一个节点根据报文schema定义序列化为一个字符串，保持字段的顺序
 * 
 * @author spc
 * 
 */
public class CNode2XmlNodeVisitor implements INodeVisitor
{
	protected boolean pretty;
	protected int level; // 当前结构所处报文层次，在pretty模式下决定有几个\t
	protected StringBuilder buf = new StringBuilder();

	public CNode2XmlNodeVisitor()
	{
	}

	public CNode2XmlNodeVisitor(boolean pretty, int level)
	{
		this.pretty = pretty;
		this.level = level;
	}

	public void clear()
	{
		buf.setLength(0);
	}

	public boolean start(INode node, TreeNode nodeSchema) throws Exception
	{
		if (node == null) return true;
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();

		if (schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY)
		{
			if (node instanceof IAtomNode) return true; // 说明为空节点
			if ((node instanceof ICompositeNode) && ((ICompositeNode) node).size() <= 0)
				return true;
			if ((node instanceof IArrayNode) && ((IArrayNode) node).size() <= 0) return true;
		}
		buf.append(pretty());
		buf.append('<');
		buf.append(getTagName(node, nodeSchema));
		buf.append('>');
		if (schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY)
		{
			level++;
		}
		return true;
	}

	protected String getTagName(INode node, TreeNode nodeSchema)
	{
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();
		return schema.getEsbName();
	}

	public boolean end(INode node, TreeNode nodeSchema) throws Exception
	{
		if (node == null) return true;
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();

		if (schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY)
		{
			if (node instanceof IAtomNode) return true; // 说明为空节点
			if ((node instanceof ICompositeNode) && ((ICompositeNode) node).size() <= 0)
				return true;
			if ((node instanceof IArrayNode) && ((IArrayNode) node).size() <= 0) return true;
			level--;
			buf.append(pretty());
		}
		else
		{
			buf.append(getValue(node, nodeSchema));
		}
		buf.append('<');
		buf.append('/');
		buf.append(getTagName(node, nodeSchema));
		buf.append('>');
		return true;
	}

	protected String pretty()
	{
		if (!pretty) return StringX.EMPTY_STRING;
		StringBuffer buf = new StringBuffer();
		buf.append('\n');
		for (int i = 0; i < level; i++)
			buf.append('\t');
		return buf.toString();
	}

	protected String getValue(INode node, TreeNode nodeSchema)
	{
		String v = ((IAtomNode) node).stringValue();
		if (v.indexOf('<') >= 0) v = StringX.replaceAll(v, "<", "&lt;");
		if (v.indexOf('>') >= 0) v = StringX.replaceAll(v, ">", "&gt;");
		if (v.indexOf('&') >= 0) v = StringX.replaceAll(v, "&", "&amp;");
		return v;
	}

	public StringBuilder toXml()
	{
		return buf;
	}
}
