package spc.esb.common.service;

import java.util.Map;

import spc.esb.data.IMessage;
import spc.esb.data.MessageAttr;
import spc.esb.data.MessageSchema;
import spc.esb.model.FAMessagePO;
import spc.esb.model.MessagePO;
import spc.esb.model.ServicePO;
import spc.webos.util.tree.TreeNode;

/**
 * 报文定义服务接口
 * 
 * @author spc
 * 
 */
public interface MsgDefService extends MessageSchema
{
	Map<String, ServicePO> getServices(); // 415_20141015

	// 401_20131105 通过报文活的服务的location地址
	String getLocation(IMessage msg);

	// adde by chenjs 2011-09-05 for 服务表调整
	ServicePO getService(String msgCd);

	ServicePO getService(String serviceId, String ver);

	// 根据报文编号获取报文对象
	MessagePO getMessage(String msgCd);

	String getESBMsgCdByBA(String appCd, String appMsgCd);

	// 根据服务方报文编号获取返回报文对象
	MessagePO getRcvMessage(String rcvAppCd, String rcvMsgCd);

	// 根据报文编号返回报文属性
	MessageAttr getMsgAttr(String msgCd);

	// for FA start...
	// 通过发送节点号和ESB报文编号获取当前发送节点号的报文配置信息，比如定长信息等
	FAMessagePO getFAMessage(String sndNode, String esbMsgCd);

	// 通过报文编号和发送方节点名获取报文结构, 用于支持做前端适配
	TreeNode getMsgSchemaByFA(String sndNode, String esbMsgCd);

	// 通过发送系统的服务编号和发送系统编号得到ESB标准服务编号
	String getESBMsgCdByFA(String sndNode, String sndMsgCd);

	String getSndMsgCdByFA(String sndNode, String esbMsgCd);

	// for FA end...
}
