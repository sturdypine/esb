package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "esb_reversal_sub")
public class ReversalSubPO implements Serializable
{
	public static final long serialVersionUID = 20100612L;
	// 和物理表对应字段的属性
	@Id
	@Column
	String orgMsgSn; //
	@Id
	@Column
	Integer stepNo; //
	@Column
	String orgSubMsgSn; //
	@Column
	String orgSubMsgCd; //
	@Column
	String orgSubSndDt; //
	@Column
	String orgSubSndTm; //
	@Column
	String orgSubRcvAppSn; //
	@Column
	String typ; //
	@Column
	Integer times; //
	@Column
	String lrMsgSn; //
	@Column
	String lrTm; //
	@Column
	String lrRetCd; //
	@Column
	String lrRetDesc; //
	@Column
	String orgSubContent; //
	@Column
	String tmStamp; //

	public ReversalSubPO()
	{
	}

	public String getTmStamp()
	{
		return tmStamp;
	}

	public void setTmStamp(String tmStamp)
	{
		this.tmStamp = tmStamp;
	}

	public String getOrgMsgSn()
	{
		return orgMsgSn;
	}

	public void setOrgMsgSn(String orgMsgSn)
	{
		this.orgMsgSn = orgMsgSn;
	}

	public Integer getStepNo()
	{
		return stepNo;
	}

	public void setStepNo(Integer stepNo)
	{
		this.stepNo = stepNo;
	}

	public String getOrgSubMsgSn()
	{
		return orgSubMsgSn;
	}

	public void setOrgSubMsgSn(String orgSubMsgSn)
	{
		this.orgSubMsgSn = orgSubMsgSn;
	}

	public String getOrgSubMsgCd()
	{
		return orgSubMsgCd;
	}

	public void setOrgSubMsgCd(String orgSubMsgCd)
	{
		this.orgSubMsgCd = orgSubMsgCd;
	}

	public String getOrgSubSndDt()
	{
		return orgSubSndDt;
	}

	public void setOrgSubSndDt(String orgSubSndDt)
	{
		this.orgSubSndDt = orgSubSndDt;
	}

	public String getOrgSubSndTm()
	{
		return orgSubSndTm;
	}

	public void setOrgSubSndTm(String orgSubSndTm)
	{
		this.orgSubSndTm = orgSubSndTm;
	}

	public String getOrgSubRcvAppSn()
	{
		return orgSubRcvAppSn;
	}

	public void setOrgSubRcvAppSn(String orgSubRcvAppSn)
	{
		this.orgSubRcvAppSn = orgSubRcvAppSn;
	}

	public String getTyp()
	{
		return typ;
	}

	public void setTyp(String typ)
	{
		this.typ = typ;
	}

	public Integer getTimes()
	{
		return times;
	}

	public void setTimes(Integer times)
	{
		this.times = times;
	}

	public String getLrMsgSn()
	{
		return lrMsgSn;
	}

	public void setLrMsgSn(String lrMsgSn)
	{
		this.lrMsgSn = lrMsgSn;
	}

	public String getLrTm()
	{
		return lrTm;
	}

	public void setLrTm(String lrTm)
	{
		this.lrTm = lrTm;
	}

	public String getLrRetCd()
	{
		return lrRetCd;
	}

	public void setLrRetCd(String lrRetCd)
	{
		this.lrRetCd = lrRetCd;
	}

	public String getLrRetDesc()
	{
		return lrRetDesc;
	}

	public void setLrRetDesc(String lrRetDesc)
	{
		this.lrRetDesc = lrRetDesc;
	}

	public String getOrgSubContent()
	{
		return orgSubContent;
	}

	public void setOrgSubContent(String orgSubContent)
	{
		this.orgSubContent = orgSubContent;
	}
}
