package spc.esb.data.util;

import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;

/**
 * 将报文中属性和tag标签进行相互转换， 此时ESB标准schema结构是标签模式，服务系统是属性模式 2011-12-30
 * 
 * @author chenjs
 * 
 */
public class Tag2AttrAtomConverter extends AtomConverter
{
	public IAtomNode converter(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		return esb2rcv ? tag2attr(msg, src, schema, esb2rcv, pnode, path, tpnode) : attr2tag(msg,
				src, schema, esb2rcv, pnode, path, tpnode);
	}

	// 当前报文节点含有属性，目标使用标签
	public IAtomNode attr2tag(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		// ext1 配置模式为amt:Ccy, amt节点的Ccy属性
		String attrNodeNm = null;
		String attrNm = null; // 属性名
		if (schema.getExt1().indexOf(':') > 0)
		{
			String[] nodeattr = StringX.split(schema.getExt1(), ":");
			attrNodeNm = nodeattr[0]; // 在父节点中属性节点名
			attrNm = nodeattr[1]; // 属性名
		}
		else attrNm = schema.getExt1(); // 如果ext1里面不含有:, 则表示当前属性属于父节点
		INode attrNode = StringX.nullity(attrNodeNm) ? tpnode : tpnode.getNode(attrNodeNm);
		if (attrNode == null)
		{
			if (log.isDebugEnabled()) log.debug("attrNode is null by:" + attrNodeNm + ",tpnode:"
					+ tpnode);
			return null;
		}
		Object attr = attrNode.getExt(attrNm);
		return attr == null ? null : new AtomNode(StringX.null2emptystr(attr));
	}

	// 当前报文节点使用的是标签, 需要绑定到一个属性上
	public IAtomNode tag2attr(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		if (src == null) return null;
		// ext1 配置模式为amt:Ccy, amt节点的Ccy属性
		String attrNodeNm = null;
		String attrNm = null; // 属性名
		if (schema.getExt1().indexOf(':') > 0)
		{
			String[] nodeattr = StringX.split(schema.getExt1(), ":");
			attrNodeNm = nodeattr[0]; // 在父节点中属性节点名
			attrNm = nodeattr[1]; // 属性名
		}
		else attrNm = schema.getExt1(); // 如果ext1里面不含有:, 则表示当前属性属于父节点
		INode attrNode = StringX.nullity(attrNodeNm) ? tpnode : tpnode.getNode(attrNodeNm);
		if (attrNode == null)
		{
			if (log.isDebugEnabled()) log.debug("attrNode is null by:" + attrNodeNm + ",tpnode:"
					+ tpnode);
			return null; // 返回null表示此节点未来不能放入tpnode
		}
		attrNode.setExt(attrNm, src.stringValue());
		if (log.isDebugEnabled()) log.debug("tag2attr:attrNodeNm:" + attrNodeNm + ",attrNm:"
				+ attrNm);
		return null; // 返回null表示此节点未来不能放入tpnode
	}

	public Tag2AttrAtomConverter()
	{
		name = "tag2attr";
	}
}
