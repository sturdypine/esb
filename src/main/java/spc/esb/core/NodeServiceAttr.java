package spc.esb.core;

import spc.webos.util.StringX;

/**
 * 渠道服务关系属性 
 * X 1位(0表示未授权访问，1表示已授权)
 * X 1位(1表示自动过滤为过滤标签)
 * X 1位(1表示校验报文体)
 * 
 * @author chenjs
 * 
 */
public class NodeServiceAttr
{
	String attr = "10000000000000000000000000000000"; // 默认第一位表示授权访问

	public NodeServiceAttr()
	{
	}

	public NodeServiceAttr(String attr)
	{
		if (!StringX.nullity(attr)) this.attr = (attr.length() >= 32 ? attr : attr + this.attr);
	}

	public boolean isAuth()
	{
		return attr.charAt(0) != '0';
	}

	public boolean isAutoFilterUndefinedTag()
	{
		return attr.charAt(1) != '0';
	}

	public boolean isValidateBody()
	{
		return attr.charAt(2) != '0';
	}
}