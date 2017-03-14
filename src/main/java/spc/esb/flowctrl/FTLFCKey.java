package spc.esb.flowctrl;

import java.util.Map;

import spc.esb.data.IMessage;
import spc.esb.data.util.MsgFTLUtil;
import spc.esb.model.MessagePO;
import spc.webos.util.FTLUtil;
import spc.webos.util.StringX;

/**
 * 使用FTL模板生成想要的流量控制正则表达式
 * 
 * @author chenjs
 * 
 */
public class FTLFCKey implements FCKey
{
	public String key(IMessage msg, MessagePO msgVO, int priority) throws Exception
	{
		Map root = MsgFTLUtil.model(null, msg);
		root.put("_msgVO", msgVO);
		root.put("_priority", String.valueOf(priority));
		root.put("_isRequest", msg.isRequestMsg());
		return StringX.trim(FTLUtil.freemarker(ftl, root));
	}

	protected String ftl;

	public void setFtl(String ftl)
	{
		this.ftl = ftl;
	}
}
