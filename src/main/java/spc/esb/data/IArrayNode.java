package spc.esb.data;

import java.util.List;

public interface IArrayNode extends INode
{
	IArrayNode clone();
	
	int size();

	boolean isEmpty();

	List listValue();
	
	List plainListValue();

	boolean contains(Object o);

	boolean add(Object o);

	void addAll(IArrayNode an);

	Object set(int index, Object o);

	Object removeNode(Object o);

	Object remove(int index);

	void clear();

	INode getNode(int index);

	INode find(String path);
	
	INode findIgnoreCase(String path);

	// 获得原始对象
	Object get(int index);

	// Object clone();
}
