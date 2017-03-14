package spc.esb.data.util;

import spc.esb.data.INode;
import spc.webos.util.tree.TreeNode;

/**
 * 根据报文结构对报文进行遍历 所使用的节点访问接口
 * 
 * @author spc
 * 
 */
public interface INodeVisitor
{
	/**
	 * 表示进入该节点事件，如果返回true表示访问继续，返回false则不再遍历主报文结构
	 * 
	 * @param node
	 * @param nodeSchema
	 * @return
	 */
	boolean start(INode node, TreeNode nodeSchema) throws Exception;

	/**
	 * 表示结束该节点事件
	 * 
	 * @param node
	 * @param nodeSchema
	 * @return
	 * @throws Exception
	 */
	boolean end(INode node, TreeNode nodeSchema) throws Exception;
}
