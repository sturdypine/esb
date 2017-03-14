package spc.esb.data.xml;

import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;

/**
 * 容许处理任意类型的INode节点，并将它转换为任意类型, 用于xml - xml'转换时改变节点类型，比如当前节点是数组类型，目标节点是复杂节点类型
 * 
 * @author chenjs 2012-01-10
 * 
 */
public interface INodeProcessor
{
	INode process(IMessage msg, INode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception;
}
