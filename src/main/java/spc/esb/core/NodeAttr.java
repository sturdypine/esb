package spc.esb.core;

import spc.webos.util.StringX;

/**
 * 应用节点/渠道的属性
 * X 1位(优先级，0-9) 
 * X 2位(0表示基于body签名，1表示基于要素签名) 
 * X 3位( 0:加签并且验签;1:加签但不验签;2:不加签但是验签;3. 不加签也不验签)
 * X 4位(0: ESB内部ISO20022报文， 1: 表示此渠道接收SOAP报文1.0, 2: SOAP 2.0,  8：json, 9: 表示自定义报文(定长, ISO8583 etc.)) 
 * X 5位(1表示此渠道使用JMS协议调用，默认为MQ) 
 * X 6位(0: utf-8, 1:EBCD, 2:GBK)
 * X 7位(PIN字段类型, 0表示渠道没有PIN字段，1表示DES)  
 * X 8位(是否异常退出，0: 请求退出，应答不退出(也是我们以前的默认情况)
                      1: 请求不退出，应答不退出；
                      2: 请求退出，应答退出；
                      3. 请求不退出，应答退出。
* X 9位(1: 签名采用MD5进行摘要，2: 用SHA-1进行摘要，其他表示不对签名内容进行摘要直接计算签名内容)
* X 10位(作为渠道方是否有效:0 均有效，1: 渠道无效，2：表示服务无效，3:表示均无效)
* X 11位(同步应答消息REP.NBS的消息有效时间，平台默认为10s: 0表示使用平台默认，1-8每个单位为1s, 9表示60s)
* X 12位(1表示自动过滤未定义的标签, 如果0表示使用平台esb_config属性)
* X 13位(1表示节点作为请求方时，应答报文不需要报文request标签信息)
* X 14位(1表示节点作为请求方时，应答报文不需要报文header/ext标签信息)
* X 15位(1表示节点只能接收header/msg/ext作为字符串标签，而非复杂标签)
* X 16位()
* X 17位(用于表示作为前端渠道时, 集群ID位数，用于应答报文生成A1000000000)
* X 18位(1用于表示服务系统需要把replyToQ放入header/ext)
* X 19位(1用于表示系统和ESB通讯的esb xml报文使用了3DES软加密)
 * @author chenjs
 * 
 */
public class NodeAttr
{
	String appAttr = "00000000000000000000000000000000";

	public NodeAttr()
	{
	}

	public NodeAttr(String appAttr)
	{
		if (!StringX.nullity(appAttr)) this.appAttr = (appAttr.length() >= 32 ? appAttr : appAttr
				+ this.appAttr);
	}

	public int priority()
	{
		return Integer.parseInt(String.valueOf(appAttr.charAt(0)));
	}

	public boolean isBodySig()
	{
		return appAttr.charAt(1) == '0';
	}

	public boolean isElementSig()
	{
		return appAttr.charAt(1) == '1';
	}

	public String getSigMode()
	{
		return String.valueOf(appAttr.charAt(1));
	}

	public boolean isNotUnsig()
	{
		return appAttr.charAt(2) == '1' || appAttr.charAt(2) == '3';
	}

	public boolean isNotSig()
	{
		return appAttr.charAt(2) == '2' || appAttr.charAt(2) == '3';
	}

	public boolean isSOAP()
	{
		return appAttr.charAt(3) == '1' || appAttr.charAt(3) == '2';
	}
	
	// 支持不同版本的soap规范2013-03-26
	public String getSOAPVersion()
	{
		return String.valueOf(appAttr.charAt(3));
	}
	
	// json格式
	public boolean isJSON()
	{
		return appAttr.charAt(3) == '8';
	}
	
	// 用户自定义报文规范
	public boolean isUserDefined()
	{
		return appAttr.charAt(3) == '9';
	}

	public boolean isJMS()
	{
		return appAttr.charAt(4) == '1';
	}

	public boolean isUTF8()
	{
		return appAttr.charAt(5) == '0';
	}

	public boolean isEBCD()
	{
		return appAttr.charAt(5) == '1';
	}

	public boolean isGBK()
	{
		return appAttr.charAt(5) == '2';
	}

	public String getCharset()
	{
		return String.valueOf(appAttr.charAt(5));
	}

	public boolean isDesPin()
	{
		return appAttr.charAt(6) == '1';
	}

	public String getDesPin()
	{
		return String.valueOf(appAttr.charAt(6));
	}

	public boolean isRequestExExit()
	{
		return appAttr.charAt(7) == '0' || appAttr.charAt(7) == '2';
	}

	public boolean isResponseExExit()
	{
		return appAttr.charAt(7) == '2' || appAttr.charAt(7) == '3';
	}

	public String getSigDigestAlg()
	{
		switch (appAttr.charAt(8))
		{
			case '1':
				return "MD5";
			case '2':
				return "SHA-1";
		}
		return StringX.EMPTY_STRING;
	}

	public boolean isUnvalidChannel()
	{
		return (appAttr.charAt(9) == '1' || appAttr.charAt(9) == '3');
	}

	public boolean isUnvalidServer()
	{
		return (appAttr.charAt(9) == '2' || appAttr.charAt(9) == '3');
	}

	public int getSynRepExpireSeconds()
	{
		int s = appAttr.charAt(10) - '0';
		if (s >= 0 && s < 7) return s;
		if (s == 7) return 20;
		if (s == 8) return 30;
		return 60;
	}

	public boolean isAutoFilterUndefinedTag()
	{
		return appAttr.charAt(11) == '1';
	}

	public boolean isNoRequestTag()
	{
		return appAttr.charAt(12) == '1';
	}

	public boolean isNoExtTag()
	{
		return appAttr.charAt(13) == '1';
	}

	public boolean isExt2Str()
	{ // 500, 1表示服务方只接受字符串ext类型，0表示任意类型
		return appAttr.charAt(14) == '1';
	}

	public int getClusterIdLen()
	{
		if (appAttr.charAt(16) == '0') return 2;
		return appAttr.charAt(16) - '0';
	}
	
	public boolean isReplyToQInExt()
	{
		return appAttr.charAt(17) != '0';
	}
	
	public boolean isBodyEncryption()
	{
		return appAttr.charAt(18) != '0';
	}
}
