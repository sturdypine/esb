package spc.esb.constant;

/**
 * ESB app常用的参数
 * 
 * @author spc
 * 
 */
public class ESBCommon
{
	// 代码版本
	public static final String VERSION = "6.0.1";
	public static final String VERSION_DATE = "20170317";

	public static String ESB_DS = "esbDS";
	public static final String JMS_LOGPOINT = "LogPoint";
	
	public static final String MODEL_MSG = "msg";
	public static final String MODEL_PARENT_MSG = "pmsg";
	public static final String MODEL_MSG_PREQUEST = "prequest";
	public static final String MODEL_MSG_PRESPONSE = "presponse";
	public static final String MODEL_XML = "xml";
	public static final String MODEL_MSG_OBJ = "MSG";
	public static final String MODEL_MSG_SN = "msgSn";
	public static final String MODEL_BODY = "body";
	public static final String MODEL_MSG_CD = "msgCd";
	public static final String MODEL_MSG_LOCAL = "msgLocal";
	public static final String MODEL_MSG_LOCAL_BPL_VARS = "msgBplVars";
	public static final String MODEL_MSG_ATTR = "msgAttr";

	public static String CCSID_UTF8 = "1208";
	public static String CCSID_EBCD = "1388";
	public static String CCSID_GBK = "1386";

	// ****ESB核心配置的key
	public static final String REQFIX = "REQ.";
	public static final String REPFIX = "REP.";
	public static final String QSPLIT = ".";
	public static final String BLOBFIX = ".BLOB";
	public static final String STRING_MSG = "msg";

	// 其他常量标识符
	public static final String RENOTICE_MBRCD = "origMbrCd";// 补发报文中的原报文流水
	public static final String RENOTICE_APPCD = "origAppCd";// 补发报文中的原报文流水
	public static final String RENOTICE_SNDDT = "origSndDt";// 补发报文中的原报文流水
	public static final String RENOTICE_SEQNB = "origSeqNb";// 补发报文中的原报文流水

	// ESB流水日志点
	public static final String REQ_IN_POINT = "0";
	public static final String REQ_OUT_POINT = "1";
	public static final String REP_IN_POINT = "2";
	public static final String REP_OUT_POINT = "3";
	public static final int DBMSG9K = 9144;
	public static final String BLOB_MSG_PATH = "blobext";
	public static final String BLOB_MSG_LAST_FLAG = "isLast";
	public static final String BLOB_MSG_ISLAST = "1";

	// ESB冲正和重发
	public static final String RVSL_REASON_TIMEOUT = "TIMEOUT";
	public static final String RVSL_REASON_BPL_ERR = "BPLERR";
	public static final String RVSL_REASON_FAILED = "FAILED";
	public static final String RVSL_ACTION_REVERSAL = "REVERSAL";
	public static final String RVSL_ACTION_RESEND = "RESEND";
	public static final String RVSL_STATUS_DOING = "U";
	public static final String RVSL_STATUS_DONE = "S";
	public static final String RVSL_STATUS_FAIL = "F";
	public static final String RVSLSUB_STATUS_DOING = "doing";// 开始冲正尚无返回
	public static final String RVSLSUB_STATUS_DONE = "000000";
	public static final String RVSLSUB_STATUS_TIMOUT = "timout";
	public static final int RVSL_STEP_ATOM = 1;
	public static final int RVSL_STEP_DONE = 0;
	public static final int RVSL_TIMES_DEFAULT = 5;
	public static final int RVSL_TIMES_OVER = 0;

	// 2012-9-12 add by wangyong IBMMB超时常量定义----start-----
	public static final String DEFAULT_ESBLOG = "ESB.LOG";
	public static final String DEFAULT_ESBALARM = "ESB.ALARM";
	public static final String DEFAULT_TIMEOUTFILTERQ = "TIMEOUT.FILTER";
	public static final String DEFAULT_TIMEOUTQ = "ESB.TIMEOUT";
	public static final String ESB_DEFAULT_TIMEOUTQM = "QM_GW_IN";
	public static final int REQ_DEF_TIMEOUTTIME = 600;
	public static final int REP_DEF_TIMEOUTTIME = 3000;
	// 2012-9-12 add by wangyong IBMMB超时常量定义----end-----

	public static final String VERSION()
	{
		return VERSION;
	}

	public static final String VERSION_DATE()
	{
		return VERSION_DATE;
	}
}
