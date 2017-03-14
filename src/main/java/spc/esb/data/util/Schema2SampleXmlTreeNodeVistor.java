package spc.esb.data.util;

import spc.esb.core.TagAttr;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 根据报文的schema结构生产样例报文
 * 
 * @author spc
 * 
 */
public class Schema2SampleXmlTreeNodeVistor extends AbstractSchema2SampleTreeNodeVistor
{
	public Schema2SampleXmlTreeNodeVistor()
	{
	}

	public Schema2SampleXmlTreeNodeVistor(int level)
	{
		this.level = level;
	}

	public boolean start(TreeNode treeNode, TreeNode parent, int index)
	{
		MsgSchemaPO schema = (MsgSchemaPO) treeNode.getTreeNodeValue();
		TagAttr tagAttr = new TagAttr(schema.getTagAttr());
		if (tagAttr.isHidden()) return true;
		buf.append(pretty());
		buf.append('<');
		buf.append(schema.getEsbName());
		buf.append('>');
		if (schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY)
			level++;
		else
		{
			String defValue = getAtomValue(schema);
			if (StringX.nullity(defValue) && "M".equalsIgnoreCase(schema.getOptional()))
				defValue = "M";
			buf.append(defValue);
			buf.append('<');
			buf.append('/');
			buf.append(schema.getEsbName());
			buf.append('>');
		}
		return true;
	}

	protected String getAtomValue(MsgSchemaPO schema)
	{
		return StringX.null2emptystr(schema.getDefValue());
	}

	public boolean end(TreeNode treeNode, TreeNode parent, int index)
	{
		MsgSchemaPO schema = (MsgSchemaPO) treeNode.getTreeNodeValue();
		TagAttr tagAttr = new TagAttr(schema.getTagAttr());
		if (tagAttr.isHidden()) return true;
		if (schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY)
		{
			level--;
			buf.append(pretty());
			buf.append('<');
			buf.append('/');
			buf.append(schema.getEsbName());
			buf.append('>');
		}
		return true;
	}
}
