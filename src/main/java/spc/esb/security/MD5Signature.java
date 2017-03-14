package spc.esb.security;

import java.util.Map;

import spc.webos.util.StringX;

/*
 * ”√”⁄≤‚ ‘£¨MD5«©√˚
 */
public class MD5Signature extends AbstractSignature {
	public String sign(String nodeCd, byte[] content, Map<String, Object> attribute) {
		return StringX.md5(content);
	}

	public boolean unsign(String nodeCd, String sign, byte[] content, Map<String, Object> attribute) {
		boolean b = sign.equals(StringX.md5(content));
		if (!b)
			log.warn("fail to unsign, sndNode: [" + nodeCd + "], sign:[" + sign + "],src:["
					+ new String(StringX.encodeBase64(content)) + "]");
		return b;
	}

	public MD5Signature() {
		name = "MD5";
	}
}
