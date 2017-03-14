package spc.esb.constant;

public class ESBMsgCode
{
	// app code
	public static String APPCD_ECHO = "ECHO"; // ESB 内部联通测试系统身份
	public static String APPCD_MB = "MB"; // ESB 内部消息模块系统身份
	public static String APPCD_RVSL = "RVSL"; // ESB 内部冲正模块系统身份
	public static String APPCD_SEC = "SEC"; // ESB 内部安全模块系统身份

	public static String APPCD_TIPS = "TIPS";
	public static String APPCD_TELLER = "TELLER";
	public static String APPCD_TLR = "TLR";
	public static String APPCD_NBS = "NBS";
	public static String APPCD_OA = "OA";
	public static String APPCD_PMS = "PMS";
	public static String APPCD_NDS = "NDS";
	public static String APPCD_ECD = "ECD";
	public static String APPCD_ESB = "ESB";
	public static String APPCD_NNS = "NNS";
	public static String APPCD_CBS = "CBS";
	public static String APPCD_CMS = "CMS";
	public static String APPCD_NDM = "NDM";
	public static String NCC_NODECODE = "0000";
	public static String SELF_NODECODE = "0000";
	public static String NOTSELF_NODECODE = "NONE";
	public static String ALL_NODE = "9999";
	public static String ALL_APP = "ALL";

	// 报文编号
	public static String MSGCD_ESBREQ = "ESBREQ"; // ESB请求头编号
	public static String MSGCD_ESBREP = "ESBREP"; // ESB应答头编号
	// public static String MSGCD_FC_APLLY = "fcAplly"; // 申请流控资源
	// public static String MSGCD_FC_RELEASE = "fcRelease"; // 释放流控资源
	// public static String MSGCD_FC_CHECK = "fcCheck"; // 检查流量资源状态

	public static String MSGCD_NOMSGCD_ERR = "ESB.00000010.01"; // ESB通用请求流程没有报文编号，返回通用默认报文编号
	public static String MSGCD_REQERR = "ESB.00000011.01"; // ESB通用请求流程异常返回报文编号
	public static String MSGCD_REPERR = "ESB.00000021.01"; // ESB通用应答流程异常返回报文编号
	public static String MSGCD_FILEERR = "ESB.00000031.01"; // ESB通用文件传输异常
	public static String MSGCD_COMM_RECEIPT = "ESB.99999971.01";// ESB通讯回执报文编号
	public static String MSGCD_RENOTICE = "ESB.00000040.01"; // ESB补发通知报文编号

	public static String MSGCD_RVSL_ASYN = "RVSL.99999980.01";// 异步冲正报文编号
	public static String MSGCD_RVSL_ASYN_REP = "RVSL.99999981.01";// 异步冲正应答报文编号
	public static String MSGCD_RVSL_SYN = "RVSL.99999990.01";// 同步冲正报文编号
	public static String MSGCD_RVSL_SYN_REP = "RVSL.99999991.01";// 同步冲正应答报文编号

	// public static String MSGCD_SEC_APPLY_PK = "SEC000000100"; // 申请ESB公钥报文编号
	// public static String MSGCD_SEC_NNS_ENDECODE = "SEC000000200"; //
	// 申请ESB公钥报文编号

	public static String MSGCD_ESBREQ()
	{
		return MSGCD_ESBREQ;
	}

	public static String MSGCD_ESBREP()
	{
		return MSGCD_ESBREP;
	}

	public static String MSGCD_REQERR()
	{
		return MSGCD_REQERR;
	}

	public static String MSGCD_REPERR()
	{
		return MSGCD_REPERR;
	}

	public static String MSGCD_FILEERR()
	{
		return MSGCD_FILEERR;
	}

	public static String MSGCD_COMM_RECEIPT()
	{
		return MSGCD_COMM_RECEIPT;
	}

	public static String MSGCD_RVSL_ASYN()
	{
		return MSGCD_RVSL_ASYN;
	}

	public static String MSGCD_RVSL_ASYN_REP()
	{
		return MSGCD_RVSL_ASYN_REP;
	}

	public static String MSGCD_RVSL_SYN()
	{
		return MSGCD_RVSL_SYN;
	}

	public static String MSGCD_RVSL_SYN_REP()
	{
		return MSGCD_RVSL_SYN_REP;
	}

	public static String MSGCD_RENOTICE()
	{
		return MSGCD_RENOTICE;
	}

	// public static String MSGCD_SEC_APPLY_PK()
	// {
	// return MSGCD_SEC_APPLY_PK;
	// }
	//
	// public static String MSGCD_SEC_NNS_ENDECODE()
	// {
	// return MSGCD_SEC_NNS_ENDECODE;
	// }
}
