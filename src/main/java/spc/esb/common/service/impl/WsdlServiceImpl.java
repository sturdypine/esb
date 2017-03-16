package spc.esb.common.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import spc.esb.common.service.MsgDefService;
import spc.esb.common.service.WsdlService;
import spc.esb.constant.ESBCommon;
import spc.esb.core.TagAttr;
import spc.esb.data.INode;
import spc.esb.data.util.AbstractSchema2SampleTreeNodeVistor;
import spc.esb.data.util.Schema2SampleJsonTreeNodeVistor;
import spc.esb.data.util.Schema2SampleXmlTreeNodeVistor;
import spc.esb.model.MsgSchemaPO;
import spc.esb.model.ServicePO;
import spc.webos.persistence.jdbc.datasource.SwitchDS;
import spc.webos.service.BaseService;
import spc.webos.util.FTLUtil;
import spc.webos.util.FileUtil;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

@Service("esbWsdlService")
public class WsdlServiceImpl extends BaseService implements WsdlService
{
	String path = "esb/";
	protected String schemaFtl = "schema";
	protected String wsdlFtl = "wsdl11";
	protected String sampleFtl = "sample";
	@Value("${esb.wsdl.flat?true}")
	protected boolean flat = true;
	final static String MODEL_KEY = "MODEL";
	@Resource
	protected MsgDefService msgDefService;

	public String sample(String msgCd, int type) throws Exception
	{
		Map param = new HashMap();
		param.put("msgCd", msgCd);
		param.put("type", type);
		param.put("response", msgDefService.getService(msgCd) == null); // 是否是response报文看是否能查到service
		AbstractSchema2SampleTreeNodeVistor vistor = type == 0
				? new Schema2SampleXmlTreeNodeVistor(2) : new Schema2SampleJsonTreeNodeVistor(2);
		param.put("sample", vistor.sample(getMsgSchema(msgCd)));
		return FTLUtil.freemarker(FTLUtil.getTemplate(path + sampleFtl), param);
	}

	public String wsdl(String msgCd) throws Exception
	{
		Map param = new HashMap();
		param.put("noheader", Boolean.TRUE);
		ServicePO serviceVO = msgDefService.getService(msgCd);
		String resMsgCd = (String) param.get("resMsgCd");
		if (StringX.nullity(resMsgCd)) resMsgCd = serviceVO.getRepMsgCd();
		log.info("wsdl reqMsgCd:{}, resMsgCd:{}", msgCd, resMsgCd);
		param.put("serviceVO", serviceVO);
		param.put("reqMsgCd", msgCd);
		param.put("resMsgCd", resMsgCd);
		param.put("flat", flat);
		param.put("reqCd", StringX.replaceAll(msgCd, ".", ""));
		param.put("resCd", StringX.replaceAll(resMsgCd, ".", ""));
		message(msgCd, resMsgCd, getMsgSchema(msgCd), getMsgSchema(resMsgCd), param);

		return FTLUtil.freemarker(FTLUtil.getTemplate(path + wsdlFtl), param);
	}

	public String schema(String msgCd) throws Exception
	{
		Map param = new HashMap();
		param.put("msgCd", msgCd);
		param.put("response", msgDefService.getService(msgCd) == null); // 是否是response报文看是否能查到service
		return schema(msgCd, getMsgSchema(msgCd), param);
	}

	public void schema2java(String appCd, String category, String basePkg, String apiDir,
			String implDir) throws Exception
	{
		try (SwitchDS ds = new SwitchDS(ESBCommon.ESB_DS))
		{
			ServicePO po = new ServicePO();
			po.setAppCd(appCd);
			List<ServicePO> services = persistence.get(po);
			if (services == null || services.isEmpty()) return;
			basePkg = basePkg + ".esb." + appCd.toLowerCase();
			schema2java(appCd, services, basePkg, apiDir, implDir);
		}
	}

	protected void schema2java(String appCd, List<ServicePO> services, String basePkg,
			String apiDir, String implDir) throws Exception
	{
		String voPkg = basePkg + ".vo";
		String inf_name = appCd.toUpperCase() + "Service";
		String impl_name = inf_name + "Impl";
		StringBuilder inf = new StringBuilder("package " + basePkg
				+ ".service;\nimport spc.webos.exception.AppException;\nimport " + voPkg
				+ ".*;\npublic interface " + inf_name + "{");

		StringBuilder impl = new StringBuilder("package " + basePkg
				+ ".service.impl;\nimport javax.annotation.Resource;\nimport org.springframework.stereotype.Service;\nimport spc.esb.core.service.ESBService;\nimport "
				+ basePkg + ".service." + inf_name + ";\nimport " + voPkg + ".*;\n@Service(\"esb"
				+ inf_name + "\")\npublic class " + impl_name + " implements " + inf_name
				+ "{\n@Resource\nESBService esbService;");
		for (ServicePO s : services)
		{
			String reqfn = StringX.replaceAll(s.getReqMsgCd(), ".", "").toUpperCase();
			String repfn = StringX.replaceAll(s.getRepMsgCd(), ".", "").toUpperCase();
			if (!StringX.nullity(s.getReqMsgCd()))
				FileUtil.writeToFile(schema2vo(s.getReqMsgCd(), voPkg).getBytes(),
						new File(apiDir + voPkg.replace('.', '/') + "/" + reqfn + ".java"), false);
			if (!StringX.nullity(s.getRepMsgCd()))
				FileUtil.writeToFile(schema2vo(s.getRepMsgCd(), voPkg).getBytes(),
						new File(apiDir + voPkg.replace('.', '/') + "/" + repfn + ".java"), false);
			inf.append("\n" + repfn + " call(" + reqfn + " request) throws AppException;");
			impl.append("\npublic " + repfn + " call(" + reqfn + " request){");
			impl.append("\nreturn esbService.call(\"" + s.getReqMsgCd() + "\", request, new "
					+ repfn + "());");
			impl.append("\n}");
		}
		inf.append("\n}");
		impl.append("\n}");
		FileUtil.writeToFile(inf.toString().getBytes(),
				new File(apiDir + basePkg.replace('.', '/') + "/service/" + inf_name + ".java"),
				false);
		FileUtil.writeToFile(impl.toString().getBytes(), new File(
				implDir + basePkg.replace('.', '/') + "/service/impl/" + impl_name + ".java"),
				false);
	}

	public String schema2vo(String msgCd, String pkg) throws Exception
	{
		try (SwitchDS ds = new SwitchDS(ESBCommon.ESB_DS))
		{
			final StringBuilder str = new StringBuilder();
			str.append("package " + pkg + ";\n");
			str.append("\nimport java.util.List;\nimport java.io.Serializable;\npublic ");
			str.append(schema2vo(getMsgSchema(msgCd),
					StringX.replaceAll(msgCd, ".", "").toUpperCase()));
			return str.toString();
		}
	}

	protected String schema2vo(TreeNode schema, String vo)
	{
		final StringBuilder str = new StringBuilder();
		str.append("class " + vo
				+ " implements Serializable\n{\npublic static final long serialVersionUID = 1L;");
		List<TreeNode> items = schema.getChildren();
		items.forEach((item) -> {
			MsgSchemaPO s = (MsgSchemaPO) item.getTreeNodeValue();
			TagAttr tagAttr = new TagAttr(s.getTagAttr());
			if (tagAttr.isHidden()) return;
			String type = "String";
			String name = s.getEsbName();
			if (s.getFtyp().charAt(0) == 'L') type = "Long";
			else if (s.getFtyp().charAt(0) == 'I') type = "Integer";
			else if (s.getFtyp().charAt(0) == 'D') type = "Double";
			else if (s.getFtyp().charAt(0) == 'M')
				type = name.substring(0, 1).toUpperCase() + name.substring(1);
			else if (s.getFtyp().charAt(0) == 'A')
			{
				if (s.getFtyp().charAt(1) == 'L') type = "Long";
				else if (s.getFtyp().charAt(1) == 'I') type = "Integer";
				else if (s.getFtyp().charAt(1) == 'M')
					type = name.substring(0, 1).toUpperCase() + name.substring(1);
				type = "List<" + type + ">";
			}
			if (s.getFtyp().endsWith("M"))
			{ // 嵌套类型
				str.append("\npublic final static ");
				str.append(schema2vo(item, name.substring(0, 1).toUpperCase() + name.substring(1)));
			}
			str.append("\npublic " + type + " " + name + "; // "
					+ StringX.null2emptystr(s.getFdesc()));
			str.append("\npublic void set" + name.toUpperCase().charAt(0) + name.substring(1) + "("
					+ type + " " + name + "){");
			str.append("\nthis." + name + " = " + name + ";\n}");
			str.append("\npublic " + type + " get" + name.toUpperCase().charAt(0)
					+ name.substring(1) + "(){");
			str.append("\nreturn " + name + ";\n}");
		});
		str.append("\n}");
		return str.toString();
	}

	protected TreeNode getMsgSchema(String msgCd)
	{
		return msgDefService.getMsgSchema(msgCd);
	}

	/**
	 * 根据一个报文编号, 一个请求报文结构， 一个响应报文结构生成一个schema
	 * 
	 * @param msgCd
	 * @param reqRoot
	 * @param resRoot
	 * @return
	 * @throws Exception
	 */
	public String schema(String msgCd, TreeNode root, Map param) throws Exception
	{
		StringBuilder schema = new StringBuilder();

		MsgSchemaPO vo = null;
		if (root != null && root.getChildren() != null)
		{
			vo = (MsgSchemaPO) root.getTreeNodeValue();
			vo.setFtyp("M");
			schema.append(struct2schema(root, "Body", false));
		}

		if (schema.length() > 5) param.put("schema", schema.toString());
		return FTLUtil.freemarker(FTLUtil.getTemplate(path + schemaFtl), param);
	}

	// 生成wsdl input/output message
	protected String message(String reqMsgCd, String resMsgCd, TreeNode reqRoot, TreeNode resRoot,
			Map param) throws Exception
	{
		StringBuilder request = new StringBuilder();
		StringBuilder reqMsg = new StringBuilder(); // 请求参数使用message模式，直接展开
		StringBuilder reqMethod = new StringBuilder(); // 请求参数使用报文编号封装模式，Body报文下会多一层
		StringBuilder response = new StringBuilder();
		StringBuilder resMsg = new StringBuilder(); // 应答参数使用message模式，直接展开
		StringBuilder resMethod = new StringBuilder(); // 应答参数使用报文编号封装模式，Body报文下会多一层

		if (resRoot != null && resRoot.getChildren() != null && !resRoot.getChildren().isEmpty())
			message(resRoot, response, resMsg, resMethod);

		if (reqRoot != null && reqRoot.getChildren() != null && !reqRoot.getChildren().isEmpty())
			message(reqRoot, request, reqMsg, reqMethod);

		if (request.length() > 5) param.put("request", request.toString());
		if (response.length() > 5) param.put("response", response.toString());
		param.put("reqMessage", reqMsg.toString());
		param.put("resMessage", resMsg.toString());
		param.put("reqMethod", reqMethod.toString());
		param.put("resMethod", resMethod.toString());
		return FTLUtil.freemarker(FTLUtil.getTemplate(path + wsdlFtl), param);
	}

	void message(TreeNode root, StringBuilder schema, StringBuilder message, StringBuilder method)
	{
		MsgSchemaPO vo = (MsgSchemaPO) root.getTreeNodeValue();
		vo.setFtyp("M");
		for (TreeNode tnode : root.getChildren())
		{
			MsgSchemaPO po = (MsgSchemaPO) tnode.getTreeNodeValue();
			String minOccurs = "M".equalsIgnoreCase(po.getOptional()) ? "1" : "0";
			String maxOccurs = po.getFtyp().startsWith("A") ? "unbounded" : "1";
			if (po.getFtyp().endsWith("M"))
			{
				String beanName = po.getEsbName().substring(0, 1).toUpperCase()
						+ po.getEsbName().substring(1);
				schema.append(struct2schema(tnode, po.getEsbName(), po.getFtyp().startsWith("A")));
				message.append("\n<wsdl:part name=\"" + po.getEsbName() + "\" element=\"esb:"
						+ beanName + "\" />");
				method.append("\n<xs:element name=\"" + po.getEsbName() + "\" minOccurs=\""
						+ minOccurs + "\" maxOccurs=\"" + maxOccurs + "\" type=\"esb:" + beanName
						+ "\" />");
			}
			else
			{
				message.append("\n<wsdl:part name=\"" + po.getEsbName() + "\" type=\"xs:"
						+ wsdlType(po) + "\" />");
				method.append("\n<xs:element name=\"" + po.getEsbName() + "\" minOccurs=\""
						+ minOccurs + "\" maxOccurs=\"" + maxOccurs + "\" type=\"xs:" + wsdlType(po)
						+ "\" />");
			}
		}
	}

	String atom2schema(String prefix, String name, String type, MsgSchemaPO msgstruct,
			boolean array)
	{
		return prefix + "<xs:element name=\"" + name + "\" type=\"" + type + "\" minOccurs=\""
				+ (msgstruct.getOptional().equals("M") ? "1" : "0")
				+ (array ? "\" maxOccurs='unbounded' />" : "\" maxOccurs='1' />");
	}

	// 将esb类型转换为wsdl类型: xs:string, xs:int, xs:long, xs:double
	String wsdlType(MsgSchemaPO po)
	{
		if (po.getFtyp().endsWith("D")) return "double";
		if (po.getFtyp().endsWith("I")) return "int";
		if (po.getFtyp().endsWith("L")) return "long";
		return "string";
	}

	String array2schema(TreeNode node, String type)
	{
		StringBuffer schema = new StringBuffer();
		List child = node.getChildren();
		// String t = type;
		if (child == null || child.size() == 0)
		{ // 数组节点为原子节点
			MsgSchemaPO vo = (MsgSchemaPO) node.getTreeNodeValue();
			return atom2schema("\n\t", vo.getEsbName(), "xs:" + wsdlType(vo), vo, true);
		}
		else
		{ // 为map类型
			schema.append(struct2schema(node, type, true));
		}
		return schema.toString();
	}

	// 结构类型变为schema
	public String struct2schema(TreeNode node, String type, boolean array)
	{
		List child = node.getChildren();
		StringBuffer schema = new StringBuffer();
		if (child == null)
		{
			MsgSchemaPO vo = (MsgSchemaPO) node.getTreeNodeValue();
			log.warn("No child in " + vo.getEsbName() + "," + vo.getSeq());
			return schema.toString();
		}
		String beanName = type.substring(0, 1).toUpperCase() + type.substring(1);
		if (flat)
			schema.append("\n<xs:element name=\"" + beanName + "\" minOccurs=\"0\" maxOccurs=\""
					+ (array ? "unbounded" : "1") + "\"><xs:complexType><xs:all>");
		else schema.append("\n<xs:complexType name=\"" + beanName + "\" ><xs:all>");
		for (int i = 0; i < child.size(); i++)
		{
			TreeNode n = (TreeNode) child.get(i);
			MsgSchemaPO vo = (MsgSchemaPO) n.getTreeNodeValue();
			if (vo.getFtyp().charAt(0) == (char) INode.TYPE_MAP)
				schema.append(struct2schema(n, vo.getEsbName(), false));
			else if (vo.getFtyp().charAt(0) == (char) INode.TYPE_ARRAY)
				schema.append(array2schema(n, vo.getEsbName()));
			else schema
					.append(atom2schema("\n\t", vo.getEsbName(), "xs:" + wsdlType(vo), vo, false));
		}
		if (flat) schema.append("\n</xs:all></xs:complexType></xs:element>");
		else schema.append("\n</xs:all></xs:complexType>");
		return schema.toString();
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public void setSchemaFtl(String schemaFtl)
	{
		this.schemaFtl = schemaFtl;
	}

	public void setWsdlFtl(String wsdlFtl)
	{
		this.wsdlFtl = wsdlFtl;
	}

	public void setMsgDefService(MsgDefService msgDefService)
	{
		this.msgDefService = msgDefService;
	}
}
