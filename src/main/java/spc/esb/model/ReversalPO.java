package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "esb_reversal")
public class ReversalPO implements Serializable
{
	public static final long serialVersionUID = 20100612L;
	// 和物理表对应字段的属性
	@Id
	@Column
	String orgMsgSn; //
	@Column
	String orgMsgCd; //
	@Column
	String orgSndDt; //
	@Column
	String reason; //
	@Column
	String action; //
	@Column
	String status; //
	@Column
	Integer curStepNo; //
	@Column
	String tmStamp; //

	public ReversalPO()
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

	public String getOrgMsgCd()
	{
		return orgMsgCd;
	}

	public void setOrgMsgCd(String orgMsgCd)
	{
		this.orgMsgCd = orgMsgCd;
	}

	public String getOrgSndDt()
	{
		return orgSndDt;
	}

	public void setOrgSndDt(String orgSndDt)
	{
		this.orgSndDt = orgSndDt;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Integer getCurStepNo()
	{
		return curStepNo;
	}

	public void setCurStepNo(Integer curStepNo)
	{
		this.curStepNo = curStepNo;
	}
}
