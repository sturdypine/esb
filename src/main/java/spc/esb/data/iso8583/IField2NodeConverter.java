package spc.esb.data.iso8583;

import spc.esb.data.INode;

/**
 * 将一个8583的Field域变成esb xml中的三种类型节点IAtomNode, IArrayNode, ICompositeNode
 * 
 * @author chenjs
 * 
 */
public interface IField2NodeConverter
{
	INode field2node(Field f);

	Field node2field(Field f, INode node);
}
