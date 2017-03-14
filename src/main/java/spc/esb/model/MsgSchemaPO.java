package spc.esb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.util.StringX;
import spc.webos.util.tree.ITreeNodeValue;

@Entity
@Table(name = "esb_msgschema")
public class MsgSchemaPO implements Serializable, ITreeNodeValue
{
	public static final long serialVersionUID = 20090722L;
	// 和物理表对应字段的属性
	@Id
	@Column
	Integer seq; // 主键
	@Column
	Integer parentSeq; //
	@Id
	@Column
	String msgCd; //
	@Column
	String esbName; //
	@Column
	String rcvName; //
	@Column
	String ftyp; //
	@Column
	String min; //
	@Column
	String max; //
	@Column
	String minLen; // for compatible
	@Column
	String maxLen; // for compatible
	@Column
	Integer deci; //
	@Column
	String optional; //
	@Column
	String encrypt; //
	@Column
	String sig;
	@Column
	String reversal;
	@Column
	String iso8583; // for iso8583 属性
	@Column
	String fixmsg; // added by chenjs 2011-11-10 定长报文属性
	@Column
	String cmtTag; // added by chenjs 2011-11-22 人行cmt报文
	@Column
	String defValue; //
	@Column
	String dict;
	@Column
	String fvMapId; //
	@Column
	String fdesc; //
	@Column
	String cvtr; //
	@Column
	String ftl; //
	@Column
	String validator; // 验证器
	@Column
	String pattern; // 正则表达式模板
	@Column
	String tagAttr; // 扩展属性, xml标签的扩展属性
	@Column
	String metaData;
	@Column
	String ext1;
	@Column
	String ext2;
	@Column
	String ext3;
	@Column
	String ext4;
	@Column
	String ext5;
	@Column
	String ext6;

	protected List<MsgSchemaPO> attributes; // 属性信息

	public MsgSchemaPO()
	{
	}

	public MsgSchemaPO(Integer seq)
	{
		this.seq = seq;
	}

	// set all properties to NULL
	public void setNULL()
	{
		this.seq = null;
		this.parentSeq = null;
		this.msgCd = null;
		this.esbName = null;
		this.rcvName = null;
		this.ftyp = null;
		this.min = null;
		this.max = null;
		this.deci = null;
		this.optional = null;
		this.encrypt = null;
		this.fvMapId = null;
		this.fdesc = null;
		this.cvtr = null;
		this.validator = null;
	}

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

	public String getMin()
	{
		return StringX.nullity(minLen) ? min : minLen;
	}

	public void setMin(String min)
	{
		this.min = min;
	}

	public void setMinLen(String min)
	{
		this.min = min;
	}

	public String getMinLen()
	{
		return getMin();
	}

	public String getMaxLen()
	{
		return getMax();
	}

	public String getMax()
	{
		return StringX.nullity(maxLen) ? max : maxLen;
	}

	public void setMax(String max)
	{
		this.max = max;
	}

	public void setMaxLen(String max)
	{
		this.max = max;
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
		this.sig = StringX.trim(sig);
	}

	public String getDefValue()
	{
		return defValue;
	}

	public void setDefValue(String defValue)
	{
		this.defValue = defValue;
	}

	public String getDict()
	{
		return dict;
	}

	public void setDict(String dict)
	{
		this.dict = dict;
	}

	public String getReversal()
	{
		return reversal;
	}

	public void setReversal(String reversal)
	{
		this.reversal = reversal;
	}

	public String getFvMapId()
	{
		return fvMapId;
	}

	public void setFvMapId(String fvMapId)
	{
		this.fvMapId = fvMapId;
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

	public String getFtl()
	{
		return ftl;
	}

	public void setFtl(String ftl)
	{
		this.ftl = ftl;
	}

	public String getTagAttr()
	{
		return tagAttr;
	}

	public void setTagAttr(String tagAttr)
	{
		this.tagAttr = tagAttr;
	}

	public String getFixmsg()
	{
		return fixmsg;
	}

	public void setFixmsg(String fixmsg)
	{
		this.fixmsg = fixmsg;
	}

	public String getExt1()
	{
		return ext1;
	}

	public String getMetaData()
	{
		return metaData;
	}

	public void setMetaData(String metaData)
	{
		this.metaData = metaData;
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

	public String getExt4()
	{
		return ext4;
	}

	public void setExt4(String ext4)
	{
		this.ext4 = ext4;
	}

	public String getExt5()
	{
		return ext5;
	}

	public void setExt5(String ext5)
	{
		this.ext5 = ext5;
	}

	public String getExt6()
	{
		return ext6;
	}

	public void setExt6(String ext6)
	{
		this.ext6 = ext6;
	}

	// treenode interface...
	public Object treeId()
	{
		return seq;
	}

	public String treeText()
	{
		return esbName;
	}

	public Object parentTreeId()
	{
		return parentSeq;
	}

	public boolean treeRoot()
	{
		return this.seq == null || seq.longValue() <= 0;
	}

	// treenode interface

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

	public List<MsgSchemaPO> getAttributes()
	{
		return attributes;
	}

	public void setAttributes(List<MsgSchemaPO> attributes)
	{
		this.attributes = attributes;
	}

	public void addAttribute(MsgSchemaPO schema)
	{
		if (attributes == null) attributes = new ArrayList<>();
		attributes.add(schema);
	}
}
