package spc.esb.data.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.FTLUtil;
import spc.webos.util.StringX;

public class DefaultAtomProcessor implements IAtomProcessor
{
	protected Logger log = LoggerFactory.getLogger(getClass());

	public IAtomNode process(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		// 是否需要指定的转换器进行转换
		if (!StringX.nullity(schema.getCvtr()))
		{ // modifeid by chenjs 2011-12-07 支持同时使用几个cvt，按配置先后顺序执行
			String[] cvtrs = StringX.split(schema.getCvtr(), StringX.COMMA);
			for (int i = 0; i < cvtrs.length; i++)
				src = cvt(srcmsg, src, schema, esb2rcv, pnode, path, cvtrs[i], tpnode);
		}
		// 是否需要FTL转换
		else if (!StringX.nullity(schema.getFtl())) src = ftl(srcmsg, src, schema, esb2rcv, pnode,
				path, schema.getFtl());
		if (src == null) return null;
		// 如果是double类型，则自动给小数点补0
		if (schema.getFtyp().equalsIgnoreCase(String.valueOf((char) INode.TYPE_DOUBLE))
				&& schema.getDeci().intValue() > 0) src = new AtomNode(StringX.float2str(
				src.toString(), schema.getDeci().intValue()), src.getExt());
		return src;
	}

	public IAtomNode cvt(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, String cvtr, ICompositeNode tpnode) throws Exception
	{
		IAtomConverter converter = (IAtomConverter) IAtomConverter.CONVERTERS.get(cvtr);
		if (log.isDebugEnabled()) log.debug("cvtr:" + cvtr + ", for " + schema.getEsbName());
		// if (converter == null) throw new
		// AppException(AppRetCode.NO_ATOMCONVERTER(),
		// new Object[] { cvtr });
		if (converter == null)
		{ // modified by chenjs 2012-01-10 转换器如果不存在只警告, 不执行异常
			log.warn("cvtr is null by:[" + cvtr + "]!!!");
			return src;
		}
		src = converter.converter(srcmsg, src, schema, esb2rcv, pnode, path, tpnode);
		if (src == null) log.debug("after cvtr value is null!!!");
		else if (log.isDebugEnabled()) log.debug("after cvtr:" + src.stringValue());
		return src;
	}

	public IAtomNode ftl(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, String ftl) throws Exception
	{
		return ftl(srcmsg, src, schema, esb2rcv, pnode, path, ftl, new HashMap());
	}

	protected IAtomNode ftl(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, String ftl, Map root) throws Exception
	{
		if (log.isDebugEnabled()) log.debug("ftl:" + ftl + ", for " + schema.getEsbName());
		MsgFTLUtil.model(root, srcmsg);
		root.put("schema", schema);
		root.put("pnode", pnode);
		root.put("path", path);
		root.put("curvalue", StringX.null2emptystr(src));
		root.put("esb2rcv", new Boolean(esb2rcv));
		try
		{
			String strvalue = StringX.trim(FTLUtil.freemarker(ftl, root));
			if (log.isDebugEnabled()) log.debug("after ftl : " + strvalue);
			return new AtomNode(strvalue, src == null ? null : src.getExt());
		}
		catch (Exception e)
		{
			log.warn("FTL(" + ftl + ")", e);
			throw e;
		}
	}
}
