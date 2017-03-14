package spc.esb.common.service;

import spc.esb.data.IMessage;

/**
 * 报文路由服务接口
 * 
 * @author sunqian at 2010-5-24
 * 
 */
public interface RouteService
{
	// 返回Message的路由队列QNAME值
	public String getQname(IMessage msg) throws Exception;

	boolean isValidReplyToQ(IMessage msg, String replyToQ);

	// 返回Message的广播路由队列列表QNAMES值
	// public String getBroadcastQnames(IMessage msg) throws Exception;
}
