package spc.esb.common.service;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IMessage;

public interface MsgRoute
{
	String route(Route route, IMessage msg) throws Exception;

	public static Map ROUTES = new HashMap();
}
