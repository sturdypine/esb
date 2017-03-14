package spc.esb.converter;

import java.util.Map;

import spc.esb.common.service.MsgDefService;
import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.esb.data.util.MessageTranslator;
import spc.webos.util.JsonUtil;
import spc.webos.util.tree.TreeNode;

public class JsonMsgConverter extends AbstractMsgConverter
{
	public JsonMsgConverter()
	{
	}

	public JsonMsgConverter(MsgDefService msgDefService, String charset)
	{
		this.msgDefService = msgDefService;
		this.charset = charset;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		if (isContainArray(msg))
		{ // 如果有报文包含数组，则可能调整单一数组元素的情况
			TreeNode schema = msgDefService.getMsgSchema(msg.getMsgCd());
			if (schema != null)
			{
				ICompositeNode cnode = MessageTranslator.adjust(schema,
						msg.isRequestMsg() ? msg.getRequest() : msg.getResponse());
				if (cnode != null)
				{
					if (msg.isRequestMsg()) msg.setRequest(cnode);
					else msg.setResponse(cnode);
				}
			}
		}
		return JsonUtil.obj2json(msg.getTransaction()).getBytes(charset);
	}

	public IMessage deserialize(byte[] buf, IMessage reqmsg) throws Exception
	{
		String json = new String(buf, 0, buf.length, charset);
		try
		{
			return new Message(new CompositeNode((Map) JsonUtil.json2obj(json)));
		}
		catch (Exception e)
		{
			log.warn("json:" + json + ",e:" + e);
			throw e;
		}
	}
}
