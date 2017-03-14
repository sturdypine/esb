package spc.esb.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.converter.NodeConverterFactory;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.exception.Status;
import spc.webos.util.StringX;

public class Message extends AbstractMessage
{
	private static final long serialVersionUID = 1L;
	protected ICompositeNode transaction;
	// protected ICompositeNode header;
	// protected ICompositeNode body;
	// protected ICompositeNode request;
	// protected ICompositeNode response;
	protected MessageAttr attr; // 当前报文属性
	protected MessageAttr refAttr; // 当前参考报文属性
	protected byte[] correlationID;
	protected int expirySeconds;
	protected long start; // 开始处理时间
	protected long end; // 结束处理时间

	// 流程使用
	protected Map local = new HashMap(); // 消息报文本地环境
	protected boolean containExFnode; // 是否有异常处理链
	protected Throwable ex; // 处理中的异常
	protected boolean exitFlow = false; // 是否立刻终止流程

	public String getOriginalBytesPlainStr()
	{
		return StringX.trim(StringX.null2emptystr(transaction.get(IMessage.TAG_ORIGINALBYTES)));
	}

	public void setOriginalBytesPlainStr(String original)
	{
		if (!StringX.nullity(original)) transaction.set(IMessage.TAG_ORIGINALBYTES, original);
		else transaction.remove(IMessage.TAG_ORIGINALBYTES);
	}

	public byte[] getOriginalBytes()
	{
		IAtomNode node = (IAtomNode) transaction.get(IMessage.TAG_ORIGINALBYTES);
		if (node != null) return node.byteValue();
		return null;
	}

	public void setOriginalBytes(byte[] original)
	{
		if (original == null) transaction.remove(IMessage.TAG_ORIGINALBYTES);
		else transaction.set(IMessage.TAG_ORIGINALBYTES, new AtomNode(original));
	}

	public MessageAttr getAttr()
	{
		return attr;
	}

	public void setAttr(MessageAttr attr)
	{
		this.attr = attr;
	}

	public MessageAttr getRefAttr()
	{
		return refAttr;
	}

	public void setRefAttr(MessageAttr refAttr)
	{
		this.refAttr = refAttr;
	}

	public String getVersion()
	{
		return StringX.trim(StringX.null2emptystr(findInHeader(IMessage.TAG_HEADER_VERSION)));
	}

	public void setVersion(String version)
	{
		setInHeader(Message.TAG_HEADER_VERSION, version);
	}

	public long getStart()
	{
		return start;
	}

	public void setStart(long start)
	{
		this.start = start;
	}

	public long getEnd()
	{
		return end;
	}

	public void setEnd(long end)
	{
		this.end = end;
	}

	public String getSndDt()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_SNDDT)));
	}

	public void setSndDt(String sndDt)
	{
		getHeader().set(TAG_SNDDT, sndDt);
	}

	public String getSndTm()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_SNDTM)));
	}

	public void setSndTm(String sndTm)
	{
		getHeader().set(TAG_SNDTM, sndTm);
	}

	public String getRcvAppSN()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_RCVAPPSN)));
	}

	public void setRcvAppSN(String rcvAppSN)
	{
		getHeader().set(TAG_HEADER_MSG_RCVAPPSN, rcvAppSN);
	}

	public String getSeqNb()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_SN)));
	}

	public void setSeqNb(String seqNb)
	{
		getHeader().set(TAG_HEADER_MSG_SN, seqNb);
	}

	public String getRefSndDt()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_REFSNDDT)));
	}

	public void setRefSndDt(String refSndDt)
	{
		getHeader().set(TAG_HEADER_MSG_REFSNDDT, refSndDt);
	}

	public void setRefSndNode(String refSndNode)
	{
		getHeader().set(TAG_HEADER_MSG_REFSNDNODE, refSndNode);
	}

	public void setRefSndApp(String refSndApp)
	{
		getHeader().set(TAG_HEADER_MSG_REFSNDAPP, refSndApp);
	}

	public void setRcvNode(String rcvNode)
	{
		getHeader().set(TAG_HEADER_MSG_RCVNODE, rcvNode);
	}

	public void setSndNode(String sndNode)
	{
		getHeader().set(TAG_HEADER_MSG_SNDNODE, sndNode);
	}

	public void setSndAppCd(String sndAppCd)
	{
		getHeader().set(TAG_HEADER_MSG_SNDAPP, sndAppCd);
	}

	public void setRcvAppCd(String rcvAppCd)
	{
		getHeader().set(TAG_HEADER_MSG_RCVAPP, rcvAppCd);
	}

	public String getRefSeqNb()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_REFSNDSN)));
	}

	public void setRefSeqNb(String refSeqNb)
	{
		getHeader().set(TAG_HEADER_MSG_REFSNDSN, refSeqNb);
	}

	public String getSndNode()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_SNDNODE)));
	}

	public String getRcvNode()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_RCVNODE)));
	}

	public String getSndApp()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_SNDAPP)));
	}

	public String getRefSndApp()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_REFSNDAPP)));
	}

	public String getRcvApp()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_RCVAPP)));
	}

	public String getRefSndNode()
	{
		return StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_REFSNDNODE)));
	}

	public String getReplyToQ()
	{
		String replyToQ = StringX
				.trim(StringX.null2emptystr(getHeader().get(IMessage.TAG_HEADER_MSG_REPLYTOQ)));
		if (!StringX.nullity(replyToQ)) return replyToQ;
		ICompositeNode ext = getHeaderExt();
		if (ext == null) return StringX.EMPTY_STRING;
		return StringX.null2emptystr(ext.findAtom(IMessage.TAG_HEADER_MSG_REPLYTOQ, null));
	}

	public void setReplyToQ(String replyToQ)
	{
		getHeader().set(TAG_HEADER_MSG_REPLYTOQ, replyToQ);
	}

	public String getSignature()
	{
		// IAtomNode node = (IAtomNode)
		// transaction.getNode(IMessage.TAG_SIGNATURE);
		// String sig = node == null ? StringX.EMPTY_STRING :
		// StringX.trim(node.toString());
		// 700 2013-05-09 签名信息调整到header
		String sig = StringX.null2emptystr(findInHeader(IMessage.TAG_SIGNATURE));
		// added by chenjs 2011-11-10. 去掉签名信息中的\t, \n, 空格
		return StringX.replaceAll(StringX.replaceAll(StringX.replaceAll(sig, "\t", ""), "\n", ""),
				" ", "");
	}

	public void setSignature(String signature)
	{
		setInHeader(Message.TAG_SIGNATURE, signature); // 将signature调整到header下
		// transaction.set(Message.TAG_SIGNATURE, signature);
	}

	public String getLocation()
	{
		return StringX.null2emptystr(transaction.getNode(IMessage.TAG_LOCATION));
	}

	public void setLocation(String location)
	{
		transaction.set(Message.TAG_LOCATION, location);
	}

	public void setCn2utf8(boolean cn2utf8)
	{
		setInHeader(TAG_CN2UTF8, cn2utf8 ? AtomNode.TRUE : AtomNode.FALSE);
	}

	// 基本数据类型
	public boolean isCn2utf8()
	{
		if (getHeader() == null) return false;
		return ((IAtomNode) getHeader().find(TAG_HEADER, TAG_CN2UTF8, INode.TYPE_BOOL,
				AtomNode.FALSE)).booleanValue();
	}

	public byte[] getCorrelationID()
	{
		return correlationID;
	}

	public void setCorrelationID(byte[] correlationID)
	{
		this.correlationID = correlationID;
	}

	public int getExpirySeconds()
	{
		return expirySeconds;
	}

	public void setExpirySeconds(int expirySeconds)
	{
		this.expirySeconds = expirySeconds;
	}

	public Object getBplVariable(String key)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_BPL_VARIABLES));
		if (variables != null) return variables.get(key);
		return null;
	}

	public void setBplVariable(String key, Object value)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_BPL_VARIABLES));
		if (variables != null) variables.put(key, value);
	}

	public void removeBplVariable(String key)
	{
		Map variables = (Map) getInLocal(ESBMsgLocalKey.LOCAL_BPL_VARIABLES);
		if (variables != null) variables.remove(key);
	}

	public Object getMQAttribute(String key)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_MQ_ATTRIBUTE));
		if (variables != null) return variables.get(key);
		return null;
	}

	public void setMQAttribute(String key, Object value)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_MQ_ATTRIBUTE));
		if (variables != null) variables.put(key, value);
	}

	public void removeMQAttribute(String key)
	{
		Map variables = (Map) getInLocal(ESBMsgLocalKey.LOCAL_MQ_ATTRIBUTE);
		if (variables != null) variables.remove(key);
	}

	public Object getMBAttribute(String key)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_MB_ATTRIBUTE));
		if (variables != null) return variables.get(key);
		return null;
	}

	public void setMBAttribute(String key, Object value)
	{
		Map variables = (Map) (getInLocal(ESBMsgLocalKey.LOCAL_MB_ATTRIBUTE));
		if (variables != null) variables.put(key, value);
	}

	public void removeMBAttribute(String key)
	{
		Map variables = (Map) getInLocal(ESBMsgLocalKey.LOCAL_MB_ATTRIBUTE);
		if (variables != null) variables.remove(key);
	}

	public String getRefMsgCd()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().getNode(TAG_HEADER_MSG_REFMSGCD)));
	}

	public void setRefMsgCd(String refMsgCd)
	{
		getHeader().set(TAG_HEADER_MSG_REFMSGCD, refMsgCd);
	}

	public String getMsgCd()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(TAG_HEADER_MSG_CD)));
	}

	public void setMsgCd(String msgCd)
	{
		getHeader().set(TAG_HEADER_MSG_CD, msgCd);
	}

	public String getReplyMsgCd()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().get(TAG_HEADER_REPLYMSGCD)));
	}

	public void setReplyMsgCd(String replyMsgCd)
	{
		getHeader().set(TAG_HEADER_REPLYMSGCD, replyMsgCd);
	}

	public String getCallType()
	{
		String callType = StringX
				.trim(StringX.null2emptystr(getHeader().getNode(TAG_HEADER_MSG_REFCALLTYP)));
		if (!StringX.nullity(callType)) return callType; // 为了兼容以前中德的情况，优先使用refcalltype。2013-08-10
		// 700 2013-06-01 兼容refcalltype
		return StringX.trim(StringX.null2emptystr(getHeader().getNode(TAG_HEADER_CALLTYPE)));
	}

	public void setCallType(String callType)
	{
		getHeader().set(TAG_HEADER_CALLTYPE, callType);
	}

	public String getRefCallType()
	{
		return StringX.trim(StringX.null2emptystr(getHeader().getNode(TAG_HEADER_MSG_REFCALLTYP)));
	}

	public void setRefCallType(String callType)
	{
		if (StringX.nullity(callType)) getHeader().remove(TAG_HEADER_MSG_REFCALLTYP);
		else getHeader().set(TAG_HEADER_MSG_REFCALLTYP, callType);
	}

	public void setSn(String sn)
	{
		getHeader().set(TAG_HEADER_MSG_SN, sn);
	}

	public void mustContain(Collection paths)
	{
		if (paths == null) return;
		Iterator p = paths.iterator();
		while (p.hasNext())
		{
			String path = (String) p.next();
			transaction.find(StringX.EMPTY_STRING, path, INode.TYPE_UNDEFINED, false);
		}
	}

	public void mustContain(String[] paths)
	{
		if (paths == null) return;
		for (int i = 0; i < paths.length; i++)
			transaction.find(StringX.EMPTY_STRING, paths[i], INode.TYPE_UNDEFINED, false);
	}

	public void setStatus(Status status)
	{
		getHeader().set(IMessage.TAG_HEADER_STATUS, status);
	}

	// 基本数据 end

	// msg, pnt, esb(request,response)
	public ICompositeNode getHeaderExt()
	{
		INode ext = getHeader().find(TAG_HEADER_EXT);
		// 700, chenjs 2013-09-22 如果为空则创建一个headerext环境
		if (ext == null) return null;
		if (ext instanceof ICompositeNode) return (ICompositeNode) ext;
		return null;
		// try
		// {
		// return XMLConverter2.getInstance().deserialize2composite(
		// StringX.decodeBase64(ext.toString().getBytes()));
		// }
		// catch (Exception e)
		// { // 752, 容许ext标签不是xml信息
		// return null;
		// }
	}

	public void setHeaderExt(ICompositeNode hdrExt)
	{
		getHeader().set(TAG_HEADER_EXT, hdrExt);
	}

	public String getHeaderExtStr()
	{
		INode ext = getHeader().find(TAG_HEADER_EXT);
		if (ext == null) return null;
		if (ext instanceof IAtomNode) return ext.toString();
		return null;
	}

	public void setHeaderExt(String hdrExt)
	{
		getHeader().set(TAG_HEADER_EXT, hdrExt);
	}

	public void setInHeaderExt(String key, Object val)
	{
		getHeader().create(TAG_HEADER_EXT).set(key, val);
	}

	public INode findInHeaderExt(String key)
	{
		return getHeader().create(TAG_HEADER_EXT).find(key);
	}

	public ICompositeNode getMsgLocal()
	{
		return transaction.create(TAG_LOCAL);
	}

	public ICompositeNode setMsgLocal(ICompositeNode local)
	{
		if (local == null || local.size() == 0)
		{
			transaction.remove(TAG_LOCAL);
			return null;
		}
		return (ICompositeNode) transaction.set(TAG_LOCAL, local);
	}

	// public ICompositeNode getPnt()
	// {
	// return header.create(TAG_HEADER_PNT);
	// }
	//
	// public ICompositeNode setPnt(ICompositeNode pnt)
	// {
	// return (ICompositeNode) header.set(TAG_HEADER_PNT, pnt);
	// }

	// msg, pnt, esb(request,response) end...

	public void setTransaction(InputStream is)
	{
		try
		{
			transaction = SOAPConverter.getInstance().deserialize(is).getTransaction();
			init();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void setTransaction(ICompositeNode transaction)
	{
		this.transaction = transaction;
		if (transaction != null) init();
	}

	public INode find(String path)
	{
		return transaction.find(path);
	}

	public IAtomNode findAtom(String path, IAtomNode def)
	{
		return transaction.findAtom(path, def);
	}

	public IArrayNode findArray(String path, IArrayNode def)
	{
		return transaction.findArray(path, def);
	}

	public ICompositeNode findComposite(String path, ICompositeNode def)
	{
		return transaction.findComposite(path, def);
	}

	public INode findIgnoreCase(String path)
	{
		return transaction.findIgnoreCase(path);
	}

	public INode find(String parentPath, ICompositeNode parent, String path, byte type,
			boolean canNull)
	{
		return parent.find(parentPath, path, type, canNull);
	}

	public void init()
	{
		if (transaction == null) transaction = new CompositeNode();
		// header = (ICompositeNode) transaction.find(TAG_HEADER);
		// if (header == null) header = transaction.create(TAG_HEADER);
		// body = (ICompositeNode) transaction.findComposite(TAG_BODY, new
		// CompositeNode());
		// transaction.set(TAG_BODY, body); // added by chenjs 2011-12-30 回填

		// request = (ICompositeNode) transaction.findComposite(TAG_BODY, new
		// CompositeNode());
		// // if (request == null) request = transaction.create(PATH_REQUEST);
		// transaction.set(TAG_BODY, request); // added by chenjs 2011-12-30 回填
		//
		// response = (ICompositeNode) transaction.findComposite(TAG_BODY, new
		// CompositeNode());
		// // if (response == null) response =
		// transaction.create(PATH_RESPONSE);
		// transaction.set(TAG_BODY, response); // added by chenjs
		// 2011-12-30 回填

		if (!local.containsKey(ESBMsgLocalKey.LOCAL_MQ_ATTRIBUTE))
			local.put(ESBMsgLocalKey.LOCAL_MQ_ATTRIBUTE, new HashMap());
		if (!local.containsKey(ESBMsgLocalKey.LOCAL_MB_ATTRIBUTE))
			local.put(ESBMsgLocalKey.LOCAL_MB_ATTRIBUTE, new HashMap());
		if (!local.containsKey(ESBMsgLocalKey.LOCAL_BPL_VARIABLES))
			local.put(ESBMsgLocalKey.LOCAL_BPL_VARIABLES, new HashMap());
	}

	public Map getLocal()
	{
		return local;
	}

	public void clearLocal()
	{
		local.clear();
	}

	public Object getInLocal(String key)
	{
		return local.get(key);
	}

	public Object getInLocal(String key, Object obj)
	{
		Object o = local.get(key);
		if (o == null) return o;
		if (o instanceof INode) NodeConverterFactory.getInstance().pack((INode) o, obj, null);
		return o;
	}

	public void setInLocal(String key, Object value)
	{
		local.put(key, value);
	}

	public ICompositeNode getHeader()
	{
		return (ICompositeNode) transaction.getNode(TAG_HEADER);
	}

	public ICompositeNode setBody(ICompositeNode body)
	{
		transaction.set(TAG_BODY, body == null ? new CompositeNode() : body);
		return body;
	}

	public ICompositeNode getBody()
	{
		INode node = transaction.find(TAG_BODY);
		if (node instanceof ICompositeNode) return (ICompositeNode) node;
		ICompositeNode body = new CompositeNode();
		transaction.set(TAG_BODY, body);
		return body;
	}

	public ICompositeNode getRequest()
	{
		return getBody();
	}

	public ICompositeNode getResponse()
	{
		return getBody();
	}

	public INode findInHeader(String path)
	{
		return getHeader().find(path);
	}

	public Object getInHeader(String path)
	{
		return getHeader().get(path);
	}

	public INode findInRequest(String path)
	{
		return getRequest().find(path);
	}

	public IAtomNode findAtomInRequest(String path, IAtomNode def)
	{
		return getRequest().findAtom(path, def);
	}

	public IArrayNode findArrayInRequest(String path, IArrayNode def)
	{
		return getRequest().findArray(path, def);
	}

	public ICompositeNode findCompositeInRequest(String path, ICompositeNode def)
	{
		return getRequest().findComposite(path, def);
	}

	public IAtomNode findAtomInResponse(String path, IAtomNode def)
	{
		return getResponse().findAtom(path, def);
	}

	public IArrayNode findArrayInResponse(String path, IArrayNode def)
	{
		return getResponse().findArray(path, def);
	}

	public ICompositeNode findCompositeInResponse(String path, ICompositeNode def)
	{
		return getResponse().findComposite(path, def);
	}

	public Object getInRequest(String path)
	{
		return getRequest().get(path);
	}

	public INode findInResponse(String path)
	{
		return getResponse().find(path);
	}

	public Object getInResponse(String path)
	{
		return getResponse().get(path);
	}

	public void setInHeader(String key, Object value)
	{
		getHeader().set(key, value);
	}

	public void setInRequest(String key, Object value)
	{
		getRequest().set(key, value);
	}

	public void setInResponse(String key, Object value)
	{
		getResponse().set(key, value);
	}

	public void clearRequest()
	{
		if (getRequest() != null) getRequest().clear();
	}

	public void clearHeader()
	{
		((ICompositeNode) (transaction.find(TAG_HEADER))).clear();
	}

	public String toString()
	{
		return toXml(true);
	}

	public ICompositeNode getTransaction()
	{
		return transaction;
	}

	public Throwable getEx()
	{
		return ex;
	}

	public void setEx(Throwable ex)
	{
		this.ex = ex;
	}

	public ICompositeNode createInResponse(String path)
	{
		return getResponse().create(path);
	}

	public ICompositeNode createInResquest(String path)
	{
		return getRequest().create(path);
	}

	public ICompositeNode createInHeader(String path)
	{
		return getHeader().create(path);
	}

	public boolean isContainExFnode()
	{
		return containExFnode;
	}

	public void setContainExFnode(boolean containExFnode)
	{
		this.containExFnode = containExFnode;
	}

	public boolean isExitFlow()
	{
		return exitFlow;
	}

	public void setExitFlow(boolean exitFlow)
	{
		this.exitFlow = exitFlow;
	}

	public Object findInHeader(String path, Object target)
	{
		return getHeader().find(path, target);
	}

	public Object findInRequest(String path, Object target)
	{
		return getRequest().find(path, target);
	}

	public Object findInResponse(String path, Object target)
	{
		return getResponse().find(path, target);
	}

	public ICompositeNode setHeader(ICompositeNode header)
	{
		transaction.set(TAG_HEADER, header);
		return header;
	}

	public ICompositeNode setRequest(ICompositeNode request)
	{
		transaction.set(TAG_BODY, request);
		return request;
	}

	public ICompositeNode setResponse(ICompositeNode response)
	{
		transaction.set(TAG_BODY, response);
		return response;
	}

	public Message()
	{
		this(new CompositeNode());
		setHeader(new CompositeNode());
		setBody(new CompositeNode());
		init();
	}

	public Message(ICompositeNode transaction)
	{
		this.transaction = transaction;
		if (getHeader() == null) setHeader(new CompositeNode());
		if (getBody() == null) setBody(new CompositeNode());
		init();
	}

	public Message(byte[] xml)
	{
		this(new ByteArrayInputStream(xml, 0, xml.length));
	}

	public Message(byte[] xml, boolean gzip) throws IOException
	{
		this(gzip
				? (InputStream) (new GZIPInputStream(new ByteArrayInputStream(xml, 0, xml.length)))
				: (InputStream) (new ByteArrayInputStream(xml, 0, xml.length)));
	}

	public Message(byte[] xml, int start, int len)
	{
		this(new ByteArrayInputStream(xml, start, len));
	}

	public Message(InputStream iis)
	{
		try (InputStream is = iis)
		{
			transaction = SOAPConverter.getInstance().deserialize(is).getTransaction();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		init();
	}

	// public IMessage newInstance()
	// {
	// Message msg = new Message();
	// msg.init();
	// return msg;
	// }
}
