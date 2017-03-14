package spc.esb.data;

/**
 * 用来储存签名信息
 * 
 * @author spc
 * 
 */
public class SignatureInfo implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	protected int len;
	protected String digest;
	protected String digestAlg;
	protected String md5Digest;
	protected String sig;

	public int getLen()
	{
		return len;
	}

	public void setLen(int len)
	{
		this.len = len;
	}

	public String getDigest()
	{
		return digest;
	}

	public void setDigest(String digest)
	{
		this.digest = digest;
	}

	public String getDigestAlg()
	{
		return digestAlg;
	}

	public void setDigestAlg(String digestAlg)
	{
		this.digestAlg = digestAlg;
	}

	public String getMd5Digest()
	{
		return md5Digest;
	}

	public void setMd5Digest(String md5Digest)
	{
		this.md5Digest = md5Digest;
	}

	public String getSig()
	{
		return sig;
	}

	public void setSig(String sig)
	{
		this.sig = sig;
	}
}
