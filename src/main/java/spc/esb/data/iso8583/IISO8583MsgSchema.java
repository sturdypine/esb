package spc.esb.data.iso8583;

import spc.esb.data.IMessage;
import spc.webos.util.tree.TreeNode;

public interface IISO8583MsgSchema
{
	/**
	 * 给出8583报文的字节数组，通过此获取该报文对应的xml报文结构
	 * 
	 * @param buf
	 * @param offset
	 * @param len
	 * @param reqmsg
	 * @return
	 */
	TreeNode getMsgSchema(byte[] buf, int offset, int len, IMessage reqmsg);
}
