package spc.esb.core;

import spc.webos.util.StringX;

/**
 * ESB_msgschema表的字段属性
 * X 1位(1表示是字段的扩展属性) 
 * X 2位(1表示是敏感字段，日志是不能输出)
 * X 3位(1表示是BCD报文的P类型(Pack))
 * X 4位(0表示不记录数据库biz字段，1-9表示记录进对应的biz字段)
 * X 5位(1表示此字段需要引用元数据信息)
 * X 6位(1表示此字段无需再报文规范中体现属于隐藏字段，其值可能是默认值可能是后台加工值)    
 * @author chenjs
 * 
 */
public class TagAttr
{
	String attr = "00000000000000000000000000000000";

	public TagAttr()
	{
	}

	public TagAttr(String attr)
	{
		if (!StringX.nullity(attr)) this.attr = (attr.length() >= 32 ? attr : attr + this.attr);
	}

	public boolean isTagExtAttr()
	{
		return attr.charAt(0) == '1';
	}

	public boolean isSensitive()
	{
		return attr.charAt(1) == '1';
	}

	public boolean isBcdPack()
	{
		return attr.charAt(2) == '1';
	}
	
	public String getBizNo()
	{
		return String.valueOf(attr.charAt(3));
	}
	
	public boolean isMetaData()
	{
		return attr.charAt(4) == '1';
	}
	
	public boolean isHidden()
	{
		return attr.charAt(5) == '1';
	}

	public String toString()
	{
		return attr;
	}
}
