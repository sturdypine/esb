package spc.esb.data.util;

import java.util.List;

import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;

/**
 * 将报文中属性和tag标签进行相互转换， 此时ESB标准schema结构是属性模式，服务系统是标签模式 2011-12-30
 * 
 * @author chenjs
 * 
 */
public class Attr2TagAtomConverter extends AtomConverter
{
	public IAtomNode converter(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		return esb2rcv ? attr2tag(msg, src, schema, esb2rcv, pnode, path, tpnode) : tag2attr(msg,
				src, schema, esb2rcv, pnode, path, tpnode);
	}

	// 当前报文节点含有属性，目标使用标签
	public IAtomNode attr2tag(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		List<MsgSchemaPO> attrSchemas = schema.getAttributes();// 得到当前原子节点所有配置的属性列表
		if (attrSchemas == null || attrSchemas.size() == 0 || src.getExt() == null
				|| src.getExt().size() == 0) return src;

		for (int i = 0; i < attrSchemas.size(); i++)
		{
			MsgSchemaPO attrSchema = attrSchemas.get(i);
			String attrValue = (String) src.getExt(attrSchema.getEsbName());
			if (StringX.nullity(attrValue)) continue;
			if (log.isDebugEnabled()) log.debug("attr2tag:attr:" + attrSchema.getEsbName()
					+ ",tag.ext1: " + attrSchema.getExt1() + ",val:" + attrValue);
			tpnode.set(attrSchema.getExt1(), attrValue);
		}
		return new AtomNode(src.stringValue());
	}

	// 当前报文节点使用的是标签, 需要绑定到一个属性上
	public IAtomNode tag2attr(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		List<MsgSchemaPO> attrSchemas = schema.getAttributes();// 得到当前原子节点所有配置的属性列表
		if (attrSchemas == null || attrSchemas.size() == 0) return src;
		IAtomNode atom = new AtomNode(src.stringValue());
		for (int i = 0; i < attrSchemas.size(); i++)
		{
			MsgSchemaPO attrSchema = attrSchemas.get(i);
			IAtomNode attrValue = pnode.findAtom(attrSchema.getExt1(), null); // 通过ext1配置的值，在当前父节点中找到属性标签值
			if (attrValue == null) continue;
			if (log.isDebugEnabled()) log.debug("tag2attr:attr:" + attrSchema.getEsbName()
					+ ",tag.ext1: " + attrSchema.getExt1() + ",val:" + attrValue);
			atom.setExt(attrSchema.getEsbName(), attrValue.stringValue()); // 将当前原子标签设置属性值
		}
		return atom;
	}

	public Attr2TagAtomConverter()
	{
		name = "attr2tag";
	}
}
