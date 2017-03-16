package spc.esb.common.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spc.esb.common.service.ESBInfoService;
import spc.esb.common.service.MsgDefService;
import spc.esb.common.service.SignatureService;
import spc.esb.constant.ESBCommon;
import spc.esb.core.NodeAttr;
import spc.esb.data.IMessage;
import spc.esb.data.MessageAttr;
import spc.esb.data.sig.DefaultMsgSigContent;
import spc.esb.data.sig.DefaultSigNodeVisitor;
import spc.esb.data.sig.MsgSigContent;
import spc.esb.data.util.MessageTraversal;
import spc.esb.model.NodePO;
import spc.esb.security.Signature;
import spc.webos.constant.Common;
import spc.webos.service.BaseService;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 根据接收节点 完成签名 和 验证签名
 * 
 * 
 */
@Service("esbSignatureService")
public class SignatureServiceImpl extends BaseService implements SignatureService
{
	@Autowired(required = false)
	protected MsgDefService msgDefService;
	@Autowired(required = false)
	protected ESBInfoService esbInfoService;
	protected String charset = Common.CHARSET_UTF8;

	public String sig(IMessage msg, String nodeApp, byte[] srcBytes) throws Exception
	{
		NodePO node = esbInfoService.getNode(nodeApp);
		NodeAttr nodeAttr = new NodeAttr(node.getAppAttr());
		if (nodeAttr.isNotSig())
		{
			log.info("no sig");
			return StringX.EMPTY_STRING;
		}
		Signature sig = Signature.SIGS.get(node.getSigBeanId());
		if (sig == null)
		{ // 找不到签名句柄时，不签名，返回空串
			log.warn("cannot find Signature for " + node.getAppCd() + ", name:"
					+ node.getSigBeanId());
			return StringX.EMPTY_STRING;
		}
		String sigNodeCd = StringX.null2emptystr(node.getMbrCd()).trim()
				+ StringX.null2emptystr(node.getAppCd()); // 接收报文的节点信息
		byte[] sigCnt = null; // 签名内容
		if (nodeAttr.isBodySig()) sigCnt = srcBytes;
		else
		{ // signature by part content..
			TreeNode schema = msgDefService.getMsgSchema(msg.getMsgCd());
			if (schema == null)
			{
				log.warn("Fail to sig by element: msg(" + msg.getMsgCd() + ")'s schema is null!!!");
				return StringX.EMPTY_STRING;
			}

			List<Object[]> sigCnts = getSigCnts(msg, schema);
			if (sigCnts.size() == 0)
			{
				log.warn(
						"Fail to sig by element: msg(" + msg.getMsgCd() + ")'s sigCnts is null!!!");
				return StringX.EMPTY_STRING;
			}
			MsgSigContent msgSigCnt = StringX.nullity(node.getSigCntBeanId())
					? new DefaultMsgSigContent()
					: (MsgSigContent) MsgSigContent.SIG.get(node.getSigCntBeanId());
			sigCnt = msgSigCnt.getSigCnts(msg, sigNodeCd, sigCnts, charset); // 获取参与签名内容
			if (sigCnt == null || sigCnt.length == 0)
			{
				log.warn("Fail to sig by element: msg(" + msg.getMsgCd() + ")'s sigcnt is null!!!");
				return StringX.EMPTY_STRING;
			}
			else if (log.isInfoEnabled()) log.info("sigCnt ele:" + new String(sigCnt, charset));
		}

		byte[] sigDigBytes = StringX.nullity(nodeAttr.getSigDigestAlg()) ? sigCnt
				: StringX.digest(sigCnt, nodeAttr.getSigDigestAlg()).getBytes(); // 获取签名摘要
		if (log.isDebugEnabled())
			log.debug("Sig: sigCnt.base64:" + new String(StringX.encodeBase64(sigCnt))
					+ ", sigDigBytes.base64:" + new String(StringX.encodeBase64(sigDigBytes)));
		// added by guodd 20120426
		Map<String, Object> map = new HashMap<>();
		map.put(ESBCommon.STRING_MSG, msg);
		String sigStr = sig.sign(sigNodeCd, sigDigBytes, map); // 签名
		if (log.isInfoEnabled())
			log.info("Sig: sigCnt[" + sigCnt.length + "], DigestAlg[" + nodeAttr.getSigDigestAlg()
					+ "], DigBytes:[" + sigDigBytes.length + "], sig:" + sigStr);
		return sigStr;
	}

	protected List<Object[]> getSigCnts(IMessage msg, TreeNode schema) throws Exception
	{
		DefaultSigNodeVisitor dsnv = new DefaultSigNodeVisitor(); // 签名节点访问类
		MessageTraversal traversal = new MessageTraversal(
				msg.isRequestMsg() ? msg.getRequest() : msg.getResponse(), schema); // 消息遍历类
		traversal.dfs(dsnv);
		return dsnv.getSigCnts();
	}

	public boolean unsig(IMessage msg, String nodeApp, byte[] srcBytes, String signature)
			throws Exception
	{
		NodePO node = esbInfoService.getNode(nodeApp);
		NodeAttr nodeAttr = new NodeAttr(node.getAppAttr());
		if (nodeAttr.isNotUnsig())
		{
			log.info("node no unsig");
			return true;
		}
		Signature sig = Signature.SIGS.get(node.getSigBeanId());
		if (sig == null)
		{ // 找不到签名句柄时，不核签，默认核签成功, 可以利用此特性不对某成员测试环境时验证签名
			log.warn("cannot find Signature for " + node.getAppCd() + ", name:"
					+ node.getSigBeanId() + ", default unsig is true!!!");
			return true;
		}
		String unsigNodeCd = StringX.null2emptystr(node.getMbrCd()).trim()
				+ StringX.null2emptystr(node.getAppCd()); // 发起报文应用编号
		byte[] sigCnt = null; // 签名内容

		// unsig by body of xml
		if (nodeAttr.isBodySig()) sigCnt = srcBytes;
		else
		{ // unsig by the part of content..
			TreeNode schema = msgDefService.getMsgSchema(msg.getMsgCd());
			if (schema == null)
			{
				log.warn("Fail to unsig by element: msg(" + msg.getMsgCd()
						+ ")'s schema is null!!!");
				return false;
			}
			DefaultSigNodeVisitor dsnv = new DefaultSigNodeVisitor(); // 签名节点访问类
			MessageTraversal traversal = new MessageTraversal(
					msg.isRequestMsg() ? msg.getRequest() : msg.getResponse(), schema); // 消息遍历类
			traversal.dfs(dsnv);
			List<Object[]> sigCnts = dsnv.getSigCnts();
			if (sigCnts.size() == 0)
			{
				log.warn("Fail to unsig by element: msg(" + msg.getMsgCd()
						+ ")'s sigCnts is null!!!");
				return false;
			}
			MsgSigContent msgSigCnt = StringX.nullity(node.getSigCntBeanId())
					? new DefaultMsgSigContent()
					: (MsgSigContent) MsgSigContent.SIG.get(node.getSigCntBeanId());
			sigCnt = msgSigCnt.getSigCnts(msg, unsigNodeCd, sigCnts, charset); // 获取参与签名内容
			if (log.isInfoEnabled()) log.info("sigCnt ele:" + new String(sigCnt, charset));
		}
		byte[] sigDigBytes = StringX.nullity(nodeAttr.getSigDigestAlg()) ? sigCnt
				: StringX.digest(sigCnt, nodeAttr.getSigDigestAlg()).getBytes(); // 获取签名摘要
		signature = signature.replaceAll("\t|\n|\\s", StringX.EMPTY_STRING); // 去掉signature中空格
		if (log.isDebugEnabled())
			log.debug("Unsig: sigCnt.base64:" + new String(StringX.encodeBase64(sigCnt))
					+ ", DigBytes.base64:" + new String(StringX.encodeBase64(sigDigBytes)));
		// added by guodd 20120426
		Map<String, Object> map = new HashMap<>();
		map.put(ESBCommon.STRING_MSG, msg);
		boolean unsigOK = sig.unsign(unsigNodeCd, signature, sigDigBytes, map); // 验证签名
		if (!unsigOK) log.warn("Fail to unsig: node[" + unsigNodeCd + "],sigCnt.base64["
				+ new String(StringX.encodeBase64(sigCnt)) + "], sigCnt.utf-8["
				+ new String(sigCnt, Common.CHARSET_UTF8) + "], signature:" + signature);
		return unsigOK;
	}

	// 判断是否需要验证签名
	public boolean isUnsig(IMessage msg)
	{
		MessageAttr msgAttr = msgDefService.getMsgAttr(msg.getMsgCd());
		if ((msgAttr.isSndSig() && msgAttr.isMbSig())) return true;
		return false;
	}

	public void setMsgDefService(MsgDefService msgDefService)
	{
		this.msgDefService = msgDefService;
	}

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}
}
