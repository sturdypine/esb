package spc.esb.data.sig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spc.esb.data.IMessage;

/**
 * 从报文中抽取参与签名信息的组织方式, 有的用竖线分割数据，有的用逗号，或者其他方式分割数据
 * 
 * @author spc
 * 
 */
public interface MsgSigContent
{
	byte[] getSigCnts(IMessage msg, String nodeCd, List<Object[]> sigCnts, String charset)
			throws Exception;

	Map<String, MsgSigContent> SIG = new HashMap<>();
}
