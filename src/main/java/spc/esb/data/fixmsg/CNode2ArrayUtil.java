package spc.esb.data.fixmsg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.tree.TreeNode;

/**
 * 将一个复杂节点，变成一个数组
 * 
 * @author spc
 * 
 */
public class CNode2ArrayUtil
{
	static Logger log = LoggerFactory.getLogger(CNode2ArrayUtil.class);

	public static String[] pack(ICompositeNode cnode, TreeNode schema,
			IAtom2ArrayNodeConverter atom2arraynode) throws Exception
	{
		List items = schema.getChildren();
		if (items == null) return null;
		if (log.isDebugEnabled()) log.debug("cnode schema children size:" + items.size());
		String[] array = new String[items.size()];
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) item.getTreeNodeValue();
			atom2arraynode.pack(array, i, cnode.findAtom(vo.getEsbName(), null), vo);
		}
		return array;
	}

	public static ICompositeNode unpack(String[] array, TreeNode schema,
			IAtom2ArrayNodeConverter atom2arraynode) throws Exception
	{
		List items = schema.getChildren();
		if (items == null) return null;
		if (log.isDebugEnabled()) log.debug("cnode schema children size:" + items.size());
		ICompositeNode cnode = new CompositeNode();
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) item.getTreeNodeValue();
			INode node = atom2arraynode.unpack(array, i, vo);
			if (node != null) cnode.set(vo.getEsbName(), node);
		}
		return cnode;
	}
}
