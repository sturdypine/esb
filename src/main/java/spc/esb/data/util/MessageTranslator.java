package spc.esb.data.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.ArrayNode;
import spc.esb.data.AtomNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.MessageSchema;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 完成语义层次的报文转换,从标准的ESB报文到目标ESB报文,中间做签名检查, 转加密, 类型检查, 长度检查, 字段名转换.
 * 生成的ESB报文信息上完全是后台要求的报文, 只是报文格式上还是esb报文
 * 
 * @author spc
 * 
 */
public class MessageTranslator
{
	protected static Logger log = LoggerFactory.getLogger(MessageTranslator.class);

	// 将一个cnode根据schema结构，调整里面的数组和复杂节点类型，
	// 因为数组如果只有一个节点可能在解析时认为是一个原子类型的标签
	public static ICompositeNode adjust(TreeNode schema, ICompositeNode cnode)
	{
		if (cnode == null || schema == null) return null;
		List items = schema.getChildren();
		if (items == null) return null;
		ICompositeNode node = new CompositeNode();
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode item = (TreeNode) items.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) item.getTreeNodeValue();
			byte type = (byte) vo.getFtyp().charAt(0);
			if (type == INode.TYPE_MAP)
			{
				ICompositeNode value = cnode.findComposite(vo.getEsbName(), null);
				if (value == null) continue;
				node.set(vo.getEsbName(), adjust(item, value));
			}
			else if (type == INode.TYPE_ARRAY)
			{
				IArrayNode value = cnode.findArray(vo.getEsbName(), null);
				if (value == null) continue;
				IArrayNode nanode = new ArrayNode();
				for (int j = 0; j < value.size(); j++)
				{
					INode nn = value.getNode(j);
					if (nn instanceof ICompositeNode) nanode.add(adjust(item, (ICompositeNode) nn));
					else nanode.add(nn);
				}
				node.set(vo.getEsbName(), nanode);
			}
			else
			{
				IAtomNode value = cnode.findAtom(vo.getEsbName(), null);
				if (value == null) continue;
				node.set(vo.getEsbName(), value);
			}
		}
		return node;
	}

	public void translateMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv) throws Exception
	{
		translateMap(schema, srcMsg, src, target, esb2rcv, StringX.EMPTY_STRING);
	}

	public void translateMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, String path) throws Exception
	{
		translateMap(schema, srcMsg, src, target, esb2rcv, false, StringX.EMPTY_STRING);
	}

	/**
	 * 只做语义转换
	 * 
	 * @param schema
	 * @param srcMsg
	 * @param src
	 * @param target
	 */
	public void translateMap(TreeNode schema, IMessage srcMsg, ICompositeNode src,
			ICompositeNode target, boolean esb2rcv, boolean autoFilterUndefinedTag, String path)
					throws Exception
	{
		List items = schema.getChildren();
		if (items == null || items.size() == 0)
		{
			log.debug("schema children is null, path:{}", path);
			return;
		}
		for (int i = 0; i < items.size(); i++)
		{
			TreeNode subSchema = (TreeNode) items.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) subSchema.getTreeNodeValue();
			byte type = (byte) vo.getFtyp().charAt(0);

			String esbNm = vo.getEsbName();
			if (type == INode.TYPE_UNDEFINED)
			{
				INode node = src.find(esbNm);
				if (node != null) target.set(esbNm, node);
				continue;
			}
			else if (type == INode.TYPE_MAP)
			{
				ICompositeNode node = src.findComposite(esbNm, null);
				// modified by chenjs 2011-09-02 支持不上送M类型值时给一个默认值
				if ((node == null || node.size() == 0)
						&& !MessageSchema.MO_must.equals(vo.getOptional()))
					continue;
				if (node == null) node = new CompositeNode();

				ICompositeNode targetNode = target.newInstance();
				// 2012-06-12 将原内容全量复制一份
				if (!autoFilterUndefinedTag) targetNode.set(node);
				targetNode.setExt(node.getExt()); // 属性不做修改

				// 700 2013-06-05 增加autoFilterUndefinedTag配置参数传入
				translateMap(subSchema, srcMsg, node, targetNode, esb2rcv, autoFilterUndefinedTag,
						StringX.nullity(path) ? vo.getEsbName() : path + '.' + vo.getEsbName());
				if (targetNode.size() == 0
						&& (targetNode.getExt() == null || targetNode.getExt().size() == 0))
				{ // 如果当前cnode节点没有任何子节点，也没有任何属性则过滤 2012-03-02
					log.debug("esbNm:{}, a empty cnode without attrs", esbNm);
					target.remove(esbNm); // 2012-07-10 如果需要过滤标签则删除原来值
				}
				else target.set(esbNm, targetNode);
			}
			else if (type == INode.TYPE_ARRAY)
			{
				IArrayNode node = src.findArray(esbNm, null);
				if (node == null || node.size() == 0) continue;
				IArrayNode targetNode = new ArrayNode();
				translateArray(subSchema, srcMsg, node, targetNode, target.newInstance(), esb2rcv,
						autoFilterUndefinedTag,
						StringX.nullity(path) ? vo.getEsbName() : path + '.' + vo.getEsbName());

				if (targetNode.size() == 0
						&& (targetNode.getExt() == null || targetNode.getExt().size() == 0))
				{ // 如果当前cnode节点没有任何子节点，也没有任何属性则过滤 2012-08-22
					log.debug("esbNm:{}, a empty cnode without attrs", esbNm);
					target.remove(esbNm); // 2012-08-22 如果需要过滤标签则删除原来值
				}
				else target.set(esbNm, targetNode);
			}
			else
			{ // atom
				IAtomNode node = src.findAtom(esbNm, null);
				if (!StringX.nullity(vo.getDefValue())
						&& ((node == null && MessageSchema.MO_OPTIONAL.equals(vo.getOptional())
								|| ((node == null || StringX.nullity(node.stringValue()))
										&& MessageSchema.MO_optional.equals(vo.getOptional())))))
				{ // 如果当前报文配置了默认值，而报文没有传此标签，或是小o类型报文却只传了一个空标签
					node = new AtomNode(vo.getDefValue());
					log.debug("set def value for [{}]={}", vo.getEsbName(), node);
				}
				else if (node == null && MessageSchema.MO_must.equals(vo.getOptional()))
				{ // 如果是小m类型，而没有输入此标签，则用默认值或者空字符串填充
					node = new AtomNode(StringX.null2emptystr(vo.getDefValue()).trim());
					log.debug("set m for [{}]=[{}]", vo.getEsbName(), node.stringValue());
				}
				else if (node == null && log.isDebugEnabled())
					log.debug("{} is null, optional:{}", vo.getEsbName(), vo.getOptional());
				// modified by chenjs 2011-07-20, 容许对不存在的标签进行ftl, cvt
				node = atomProcessor.process(srcMsg, node, vo, esb2rcv, src, path, target);

				// modified by chenjs 2012-12-01
				// 将此忽略标签代码放在processAtom之后，这样容许processAtom处理null标签
				// 如果当前子标签为空字符串标签，且为可选(O,o)标签，则过滤此标签
				if ((node == null || StringX.nullity(node.toString()))
						&& MessageSchema.MO_OPTIONAL.equalsIgnoreCase(vo.getOptional()))
				{
					log.debug("remove null tag({}), value:{}", esbNm,
							StringX.null2emptystr(target.getNode(esbNm)));
					target.remove(esbNm); // 2012-07-10 如果需要过滤标签则删除原来值
					continue;
				}
				if (node != null) target.set(esbNm, node);
			}
		}
	}

	public void translateArray(TreeNode msgStruct, IMessage srcMsg, IArrayNode src,
			IArrayNode target, ICompositeNode targetCN, boolean esb2rcv, String path)
					throws Exception
	{
		translateArray(msgStruct, srcMsg, src, target, targetCN, esb2rcv, false, path);
	}

	public void translateArray(TreeNode msgStruct, IMessage srcMsg, IArrayNode src,
			IArrayNode target, ICompositeNode targetCN, boolean esb2rcv,
			boolean autoFilterUndefinedTag, String path) throws Exception
	{
		List items = msgStruct.getChildren();
		// 1. 数组中的元素为原子类型, 而非结构类型
		if (items == null || items.size() == 0)
		{
			if (((MsgSchemaPO) msgStruct.getTreeNodeValue()).getFtyp()
					.endsWith(String.valueOf((char) INode.TYPE_UNDEFINED)))
			{ // 如果数组里面的元素是U类型，说明此节点不需要解析，直接放入，属于穿透类型
				for (int i = 0; i < src.size(); i++)
					target.add(src.getNode(i));
			}
			else
			{
				for (int i = 0; i < src.size(); i++)
					target.add(atomProcessor.process(srcMsg, (IAtomNode) src.getNode(i),
							(MsgSchemaPO) msgStruct.getTreeNodeValue(), esb2rcv, null,
							path + '[' + i + ']', null));
			}
			return;
		}
		// 2. 数组的元素为结构类型
		for (int i = 0; i < src.size(); i++)
		{
			INode n = src.getNode(i);
			// 2012-06-12, 有些情况报文返回<result></result>的空标签，而schema规范为AM。
			if (!(n instanceof ICompositeNode)) continue;
			ICompositeNode cnode = (ICompositeNode) n;
			ICompositeNode cn = targetCN.newInstance();

			// chenjs 2012-09-03 数组中的节点判断是否自动过滤，
			if (!autoFilterUndefinedTag) cn.set(cnode);
			cn.setExt(cnode.getExt());

			translateMap(msgStruct, srcMsg, cnode, cn, esb2rcv, autoFilterUndefinedTag,
					path + '[' + i + ']');
			// 700 2013-08-22 如果数组节点中的复杂节点是空标签，则不加入数组节点
			if (cn.size() > 0 || (cn.getExt() != null && cn.getExt().size() > 0)) target.add(cn);
		}
	}

	protected IAtomProcessor atomProcessor = new DefaultAtomProcessor();

	public void setAtomProcessor(IAtomProcessor atomProcessor)
	{
		this.atomProcessor = atomProcessor;
	}
}
