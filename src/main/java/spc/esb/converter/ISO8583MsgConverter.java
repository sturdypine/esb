package spc.esb.converter;

import spc.esb.data.AtomNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.iso8583.BitMap;
import spc.esb.data.iso8583.ISO8583Util;
import spc.esb.data.xml.XMLConverterUtil;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 先使用schema表固定的报文编号(比如银联使用CUPS8583)定义的8583 128个域全集解析，然后使用具体的业务报文编号进行xml - xml'
 * 解析
 * 
 * @author chenjs
 * 
 */
public class ISO8583MsgConverter extends AbstractMsgConverter
{
	/**
	 * 由于此适配器一般运行于MB模式, msg对象在BA模式时是原reqmsg, 在FA模式时是请求报文初始值, 也就是进入MB时的报文对象
	 */
	public IMessage deserialize(byte[] iso8583, IMessage msg) throws Exception
	{
		if (log.isDebugEnabled())
		{
			log.debug("iso8583:[[" + (iso8583 == null ? "null" : StringX.base64(iso8583)));
			BitMap bitmap = new BitMap(iso8583, offset);
			// 使用偏移量构造bitmap, 并调试bitmap值
			log.debug("offset:" + offset + ", bitmap:" + bitmap.getValidFields());
		}

		// 使用偏移量对8583报文进行解析, 采用系统特定的全配置解析方案,
		// 全集合8583schema信息放入esb_msgschema表中，如果FA使用也需要把全集信息放入此表
		ICompositeNode cnode = ISO8583Util.deserialize(iso8583MsgConverter, iso8583, offset,
				iso8583.length - offset, msg, getMsgSchema(iso8583, msg));
		if (log.isDebugEnabled()) log.debug("8583 cnode: " + cnode);

		ICompositeNode target = convertMap(msg, cnode, new CompositeNode()); // 把iso8583全字段集合根据ESB
		// schema映射成当前报文规范指定的规范
		if (ba)
		{ // 如果是BA模式，则认为是服务系统应答报文
			req2rep(msg, cnode, target);
			msg.setResponse(target); // 如果是BA适配器则设置response
		}
		else msg.setRequest(target); // 如果是FA适配器则设置request

		// 处理ESB报文头信息
		ICompositeNode msgHdr = msg.getHeader();
		msgHdr.apply(getESBMsgHdr(msg, cnode, target));
		msg.setHeader(msgHdr);

		if (log.isDebugEnabled()) log.debug("after deserialize msg :" + msg.toXml(true));

		return msg;
	}

	// 获得当前8583报文的schema， 默认使用全量配置的schema结构
	protected TreeNode getMsgSchema(byte[] iso8583, IMessage msg)
	{
		return msgDefService.getMsgSchema(schema8583);
	}

	// 在BA同步模式时根据请求报文变成一个应答报文
	protected void req2rep(IMessage msg, ICompositeNode cnode, ICompositeNode target)
	{
//		ResponseAFNode.req2rep(msg); // 变换报文，设置参考信息
	}

	// 得到ESB报文头信息, msgCd, seqNb, sndDt, sndTm, sndMbrCd etc.
	protected ICompositeNode getESBMsgHdr(IMessage msg, ICompositeNode cnode, ICompositeNode target)
	{
		ICompositeNode msgHdr = new CompositeNode();
		String appMsgCd = cnode.findAtom(appMsgCdNm, new AtomNode(StringX.EMPTY_STRING))
				.stringValue();
		String esbMsgCd = ba ? msgDefService.getESBMsgCdByBA(appCd.toLowerCase(), appMsgCd)
				: msgDefService.getESBMsgCdByFA(appCd, appMsgCd);
		msgHdr.set(IMessage.TAG_HEADER_MSG_CD, esbMsgCd); // 得到ESB报文编号
		return msgHdr;
	}

	// 然后使用特定报文编号将全集映射到子集上, 使用xml - xml' 模式处理
	protected ICompositeNode convertMap(IMessage msg, ICompositeNode cnode, ICompositeNode target)
			throws Exception
	{
		String appMsgCd = cnode.findAtom(appMsgCdNm, new AtomNode(StringX.EMPTY_STRING))
				.stringValue(); // 8583报文中提供的当前报文的交易码
		String esbMsgCd = ba ? msgDefService.getESBMsgCdByBA(appCd.toLowerCase(), appMsgCd)
				: msgDefService.getESBMsgCdByFA(appCd, appMsgCd); // 找到对于的ESB的报文编号
		TreeNode schema = ba ? msgDefService.getMsgSchema(esbMsgCd) : msgDefService
				.getMsgSchemaByFA(appCd, esbMsgCd); // 得到esb报文编号8583schema结构信息
		if (schema == null)
		{ // 如果某交易没有配置特殊子集，则使用全集解析结果
			if (log.isInfoEnabled()) log.info("schema is null by " + msg.getMsgCd());
			return cnode;
		}
		XMLConverterUtil.convertMap(schema, msg, cnode, target, false, atomProcessor,
				nodeProcessor, StringX.EMPTY_STRING, rcvIgnore, emptyIgnore, schemaTargetXMLTag);
		return target;
	}

	// 序列化时直接使用ESB报文编号的schema信息，而不是用全集结构信息
	public byte[] serialize(IMessage msg) throws Exception
	{
		if (log.isDebugEnabled()) log.debug("msg:\n" + msg.toXml(true));
		// 得到8583schema结构信息
		TreeNode schema = ba ? msgDefService.getMsgSchema(msg.getMsgCd()) : msgDefService
				.getMsgSchemaByFA(appCd, msg.getMsgCd());
		if (schema == null)
		{ // 如果某交易没有配置特殊子集，则使用全集解析结果
			if (log.isInfoEnabled()) log.info("schema is null by " + msg.getMsgCd());
			schema = msgDefService.getMsgSchema(schema8583);
		}
		ICompositeNode cnode = ba ? msg.getRequest() : msg.getResponse(); // 获得需要序列化的复杂节点信息
		byte[] buf = ISO8583Util.serialize(iso8583MsgConverter, cnode, schema);
		if (log.isDebugEnabled()) log.debug("after serialize buf.base64: "
				+ new String(StringX.encodeBase64(buf)));
		return buf;
	}

	protected String appMsgCdNm; // 全集8583中表示appmsgcd字段的name值
	protected String schema8583; // 全集8583报文编号，比如银联使用CUPS8583
	protected int offset = 0; // 一般8583前面有几个字节表示头

	public String getSchema8583()
	{
		return schema8583;
	}

	public void setSchema8583(String schema8583)
	{
		this.schema8583 = schema8583;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public void setAppMsgCdNm(String appMsgCdNm)
	{
		this.appMsgCdNm = appMsgCdNm;
	}
}
