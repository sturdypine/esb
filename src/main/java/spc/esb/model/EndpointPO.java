package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "esb_endpoint")
public class EndpointPO implements Serializable
{
	public static final long serialVersionUID = 20131010L;
	@Id
	@Column
	String location; //
	@Column
	String uri; //
	@Column
	String appCd;
	@Column
	String remark; //
	//version信息
	@Column
	String userCd;//操作用户
	@Column
	String lastUpdTm;//最后更新时间
	@Column
	String verDt; //数据版本日期
	@Column
	String verStatus;//数据版本状态
	@Column
	String actionNm;//操作名称
	public String getLocation()
	{
		return location;
	}
	public void setLocation(String location)
	{
		this.location = location;
	}
	public String getUri()
	{
		return uri;
	}
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	public String getAppCd()
	{
		return appCd;
	}
	public void setAppCd(String appCd)
	{
		this.appCd = appCd;
	}
	public String getRemark()
	{
		return remark;
	}
	public void setRemark(String remark)
	{
		this.remark = remark;
	}
	public String getUserCd()
	{
		return userCd;
	}
	public void setUserCd(String userCd)
	{
		this.userCd = userCd;
	}
	public String getLastUpdTm()
	{
		return lastUpdTm;
	}
	public void setLastUpdTm(String lastUpdTm)
	{
		this.lastUpdTm = lastUpdTm;
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
