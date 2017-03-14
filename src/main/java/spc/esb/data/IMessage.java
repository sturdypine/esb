package spc.esb.data;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import spc.webos.exception.Status;

/**
 * 通用请求报文结构为soap格式,结构为： <Envelope> <Header></Header> <Body></Body> </Envelope>
 * 
 * @author spc
 * 
 */
public interface IMessage extends Serializable
{
	// 基本数据类型，version, clusterId, cn2utf8, signature, transSN, transId, status
	void setOriginalBytes(byte[] original);

	byte[] getOriginalBytes();

	String getOriginalBytesPlainStr();

	void setOriginalBytesPlainStr(String original);

	boolean isRequestMsg(); // 当前报文是请求报文

	boolean isCanResponse(); // 当前报文能返回

	String getLang();

	void setLang(String lang);

	String getSndDt();

	void setSndDt(String sndDt);

	String getSndTm();

	void setSndTm(String sndTm);

	String getSeqNb();

	void setSeqNb(String seqNb);

	String getRcvAppSN();

	void setRcvAppSN(String rcvAppSN);

	String getRefSndDt();

	void setRefSndDt(String refSndDt);

	String getRefSeqNb();

	void setRefSeqNb(String refSeqNb);

	String getRefMsgSn();

	String getMsgSn();

	void setSn(String sn);

	String getRefMsgCd();

	void setRefMsgCd(String refMsgCd);

	String getMsgCd();

	void setMsgCd(String msgCd);
	
	String getReplyMsgCd();

	void setReplyMsgCd(String replyMsgCd);

	// String getSigType();
	//
	// void setSigType(String sigType);

	String getCallType();

	void setCallType(String callType);

	String getRefCallType();

	void setRefCallType(String callType);

	boolean isSynCall();

	String getSndNode();

	String getRcvNode();

	String getSndApp();

	String getRcvApp();

	String getSndNodeApp();

	String getRcvNodeApp();

	String getRefSndNode();

	String getRefSndApp();

	String getRefSndNodeApp();

	void setSndNode(String sndNode);

	void setSndAppCd(String sndAppCd);

	void setRefSndNode(String refSndNode);

	void setRefSndApp(String refSndApp);

	void setRcvNode(String rcvNode);

	void setRcvAppCd(String rcvAppCd);

	void setReplyToQ(String replyToQ);

	String getReplyToQ();

	String getVersion();

	void setVersion(String version);

	String getSignature();

	void setSignature(String signature);

	String getLocation();

	void setLocation(String location);

	void setCn2utf8(boolean cn2utf8);

	boolean isCn2utf8();

	byte[] getCorrelationID();

	void setCorrelationID(byte[] correlationID);

	int getExpirySeconds();

	void setExpirySeconds(int expirySeconds);

	long getStart();

	void setStart(long start);

	long getEnd();

	void setEnd(long end);

	String getFixedErrDesc(); // 固定的错误信息描述 sn + transid

	Status getStatus();

	void setStatus(Status status);

//	ICompositeNode getMsg();
//
//	ICompositeNode setMsg(ICompositeNode msg);

	String getHeaderExtStr(); // 700, 2013-05-05

	void setHeaderExt(String hdrExt); // 700, 2013-05-05

	ICompositeNode getHeaderExt();

	void setHeaderExt(ICompositeNode hdrExt);

	void setInHeaderExt(String key, Object val);

	INode findInHeaderExt(String key);

	// added by chenjs 2011-09-02 增加报文msg local控制
	ICompositeNode getMsgLocal();

	ICompositeNode setMsgLocal(ICompositeNode local);

	IAtomNode findAtomInMsgLocal(String path, IAtomNode def);

	IArrayNode findArrayInMsgLocal(String path, IArrayNode def);

	ICompositeNode findCompositeInMsgLocal(String path, ICompositeNode def);

	void setInMsgLocal(String key, Object val);

	// added by chenjs 2011-09-02 增加报文msg local控制

	// 2011-09-02 注释掉pnt部分
	// ICompositeNode getPnt();
	//
	// ICompositeNode setPnt(ICompositeNode pnt);

	// 基本具备数据end

	String getMQCorId();

//	void init();

	void setTransaction(ICompositeNode transaction);

	ICompositeNode getTransaction();

	void clearLocal();

	void clearRequest();

	void clearHeader();

	Throwable getEx();

	/**
	 * 检查当前节点下是否包含了以下必须包含的路径元素, 如果不包含则以异常方式抛出
	 * 
	 * @param paths
	 */
	void mustContain(Collection paths);

	void mustContain(String[] paths);

	/**
	 * 从报文中获得一个指定类型的节点，如果没有，或者节点类型不匹配则抛出异常
	 * 
	 * @param parentPath
	 * @param parent
	 * @param path
	 * @param type
	 * @param canNull
	 * @return
	 */
	INode find(String parentPath, ICompositeNode parent, String path, byte type, boolean canNull);

	/**
	 * @param path
	 * @return
	 */
	INode find(String path);

	IAtomNode findAtom(String path, IAtomNode def);

	IArrayNode findArray(String path, IArrayNode def);

	ICompositeNode findComposite(String path, ICompositeNode def);

	INode findIgnoreCase(String path);

	void setEx(Throwable ex);

	boolean isContainExFnode();

	boolean isExitFlow();

	void setExitFlow(boolean exitFlow);

	void setContainExFnode(boolean containExFnode);

	MessageAttr getAttr();

	void setAttr(MessageAttr attr);

	MessageAttr getRefAttr();

	void setRefAttr(MessageAttr refAttr);

	Map getLocal();

	Object getInLocal(String key);

	Object getInLocal(String key, Object obj);

	void setInLocal(String key, Object value);

	ICompositeNode getHeader();

	ICompositeNode createInHeader(String path);

	ICompositeNode setHeader(ICompositeNode header);

	ICompositeNode setBody(ICompositeNode body);

	ICompositeNode setRequest(ICompositeNode request);

	ICompositeNode setResponse(ICompositeNode response);

	INode findInHeader(String path);

	Object getInHeader(String path);

	Object findInHeader(String path, Object target);

	void setInHeader(String key, Object value);

	ICompositeNode createInResquest(String path);

	ICompositeNode getRequest();

	/**
	 * @param path
	 * @return
	 */
	INode findInRequest(String path);

	IAtomNode findAtomInRequest(String path, IAtomNode def);

	IArrayNode findArrayInRequest(String path, IArrayNode def);

	ICompositeNode findCompositeInRequest(String path, ICompositeNode def);

	Object getInRequest(String path);

	Object findInRequest(String path, Object target);

	void setInRequest(String key, Object value);

	ICompositeNode getBody();

	ICompositeNode getResponse();

	ICompositeNode createInResponse(String path);

	INode findInResponse(String path);

	IAtomNode findAtomInResponse(String path, IAtomNode def);

	IArrayNode findArrayInResponse(String path, IArrayNode def);

	ICompositeNode findCompositeInResponse(String path, ICompositeNode def);

	Object getInResponse(String path);

	/**
	 * @param path
	 * @return
	 */
	Object findInResponse(String path, Object target);

	void setInResponse(String key, Object value);

	void toXml(OutputStream os, boolean pretty);

	void toXml(OutputStream os, boolean pretty, INode2XML node2xml);

	void toXml(OutputStream os, boolean pretty, INode2XML node2xml, Map attribute);

	byte[] write2bytes();

	byte[] toByteArray2(boolean gzip);

	byte[] toByteArray(boolean gzip);

	byte[] toByteArray(boolean gzip, boolean pretty, INode2XML node2xml);

	String toXml(boolean pretty);

//	IMessage newInstance();

	// for local
	Object getBplVariable(String key);

	void setBplVariable(String key, Object value);

	void removeBplVariable(String key);

	Object getMQAttribute(String key);

	void setMQAttribute(String key, Object value);

	void removeMQAttribute(String key);

	Object getMBAttribute(String key);

	void setMBAttribute(String key, Object value);

	void removeMBAttribute(String key);

//	public static final String XML_HDR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
//	// modified by chenjs 2012-01-17 取消XML报文发送的头定义
//	public static final byte[] XML_HEADER = XML_HDR.getBytes();
	// 900, 服务soap规范的xml
	public static String TAG_ROOT = "Envelope"; // "transaction";
	public static String TAG_HEADER = "Header"; // "header";
	public static String TAG_BODY = "Body"; // "body";
	// public static String TAG_REQUEST = "request";
	// public static String TAG_RESPONSE = "response";
	public static String TAG_LOCAL = "local"; // 报文临时信息
	public static String TAG_LOCATION = "location"; // BA模式时用于存放服务地址

	public static String HEADER_EXT_PREFIX = "base64:"; // 800, EXT字段base64编码前缀
	public static String TAG_HEADER_EXT = "ext";
	public static String TAG_CN2UTF8 = "cn2utf8"; // 是否返回的可能含有中文的字符串转化为utf8字符格式
	public static String TAG_HEADER_STATUS = "status";

	// 在esb报文头中分为requester, esb(request, responose), provider标签
	// public static String TAG_NODE = "mbrCd"; // 接入节点
	// public static String TAG_APP = "appCd"; // 业务系统
	// public static String TAG_IP = "ip";
	public static String TAG_SNDDT = "sndDt"; // 发送日期yyyyMMdd
	public static String TAG_SNDTM = "sndTm"; // 发送时间HHmmss

//	public static String TAG_HEADER_MSG = "msg"; // 报文信息
	// public static String TAG_HEADER_SIGTYPE = "sigTyp"; //
	// 签名方式，默认是body字节全部签名，1表示基于内容签名
	public static String TAG_HEADER_LANGUAGE = "lang"; // 语言种类，默认为中文
	// 调用类型: SYN:同步， ASYN:异步
	public static String TAG_HEADER_CALLTYPE = "callTyp";
	public static String TAG_HEADER_MSG_CD = "msgCd"; // 报文编号
	public static String TAG_HEADER_MSG_SN = "seqNb"; // 报文流水号，日中唯一流水
	public static String TAG_HEADER_MSG_RCVAPPSN = "rcvAppSN"; // 后台服务流水号
	public static String TAG_HEADER_MSG_SNDNODE = "sndMbrCd"; // 发送成员编号
	public static String TAG_HEADER_MSG_SNDAPP = "sndAppCd"; // 发送应用编号
	public static String TAG_HEADER_MSG_RCVNODE = "rcvMbrCd"; // 接收成员编码
	public static String TAG_HEADER_MSG_RCVAPP = "rcvAppCd"; // 接收应用编号
	public static String TAG_HEADER_MSG_REFMSGCD = "refMsgCd"; // 参考报文编号
	public static String TAG_HEADER_MSG_REFCALLTYP = "refCallTyp"; // 参靠调用类型
	public static String TAG_HEADER_MSG_REFSNDNODE = "refSndMbrCd"; // 参考发送成员编号
	public static String TAG_HEADER_MSG_REFSNDAPP = "refSndAppCd"; // 参考发送应用编号
	public static String TAG_HEADER_MSG_REFSNDDT = "refSndDt"; // 参考发送时间
	public static String TAG_HEADER_MSG_REFSNDSN = "refSeqNb"; // 参考流水号
	// 700_130520 应答队列
	public static String TAG_HEADER_MSG_REPLYTOQ = "replyToQ";
	// 935, 返回replyMsgCd, 提供给JS服务调用
	public static String TAG_HEADER_REPLYMSGCD = "replyMsgCd";

	// public static String TAG_HEADER_PNT = "pnt"; // 参与方信息

	public static String CALLTYP_SYN = "SYN"; // 调用方式 --- 同步
	public static String CALLTYP_ASYN = "ASYN"; // 调用方式 --- 异步
	public static String NODE_CENTER = "0000"; // 中心节点
	public static String APP_ESB = "ESB";

	// 路径
	public static final String PATH_DELIM = "/";
//	public static String PATH_HEADER_MSG = TAG_HEADER + PATH_DELIM + TAG_HEADER_MSG;
	// public static String PATH_REQUEST = TAG_BODY + PATH_DELIM + TAG_REQUEST;
	// public static String PATH_RESPONSE = TAG_BODY + PATH_DELIM +
	// TAG_RESPONSE;
	public static String PATH_STATUS = TAG_HEADER + PATH_DELIM + TAG_HEADER_STATUS;

	public static String TAG_SIGNATURE = "signature"; // 签名字段
	public static String TAG_ORIGINALBYTES = "originalBytes"; // 当前xml报文对应的原报文二进制内容
	public static String TAG_HEADER_VERSION = "ver"; // 版本号
	public static String HEADER_VERSION_1_0 = "1.0"; // 版本号

	public static String TRANS_TAG_SEQ = "header,body,signature";
}
