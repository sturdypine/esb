package spc.esb.core.service;

import spc.esb.data.IMessage;

public interface ESBService
{
	IMessage sync(IMessage msg);

	byte[] request(IMessage msg) throws Exception;

	byte[] response(IMessage msg) throws Exception;

	<T> T call(String msgCd, Object request, T response);
}
