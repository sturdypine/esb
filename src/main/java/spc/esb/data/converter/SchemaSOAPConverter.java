package spc.esb.data.converter;

import javax.annotation.Resource;

import spc.esb.data.IMessage;
import spc.esb.data.MessageSchema;
import spc.esb.data.util.CNode2XmlNodeVisitor;
import spc.esb.data.util.MessageTraversal;

/**
 * 根据报文结构，序列化xml报文，使之保持schema的顺序关系
 * 
 * @author spc
 * 
 */
public class SchemaSOAPConverter extends SOAPConverter
{
	public byte[] serialize(IMessage msg) throws Exception
	{
		StringBuilder xml = new StringBuilder();
		xml.append(STR_SOAP_ROOT_START_TAG);
		CNode2XmlNodeVisitor cnode2xmlNodeVisitor = pretty ? new CNode2XmlNodeVisitor(true, 1)
				: new CNode2XmlNodeVisitor();
		MessageTraversal msgTraversal = new MessageTraversal(msg.getHeader(),
				msgSchema.getMsgSchema(esbHdrSchemaCd));
		msgTraversal.dfs(cnode2xmlNodeVisitor);
		xml.append(cnode2xmlNodeVisitor.toXml());

		xml.append('\n');
		xml.append(STR_SOAP_MID);

		msgTraversal.setRoot(msg.getResponse());
		msgTraversal.setSchema(msgSchema.getMsgSchema(msg.getMsgCd()));
		cnode2xmlNodeVisitor.clear();
		msgTraversal.dfs(cnode2xmlNodeVisitor);
		xml.append(cnode2xmlNodeVisitor.toXml());

		xml.append('\n');
		xml.append(STR_SOAP_ROOT_END_TAG);
		return xml.toString().getBytes(charset);
	}

	protected String esbHdrSchemaCd = "ESBREP";
	@Resource
	protected MessageSchema msgSchema;

	public void setMsgSchema(MessageSchema msgSchema)
	{
		this.msgSchema = msgSchema;
	}

	public void setEsbHdrSchemaCd(String esbHdrSchemaCd)
	{
		this.esbHdrSchemaCd = esbHdrSchemaCd;
	}
}
