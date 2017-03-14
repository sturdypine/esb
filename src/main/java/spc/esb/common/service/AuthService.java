package spc.esb.common.service;

import spc.esb.data.IMessage;

/**
 * 报文授权服务接口
 * 
 * @author sunqian at 2010-5-24
 * 
 */
public interface AuthService
{
	// 返回Message的授权状态
	public boolean isAuth(IMessage msg) throws Exception;
}
