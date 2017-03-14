package spc.esb.constant;

/**
 * ESB在esb_config需要的key
 * 
 * @author chenjs
 * 
 */
public class ESBConfig
{
	// ----------- for perf --------
	public static String PERF_IIBW = "esb.perf.iibW"; // IIB的分配权重
	public static String PERF_YCMBW = "esb.perf.ycmbW"; // ycmb的分配权重
	public static String PERF_YCMBJ = "esb.perf.ycmbJ"; // ycmb是否journal日志
	public static String PERF_MINC = "esb.perf.minC"; // 快速转发交易的最小毫秒耗时(10)
	// ----------- for perf end --------

	// ----------- for license --------
	public static String LICENSE_key = "esb.license.key"; // license文件
	// ----------- for license end --------

	// ----------- for security --------
	public static String SECURITY_trace = "esb.security.trace"; // false, 跟踪加解密报文
	// ----------- for security end --------

	// -------------for reversal----------------
	public static String RVSL_debug = "esb.reversal.debug"; // false
	public static String RVSL_defaultTimes = "esb.reversal.defaultTimes"; // 5
	public static String RVSL_intervalSeconds = "esb.reversal.intervalSeconds"; // 10000
	// -------------for reversal end------------

	// ---------------for timeout------------
	public static String TIMEOUT_traceQ = "esb.timeout.traceQ";
	// ---------------for timeout end------------

	// ---------------for alarm------------
	public static String ALARM_traceQ = "esb.alarm.traceQ";
	public static String ALARM_server = "esb.alarm.server"; // 哪些服务系统出错后进入esb_alarm队列
	// ---------------for alarm end------------

	// -------------for journal----------------
	public static String JOURNAL_ftsstatus = "esb.journal.ftsstatus"; // false,
																	// 是否记录fts的应答报文
	public static String JOURNAL_traceQ = "esb.journal.traceQ"; // 追踪流水日志队列(比如风险监控)
	public static String JOURNAL_traceDetail = "esb.journal.traceDetail"; // false,
																		// 用于回归测试，全报文记录
	public static String JOURNAL_fullBookInSubmsg = "esb.journal.fullBookInSubmsg"; // false,子交易属于流程交易,
																				// 记录全报文
	public static String JOURNAL_withoutSensitive = "esb.journal.withoutSensitive"; // false
	public static String JOURNAL_maxMsgSize = "esb.journal.maxMsgSize"; // 9144
	public static String JOURNAL_maxOrigBytesSize = "esb.journal.maxOrigBytesSize"; // 3000
	public static String JOURNAL_maxStatusDescSize = "esb.journal.maxStatusDescSize"; // 900
	public static String JOURNAL_charset = "esb.journal.charset"; // utf-8
	public static String JOURNAL_exSleepSeconds = "esb.journal.exSleepSeconds"; // 30
	public static String JOURNAL_performance = "esb.journal.performance"; // false
	public static String JOURNAL_percent = "esb.journal.percent"; // 0,
																// 系统在性能测试情况下记录流水的百分比(0-100)
	// -------------for journal end------------

	// -------------for dlq journal----------------
	public static String DLQ_trace2db = "esb.dlq.trace2db"; // true, 是否将死信消息登记到数据库,
	public static String DLQ_JOURNAL_PUTAPPTYP_MAX_SIZE = "esb.dlq.putAppTypeMaxSize"; // 100
	public static String DLQ_JOURNAL_PUTAPPNM_MAX_SIZE = "esb.dlq.putAppNameMaxSize"; // 100
	public static String DLQ_JOURNAL_MSG_MAX_SIZE = "esb.dlq.msgMaxSize"; // 100
	// -------------for dlq journal end------------

	// -------------for MB-----------------
	// 是否校验广播交易的权限，河南农信上线后有很多交易没校验广播交易权限, 所以默认值不能为true
	public static String MB_broadcastAuth = "esb.mb.broadcastAuth"; // "true"
	// 广播交易如果没有配置对应订阅服务则直接终止，现在广播有2种模式：1. 标准推荐模式，广播映射成服务， 2. 天农商模式，广播映射给多系统
	// 现在河南农信ecif不停发广播，但没有接收方，此时消息流需要正常接收，而不再使用天农商的广播给多系统
	public static String MB_broadcastExit = "esb.mb.broadcastExit"; // "true"

	// MB 检查log4j配置文件的间隔时间，默认为10s
	public static String MB_log4jRefreshInterval = "esb.mb.log4jRefreshInterval"; // "10"
	// MB 的 tranlog 日志输出采用简要模式，比传统减少2行日志
	// public static String MB_briefTranlog = "mb.briefTranlog"; // "true"
	// MB 默认忽略不合法的xml字符
	public static String MB_ignoreUnvalidXMLChar = "esb.mb.ignoreUnvalidXMLChar"; // "false"
	// 在某些穿透的性能测试场景下, ReqBa, RepFa时不删除orginalbytes，以提高压力测试性能
	public static String MB_removeOriginalBytes = "esb.mb.removeOriginalBytes"; // "true"
	// 是否支持基于内容签名, 3.1版本使用，以后版本不再使用
	public static String MB_bodySig = "esb.mb.bodySig"; // "false"
	// SigPre抽取报文的大小限制, -1代表大小不限制，0代表不抽取报文,
	public static String MB_traceMaxMsgLen = "esb.mb.traceMaxMsgLen"; // "0"
	// 2012-05-25 是否在MB中嵌入式执行BPL, 默认为嵌入式
	public static String MB_embedBPL = "esb.mb.embedBPL"; // "JBPM3"
	// 2012-05-12 容许MB消息流在 同步应答消息 时指定放入多个队列管理器中的指定队列,
	// 默认为空字符串，表示放入集群中某个队列，而不指定多个队列管理器
	public static String MB_synResponse2QM = "esb.mb.synResponse2QM"; // ""
	// 同步应答消息在队列中的超时时间，默认为10秒
	public static String MB_synRepExpireSeconds = "esb.mb.synRepExpireSeconds"; // 10
	public static String MB_asynRepExpireSeconds = "esb.mb.asynRepExpireSeconds"; // 259200,
																				// 3*24hours
	public static String MB_validateHeader = "esb.mb.validateHeader"; // true
	public static String MB_validateBody = "esb.mb.validateBody"; // true
	public static String MB_autoFilterUndefinedTag = "esb.mb.autoFilterUndefinedTag"; // false
	// default is [2, 5]
	public static String MB_validServiceStatus = "esb.mb.validServiceStatus";
	public static String MB_mappingRetCd = "esb.mb.mappingRetCd";
	public static String MB_replyToQPrefix = "esb.mb.replyToQPrefix"; // 应答队列前缀
	// -------------for MB end----------------------------------

	// -----------for DB--------------
	public static String DB_ok = "esb.db.ok"; // 452, 判断数据库是否可用，用于主动停止向DB记录日志, true
	public static String DB_esbInfoVerDt = "status.refresh.esb.base"; // 基础信息日期
	public static String DB_msgDefVerDt = "status.refresh.esb.msgdef"; // 报文定义版本日期
	public static String DB_flowCtrlVerDt = "status.refresh.esb.flowctrl"; // 流量控制数据版本日期
	public static String DB_bplVerDt = "status.refresh.esb.bpl"; // 流程服务定义版本日期
	// -----------for DB end----------

	// -----------for Log----------------
	public static String LOG_FTL = "Log.FTL"; // 日志FTL规则
	// -----------for Log end----------------
}
