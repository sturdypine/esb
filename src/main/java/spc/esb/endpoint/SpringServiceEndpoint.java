package spc.esb.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import spc.esb.data.AtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.converter.CompositeNodeConverter;
import spc.webos.endpoint.Endpoint;
import spc.webos.endpoint.EndpointFactory;
import spc.webos.endpoint.Executable;
import spc.webos.util.JsonUtil;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;

@Component
public class SpringServiceEndpoint implements Endpoint
{
	static
	{
		EndpointFactory.register("spring", SpringServiceEndpoint.class);
	}

	public boolean singleton()
	{
		return true;
	}

	public SpringServiceEndpoint()
	{
	}

	public SpringServiceEndpoint(String location)
	{
		setLocation(location);
	}

	public void setLocation(String location)
	{
		this.location = location;
		int start = location.indexOf("//") + 2;
		int end = location.indexOf(':', start);
		this.service = location.substring(start, end);
		start = end + 1;
		end = location.indexOf('?', start);
		this.method = location.substring(start, end);
		String[] argList = StringX.split(location.substring(end + 1), "&");
		this.args = new String[argList.length][2];
		for (int i = 0; i < argList.length; i++)
		{
			int idx = argList[i].indexOf('=');
			args[i][0] = argList[i].substring(0, idx);
			args[i][1] = argList[i].substring(idx + 1);
			if ("responseKey".equalsIgnoreCase(args[i][0])) responseKey = args[i][1];
		}
		if (log.isInfoEnabled()) log.info("s:" + service + ", m:" + method);
	}

	public void init() throws Exception
	{
	}

	public void execute(Executable exe) throws Exception
	{
		IMessage msg = (IMessage) exe.reqmsg;
		List argsList = new ArrayList();
		for (int i = StringX.nullity(responseKey) ? 0 : 1; args != null && i < args.length; i++)
		{
			if (args[i][1].equalsIgnoreCase("list"))
				argsList.add(msg.findArrayInRequest(args[i][0], null));
			else if (args[i][1].equalsIgnoreCase("map"))
				argsList.add(msg.findCompositeInRequest(args[i][0], null));
			else argsList.add(msg.getInRequest(args[i][0]));
		}
		String request = JsonUtil.obj2json(argsList, AtomNode.class);
		log.debug("m:{}.{}, args json:{}", service, method, request);
		Object ret = SpringUtil.jsonCall(null, service, method,
				(StringX.nullity(request) ? null : (List) JsonUtil.json2obj(request)));
		if (!StringX.nullity(responseKey)) msg.setInResponse(responseKey, ret);
		else msg.setResponse(
				(ICompositeNode) CompositeNodeConverter.getInstance().unpack(ret, null));
	}

	public void close()
	{
	}

	// format is:
	// spring://serviceName:method?argName=..XXXVO&argName=int&argName=list&argName=map
	protected String location;
	protected String service;
	protected String method;
	protected String responseKey;
	protected String[][] args;
	protected Logger log = LoggerFactory.getLogger(getClass());

	public String getLocation()
	{
		return location;
	}
}
