package spc.esb.converter;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.FixedMessage;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.fixmsg.CNode2FixedLenBytesUtil;
import spc.esb.model.MessagePO;
import spc.webos.util.tree.TreeNode;

/**
 * 默认xml报文到定长报文的转换器，1. 首先根据报文信息获得当前报文编号对应的定长报文的总长度 2. 根据配置的报文结构进行报文转换
 * 
 * @author chenjs
 * 
 */
public class FixMsgConverter extends AbstractMsgConverter
{
	public IMessage deserialize(byte[] buf, IMessage msg) throws Exception
	{
		String appMsgCd = new String(FixedMessage.read(buf, 0, msgCdStart, msgCdLen));
		String msgCd = msgDefService.getESBMsgCdByBA(appCd.toLowerCase(), appMsgCd);
		MessagePO msgVO = msgDefService.getMessage(msgCd);
		TreeNode schema = msgDefService.getMsgSchema(msgVO.getMsgCd());
		ICompositeNode cnode = CNode2FixedLenBytesUtil.unpack(buf, 0, schema, atom2FixedLen, null,
				charset);
//		ResponseAFNode.req2rep(msg);
		
		
		msg.setMsgCd(msgCd);// 将应答报文编号码放到MsgCd中返回
		msg.setInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES, buf);
		if (ba) msg.setResponse(cnode);
		else msg.setRequest(cnode);
		return msg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		return pack2(msg.getMsgCd(), ba ? msg.getRequest() : msg.getResponse());
	}

	// added by chenjs 2012-01-01 将一个指定报文编号和内容打包成一个数组
	public byte[] pack2(String esbMsgCd, ICompositeNode cnode) throws Exception
	{
		MessagePO msgVO = ba ? msgDefService.getMessage(esbMsgCd) : msgDefService.getFAMessage(
				appCd, esbMsgCd);
		TreeNode schema = ba ? msgDefService.getMsgSchema(esbMsgCd) : msgDefService
				.getMsgSchemaByFA(appCd, esbMsgCd);
		if (schema == null || msgVO == null)
		{
			log.warn("can not find msgvo or schema by: " + esbMsgCd);
			return null;
		}
		return pack2(msgVO, schema, esbMsgCd, cnode);
	}

	public byte[] pack2(MessagePO msgVO, TreeNode schema, String esbMsgCd, ICompositeNode cnode)
			throws Exception
	{
		byte[] fixmsg = new byte[msgVO.getLen().intValue()]; // 使用数据库len字段作为定长的长度,
		// 不再使用ext1字段
		FixedMessage.fillBlank(fixmsg, 0, fixmsg.length);
		CNode2FixedLenBytesUtil.pack2(cnode, fixmsg, 0, schema, atom2FixedLen, null, charset);
		if (log.isDebugEnabled()) log.debug("msgcd: " + esbMsgCd + ", fixmsg bytes:[["
				+ new String(fixmsg, charset) + "]]");
		return fixmsg;
	}

	protected int msgCdStart;
	protected int msgCdLen;

	public void setMsgCdStart(int msgCdStart)
	{
		this.msgCdStart = msgCdStart;
	}

	public void setMsgCdLen(int msgCdLen)
	{
		this.msgCdLen = msgCdLen;
	}
}
