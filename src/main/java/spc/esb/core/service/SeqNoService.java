package spc.esb.core.service;

import java.util.Map;

import spc.esb.data.IMessage;

public interface SeqNoService
{
	public String genSN(String key, IMessage msg, Map params) throws Exception;
}
