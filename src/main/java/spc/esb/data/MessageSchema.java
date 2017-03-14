package spc.esb.data;

import java.util.List;

import spc.webos.util.tree.TreeNode;

/**
 * 获得验证资源(类似schema)
 * 
 * @author spc
 * 
 */
public interface MessageSchema
{
	public final static String MO_OPTIONAL = "O";
	public final static String MO_optional = "o"; // 此类型如果传一个空标签，则会用默认值替代
	public final static String MO_MUST = "M";
	public final static String MO_must = "m"; // 此类型如果没有传标签，则会用默认值替代，没有默认值会赋予一个空标签

	/**
	 * 获取类似报文结构的单节点验证模式
	 * 
	 * @param name
	 * @return
	 */
	TreeNode getMsgSchema(String name);

	/**
	 * 获取报文的多节点验证模式
	 * 
	 * @param name
	 * @return
	 */
	List getMsgValidator(String name);
}
