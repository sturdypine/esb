package spc.esb.data.xml;

import spc.esb.model.MsgSchemaPO;

/**
 * XML转换时使用的目标，目前平台默认使用rcvname， 容许客户化使用cmt等未来schema表的其他字段 2011-12-08
 * 
 * @author chenjs
 * 
 */
public interface ISchemaTargetXMLTag
{
	String xmlTag(MsgSchemaPO schema);
}
