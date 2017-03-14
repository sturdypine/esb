package spc.esb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.persistence.jdbc.blob.ByteArrayBlob;

@Entity
@Table(name = "esb_log_detail")
public class LogDetailPO
{
	public static final long serialVersionUID = 20100602L;
	@Column
	String msgSn; //
	@Column(columnDefinition = "{prepare:true}")
	ByteArrayBlob esbXML;
	@Column(columnDefinition = "{prepare:true}")
	ByteArrayBlob origBytes;
	@Column(columnDefinition = "{prepare:true}")
	String signature;
	String tmStamp; //
	@Id
	@Column
	Long seq;

	public LogDetailPO()
	{
	}
	
	public LogDetailPO(Long seq)
	{
		this.seq = seq;
	}

	public String getMsgSn()
	{
		return msgSn;
	}

	public void setMsgSn(String msgSn)
	{
		this.msgSn = msgSn;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public ByteArrayBlob getEsbXML()
	{
		return esbXML;
	}

	public void setEsbXML(ByteArrayBlob esbXML)
	{
		this.esbXML = esbXML;
	}

	public ByteArrayBlob getOrigBytes()
	{
		return origBytes;
	}

	public void setOrigBytes(ByteArrayBlob origBytes)
	{
		this.origBytes = origBytes;
	}

	public String getTmStamp()
	{
		return tmStamp;
	}

	public void setTmStamp(String tmStamp)
	{
		this.tmStamp = tmStamp;
	}

	public Long getSeq()
	{
		return seq;
	}

	public void setSeq(Long seq)
	{
		this.seq = seq;
	}
}
