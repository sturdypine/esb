package spc.esb.data.sig;

import java.util.List;

import spc.esb.data.IAtomNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;

/**
 * 将签名要素，用|线分隔组成一个完整业务要素域
 * 
 * @author chenjs
 *
 */
public class DefaultMsgSigContent extends AbstractMsgSigContent
{
	protected boolean containLastDelim = true; // 人行在组签名内容时包含最后一个分隔符
	protected String delim = "|"; // 分隔符
	protected boolean ignoreBlankSigCnt = true; // 如果签名内容为空，则忽略签名域

	public DefaultMsgSigContent()
	{
	}

	public DefaultMsgSigContent(String delim, boolean containLastDelim, boolean ignoreBlankSigCnt)
	{
		this.delim = delim;
		this.containLastDelim = containLastDelim;
		this.ignoreBlankSigCnt = ignoreBlankSigCnt;
	}

	// 拼组加签串,竖线"|"分隔
	public byte[] getSigCnts(IMessage msg, String nodeCd, List<Object[]> sigCnts, String charset)
			throws Exception
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < sigCnts.size(); i++)
		{
			Object[] items = (Object[]) sigCnts.get(i);
			String sigCnt = sigCnt(msg, nodeCd, (INode) items[0], (MsgSchemaPO) items[1]); // 当前节点的schema配置信息
			if (sigCnt == null) continue;
			if (StringX.nullity(sigCnt) && ignoreBlankSigCnt) continue; // 如果签名内容为空白，则忽略
			buf.append(sigCnt);
			if (i < sigCnts.size() - 1 || containLastDelim) buf.append(delim);
		}
		if (log.isDebugEnabled())
			log.debug("nodeCd:" + nodeCd + ", charset:" + charset + ", sigCnts:[[" + buf + "]]");
		return buf.toString().getBytes(charset);
	}

	protected String sigCnt(IMessage msg, String nodeCd, INode value, MsgSchemaPO schema)
			throws Exception
	{
		AtomNode2SigContent ansc = AtomNode2SigContent.SIGS.get(schema.getSig());
		
		// 默认根据元素的原始值组成签名要素
		if (ansc == null) return value == null ? StringX.EMPTY_STRING
				: StringX.trim(((IAtomNode) value).stringValue());

		return ansc.sigCnt(msg, nodeCd, value, schema);
	}

	public boolean isContainLastDelim()
	{
		return containLastDelim;
	}

	public void setContainLastDelim(boolean containLastDelim)
	{
		this.containLastDelim = containLastDelim;
	}

	public String getDelim()
	{
		return delim;
	}

	public void setDelim(String delim)
	{
		this.delim = delim;
	}

	public boolean isIgnoreBlankSigCnt()
	{
		return ignoreBlankSigCnt;
	}

	public void setIgnoreBlankSigCnt(boolean ignoreBlankSigCnt)
	{
		this.ignoreBlankSigCnt = ignoreBlankSigCnt;
	}
}
