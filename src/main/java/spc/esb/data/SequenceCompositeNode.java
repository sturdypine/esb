package spc.esb.data;

import java.util.Iterator;
import java.util.LinkedList;

import spc.esb.data.converter.NodeConverterFactory;
import spc.webos.util.StringX;

/**
 * 针对某些特殊的后台系统,需要的xml报文复杂类型其成员节点需要顺序的呈现在xml文档中, 此类保证每个加入的元素的顺序就是将来生成xml字符串的顺序
 * 
 * @author spc
 * 
 */
public class SequenceCompositeNode extends CompositeNode
{
	protected LinkedList keys = new LinkedList(); // 可能有重复的key放入到map
	private static final long serialVersionUID = 1L;

	public Object set(String name, Object value)
	{
		if (value == null) return null;
		int index = name.lastIndexOf('/');
		SequenceCompositeNode parent = this;
		if (index >= 0)
		{
			parent = (SequenceCompositeNode) create(name.substring(0, index),
					new SequenceCompositeNode());
			name = name.substring(index + 1);
		}
		return parent.put(name, NodeConverterFactory.getInstance().unpack(value, null));
	}

	public ICompositeNode newInstance()
	{
		return new SequenceCompositeNode();
	}

	public Iterator keys()
	{
		Iterator ks = super.keySet().iterator();
		while (ks.hasNext())
		{ // added by chenjs 2011-11-01 将map中含有，但keys中没有的加入到后面
			String key = ks.next().toString();
			if (!keys.contains(key)) keys.add(key);
		}
		return keys.iterator();
	}

	public Object put(Object key, Object o)
	{
		// if (keys.contains(key)) keys.remove(key);
		// keys.add(key); // 如果某个键值被重复放入到map中， 以最后一次的次序为准
		// modified by chenjs 2011-12-09 如果里面包含了key则保持原来key所在的位置，也就是修改值不改变次序
		if (!keys.contains(key)) keys.add(key);
		super.put(key, o);
		return o;
	}

	public void remove(String key)
	{
		if (key == null) return;
		super.remove(key);
		keys.remove(key);
	}

	public Object putAsFirstChild(String key, Object o)
	{
		if (key == null) return null;
		super.remove(key);
		keys.remove(key);
		keys.addFirst(key);
		super.put(key, o);
		return o;
	}

	public Object putAsLastChild(String key, Object o)
	{
		if (key == null) return null;
		super.remove(key);
		keys.remove(key);
		keys.add(key);
		super.put(key, o);
		return o;
	}

	public LinkedList getKeys()
	{
		return keys;
	}

	public void setKeys(LinkedList keys)
	{
		this.keys = keys;
	}

	public INode getNode(String name)
	{
		Object o = super.get(name);
		if (o == null || o instanceof INode) return (INode) o;
		INode node = NodeConverterFactory.getInstance().unpack(o, null);
		// set(name, node); // 不能执行放入操作， 否则会读取会改变原有顺序状态
		return node;
	}

	public SequenceCompositeNode()
	{
	}

	public SequenceCompositeNode(ICompositeNode cnode, String keys)
	{
		this.keys = new LinkedList();
		set(cnode);
		this.keys = new LinkedList(StringX.split2list(keys, StringX.COMMA));
	}
}
