package spc.esb.core.service;

import java.util.Map;

import spc.esb.core.NodeAttr;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.converter.CoreMessageConverter;
import spc.esb.data.validator.MessageErrors;
import spc.esb.model.NodePO;

/**
 * 核心通用服务
 * 
 * @author chenjs
 * 
 */
public interface CoreService
{
	// 解密body部分
	public ICompositeNode decryptBody(IMessage msg, NodePO node, byte[] body) throws Exception;

	// 加密body部分
	public byte[] encryptBody(IMessage msg, NodePO node, ICompositeNode body) throws Exception;

	String[] getBroadcastService(IMessage msg) throws Exception;

	// 核心生成后台服务系统流水号. chenjs 2011-03-11
	void genRcvAppSN(IMessage msg) throws Exception;

	// 验证ESB报文头
	MessageErrors validateHdr(IMessage msg) throws Exception;

	MessageErrors validateBody(IMessage msg) throws Exception;

	// 核心对报文进行语义转换
	void translator(IMessage msg) throws Exception;

	// 核心如果需要做FA转换，则返回做FA转换的spring bean id, point 为日志点0,1,2,3
	String getAdapterBeanId(IMessage msg, boolean request, boolean ba) throws Exception;

	CoreMessageConverter getCoreMsgConverter(IMessage msg, boolean request, boolean ba)
			throws Exception;

	// 计算当前请求报文和渠道的优先级
	int priority(IMessage msg);

	// 申请流量控制资源
	boolean applyFCRes(IMessage msg);

	// 释放流量控制资源
	void releaseFCRes(IMessage msg) throws Exception;

	// 根据输入的MQ ccsid将MQ消息的变成utf8
	byte[] toBytes(byte[] xml, String ccsid) throws Exception;

	byte[] toBytes(byte[] xml, NodeAttr attr) throws Exception;

	// 调试全报文时不打印敏感字段信息
	String msg2strWithoutSensitive(IMessage msg);

	// 使用原始发生二进制报文(定长，8583等)， 根据发送方系统信息组件符合ESB规范的XML报文
	byte[] toESBXML(byte[] originalBytes, NodePO node, Map attr) throws Exception;

	// 处理报文中的非法xml字符
	void handleUnvalidXMLChar(IMessage msg);

	// 获取报文的超时时间
	int getTimeout(IMessage msg);

}
