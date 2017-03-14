package spc.esb.data.xml;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.ArrayNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.util.IAtomProcessor;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * xml报文格式转换
 * 
 * @author chenjs
 * 
 */
public class XMLConverterUtil
{
	static Logger log = LoggerFactory.getLogger(XMLConverterUtil.class);

	public static void convertMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv) throws Exception
	{
		convertMap(schema, srcMsg, src, target, esb2rcv, null, StringX.EMPTY_STRING);
	}

	public static void convertMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, IAtomProcessor atomProcessor, String esbPath)
			throws Exception
	{
		convertMap(schema, srcMsg, src, target, esb2rcv, atomProcessor, esbPath, true, true);
	}

	public static void convertMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, IAtomProcessor atomProcessor, String esbPath,
			boolean rcvIgnore, boolean emptyIgnore) throws Exception
	{
		convertMap(schema, srcMsg, src, target, esb2rcv, atomProcessor, null, esbPath, true, true,
				null);
	}

	public static void convertMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, IAtomProcessor atomProcessor, String esbPath,
			boolean rcvIgnore, boolean emptyIgnore, ISchemaTargetXMLTag schemaTargetXMLTag)
			throws Exception
	{
		convertMap(schema, srcMsg, src, target, esb2rcv, atomProcessor, null, esbPath, rcvIgnore,
				emptyIgnore, schemaTargetXMLTag);
	}

	public static void convertMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, IAtomProcessor atomProcessor,
			INodeProcessor nodeProcessor, String esbPath, boolean rcvIgnore, boolean emptyIgnore,
			ISchemaTargetXMLTag schemaTargetXMLTag) throws Exception
	{
		if (schema == null) return;
		List items = schema.getChildren();
		if (items == null || src == null) return;
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) vo.getFtyp().charAt(0);

			String esbNm = vo.getEsbName();
			String rcvNm = (schemaTargetXMLTag == null) ? vo.getRcvName() : schemaTargetXMLTag
					.xmlTag(vo); // modifed by chenjs 2011-12-08
									// 支持xml转换目标标签为schema表的其他字段
			// 如果报文结构为原子节点类型时，接受方字段为空，表示和ESB字段相同
			if (StringX.nullity(rcvNm)
					&& (rcvIgnore || (type == INode.TYPE_UNDEFINED && type == INode.TYPE_MAP && type == INode.TYPE_ARRAY))) rcvNm = esbNm;
			String srcNm = esb2rcv ? esbNm : rcvNm;
			String targetNm = esb2rcv ? rcvNm : esbNm;
			INode node = StringX.nullity(srcNm) ? null : src.find(srcNm);
			if (emptyIgnore && node == null) continue;
			INode targetNode = null;
			// 1. 如果当前报文结构中需要的类型是U类型，也就是无特定结构类型，则直接返回
			if (type == INode.TYPE_UNDEFINED) targetNode = node;
			else if (type == INode.TYPE_MAP) // node.type()
			{
				node = StringX.nullity(srcNm) ? src : src.findComposite(srcNm, null);
				if (node == null && log.isDebugEnabled()) log.debug("cnode is null by " + srcNm);
				// added by chenjs 2012-01-25
				// 容许使用接口改变当前xml结构，比如将一个Map节点变为array节点
				INode tnode = (nodeProcessor == null ? node : nodeProcessor.process(srcMsg, node,
						vo, esb2rcv, src, esbPath, target));
				if (tnode != node)
				{ // 如果返回的节点对象不是原对象，认为此节点发生变化，直接作为目标节点
					log.debug("Map:using nodeProcessor result...");
					targetNode = tnode;
				}
				else
				{
					if (!StringX.nullity(targetNm))
					{ // 如果没有提供目标名， 则认为是要将树结构变为扁平结构，直接采用当前的target结构进行递归处理
						targetNode = target.newInstance();
						if (node != null) targetNode.setExt(((ICompositeNode) node).getExt()); // 属性不做修改
					}
					else targetNode = target;
					convertMap(item, srcMsg, (ICompositeNode) node, (ICompositeNode) targetNode,
							esb2rcv, atomProcessor, nodeProcessor, StringX.nullity(esbPath) ? srcNm
									: esbPath + '.' + srcNm, rcvIgnore, emptyIgnore,
							schemaTargetXMLTag);
				}
			}
			else if (type == INode.TYPE_ARRAY) // node.type()
			{
				node = src.findArray(srcNm, null);
				if (node == null && log.isDebugEnabled()) log
						.debug("arraynode is null by " + srcNm);
				// added by chenjs 2012-01-25
				// 容许使用接口改变当前xml结构，比如将一个Map节点变为array节点
				INode tnode = (nodeProcessor == null ? node : nodeProcessor.process(srcMsg, node,
						vo, esb2rcv, src, esbPath, target));
				if (tnode != node)
				{ // 如果返回的节点对象不是原对象，认为此节点发生变化，直接作为目标节点
					log.debug("Array:using nodeProcessor result...");
					targetNode = tnode;
				}
				else
				{
					targetNode = new ArrayNode();
					convertArray(item, srcMsg, (IArrayNode) node, (IArrayNode) targetNode,
							target.newInstance(), esb2rcv, atomProcessor, nodeProcessor,
							StringX.nullity(esbPath) ? srcNm : esbPath + '.' + srcNm, rcvIgnore,
							emptyIgnore);
				}
			}
			// 4. 处理原子节点
			else targetNode = (atomProcessor == null ? node : atomProcessor.process(srcMsg,
					(IAtomNode) node, vo, esb2rcv, src, esbPath, target));
			if (!StringX.nullity(targetNm) && targetNode != null)
			{
				// added by chenjs 2012-03-15 如果Map or Array节点，但节点没有子节点则不加入目标节点中
				if ((targetNode instanceof ICompositeNode)
						&& ((ICompositeNode) targetNode).size() == 0) continue;
				if ((targetNode instanceof IArrayNode) && ((IArrayNode) targetNode).size() == 0) continue;
				target.set(targetNm, targetNode);
			}
		}
	}

	// public static void convertArray(TreeNode msgStruct, IMessage srcMsg,
	// IArrayNode src,
	// IArrayNode target, ICompositeNode targetCN, boolean esb2rcv) throws
	// Exception
	// {
	// convertArray(msgStruct, srcMsg, src, target, targetCN, esb2rcv, null,
	// StringX.EMPTY_STRING);
	// }

	public static void convertArray(TreeNode msgStruct, IMessage srcMsg, IArrayNode src,
			IArrayNode target, ICompositeNode targetCN, boolean esb2rcv,
			IAtomProcessor atomProcessor, INodeProcessor nodeProcessor, String esbPath,
			boolean rcvIgnore, boolean emptyIgnore) throws Exception
	{
		if (src == null) return;
		List items = msgStruct.getChildren();
		// 1. 数组中的元素为原子类型, 而非结构类型
		if (items == null || items.size() == 0)
		{
			if (((MsgSchemaPO) msgStruct.getTreeNodeValue()).getFtyp().endsWith(
					String.valueOf((char) INode.TYPE_UNDEFINED)))
			{ // 如果数组里面的元素是U类型，说明此节点不需要解析，直接放入，属于穿透类型
				for (int i = 0; i < src.size(); i++)
					target.add(src.getNode(i));
			}
			else
			{
				for (int i = 0; i < src.size(); i++)
					// target.add((IAtomNode) src.getNode(i));
					target.add((atomProcessor == null ? (IAtomNode) src.getNode(i) : atomProcessor
							.process(srcMsg, (IAtomNode) src.getNode(i),
									(MsgSchemaPO) msgStruct.getTreeNodeValue(), esb2rcv, null,
									esbPath + '[' + i + ']', null)));
			}
			return;
		}
		// 2. 数组的元素为结构类型
		for (int i = 0; i < src.size(); i++)
		{
			ICompositeNode cnode = (ICompositeNode) src.getNode(i);
			ICompositeNode cn = targetCN.newInstance();
			target.add(cn);
			convertMap(msgStruct, srcMsg, cnode, cn, esb2rcv, atomProcessor, nodeProcessor, esbPath
					+ '[' + i + ']', rcvIgnore, emptyIgnore, null);
		}
	}
}
