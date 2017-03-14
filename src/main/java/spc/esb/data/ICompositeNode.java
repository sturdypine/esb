package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

/**
 * 一个再复杂的对象，也是由ICompsiteData(类似Map)，IArray和简单数据的树结构嵌套组成。 数据类型为:简单类型：String, int,
 * float, double, long, byte[] 复杂组合类型为：IArray, ICompsiteData
 * 
 * @author spc
 * 
 */
public interface ICompositeNode extends INode
{
	ICompositeNode clone();
	
	int size();

	void remove(String key);

	void remove(String[] keys);

	void removeNotIn(String[] keys);

	void removeNotIn(String[] keys, boolean ignoreCase);

	boolean dfs(INodeVisitor visitor);

	boolean dfs(INodeVisitor visitor, INode parent, String tag);

	Object putAsFirstChild(String key, Object o);

	Object putAsLastChild(String key, Object o);

	/**
	 * 找到当前ICompsiteData为根的对象的，path路经下的节点 返回ICompsiteData,IArray和简单类型
	 * 
	 * @param path
	 * @return
	 */
	INode find(String path);

	ICompositeNode findComposite(String path, ICompositeNode def);

	IArrayNode findArray(String path, IArrayNode def);

	IAtomNode findAtom(String path, IAtomNode def);

	/**
	 * 获取参数的值
	 * 
	 * @param curPath
	 *            当前节点在message中的路径
	 * @param path
	 *            当前节点下的参数名
	 * @param type
	 * @param canNull
	 * @return
	 */
	INode find(String curPath, String path, byte type, boolean canNull);

	INode find(String curPath, String path, byte type, INode defaultValue);

	/**
	 * 找到当前为根的对象path路径下的节点，返回指定的java类型
	 * 
	 * @param path
	 * @param target
	 * @return
	 */
	Object find(String path, Object target);

	INode findIgnoreCase(String path);

	/**
	 * 查找节点下的路径，如果不存在则用默认值
	 * 
	 * @param path
	 * @param defaultValue
	 * @return
	 */
	Object lookup(String path, Object defaultValue);

	ICompositeNode create(String path);

	/**
	 * 找到当前ICompsiteData为根的对象的，path路经下的节点， data为不存在路径中ICompsiteData具体实现
	 * 
	 * @param path
	 * @param data
	 * @return
	 */
	ICompositeNode create(String path, ICompositeNode data);

	/**
	 * 设置名下的属性值
	 * 
	 * @param name
	 * @param value
	 */
	Object set(String name, Object value);

	/**
	 * 增加一个属性值到cnode节点中，如果原来存在了，则加入到原来的数组后面
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Object add(String name, Object value);

	Object set(String xml);

	ICompositeNode setAll(ICompositeNode cn);

	ICompositeNode set(ICompositeNode cn);

	ICompositeNode apply(ICompositeNode cn);

	ICompositeNode apply(ICompositeNode cn, String[] keys);

	ICompositeNode apply(ICompositeNode cn, String[] keys, String[] names);

	ICompositeNode applyIf(ICompositeNode cn);

	ICompositeNode applyIf(ICompositeNode cn, String[] keys);

	ICompositeNode applyIf(ICompositeNode cn, String[] keys, String[] names);

	ICompositeNode set(Object object);

	ICompositeNode setByMapping(Object object, Object mapping);

	ICompositeNode setByAttr(Object object, Map attr);

	INode getNode(String name);

	INode getNodeIgnoreCase(String name);

	Object toObject(Object target);

	Object toObject(Object target, Map attr);

	// 获得原始对象，可能是INode,可能是java对象
	Object get(Object key);

	Iterator keys();

	ICompositeNode newInstance();

	String toXml(String tag, boolean pretty) throws IOException;

	String toXml(String tag, boolean pretty, INode2XML node2xml) throws IOException;

	void toXml(OutputStream os, String tag, boolean pretty, INode2XML node2xml) throws IOException;

	void toXml(OutputStream os, String tag, boolean pretty, INode2XML node2xml, Map attribute)
			throws IOException;

	void toXml(OutputStream os, String ns, String tag, boolean pretty, INode2XML node2xml,
			Map attribute) throws IOException;

	byte[] toXml(String ns, String tag, boolean pretty, INode2XML node2xml, Map attribute)
			throws IOException;

	void clear();

	Map mapValue();

	Map plainMapValue();

	boolean containsKey(Object key);
	// Object clone();
}
