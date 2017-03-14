package spc.esb.constant;

public class ESBMsgKey
{
	// added by chenjs 2011-10-10 request标签中存放冲正参考原报文信息rvslRef
	public static final String TAG_REVERSAL_REF = "rvslRef";

	public static final String PATH_RCVAPPSN = "header/msg/rcvAppSn";
	public static final String PATH_REFRCVAPPSN = "header/msg/refRcvAppSn";

	// liujk 2013-8-14,在header/ext/中指定验签标志
	public static final String TAG_UNSIGN = "unsign";

	// for res
	public final static String RES_RESULT = "result";
	public final static String RES_ALLOCATE_SN = "sn";
	public final static String RES_ALLOCATE_KEY = "key";
	public final static String RES_ALLOCATE_HOLD_TIME = "holdTm";
	public final static String RES_ALLOCATE_TIMEOUT = "timeout";
	public final static String RES_ALLOCATE_MATCH_TYPE = "matchType";
	public final static String RES_WITHOUTRETURN = "withoutReturn";

	// for bpl
	public final static String EXT_BPL = "bpl";
	public final static String BPL_PMSGSN = "psn";
	public final static String BPL_NODE_NAME = "nodeNm";
	public final static String BPL_FAIL_ABORT = "failAbort";
	public final static String BPL_PSNDAPPCD = "psndAppCd";
	public final static String BPL_PSNDMBRCD = "psndMbrCd";

	// for fa
	public final static String EXT_FA = "fa";
	public final static String FA_CORID = "corId"; // 存放前端MQ消息匹配码
}
