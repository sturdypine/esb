package spc.esb.data.sig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.esb.data.MessageSchema;
import spc.esb.data.util.MessageUtil;
import spc.esb.security.Signature;

/**
 * 报文签名类
 * 
 * @author spc
 * 
 */
public class MessageSignature
{
	/**
	 * ESB 报文签名
	 * 
	 * @param nodeCd
	 *            接收节点编号
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public byte[] sig(String node, byte[] msg) throws Exception
	{
		byte[] body = MessageUtil.getBody(msg);
		String strSig = getSignature(node).sign(node, body, null);
		return MessageUtil.addSignature(msg, strSig.getBytes());
	}

	/**
	 * ESB 报文验签
	 * 
	 * @param nodeCd
	 *            发送方节点号
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public boolean unsig(String node, byte[] msg) throws Exception
	{
		byte[] sigs = MessageUtil.getSignature(msg);
		if (sigs == null)
		{
			log.warn("canot find signature in msg:" + node);
			return false;
		}
		return getSignature(node).unsign(node, new String(sigs), MessageUtil.getBody(msg), null);
	}

	public Signature getSignature(String node)
	{
		return sig;
	}

	protected MessageSchema msgSchema; // 验证资源，包含基于内容签名的报文结构信息
	protected Signature sig; // 默认的签名接口
	public final static Logger log = LoggerFactory.getLogger(MessageSignature.class);

	public void setSig(Signature sig)
	{
		this.sig = sig;
	}

	public void setMsgSchema(MessageSchema msgSchema)
	{
		this.msgSchema = msgSchema;
	}
}
