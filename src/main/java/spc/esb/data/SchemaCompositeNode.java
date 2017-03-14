package spc.esb.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spc.esb.model.MsgSchemaPO;
import spc.webos.util.tree.TreeNode;

/**
 * 支持cnode以指定的结构输出
 * 
 * @author spc
 * 
 */
public class SchemaCompositeNode extends CompositeNode
{
	private static final long serialVersionUID = 1L;
	protected TreeNode schema;

	protected SchemaCompositeNode()
	{
	}

	protected SchemaCompositeNode(ICompositeNode cnode)
	{
		super(cnode);
	}

	public static SchemaCompositeNode getInstance(TreeNode schema, ICompositeNode cnode)
	{
		SchemaCompositeNode schemaCNode = new SchemaCompositeNode();
		schemaCNode.schema = schema;
		if (schema == null) return new SchemaCompositeNode(cnode);
		List child = schema.getChildren();
		for (int i = 0; child != null && i < child.size(); i++)
		{
			TreeNode tnode = (TreeNode) child.get(i);
			MsgSchemaPO schemaVO = (MsgSchemaPO) tnode.getTreeNodeValue();
			String esbNm = schemaVO.getEsbName();
			if (schemaVO.getFtyp().endsWith(String.valueOf((char) INode.TYPE_MAP)))
			{ // is a map
				if (schemaVO.getFtyp().startsWith(String.valueOf((char) INode.TYPE_ARRAY)))
				{ // is AM
					IArrayNode anode = cnode.findArray(esbNm, null);
					if (anode != null)
					{
						IArrayNode annode = new ArrayNode();
						for (int j = 0; j < anode.size(); j++)
							annode.add(getInstance(tnode, (ICompositeNode) anode.get(j)));
						schemaCNode.set(esbNm, annode);
					}
				}
				else
				{
					ICompositeNode cn = cnode.findComposite(esbNm, null);
					if (cn != null) schemaCNode.set(esbNm, getInstance(tnode, cn));
				}
			}
			else
			{
				INode node = cnode.getNode(esbNm);
				if (node != null) schemaCNode.set(esbNm, node);
			}
		}
		// 把schema里面没有描述的信息放入进去
		Iterator keys = cnode.keys();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			if (!schemaCNode.containsKey(key)) schemaCNode.set(key, cnode.get(key));
		}
		return schemaCNode;
	}

	public Iterator keys()
	{
		if (schema == null) return super.keys();
		List keys = new ArrayList();
		List child = schema.getChildren();
		for (int i = 0; child != null && i < child.size(); i++)
		{
			TreeNode tnode = (TreeNode) child.get(i);
			keys.add(((MsgSchemaPO) tnode.getTreeNodeValue()).getEsbName());
		}
		// 将schema里面没有描述的，但节点中实际含有的以无序方式列到最后面
		Iterator items = super.keys();
		while (items.hasNext())
		{
			String key = items.next().toString();
			if (!keys.contains(key)) keys.add(key);
		}
		return keys.iterator();
	}

	public TreeNode getSchema(String key)
	{
		if (schema == null) return null;
		List child = schema.getChildren();
		for (int i = 0; child != null && i < child.size(); i++)
		{
			TreeNode tnode = (TreeNode) child.get(i);
			MsgSchemaPO schemaVO = (MsgSchemaPO) tnode.getTreeNodeValue();
			if (schemaVO.getEsbName().equals(key)) return tnode;
		}
		return null;
	}
}
