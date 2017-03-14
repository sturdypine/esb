package spc.esb.data.iso8583;

import spc.esb.data.AtomNode;
import spc.esb.data.INode;

/**
 * 默认把一个8583节点变成一个esb xml的原子节点
 * 
 * @author chenjs
 * 
 */
public class DefaultField2NodeConverter implements IField2NodeConverter
{
	public INode field2node(Field f)
	{
		return new AtomNode(f.value);
	}

	public Field node2field(Field f, INode node)
	{
//		f.value = node.toString(); // 有bug, 不能设置enable属性
		f.setValue(node.toString()); // 800
		return f;
	}

	static DefaultField2NodeConverter df2n = new DefaultField2NodeConverter();

	public static DefaultField2NodeConverter getInstance()
	{
		return df2n;
	}

}
