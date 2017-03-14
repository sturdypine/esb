package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "esb_famsgschema")
public class FAMsgSchemaPO implements Serializable
{
	public static final long serialVersionUID = 20101111L;
	// 和物理表对应字段的属性
	@Id
	@Column
	Integer seq; // 主键
	@Column
	Integer parentSeq; //
	@Id
	@Column
	String msgCd; //
	@Id
	@Column
	String sndAppCd; //
	@Column
	String esbName; //
	@Column
	String rcvName; //
	@Column
	String ftyp; //
	@Column
	String minLen; //
	@Column
	String maxLen; //
	@Column
	Integer deci; //
	@Column
	String optional; //
	@Column
	String encrypt; //
	@Column
	String sig; //
	@Column
	String iso8583; //
	@Column
	String cmtTag; //
	@Column
	String fixmsg;
	@Column
	String fvMapId; //
	@Column
	String ftl; //
	@Column
	String fdesc; //
	@Column
	String cvtr; //
	@Column
	String validator; //
	@Column
	String pattern; //
	@Column
	String defValue; //
	@Column
	String ext1; //
	@Column
	String ext2; //
	@Column
	String ext3; //

	public Integer getSeq()
	{
		return seq;
	}

	public void setSeq(Integer seq)
	{
		this.seq = seq;
	}

	public Integer getParentSeq()
	{
		return parentSeq;
	}

	public void setParentSeq(Integer parentSeq)
	{
		this.parentSeq = parentSeq;
	}

	public String getMsgCd()
	{
		return msgCd;
	}

	public void setMsgCd(String msgCd)
	{
		this.msgCd = msgCd;
	}

	public String getSndAppCd()
	{
		return sndAppCd;
	}

	public void setSndAppCd(String sndAppCd)
	{
		this.sndAppCd = sndAppCd;
	}

	public String getEsbName()
	{
		return esbName;
	}

	public void setEsbName(String esbName)
	{
		this.esbName = esbName;
	}

	public String getRcvName()
	{
		return rcvName;
	}

	public void setRcvName(String rcvName)
	{
		this.rcvName = rcvName;
	}

	public String getFtyp()
	{
		return ftyp;
	}

	public void setFtyp(String ftyp)
	{
		this.ftyp = ftyp;
	}

	public String getMinLen()
	{
		return minLen;
	}

	public void setMinLen(String minLen)
	{
		this.minLen = minLen;
	}

	public String getMaxLen()
	{
		return maxLen;
	}

	public void setMaxLen(String maxLen)
	{
		this.maxLen = maxLen;
	}

	public Integer getDeci()
	{
		return deci;
	}

	public void setDeci(Integer deci)
	{
		this.deci = deci;
	}

	public String getOptional()
	{
		return optional;
	}

	public void setOptional(String optional)
	{
		this.optional = optional;
	}

	public String getEncrypt()
	{
		return encrypt;
	}

	public void setEncrypt(String encrypt)
	{
		this.encrypt = encrypt;
	}

	public String getSig()
	{
		return sig;
	}

	public void setSig(String sig)
	{
		this.sig = sig;
	}

	public String getIso8583()
	{
		return iso8583;
	}

	public void setIso8583(String iso8583)
	{
		this.iso8583 = iso8583;
	}

	public String getCmtTag()
	{
		return cmtTag;
	}

	public void setCmtTag(String cmtTag)
	{
		this.cmtTag = cmtTag;
	}

	public String getFixmsg()
	{
		return fixmsg;
	}

	public void setFixmsg(String fixmsg)
	{
		this.fixmsg = fixmsg;
	}

	public String getFvMapId()
	{
		return fvMapId;
	}

	public void setFvMapId(String fvMapId)
	{
		this.fvMapId = fvMapId;
	}

	public String getFtl()
	{
		return ftl;
	}

	public void setFtl(String ftl)
	{
		this.ftl = ftl;
	}

	public String getFdesc()
	{
		return fdesc;
	}

	public void setFdesc(String fdesc)
	{
		this.fdesc = fdesc;
	}

	public String getCvtr()
	{
		return cvtr;
	}

	public void setCvtr(String cvtr)
	{
		this.cvtr = cvtr;
	}

	public String getValidator()
	{
		return validator;
	}

	public void setValidator(String validator)
	{
		this.validator = validator;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public String getDefValue()
	{
		return defValue;
	}

	public void setDefValue(String defValue)
	{
		this.defValue = defValue;
	}

	public String getExt1()
	{
		return ext1;
	}

	public void setExt1(String ext1)
	{
		this.ext1 = ext1;
	}

	public String getExt2()
	{
		return ext2;
	}

	public void setExt2(String ext2)
	{
		this.ext2 = ext2;
	}

	public String getExt3()
	{
		return ext3;
	}

	public void setExt3(String ext3)
	{
		this.ext3 = ext3;
	}
}
