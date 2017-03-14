package spc.esb.common.service;

import java.util.Map;

import spc.esb.data.IMessage;
import spc.esb.model.EndpointPO;
import spc.esb.model.FvMappingPO;
import spc.esb.model.NodePO;
import spc.esb.model.NodeServicePO;
import spc.webos.endpoint.Endpoint;

/**
 * ESB信息服务接口
 * 
 * @author sunqian at 2010-5-24
 * 
 */
public interface ESBInfoService
{
	// 401_20131024 在MQ远程队列管理器Server 2 Server模式下根据远程队列管理器名确定身份
	NodePO getNodeByQMgr(String qmgr);

	// 401_20131023 通过msglocal信息确认身份
	void identifySndApp(IMessage msg);

	// 401 2013-10-10 活动endpoint信息
	Endpoint getEndpoint(String location);

	EndpointPO getEndpointVO(String location);

	// 400 2013-05-09 得到节点服务配置信息
	NodeServicePO getNodeService(String node, String msgCd);

	// 根据映射规则Id返回映射规则对象
	Map<String, FvMappingPO> getFvMapping(String fvMapId);

	FvMappingPO getFVM(String fvMapId, String sndNodeApp, String sndValue);

	// 通过fv值，发送节点和发送方提供的值找到ESB标准值
	String getESBValueFVM(String fvMapId, String sndNodeApp, String sndValue);

	// 通过ESB值获取接受方值
	String getRcvValueFVM(String fvMapId, String sndNodeApp, String esbValue);

	/**
	 * 根据映射规则将发送方系统的某个值转换为接受方系统的指定值，如果接受方rcvNodeApp为空则转换为ESB标准值
	 * 
	 * @param fvMapId
	 * @param sndNodeApp
	 * @param rcvNodeApp
	 * @param sndValue
	 * @return
	 */
	String getFvMapping(String fvMapId, String sndNodeApp, String rcvNodeApp, String sndValue);

	// 根据数据字典Id返回数据字典对象
	// Object getDict(String dictId);

	// 返回接入节点对象
	NodePO getNode(String nodeApp);

	// 通过远程发送的机器和本地服务端口确定发送方系统，用于前端统一FA
	NodePO getNodeByUriPortIP(int port, String remoteIP);

	Map<String, NodePO> getNodes();
}
