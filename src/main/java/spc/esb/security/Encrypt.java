package spc.esb.security;

import java.util.HashMap;
import java.util.Map;

/**
 * 对PIN字段进行加解密
 * 
 * @author spc
 * 
 */
public interface Encrypt {
	// 用两个帐号对pin字段进行转加密
	byte[] translatePinWith2AccNo(String pin, String nodeCd1, String nodeCd2, String acc1, String acc2)
			throws Exception;

	// 加密
	byte[] encode(String nodeCd, byte[] src, Map<String, Object> attribute) throws Exception;

	// 解密
	byte[] decode(String nodeCd, byte[] src, Map<String, Object> attribute) throws Exception;

	Map<String, Encrypt> ENCRYPTS = new HashMap<>();
}
