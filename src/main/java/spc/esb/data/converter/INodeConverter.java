package spc.esb.data.converter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.INode;

/**
 * 用于java普通对象和抽象数据类型节点之间的转换
 * 
 * @author spc
 * 
 */
public interface INodeConverter
{
	boolean support(Object obj);

	boolean supportNode(INode node);

	INode unpack(Object obj, Map attribute);

	Object pack(INode node, Object target, Map attribute);

	static final Logger log = LoggerFactory.getLogger(INodeConverter.class);
}
