package spc.esb.core;

import spc.webos.util.StringX;

/**
 * ESB_service表。服务属性
 * X 1位(服务的报文类型，0-9) 
 * @author chenjs
 * 
 */
public class ServiceAttr
{
	String attr = "00000000000000000000000000000000";

	public ServiceAttr()
	{
	}

	public ServiceAttr(String attr)
	{
		if (!StringX.nullity(attr)) this.attr = (attr.length() >= 32 ? attr : attr + this.attr);
	}
	
	public int getMsgType()
	{
		return attr.charAt(0) - '0';
	}

	public String toString()
	{
		return attr;
	}
}
