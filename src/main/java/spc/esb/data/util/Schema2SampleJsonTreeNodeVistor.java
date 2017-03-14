package spc.esb.data.util;

import spc.esb.core.TagAttr;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.tree.TreeNode;

public class Schema2SampleJsonTreeNodeVistor extends AbstractSchema2SampleTreeNodeVistor
{
	public Schema2SampleJsonTreeNodeVistor()
	{
	}

	public Schema2SampleJsonTreeNodeVistor(int level)
	{
		this.level = level;
	}

	public boolean start(TreeNode treeNode, TreeNode parent, int index)
	{
		MsgSchemaPO schema = (MsgSchemaPO) treeNode.getTreeNodeValue();
		TagAttr tagAttr = new TagAttr(schema.getTagAttr());
		if (tagAttr.isHidden()) return true;
		buf.append(pretty());
		buf.append('"');
		buf.append(schema.getEsbName());
		buf.append("\":");
		if (schema.getFtyp().charAt(0) == INode.TYPE_ARRAY) buf.append("[");
		if (schema.getFtyp().endsWith("M"))
		{
			level++;
			buf.append("{");
		}
		else buf.append(getAtomValue(schema));
		return true;
	}

	protected String getAtomValue(MsgSchemaPO schema)
	{
		String type = schema.getFtyp();
		String v = "\"\"";
		if (type.endsWith("I") || type.endsWith("L") || type.endsWith("D")) v = "0";
		return v;
	}

	public boolean end(TreeNode treeNode, TreeNode parent, int index)
	{
		MsgSchemaPO schema = (MsgSchemaPO) treeNode.getTreeNodeValue();
		TagAttr tagAttr = new TagAttr(schema.getTagAttr());
		if (tagAttr.isHidden()) return true;
		if (schema.getFtyp().endsWith("M"))
		{
			level--;
			buf.append(pretty());
			buf.append('}');
		}
		if (schema.getFtyp().charAt(0) == INode.TYPE_ARRAY) buf.append("]");
		if (parent.getChildren().size() != index + 1) buf.append(','); // 不是最后一个节点加一个,
		return true;
	}
}
