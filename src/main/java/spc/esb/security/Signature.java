package spc.esb.security;

import java.util.HashMap;
import java.util.Map;

/**
 * 签名/验签名接口。
 * 
 * @author spc
 * 
 */
public interface Signature
{
	// 加上签名， 可能有时候后台服务系统的xml报文结构和esb的报文结构不一致，所以接口采用cnode模式
	String sign(String nodeCd, byte[] content, Map<String, Object> attribute) throws Exception;

	boolean unsign(String nodeCd, String sign, byte[] content, Map<String, Object> attribute)
			throws Exception;

	Map<String, Signature> SIGS = new HashMap<>();
}
