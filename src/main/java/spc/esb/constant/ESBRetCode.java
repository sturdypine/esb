package spc.esb.constant;

import spc.webos.constant.AppRetCode;

/**
 * 系统错误采用统一编码模式实现, 错误码为12位: XX+XXXX 两位类别编号 编号说明 备注 0X 成功
 * 处理成功后的返回信息码，通用成功码为000000 1X 系统类错误 10 通信类错误 系统通信产生的错误类 11 文件类错误 12 数据库类错误 13
 * 中间件类错误 MQ,tuxedo，MB，WPS等 19 其他系统类错误 2X 报文类错误 20 报文格式类错误 29 其他报文类错误 3X 业务类错误
 * 30 业务授权错误 31 账户类错误 39 其他未归类业务错误 99 其他未归类错误
 * 
 * @author spc
 * 
 */
public class ESBRetCode extends AppRetCode
{
	public static String NO_ENDPOINT = "100000"; // 没有指定endpoint

	// 20报文格式错误
//	public static String MSG_ERRS = "200000"; // 报文整体校验不通过
	public static String MSG_SNDAPPCD = "208886"; // 不合法的发送方系统
	public static String MSG_UNVALIDCHAR = "208888"; // 报文含有非法字符
	public static String MSG_FIELD_VALIDATOR = "209999"; // 报文中节点验证通用错误码，此错误码不指定具体的错误模板信息,不配置入数据库
	public static String MSG_BYTE2XML = "200001"; // 从bytes到xml解析错误
	public static String MSG_2INNEROBJ = "200002"; // 从xml报文变为bytes出错
	public static String MSG_UNDEF_TAG = "200003"; // 请求报文中没有指定TAG
	public static String MSG_UNMATCH_TYPE = "200004"; // 应用程序要求的节点类型和报文不符合
														// args=path,cur_type,target_type
	public static String MSG_UNSIG_FAIL = "200005"; // 报文验签失败
	public static String MUST_OPTIONAL = "200006"; // 必输项未填写
	public static String MSG_STRING_FORMAT = "200007"; // 把字符串{0}从当前格式{1}转为目标格式{2]失败
	public static String MSG_FIELD_INVALID = "200008"; // 报文字段{0}的值{1}不合法{2}
	// public static String NO_ATOMCONVERTER = "200009"; //
	// 找不到对于的转换器args=fconverter
	public static String MSG_SCHEMA_FAILD = "200010"; // 报文schema检查失败{0}
	public static String MSG_FIELD_LEN = "200011"; // 报文字段{0}的长度{1}范围不合法{2}-{3}
	public static String MSG_FIELD_VAL_RANGE = "200012"; // 报文字段{0}{1}范围不合法{2}-{3}
	public static String MSG_FIELD_REG = "200013"; // 报文字段{0}的值{1}不正则表达式{2}
	public static String MSG_FIELD_DECIMAL = "200014"; // 报文字段{0}值为{1]的小数点长度超过{2}
	public static String MSG_FIELD_NOTNUMBER = "200015"; // 报文字段{0}值为{1}不是数字类型

	// 21 cache中无法找到需要从数据库表加载的信息
	public static String MSGCD_NOTFOUND = "210001"; // 找不到报文信息
	public static String MSGSTRUCT_NOTFOUND = "210002";// 找不到报文结构
	public static String CACHEINFO_NOTFOUND = "210003";// 找不到需要的数据库缓存信息

	// 22
	public static String MSGCD_UNDEFINDED = "220001"; // 报文编号找不到
	public static String MSG_BRD_NOTRETURN = "200017"; // 广播报文无返回
}
