package spc.esb.data.sig;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;

/**
 * 原子节点的签名处理方式接口
 * 
 * @author spc
 * 
 */
public interface AtomNode2SigContent
{
	String sigCnt(IMessage msg, String nodeCd, INode value, MsgSchemaPO schema);

	Map<String, AtomNode2SigContent> SIGS = new HashMap<>(); // 当前jvm环境注册的所有sig
}
