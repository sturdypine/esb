package spc.esb.security;

import java.util.Map;

import javax.annotation.Resource;

import spc.esb.common.service.ESBInfoService;
import spc.esb.model.NodePO;
import spc.webos.util.CipherUtil;
import spc.webos.util.StringX;

public class DES extends AbstractEncryptSig
{
	public byte[] encode(String nodeCd, byte[] content, Map<String, Object> attribute)
			throws Exception
	{
		byte[] deskey = this.deskey;
		if (deskey == null)
		{
			NodePO node = esbInfoService.getNode(nodeCd);
			if (node == null || StringX.nullity(node.getDesKey()))
			{
				log.warn("DESKey is null or node is null  by: " + nodeCd);
				return content;
			}
			deskey = StringX.decodeBase64(node.getDesKey().getBytes());
		}
		byte[] buf = CipherUtil.desEncrypt(content, deskey);
		if (log.isDebugEnabled()) log.debug(
				"cnt.base64:[" + new String(StringX.encodeBase64(content)) + "], deskey.base64:["
						+ deskey + "], buf.base64:" + new String(StringX.encodeBase64(buf)));
		return buf;
	}

	public byte[] decode(String nodeCd, byte[] content, Map<String, Object> attribute)
			throws Exception
	{
		byte[] deskey = this.deskey;
		if (deskey == null)
		{
			NodePO node = esbInfoService.getNode(nodeCd);
			if (node == null || StringX.nullity(node.getDesKey()))
			{
				log.warn("DESKey is null or node is null  by: " + nodeCd);
				return content;
			}
			deskey = StringX.decodeBase64(node.getDesKey().getBytes());
		}
		byte[] desCnt = CipherUtil.desDecrypt(content, deskey);
		if (log.isDebugEnabled()) log.debug("deskey.base64:[" + deskey + "], cnt.base64:["
				+ new String(StringX.encodeBase64(content)) + "], desCnt.base64:["
				+ new String(StringX.encodeBase64(desCnt)) + "]");
		return desCnt;
	}

	public String sign(String nodeCd, byte[] content, Map<String, Object> attribute)
			throws Exception
	{
		byte[] deskey = this.deskey;
		if (deskey == null)
		{
			NodePO node = esbInfoService.getNode(nodeCd);
			if (node == null || StringX.nullity(node.getDesKey()))
			{
				log.warn("publicKey is null or node is null  by: " + nodeCd);
				return null;
			}
			deskey = StringX.decodeBase64(node.getDesKey().getBytes());
		}
		content = CipherUtil.desEncrypt(content, deskey);
		String sign = new String(StringX.encodeBase64(content));
		if (log.isDebugEnabled()) log.debug(
				"sign:[" + sign + "], cnt.base64:[" + new String(StringX.encodeBase64(content))
						+ "], deskey.base64:[" + new String(StringX.encodeBase64(deskey)) + "]");
		return sign;
	}

	public boolean unsign(String nodeCd, String sign, byte[] content, Map<String, Object> attribute)
			throws Exception
	{
		byte[] deskey = this.deskey;
		if (deskey == null)
		{
			NodePO node = esbInfoService.getNode(nodeCd);
			if (node == null || StringX.nullity(node.getDesKey()))
			{
				log.warn("publicKey is null or node is null  by: " + nodeCd);
				return false;
			}
			deskey = StringX.decodeBase64(node.getDesKey().getBytes());
		}
		byte[] desCnt = content;
		desCnt = CipherUtil.desDecrypt(content, deskey);
		boolean ok = sign.equalsIgnoreCase(new String(StringX.encodeBase64(desCnt)));
		if (!ok) log.warn(
				"sign:[" + sign + "],  deskey.base64:[" + new String(StringX.encodeBase64(deskey))
						+ "], cnt.base64:[" + new String(StringX.encodeBase64(content))
						+ "], desCnt.base64:[" + new String(StringX.encodeBase64(desCnt)) + "]");
		return ok;
	}

	public DES()
	{
		name = "DES";
	}

	@Resource
	protected ESBInfoService esbInfoService;
	protected byte[] deskey; // 优先采用注入的key

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}

	public byte[] translatePinWith2AccNo(String pin, String nodeCd1, String nodeCd2, String acc1,
			String acc2) throws Exception
	{
		return null;
	}

	public byte[] getDeskey()
	{
		return deskey;
	}

	public void setDeskey(byte[] deskey)
	{
		this.deskey = deskey;
	}

	public void setDesKey(String key)
	{
		this.deskey = StringX.decodeBase64(key.getBytes());
	}
}
