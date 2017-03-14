package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.esb.common.service.Route;
import spc.webos.util.StringX;

@Entity
@Table(name = "esb_service")
public class ServicePO implements Serializable, Route
{
	public static final long serialVersionUID = 20110915L;
	// 和物理表对应字段的属性
	@Id
	@Column
	String serviceId; // 主键
	@Id
	@Column
	String ver; // 主键
	@Column
	String name;
	@Column
	String appCd; //
	@Column
	String category;
	@Column
	String status; //
	@Column
	String reqMsgCd; //
	@Column
	String repMsgCd; //
	@Column
	String rvslMsgCd; //
	@Column
	String qname; //
	@Column
	String routeRule; //
	@Column
	String routeBeanName;
	@Column
	String rcvSNBeanId;
	@Column
	Integer rvslTimes; //
	@Column
	String rvslRule; //
	@Column
	String publishDt; //
	@Column
	String effectDt; //
	@Column
	String validRule;
	@Column
	String svcattr;
	@Column
	String location;
	@Column
	Integer maxLen;
	@Column
	String httpHeaders;
	@Column
	String remark; //
	@Column
	String ext1; //
	@Column
	String ext2; //
	@Column
	String ext3; //
	// SLA
	@Column
	Integer slaMaxCon; // 最大并发
	@Column
	Integer slaAvgCost; // 平均耗时
	@Column
	Integer slaMaxCost;// 最大耗时
	@Column
	Double slaSucRatio;// 成功率
	@Column
	Integer slaRecovery;// 故障恢复时间
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

	public String getServiceId()
	{
		return serviceId;
	}

	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}

	public String getVer()
	{
		return ver;
	}

	public void setVer(String ver)
	{
		this.ver = ver;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAppCd()
	{
		return appCd;
	}

	public void setAppCd(String appCd)
	{
		this.appCd = appCd;
	}

	public String getHttpHeaders()
	{
		return httpHeaders;
	}

	public void setHttpHeaders(String httpHeaders)
	{
		this.httpHeaders = httpHeaders;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getReqMsgCd()
	{
		return reqMsgCd;
	}

	public void setReqMsgCd(String reqMsgCd)
	{
		this.reqMsgCd = reqMsgCd;
	}

	public String getRepMsgCd()
	{
		return repMsgCd;
	}

	public void setRepMsgCd(String repMsgCd)
	{
		this.repMsgCd = repMsgCd;
	}

	public String getRvslMsgCd()
	{
		return rvslMsgCd;
	}

	public void setRvslMsgCd(String rvslMsgCd)
	{
		this.rvslMsgCd = rvslMsgCd;
	}

	public String getQname()
	{
		return qname;
	}

	public void setQname(String qname)
	{
		this.qname = qname;
	}

	public String getRouteRule()
	{
		return routeRule;
	}

	public void setRouteRule(String routeRule)
	{
		this.routeRule = routeRule;
	}

	public String getPublishDt()
	{
		return publishDt;
	}

	public void setPublishDt(String publishDt)
	{
		this.publishDt = publishDt;
	}

	public String getEffectDt()
	{
		return effectDt;
	}

	public void setEffectDt(String effectDt)
	{
		this.effectDt = effectDt;
	}

	public Integer getRvslTimes()
	{
		return rvslTimes;
	}

	public void setRvslTimes(Integer rvslTimes)
	{
		this.rvslTimes = rvslTimes;
	}

	public String getRvslRule()
	{
		return rvslRule;
	}

	public void setRvslRule(String rvslRule)
	{
		this.rvslRule = rvslRule;
	}

	public Integer getSlaMaxCon()
	{
		return slaMaxCon;
	}

	public void setSlaMaxCon(Integer slaMaxCon)
	{
		this.slaMaxCon = slaMaxCon;
	}

	public Integer getSlaAvgCost()
	{
		return slaAvgCost;
	}

	public void setSlaAvgCost(Integer slaAvgCost)
	{
		this.slaAvgCost = slaAvgCost;
	}

	public Integer getSlaMaxCost()
	{
		return slaMaxCost;
	}

	public void setSlaMaxCost(Integer slaMaxCost)
	{
		this.slaMaxCost = slaMaxCost;
	}

	public Double getSlaSucRatio()
	{
		return slaSucRatio;
	}

	public void setSlaSucRatio(Double slaSucRatio)
	{
		this.slaSucRatio = slaSucRatio;
	}

	public Integer getSlaRecovery()
	{
		return slaRecovery;
	}

	public void setSlaRecovery(Integer slaRecovery)
	{
		this.slaRecovery = slaRecovery;
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

	public String getValidRule()
	{
		return validRule;
	}

	public void setValidRule(String validRule)
	{
		this.validRule = validRule;
	}

	public String getRcvSNBeanId()
	{
		return rcvSNBeanId;
	}

	public void setRcvSNBeanId(String rcvSNBeanId)
	{
		this.rcvSNBeanId = rcvSNBeanId;
	}

	public String getSvcattr()
	{
		return svcattr;
	}

	public void setSvcattr(String svcattr)
	{
		this.svcattr = svcattr;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public Integer getMaxLen()
	{
		return maxLen;
	}

	public void setMaxLen(Integer maxLen)
	{
		this.maxLen = maxLen;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
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

	public String getFtlRule()
	{
		return routeRule;
	}

	public String getRouteBeanName()
	{
		return routeBeanName;
	}

	public void setRouteBeanName(String routeBeanName)
	{
		this.routeBeanName = routeBeanName;
	}

	public boolean isValidRoute()
	{
		return !StringX.nullity(qname) || !StringX.nullity(routeRule)
				|| !StringX.nullity(routeBeanName);
	}
}
