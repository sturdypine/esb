package spc.esb.data.converter;

import java.util.Map;

import spc.esb.data.AtomNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.Message;
import spc.webos.constant.Common;
import spc.webos.exception.Status;
import spc.webos.util.JsonUtil;

public class JSONConverter extends SOAPConverter
{
	public IMessage deserializeJSON(byte[] buf, IMessage msg) throws Exception
	{
		String json = new String(buf, charset);
		try
		{
			CompositeNode cnode = new CompositeNode((Map) JsonUtil.json2obj(json));
			if (!bodyOnly)
			{
				INode body = cnode.find(IMessage.TAG_BODY);
				// 如果body是一个字符串，则解析json格式对象
				if (body instanceof AtomNode)
					cnode.put(IMessage.TAG_BODY, JsonUtil.json2obj(body.toString()));
				return new Message(cnode);
			}
			Message m = new Message();
			m.setBody(cnode);
			return m;
		}
		catch (Exception e)
		{
			log.warn("json:" + json + ",e:" + e);
			throw e;
		}
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		if (bodyOnly)
		{ // rest api
			Status s = msg.getStatus();
			if (s != null && !s.success()) return JsonUtil.obj2json(s).getBytes(charset);
			return JsonUtil.obj2json(msg.getBody()).getBytes(charset);
		}
		if (!serializeAll)
		{
			CompositeNode cnode = new CompositeNode();
			cnode.set(IMessage.TAG_BODY, msg.getBody());
			cnode.set(IMessage.TAG_HEADER, msg.getHeader());
			return JsonUtil.obj2json(cnode).getBytes(charset);
		}
		return JsonUtil.obj2json(msg.getTransaction()).getBytes(charset);
	}

	public String getContentType()
	{
		return Common.FILE_JSON_CONTENTTYPE;
	}

	// 容许发json请求时只有body体，没有完整信封结构，便于支持restful接口
	protected boolean bodyOnly;

	public void setBodyOnly(boolean bodyOnly)
	{
		this.bodyOnly = bodyOnly;
	}
}
