package spc.esb.data.fixmsg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.ArrayNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.tree.TreeNode;

/**
 * 将一个复杂节点，变成一个定长的报文
 * 
 * @author spc
 * 
 */
public class CNode2FixedLenBytesUtil
{
	static Logger log = LoggerFactory.getLogger(CNode2FixedLenBytesUtil.class);

	public static void pack(ICompositeNode cnode, byte[] fixedLen, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, String charset) throws Exception
	{
		pack(cnode, fixedLen, 0, msgStruct, atom2fixedLen, null, charset);
	}

	public static void pack(ICompositeNode cnode, byte[] fixedLen, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception
	{
		pack(cnode, fixedLen, 0, msgStruct, atom2fixedLen, array2fixedLen, charset);
	}

	/**
	 * 根据报文结构类型把一个cnode放入到一个定长报文中,
	 * cnode中不能嵌套arraynode节点，因为逻辑上arraynode节点无法定位具体位置，需要特殊处理
	 * 
	 * @param cnode
	 * @param fixedLen
	 * @param msgStruct
	 */
	public static void pack(ICompositeNode cnode, byte[] fixedLen, int offset, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception
	{
		List items = msgStruct.getChildren();
		if (items == null) return;
		if (log.isDebugEnabled()) log.debug("cnode struct children size:" + items.size());
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO schema = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) schema.getFtyp().charAt(0);
			// 2012-06-26 定长解释嵌套节点时，如果节点fixmsg明确配置了-1表示不参与定长转换
			if (type == INode.TYPE_MAP && !NO_MAP2FIXMSG.equals(schema.getFixmsg()))
			{ // 如果是map结构，则继续嵌套处理
				ICompositeNode node = cnode.findComposite(schema.getEsbName(), null);
				if (node != null && ((ICompositeNode) node).size() > 0) pack((ICompositeNode) node,
						fixedLen, offset, item, atom2fixedLen, array2fixedLen, charset);
			}
			else if (type == INode.TYPE_ARRAY)
			{
				if (array2fixedLen != null) array2fixedLen.pack(fixedLen, offset,
						cnode.findArray(schema.getEsbName(), null), item, atom2fixedLen,
						array2fixedLen, charset);
			}
			else atom2fixedLen.pack(fixedLen, offset, cnode.findAtom(schema.getEsbName(), null),
					schema, charset);
		}
	}

	public static ICompositeNode unpack(byte[] fixedLen, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception
	{
		return unpack(fixedLen, 0, msgStruct, atom2fixedLen, array2fixedLen, charset);
	}

	public static ICompositeNode unpack(byte[] fixedLen, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, String charset) throws Exception
	{
		return unpack(fixedLen, 0, msgStruct, atom2fixedLen, null, charset);
	}

	/**
	 * 将一个定长报文变成一个复杂节点
	 * 
	 * @param fixedLen
	 * @param msgStruct
	 * @param atom2fixedLen
	 * @return
	 * @throws Exception
	 */
	public static ICompositeNode unpack(byte[] fixedLen, int offset, TreeNode msgStruct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception
	{
		List items = msgStruct.getChildren();
		if (items == null) return null;
		ICompositeNode cnode = new CompositeNode();
		if (log.isDebugEnabled()) log.debug("cnode struct children size:" + items.size());
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO struct = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) struct.getFtyp().charAt(0);
			// 2012-06-26 定长解释嵌套节点时，如果节点fixmsg明确配置了-1表示不参与定长转换
			if (type == INode.TYPE_MAP && !NO_MAP2FIXMSG.equals(struct.getFixmsg()))
			{ // 如果是map结构，则继续嵌套处理
				ICompositeNode cn = unpack(fixedLen, offset, item, atom2fixedLen, array2fixedLen,
						charset);
				if (cn != null && cn.size() > 0) cnode.set(struct.getEsbName(), cn);
			}
			else if (type == INode.TYPE_ARRAY)
			{
				if (array2fixedLen != null)
				{
					IArrayNode anode = array2fixedLen.unpack(fixedLen, offset, item, atom2fixedLen,
							array2fixedLen, charset);
					if (anode != null && anode.size() > 0) cnode.set(struct.getEsbName(), anode);
				}
			}
			else
			{
				INode value = atom2fixedLen.unpack(fixedLen, offset, struct, charset);
				if (value != null) cnode.set(struct.getEsbName(), value);
			}
		}
		return cnode;
	}

	// pack2. added by chenjs .2011-08-05
	public static int pack2(ICompositeNode root, byte[] fixedLen, int offset, TreeNode schema,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter2 array2fixedLen,
			String charset) throws Exception
	{
		return pack2(root, root, fixedLen, offset, schema, atom2fixedLen, array2fixedLen, charset);
	}

	protected static int pack2(ICompositeNode root, ICompositeNode cnode, byte[] fixedLen,
			int offset, TreeNode schema, IAtom2FixedLenConverter atom2fixedLen,
			IArray2FixedLenConverter2 array2fixedLen, String charset) throws Exception
	{
		int arrayTotalLen = 0; // 动态数组所占用的总字节长度
		List items = schema.getChildren();
		if (items == null) return arrayTotalLen;
		if (log.isDebugEnabled()) log.debug("cnode struct children size:" + items.size());
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO struct = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) struct.getFtyp().charAt(0);
			// 2012-06-26 定长解释嵌套节点时，如果节点fixmsg明确配置了-1表示不参与定长转换
			if (type == INode.TYPE_MAP && !NO_MAP2FIXMSG.equals(struct.getFixmsg()))
			{ // 如果是map结构，则继续嵌套处理
				ICompositeNode node = cnode.findComposite(struct.getEsbName(), null);
				if (node != null && ((ICompositeNode) node).size() > 0) pack2(
						(ICompositeNode) node, fixedLen, offset, item, atom2fixedLen,
						array2fixedLen, charset);
			}
			else if (type == INode.TYPE_ARRAY)
			{
				if (array2fixedLen != null)
				{
					int arrayLen = array2fixedLen.pack(root, cnode, fixedLen, offset,
							cnode.findArray(struct.getEsbName(), null), item, atom2fixedLen,
							charset);
					if (log.isDebugEnabled()) log.debug("arrayLen is : " + arrayLen);
					offset += arrayLen;
					arrayTotalLen += arrayLen;
				}
				else log.debug("array2fixedLen is null for " + struct.getEsbName());
			}
			else atom2fixedLen.pack(fixedLen, offset, cnode.findAtom(struct.getEsbName(), null),
					struct, charset);
		}
		return arrayTotalLen;
	}

	public static ICompositeNode unpack2(byte[] fixedLen, int offset, TreeNode schema,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter2 array2fixedLen,
			String charset) throws Exception
	{
		ICompositeNode root = new CompositeNode();
		return unpack2(root, root, fixedLen, offset, schema, atom2fixedLen, array2fixedLen, charset);
	}

	protected static ICompositeNode unpack2(ICompositeNode root, ICompositeNode cnode,
			byte[] fixedLen, int offset, TreeNode schema, IAtom2FixedLenConverter atom2fixedLen,
			IArray2FixedLenConverter2 array2fixedLen, String charset) throws Exception
	{
		List items = schema.getChildren();
		if (items == null) return null;
		// ICompositeNode cnode = new CompositeNode();
		if (log.isDebugEnabled()) log.debug("cnode struct children size:" + items.size());
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO struct = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) struct.getFtyp().charAt(0);
			// 2012-06-26 定长解释嵌套节点时，如果节点fixmsg明确配置了-1表示不参与定长转换
			if (type == INode.TYPE_MAP && !NO_MAP2FIXMSG.equals(struct.getFixmsg()))
			{ // 如果是map结构，则继续嵌套处理
				ICompositeNode cn = unpack2(root, new CompositeNode(), fixedLen, offset, item,
						atom2fixedLen, array2fixedLen, charset);
				if (cn != null && cn.size() > 0) cnode.set(struct.getEsbName(), cn);
			}
			else if (type == INode.TYPE_ARRAY)
			{
				if (array2fixedLen != null)
				{
					IArrayNode value = new ArrayNode();
					int arrayLen = array2fixedLen.unpack(root, cnode, fixedLen, offset, value,
							item, atom2fixedLen, charset);
					if (value.size() > 0) cnode.set(struct.getEsbName(), value);
					if (log.isDebugEnabled()) log.debug("arrayLen is " + arrayLen);
					offset += arrayLen;
				}
				else log.debug("array2fixedLen is null for " + struct.getEsbName());
			}
			else
			{
				INode value = atom2fixedLen.unpack(fixedLen, offset, struct, charset);
				if (value != null) cnode.set(struct.getEsbName(), value);
			}
		}
		return cnode;
	}
	
	// 在某些引用元数据的情况下，比如核心定长报文头，采取的是分离定长转换法则
	public static String NO_MAP2FIXMSG = "-1";
}
