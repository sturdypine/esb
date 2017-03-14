package spc.esb.converter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import spc.esb.common.service.ESBInfoService;
import spc.esb.common.service.MsgDefService;
import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.util.DefaultAtomProcessor;
import spc.esb.model.MessagePO;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;

/**
 * ESB原子节点转换, 主要用于fvmapping
 * 
 * @author chenjs
 * 
 */
public class ESBAtomProcessor extends DefaultAtomProcessor
{
	public IAtomNode process(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		if (log.isTraceEnabled()) log.trace("schema: " + schema + ", src:" + src);
		src = super.process(srcmsg, src, schema, esb2rcv, pnode, path, tpnode);

		return fvmapping(srcmsg, src, schema, esb2rcv);
	}

	protected IAtomNode fvmapping(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema,
			boolean esb2rcv)
	{
		// 是否需要数据字典转换
		if (esbInfoService == null || src == null || StringX.nullity(schema.getFvMapId())) return src;
		if (log.isDebugEnabled()) log.debug("fvmapping: " + schema.getFvMapId() + ", value:"
				+ StringX.null2emptystr(src));
		// modified by chenjs. 增加一个trim动作。防止字符串值里面有空格影响mapping
		return new AtomNode(esbInfoService.getFvMapping(schema.getFvMapId(),
				srcmsg.getSndNodeApp(), srcmsg.getRcvNodeApp(), src.stringValue().trim()));
	}

	public IAtomNode ftl(IMessage srcmsg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, String ftl) throws Exception
	{
		Map root = new HashMap();
		String msgCd = srcmsg.getMsgCd();
		MessagePO msgVO = msgDefService.getMessage(msgCd);
		root.put("msgVO", msgVO);
		return ftl(srcmsg, src, schema, esb2rcv, pnode, path, ftl, root);
	}
	
	@Resource
	protected ESBInfoService esbInfoService;
	@Resource
	protected MsgDefService msgDefService;

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}

	public void setMsgDefService(MsgDefService msgDefService)
	{
		this.msgDefService = msgDefService;
	}
}
