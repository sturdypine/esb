package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * 如果节点要序列化为webservice规范节点
 * 
 * @author chenjs
 * 
 */
public class SOAPNode2XML extends Array2Node2XML
{
	protected void ext2XML(OutputStream os, INode value, ICompositeNode parent, String name,
			Map ext, Map attribute) throws IOException
	{
		super.ext2XML(os, value, parent, name, ext, attribute);
		if ((value instanceof ICompositeNode) && (ext == null || !ext.containsKey("xsi:type"))) addAttr(
				os, "xsi:type".getBytes(), ("esb:" + name + "Type").getBytes());
		// 对于复杂节点的类型参看Wsdl11DefinitionService.struct2schema生成WSDL文件方式
	}
}
