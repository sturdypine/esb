package spc.esb.data.util;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 将报文中需要冲正的要素摘取出来
 * 
 * @author spc
 * 
 */
public class DefaultRvslNodeVisitor implements INodeVisitor
{
	protected Map rvslInfo = new HashMap();

	public boolean start(INode node, TreeNode nodeSchema) throws Exception
	{
		if (node == null) return true;
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();
		if (StringX.nullity(schema.getReversal()) || schema.getFtyp().charAt(0) == INode.TYPE_MAP
				|| schema.getFtyp().charAt(0) == INode.TYPE_ARRAY) return true;
		rvslInfo.put(schema.getReversal(), node.toString());
		return true;
	}

	public boolean end(INode node, TreeNode nodeSchema) throws Exception
	{
		return true;
	}

	public Map getRvslInfo()
	{
		return rvslInfo;
	}
}
