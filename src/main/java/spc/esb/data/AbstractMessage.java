package spc.esb.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import spc.esb.data.converter.SOAPConverter;
import spc.webos.constant.AppRetCode;
import spc.webos.constant.Common;
import spc.webos.exception.Status;
import spc.webos.util.JsonUtil;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;

/**
 * 增加一个抽象Message类，便于扩展IMessage接口
 * 
 * @author spc
 * 
 */
public abstract class AbstractMessage implements IMessage
{
	private static final long serialVersionUID = 1L;

	// 800
	public void hdrExt2str() throws Exception
	{
		ICompositeNode ext = getHeaderExt();
		if (ext == null || ext.size() == 0) return;
		// 809_20151105 header/ext字段使用json格式序列化, 优先使用json格式
		setHeaderExt(JsonUtil.obj2json(ext));
		/*
		 * setHeaderExt(HEADER_EXT_PREFIX +
		 * StringX.base64(ext.toXml(IMessage.TAG_HEADER_EXT, false,
		 * Array2Node2XML.getInstance()).getBytes(Common.CHARSET_UTF8)));
		 */
	}

	public void hdrExt2cnode() throws Exception
	{
		String ext = getHeaderExtStr();
		if (StringX.nullity(ext)) return;
		// 809_20151105 header/ext字段使用json格式序列化
		ext = StringX.trim(ext);
		if (ext.startsWith(HEADER_EXT_PREFIX))
		{ // 如果是base64:开头
			setHeaderExt(SOAPConverter.getInstance().deserialize2composite(
					StringX.decodeBase64(ext.substring(HEADER_EXT_PREFIX.length()))));
		}
		else if (ext.startsWith("{"))
		{
			try
			{
				setHeaderExt(new CompositeNode((Map) JsonUtil.json2obj(ext)));
			}
			catch (Exception e)
			{
			}
		}
	}

	// 752 处理header/ext

	public boolean isCanResponse()
	{
		if (!isRequestMsg()) return true;
		return !(StringX.nullity(getSndNodeApp()) || StringX.nullity(getSndDt())
				|| StringX.nullity(getSeqNb()));
	}

	public String getLang()
	{
		return StringX.null2emptystr(getHeader().findAtom(TAG_HEADER_LANGUAGE, null));
	}

	public void setLang(String lang)
	{
		ICompositeNode hdrmsg = getHeader();
		hdrmsg.set(TAG_HEADER_LANGUAGE, lang);
		setHeader(hdrmsg);
	}

	public boolean isRequestMsg()
	{
		// modified by chenjs 2011-09-23 重大修改:
		// 将判断是否是请求报文从原来的看是否报文编号最后一位为0变为看报文中是否有关联流水号
		// return getMsgCd().endsWith(Common.ESB_REQMSG_END);
		return StringX.nullity(getRefSeqNb());
	}

	public Status getStatus()
	{
		ICompositeNode statusCN = (ICompositeNode) getHeader().find(IMessage.TAG_HEADER_STATUS);
		// aded by chenjs 2011-11-16. 如果应答报文中没有status标签，则ESB会默认为成功
		if (!isRequestMsg() && statusCN == null)
		{
			Status s = new Status();
			s.setRetCd(AppRetCode.SUCCESS);
			s.setLocation("Message.getStatus");
			s.setIp(SpringUtil.getLocalHostIP());
			s.setAppCd(Common.APP_CD_ESB);
			return s;
		}
		return status(statusCN);
	}
	
	protected Status status(ICompositeNode cn)
	{
		Status s = new Status();
		if (cn == null) return s;
		IAtomNode an = (IAtomNode) cn.find(Status.RETCD);
		if (an != null) s.retCd = an.stringValue();
		an = (IAtomNode) cn.find(Status.DESC);
		if (an != null) s.desc = an.stringValue();
		an = (IAtomNode) cn.find(Status.LOC);
		if (an != null) s.location = an.stringValue();
		an = (IAtomNode) cn.find(Status.APPCD);
		if (an != null) s.appCd = an.stringValue();

		an = (IAtomNode) cn.find(Status.NODE);
		if (an != null) s.mbrCd = an.stringValue();
		an = (IAtomNode) cn.find(Status.IP);
		if (an != null) s.ip = an.stringValue();
		return s;
	}

	public boolean isSynCall()
	{
		return !CALLTYP_ASYN.equalsIgnoreCase(getCallType());
		// return isRequestMsg() ? !CALLTYP_ASYN.equalsIgnoreCase(getCallType())
		// : !CALLTYP_ASYN
		// .equalsIgnoreCase(getRefCallType());
	}

	public String getRefMsgSn()
	{
		return StringX.trim(StringX.null2emptystr(getRefSndNodeApp())) + '-' + getRefSndDt() + '-'
				+ getRefSeqNb();
	}

	public String getMsgSn()
	{
		return StringX.trim(StringX.null2emptystr(getSndNodeApp())) + '-' + getSndDt() + '-'
				+ getSeqNb();
	}

	public String getSndNodeApp()
	{
		String sndNode = getSndNode();
		return sndNode + getSndApp();
	}

	public String getRcvNodeApp()
	{
		String rcvNode = getRcvNode();
		return rcvNode + getRcvApp();
	}

	public String getRefSndNodeApp()
	{
		String refSndNode = getRefSndNode();
		return refSndNode + getRefSndApp();
	}

	public byte[] getOriginalBytes()
	{
		String original = getOriginalBytesPlainStr();
		if (StringX.nullity(original)) return null;
		return StringX.decodeBase64(original.getBytes());
	}

	public void setOriginalBytes(byte[] original)
	{
		if (original == null || original.length == 0) setOriginalBytesPlainStr(null);
		else setOriginalBytesPlainStr(new String(StringX.encodeBase64(original)));
	}

	public String getFixedErrDesc()
	{
		return getFixedErrDesc(this);
	}

	public String getFixedErrDesc(IMessage msg)
	{
		StringBuffer buf = new StringBuffer(100);
		buf.append('[');
		buf.append(msg.isRequestMsg() ? msg.getMsgSn() : msg.getRefSeqNb());
		// buf.append('|');
		// buf.append(msg.getRefMsgCd());
		buf.append('|');
		buf.append(msg.getMsgCd());
		buf.append(']');
		buf.append(' ');
		return buf.toString();
	}

	public IAtomNode findAtomInMsgLocal(String path, IAtomNode def)
	{
		ICompositeNode local = getMsgLocal();
		return local == null ? null : local.findAtom(path, def);
	}

	public IArrayNode findArrayInMsgLocal(String path, IArrayNode def)
	{
		ICompositeNode local = getMsgLocal();
		return local == null ? null : local.findArray(path, def);
	}

	public ICompositeNode findCompositeInMsgLocal(String path, ICompositeNode def)
	{
		ICompositeNode local = getMsgLocal();
		return local == null ? null : local.findComposite(path, def);
	}

	public void setInMsgLocal(String key, Object val)
	{
		ICompositeNode local = getMsgLocal();
		if (local == null)
		{
			local = new CompositeNode();
		}
		if (val == null) local.remove(key);
		else local.set(key, val);
		setMsgLocal(local);
	}

	/**
	 * 同步模式下MQ应答报文CorID设置规则。
	 */
	public String getMQCorId()
	{
		String seqNb = isRequestMsg() ? getSeqNb().trim() : getRefSeqNb().trim();
		String sndDt = isRequestMsg() ? getSndDt() : getRefSndDt();
		return getMQCorId(sndDt, seqNb);
	}

	public static String getMQCorId(String sndDt, String seqNb)
	{
		if (!StringX.nullity(sndDt)) sndDt = sndDt.replaceAll("-", "");
		if (!StringX.nullity(sndDt) && sndDt.length() == 8)
		{ // chenjs 2011-08-29 增加判断发送日期必须是8位
			if (seqNb.length() < 15)
			{
				String corId = StringX.ZEROS + seqNb;
				return sndDt + '-' + corId.substring(corId.length() - 15);
			}
			else if (seqNb.length() == 15) return sndDt + '-' + seqNb;
			else if (seqNb.length() == 16) return sndDt + seqNb;
		}
		String corId = StringX.ZEROS + seqNb;
		return corId.substring(corId.length() - 24);
	}

	// chenjs 2011-09-02 序列化
	public void toXml(OutputStream os, boolean pretty)
	{
		toXml(os, pretty, Array2Node2XML.getInstance());
	}

	public void toXml(OutputStream os, boolean pretty, INode2XML node2xml)
	{
		toXml(os, pretty, node2xml, new HashMap());
	}

	public void toXml(OutputStream os, boolean pretty, INode2XML node2xml, Map attribute)
	{
		toXml(os, null, pretty, node2xml, attribute);
	}

	public void toXml(OutputStream os, String ns, boolean pretty, INode2XML node2xml, Map attribute)
	{
		try
		{
//			os.write(XML_HEADER);
			List path = new ArrayList();
			path.add(TAG_ROOT);
			if (isCn2utf8()) attribute.put(INode2XML.ATTR_KEY_CN2UTF8, Boolean.TRUE);
			ICompositeNode transaction = getTransaction();
			// 2012-06-15 将transaction下一层标签顺序化
			SequenceCompositeNode tran = new SequenceCompositeNode(transaction, TRANS_TAG_SEQ);
			tran.setExt(transaction.getExt());
			node2xml.node2xml(os, tran, ns, TAG_ROOT, pretty, transaction, transaction, path,
					attribute, 0);
		}
		catch (IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	public String toXml(boolean pretty)
	{
		try
		{
			return new String(SOAPConverter.getInstance().serialize(this),Common.CHARSET_UTF8);
			// DefaultNode2XML.getInstance() 修改为 Array2Node2XML.getInstance(),
			// 2013-04-11
			// return new String(toByteArray(false, pretty,
			// Array2Node2XML.getInstance()),
			// DefaultNode2XML.DEFAULT_CHARSET);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public byte[] write2bytes()
	{
		return toByteArray(false);
	}

	public byte[] toByteArray2(boolean gzip)
	{
		return toByteArray(gzip, false, Array2Node2XML.getInstance());
	}

	public byte[] toByteArray(boolean gzip, boolean pretty)
	{
		return toByteArray(gzip, pretty, Array2Node2XML.getInstance());
	}

	public byte[] toByteArray(boolean gzip, boolean pretty, INode2XML node2xml)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream os = !gzip ? (OutputStream) baos
					: (OutputStream) new GZIPOutputStream(baos);
			toXml(os, pretty, node2xml);
			os.close();
			return baos.toByteArray();
		}
		catch (IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}

	public byte[] toByteArray(boolean gzip)
	{
		return toByteArray(gzip, false);
	}
}
