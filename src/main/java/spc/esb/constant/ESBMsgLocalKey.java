package spc.esb.constant;

/**
 * 定义了ESBMessage临时变量的名称
 * 
 */

public class ESBMsgLocalKey
{
	public final static String LOCAL_PORT = "localPort";
	public final static String REMOTE_IP = "remoteIP";
	public final static String HTTP_URI = "uri";

	// ****msg临时变量空间中的新增key
	public static final String TARGET_MSG_BYTES = "ML_TARGET_BYTES";
	public static final String MSG_TEMP_REQ_MSG = "REQ_MSG"; // 报文临时域中的key
	public static final String REF_MSG_ATTRIBUTE = "REF_MSGATTR"; // 参考报文属性
	// public static final String MSG_TEMP_MSG_VO = "MSG_VO"; // 报文临时域中的key
	// public static final String MSG_TEMP_MSGSTRUCT_VO = "MSGSTRUCT_VO"; //
	// 报文临时域中的key

	public static final String MSG_LENGTH = "MSG_LENGTH"; // 当前报文的字节长度

	// public static final String ESB_REP_BYTES = "ESB_REP_BYTES"; // esb应答bytes

	public static final String BLOB_HEADER = "BH";

	// 临时空间 from IMessage
	// ESB对象使用，应答报文超时时间 chenjs 2012-01-27
	public static final String LOCAL_RESPONSE_TIMEOUT = "ML_RESPONSE_TIMEOUT";

	public static final String LOCAL_UNVALID_XML_CHAR = "ML_UNVALID_XML_CHAR"; // 不合法的xml字符
	public static final String LOCAL_UNVALID_XML = "ML_UNVALID_XML"; // 不合法的xml报文
	public static final String LOCAL_INBUF_TIME = "ML_INBUF_TIME"; // 消息放入buffer的时间
	public static final String LOCAL_REP_BUFFER = "ML_REP_BUF"; // 当前报文的指定响应buffer
	public static final String LOCAL_REP_BUFFER_NAME = "ML_REP_BUF_NAME"; // 当前报文的指定响应buffer
																			// NAME
	public static final String LOCAL_ORIGINAL_REQ_BYTES = "ML_REQ_BYTES"; // 接受到的原始二进制报文
	public static final String LOCAL_ORIGINAL_REQ_QMSG = "ML_REQ_QUEUEMSG"; // 接受到的原始QueueMessage对象
	public static final String LOCAL_ORIGINAL_REQ_SOCKETMSG = "ML_REQ_SOCKETMSG"; // 接受到的原始SocketMessage对象
	public static final String LOCAL_ACCESS4LOCAL = "ML_ACCESS4LOCAL"; // 直接同步请求消息流，不需要放入返回buffer和队列
	public static final String LOCAL_PARENT_MSG = "ML_PARENT_MSG"; // 原父报文内容
	public static final String LOCAL_SUB_MSG = "ML_SUB_MSG"; // 主交易流程中父报文包含的子报文
	public static final String LOCAL_NSUB_MSG = "ML_NSUB_MSG"; // 主交易流程中父报文包含的新创建的子报文
	public static final String LOCAL_LAST_NODE = "ML_LAST_NODE"; // 最后一次执行的node
	public static final String LOCAL_JBPM_PROCESS_NAME = "ML_JBPM_PROCESS_NAME"; // 当前报文需要的处理流程名
	public static final String LOCAL_MSG_STRUCT_VO = "ML_MSG_STRUCT_VO"; // 当前报文的MsgStructVO
	// map attribute
	public static final String LOCAL_MQ_ATTRIBUTE = "ML_MQ_ATTRIBUTE"; // 消息报文的MQ属性
	public static final String LOCAL_MB_ATTRIBUTE = "ML_MB_ATTRIBUTE"; // 消息报文的MB属性
	public static final String LOCAL_BPL_VARIABLES = "ML_BPL_VARIABLES"; // 当前BPL变量空间

	public static final String LOCAL_EXECUTABLE = "ML_EXECUTABLE"; // 当前报文的可执行二进制内容
	public static final String LOCAL_ORIGINAL_REQUEST = "ML_ORIGINAL_REQUEST"; // 当前报文的原始请求request标签
	public static final String LOCAL_REP_BYTES = "ML_REP_BYTES"; // 应答bytes
	public static final String LOCAL_MSG_CONVERTER = "ML_MSG_CONVERTER"; // 消息转换器
	public static final String LOCAL_MSG_MSGFLOW = "ML_MSG_MSGFLOW"; // 指定消息流处理
	public static final String LOCAL_MSGCVT_DELAY = "ML_MSGCVT_DELAY"; // 报文延迟解析标志
	// 临时空间 from IMessage end

	public final static String SUB_PROCESS_NAME_KEY = "SUB_PROC_NAME"; // 子流程名

	public final static String MQGET_QMSG_KEY = "MQGET_QMSG"; // MQ接收节点发送QueueMessage
	public final static String MQPUT_QMSG_KEY = "MQPUT_QMSG"; // MQ发送节点发送QueueMessage
	public final static String MQPUT_MQMSG_KEY = "MQPUT_MQMSG"; // MQ发送节点发送MQMessage

	public final static String PUT_QMSG_KEY = "QPUT_QMSG"; // 发送节点发送QMessage
															// //add by liujk
															// 2013-3-12

	public final static String NO_RETURN = "MSG2BUF_NO_RETURN"; // 不需返回
	public final static String SND_BUF = "MSG2BUF_SND_BUF"; // 发送Buffer标志

	public final static String ACCEPTOR_PROTOCOL = "ML_ACCEPTOR_PROTOCOL"; // 报文的接入方式
	public static final String ACCEPTOR_REMOTE_HOST = "REMOTE_HOST";
	public static final String ACCEPTOR_REMOTE_URI = "REMOTE_URI";
	public static final String ACCEPTOR_LOCAL_PORT = "LOCAL_PORT";

	public final static String LOCAL_DELAY_TASKS = "ML_DELAY_TASKS"; // 需要延时处理的任务

	// for PersistenceAFNode
	public final static String JDBC_SQL_ID = "JDBC_SQL_ID";
	public final static String JDBC_RESULT = "JDBC_RESULT";

	// for blob msg in local
	public final static String BLOB_FILES = "BLOB_FILES";
}
