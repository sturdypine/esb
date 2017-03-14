package spc.esb.data;

import spc.webos.util.StringX;

/**
 * 使用独立的管理，报文属性采用32位数字统一定义，32位数字定义说明如下： 
 * X 1位(超时时间，0为默认60秒, 1-3单位为10秒，4-6单位为30秒，7-9单位为60秒) 
 * X 1位(二进制:xyz,x:1表示记录全报文，y:表示记录03点日志，z:表示记录12点日志) 
 * X 1位(0不需要冲正, 1需要后端冲正, 3表示后端和前端超时未取都需要冲正)
 * X 1位(0表示无需签名,1表示需要前端签名, 2表示服务系统需要签名，3表示前端请求和后台服务系统均需要签名) 
 * X 1位(0表示同步, 1表示异步, 2表示必须异步,不支持异步转同步) 
 * X 1位(0表示无返回,其他表示返回报文个数) 
 * X 1位(0表示ESB不提供补发, 1表示ESB提供补发机制，一般ESB只对重要的通知型报文进行补发，需要提供补发流水号) 
 * X 1位(0表示普通报文交易, 1表示BLOB消息的附件交易) 
 * X 1位(0表示报文中不含数组标签, 1表示含有) 
 * X 1位(0表示异步日志,1表示同步日志) 
 * X 1位(0普通交易, 1广播交易) 
 * X 1位(0普通交易, 1流程bpel类交易) 
 * X 1位(0普通交易, 1应答出错时交易需要冲正) 
 * X 1位(0无通讯回执交易, (1,3)表示有ESB通讯回执，(2,3)表示有接受方系统通讯回执) 
 * X 1位(0表示消息不需要MQ持久, 1表示需要持久化 
 * X 1位(1表示需要请求消息需要超时监控 
 * X 1位(1表示echo报文 
 * X 1位(表示服务的优先级0-9) 
 * X 1位(1表示穿透(不校验，不转换)报文 
 * X 1位(1表示此报文需要schema1 -> schema2的转换) added by chenjs 2011-10-17 
 * X 1位(1表示此报文含有PIN字段) added by chenjs 2011-11-10 
 * X 1位(1表示此报文需要进行操作风险监控 added by chenjs 2012-01-20) 
 * X 1位(1表示过滤掉schema未定义的标签 added by chenjs 2012-08-06) 
 * X 1位000000000000 保留 
 * X 1位(0表示无需MB转换,1表示需要MB转换) 
 * X 1位(0表示无需MB签名,1表示需要服务方MB签名)
 * 
 * @author spc
 * 
 */
public class MessageAttr
{
	String attr;
	// public final static int TIMEOUT_STEP = 30;
	public static int DEFAULT_TIMEOUT = 60;
	public static int MAX_TIMEOUT = 99999999;
	public final static char YES = '1';
	public final static char NO = '0';

	public MessageAttr(String attr)
	{
		this.attr = attr == null ? null : attr.trim();
	}

	public boolean isValid()
	{
		return !StringX.nullity(attr);
	}

	public int getTimeout()
	{
		return timeout();
	}

	public int timeout()
	{
		int timeout = Integer.parseInt(attr.substring(0, 1));
		// 2011-09-01 chenjs, 0为默认60秒, 1-3单位为10秒，4-6单位为30秒，7-9单位为60秒
		if (timeout <= 0) return DEFAULT_TIMEOUT;
		if (timeout <= 3) return timeout * 10;
		if (timeout <= 6) return timeout * 30;
		if (timeout == 9) return MAX_TIMEOUT; // 2012-02-24 如果配置为9则表示接近无限制的超时时间
		return timeout * 60;
	}

	// public boolean isQueryErr()
	// { // 查询交易出错时记录报文
	// // return '2' == attr.charAt(1);
	// return false;
	// }

	// 是否只记两个日志点, 含全报文
	public boolean isFullLog()
	{
		return (4 & (attr.charAt(1) - '0')) > 0;
	}

	// 是否只记两个日志点, 不含全报文
	public boolean isLog(String logPoint)
	{
		if (logPoint == "0" || logPoint == "3") return (2 & (attr.charAt(1) - '0')) > 0;
		return (1 & (attr.charAt(1) - '0')) > 0;
	}

	// 前端超时未取需要冲正
	public boolean isFrontReversal()
	{
		return '3' == attr.charAt(2);
	}

	public boolean isReversal()
	{
		return NO != attr.charAt(2);
	}

	public boolean isSig()
	{
		// return YES == attr.charAt(3) || '3' == attr.charAt(3);
		return NO != attr.charAt(3); // modified by spc 2011-03-08
	}

	public boolean isSndSig()
	{
		return '1' == attr.charAt(3) || '3' == attr.charAt(3);
	}

	public boolean isRcvSig()
	{
		return '2' == attr.charAt(3) || '3' == attr.charAt(3);
	}

	public String sig()
	{
		return String.valueOf(attr.charAt(3));
	}

	public String getSignature()
	{
		return String.valueOf(attr.charAt(3));
	}

	public boolean isAsyn()
	{
		return NO != attr.charAt(4);
	}

	public boolean isMustAsyn()
	{
		return '2' == attr.charAt(4);
	}

	public int getResMsgNum()
	{
		return Integer.parseInt(String.valueOf(attr.charAt(5)));
	}

	public int resMsgNum()
	{
		return Integer.parseInt(String.valueOf(attr.charAt(5)));
	}

	public boolean isRenotice()
	{
		return NO != attr.charAt(6);
	}

	public boolean isBlob()
	{
		return YES == attr.charAt(7);
	}

	// added by spc 2010.1.20
	// 当前报文是否包含数组节点，由于数据表示模式存在差异
	public boolean isContainArray()
	{
		return YES == attr.charAt(8);
	}

	// 当前报文是否为关键报文，需要同步记录数据库，而不是普通交易的异步记录数据库
	public boolean isInSynJournal()
	{
		return (Integer.parseInt(String.valueOf(attr.charAt(9))) & 1) > 0;
	}

	public boolean isOutSynJournal()
	{
		return (Integer.parseInt(String.valueOf(attr.charAt(9))) & 2) > 0;
	}

	// 当前报文是广播交易
	public boolean isBroadcast()
	{
		return YES == attr.charAt(10);
	}

	// 是否属于流程交易
	public boolean isBpl()
	{
		return NO != attr.charAt(11);
	}

	// 是否错误应答时需要冲正
	public boolean isErrReversal()
	{
		return NO != attr.charAt(12);
	}

	// 此报文是否有通讯回执
	public boolean isReturnReceipt()
	{
		return NO != attr.charAt(13);
	}

	// 此报文是否含有ESB通讯回执
	public boolean isEsbReturnReceipt()
	{
		return attr.charAt(13) == '1' || attr.charAt(13) == '3';
	}

	// 此报文是否含有接收方系统的通讯回执
	public boolean isRcvReturnReceipt()
	{
		return attr.charAt(13) == '2' || attr.charAt(13) == '3';
	}

	public boolean isPersist()
	{
		return attr.charAt(14) == YES;
	}

	public boolean isWatchTimeout()
	{
		return attr.charAt(15) == YES;
	}

	public boolean isEcho()
	{
		return attr.charAt(16) == YES;
	}

	public int priority()
	{
		return Integer.parseInt(String.valueOf(attr.charAt(17)));
	}

	public boolean isIgnoreBody()
	{
		return attr.charAt(18) == YES;
	}

	public boolean isNeedMapping()
	{
		return attr.charAt(19) == YES;
	}

	public boolean isContainPIN()
	{
		return attr.charAt(20) == YES;
	}

	public boolean isRisk()
	{
		return attr.charAt(21) != NO;
	}

	// 实时风险监测
	public boolean isRealTimeRisk()
	{
		return attr.charAt(21) == YES;
	}
	// added by spc 2010.1.20 end

	public boolean isAutoFilterUndefinedTag()
	{
		return attr.charAt(22) == YES;
	}

	// mb 需要的属性
	public boolean isMbConvert()
	{
		// 倒数第二位 modified by spc at 2010.02.05 便于属性字段的长度扩展
		return YES == attr.charAt(attr.length() - 2); // 14
	}

	public boolean isMbSig()
	{
		// 最后一位 modified by spc at 2010.02.05
		// return YES == attr.charAt(attr.length() - 1); // 15
		// modified by spc 2011-03-08
		return NO != attr.charAt(attr.length() - 1);
	}
	// mb 需要属性 end

	public String toAttr()
	{
		return attr;
	}

	public String toString()
	{
		return attr;
	}
}
