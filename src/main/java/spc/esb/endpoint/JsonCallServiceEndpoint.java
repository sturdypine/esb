package spc.esb.endpoint;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spc.esb.converter.JsonCallMsgConverter;
import spc.esb.data.AtomNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IMessage;
import spc.webos.endpoint.Endpoint;
import spc.webos.endpoint.EndpointFactory;
import spc.webos.endpoint.Executable;
import spc.webos.util.JsonUtil;

@Component
public class JsonCallServiceEndpoint implements Endpoint
{
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected String location;

	static
	{
		EndpointFactory.register("jscall", JsonCallServiceEndpoint.class);
	}

	public JsonCallServiceEndpoint()
	{
	}

	public JsonCallServiceEndpoint(String location)
	{
		setLocation(location);
	}

	public boolean singleton()
	{
		return true;
	}

	@Override
	public void execute(Executable exe) throws Exception
	{
		IMessage msg = (IMessage) exe.reqmsg;
		String msgCd = msg.getMsgCd();
		Map rsoap = (Map) msg.getInLocal(JsonCallMsgConverter.REQUEST_SOAP_KEY);
		Map<String, Object> header = (Map<String, Object>) rsoap.get(JsonUtil.TAG_HEADER);
		header.put(JsonUtil.TAG_HEADER_MSGCD, location); // 设置当前服务名
		log.info("jscall:{}", location);
		String json = JsonUtil.obj2json(rsoap, AtomNode.class);
		log.debug("response json:{}", json);
		Map<String, Object> soap = JsonUtil
				.jsonRequest((Map<String, Object>) JsonUtil.gson2obj(json), msg.getRcvApp(), true);

		Object body = soap.get(JsonUtil.TAG_BODY);
		soap.remove(JsonUtil.TAG_BODY);
		log.debug("response soap:{}\nbody:{}", soap, body);
		msg.setTransaction(new CompositeNode(soap));
		msg.setRefMsgCd(msgCd);

		msg.setInLocal(JsonCallMsgConverter.RESPONSE_BODY_KEY, body);
	}

	@Override
	public void setLocation(String location)
	{
		this.location = location.substring(location.indexOf(':') + 1); // 去掉jscall:
	}

	@Override
	public void close()
	{
	}
}
