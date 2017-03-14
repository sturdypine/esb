package spc.esb.data.validator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.Errors;

import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.webos.util.tree.TreeNode;

/**
 * 单一节点自身验证器，用于验证报文中的单一节点
 * 
 * @author spc
 * 
 */
public interface INodeValidator
{
	/**
	 * 验证报文中每个原子节点
	 * 
	 * @param msg
	 *            全报文消息
	 * @param node
	 *            被验证节点, 节点可以是原子节点和复杂，数组节点
	 * @param attr
	 *            可以是消息结构，struct 消息结构
	 * @param errors
	 *            错误容器
	 */
	void validate(IMessage msg, String field, INode node, TreeNode tnode, Errors errors);

	final static Map VALIDATOR = new HashMap();
}
