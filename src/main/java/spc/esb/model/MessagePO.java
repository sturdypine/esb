package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.esb.common.service.Route;
import spc.webos.util.StringX;

@Entity
@Table(name = "esb_msg")
public class MessagePO implements Serializable
{
	public static final long serialVersionUID = 20090722L;
	// 和物理表对应字段的属性
	@Id
	@Column
	String msgCd; // 主键
	@Column
	String repMsgCd; //
	@Column
	String rcvAppCd; //
	@Column
	String rcvMsgCd; //
	@Column
	String msgAttr; //
	@Column
	Integer publishDt; //
	@Column
	String msgDesc; //

	@Column
	String schemaMsgCd;
	@Column
	Integer len;

	@Column
	String ext1; //
	@Column
	String ext2; //
	@Column
	String ext3; //
	@Column
	String hdrTag;
	@Column
	String hdrSchema;
	@Column
	String adapterBean; // 容许后端服务按报文来决定适配器

	// version信息
	@Column
	String userCd;// 操作用户
	@Column
	String lastUpdTm;// 最后更新时间
	@Column
	String verDt; // 数据版本日期
	@Column
	String verStatus;// 数据版本状态
	@Column
	String actionNm;// 操作名称

	public String getMsgCd()
	{
		return msgCd;
	}

	public void setMsgCd(String msgCd)
	{
		this.msgCd = msgCd;
	}

	public String getRepMsgCd()
	{
		return repMsgCd;
	}

	public void setRepMsgCd(String repMsgCd)
	{
		this.repMsgCd = repMsgCd;
	}

	public String getRcvAppCd()
	{
		return rcvAppCd;
	}

	public void setRcvAppCd(String rcvAppCd)
	{
		this.rcvAppCd = rcvAppCd;
	}

	public String getRcvMsgCd()
	{
		return rcvMsgCd;
	}

	public void setRcvMsgCd(String rcvMsgCd)
	{
		this.rcvMsgCd = rcvMsgCd;
	}

	public String getMsgAttr()
	{
		return msgAttr;
	}

	public void setMsgAttr(String msgAttr)
	{
		this.msgAttr = msgAttr;
	}

	public String getLastUpdTm()
	{
		return lastUpdTm;
	}

	public void setLastUpdTm(String lastUpdTm)
	{
		this.lastUpdTm = lastUpdTm;
	}

	public String getUserCd()
	{
		return userCd;
	}

	public void setUserCd(String userCd)
	{
		this.userCd = userCd;
	}

	public Integer getPublishDt()
	{
		return publishDt;
	}

	public void setPublishDt(Integer publishDt)
	{
		this.publishDt = publishDt;
	}

	public String getMsgDesc()
	{
		return msgDesc;
	}

	public void setMsgDesc(String msgDesc)
	{
		this.msgDesc = msgDesc;
	}

	public String getHdrTag()
	{
		return hdrTag;
	}

	public void setHdrTag(String hdrTag)
	{
		this.hdrTag = hdrTag;
	}

	public String getHdrSchema()
	{
		return hdrSchema;
	}

	public void setHdrSchema(String hdrSchema)
	{
		this.hdrSchema = hdrSchema;
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

	public Integer getLen()
	{
		return len;
	}

	public void setLen(Integer len)
	{
		this.len = len;
	}

	public String getSchemaMsgCd()
	{
		return schemaMsgCd;
	}

	public void setSchemaMsgCd(String schemaMsgCd)
	{
		this.schemaMsgCd = schemaMsgCd;
	}

	public String getRouteBeanName()
	{
		return null;
	}

	public String getAdapterBean()
	{
		return adapterBean;
	}

	public void setAdapterBean(String adapterBean)
	{
		this.adapterBean = adapterBean;
	}

	public boolean isValidRoute()
	{
		return false;
	}

	public String getVerDt()
	{
		return verDt;
	}

	public void setVerDt(String verDt)
	{
		this.verDt = verDt;
	}

	public String getVerStatus()
	{
		return verStatus;
	}

	public void setVerStatus(String verStatus)
	{
		this.verStatus = verStatus;
	}

	public String getActionNm()
	{
		return actionNm;
	}

	public void setActionNm(String actionNm)
	{
		this.actionNm = actionNm;
	}
}
