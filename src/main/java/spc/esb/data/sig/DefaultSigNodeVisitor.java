package spc.esb.data.sig;

import java.util.ArrayList;
import java.util.List;

import spc.esb.data.IArrayNode;
import spc.esb.data.INode;
import spc.esb.data.util.INodeVisitor;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 默认基于内容签名的节点访问模式，返回为shcema定义的顺序中需要基于内容签名的节点内容和节点配置信息
 * 
 * @author spc
 * 
 */
public class DefaultSigNodeVisitor implements INodeVisitor {
	protected List<Object[]> sigCnts = new ArrayList<>();

	public boolean start(INode node, TreeNode nodeSchema) throws Exception {
		// modified by chenjs 2011-10-02
		// 如果值为null而此字段需要签名则继续放入，有具体生成签名内容的接口负责判断是否需要
		// if (node == null) return true;
		MsgSchemaPO schema = (MsgSchemaPO) nodeSchema.getTreeNodeValue();
		// byte type = (byte) schema.getFtyp().charAt(0);
		// if (type == INode.TYPE_ARRAY) return true;
		// if ("Y".equalsIgnoreCase(schema.getSig()))
		// modified by chenjs 2011-10-02 签名定义从原来的Y/N
		if (!StringX.nullity(schema.getSig()) && (!(node instanceof IArrayNode)))
			sigCnts.add(new Object[] { node, schema });
		return true;
	}

	public boolean end(INode node, TreeNode nodeSchema) throws Exception {
		return true;
	}

	public List<Object[]> getSigCnts() {
		return sigCnts;
	}
}
