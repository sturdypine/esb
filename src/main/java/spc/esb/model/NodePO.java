package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.util.StringX;

@Entity
@Table(name = "esb_node")
public class NodePO implements Serializable
{
	public static final long serialVersionUID = 20110902L;
	@Id
	@Column
	String mbrCd; // 主键
	@Id
	@Column
	String appCd; // 主键
	@Column
	String name; //
	@Column
	String accessType; //
	@Column
	String appAttr; //

	@Column
	String sigBeanId; //
	@Column
	String sigCntBeanId;
	@Column
	String publicKey; //
	@Column
	String desKey;
	@Column
	String host; //
	@Column
	String localPort; //
	@Column
	String httpURI;

	@Column
	String faBeanId; //
	@Column
	String baBeanId; //

	@Column
	String replyToQ; // 有效的replyToQ
	@Column
	String authMsgCd;
	@Column
	String location;
	@Column
	String echoMsgCd;
	@Column
	String publishDt; //
	@Column
	String effectDt; //
	@Column
	String ext1;
	@Column
	String ext2;
	@Column
	String ext3;
	@Column
	String remark;

	@Column
	Integer locationNo;// 拓扑图中的位置
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

	public NodePO()
	{
	}

	public NodePO(String mbrCd, String appCd)
	{
		this.mbrCd = mbrCd;
		this.appCd = appCd;
	}

	public String getMbrCd()
	{
		return mbrCd;
	}

	public void setMbrCd(String mbrCd)
	{
		this.mbrCd = (StringX.nullity(mbrCd) ? " " : mbrCd);
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

	public String getAppCd()
	{
		return appCd;
	}

	public void setAppCd(String appCd)
	{
		this.appCd = appCd;
	}

	public void setSigDigestAlg(String sigDigestAlg)
	{
		// this.sigDigestAlg = sigDigestAlg;
	}

	public String getSigBeanId()
	{
		return sigBeanId;
	}

	public void setSigBeanId(String sigBeanId)
	{
		this.sigBeanId = sigBeanId;
	}

	public String getHttpURI()
	{
		return httpURI;
	}

	public Integer getLocationNo()
	{
		return locationNo;
	}

	public void setLocationNo(Integer locationNo)
	{
		this.locationNo = locationNo;
	}

	public void setHttpURI(String httpURI)
	{
		this.httpURI = httpURI;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getAccessType()
	{
		return accessType;
	}

	public void setAccessType(String accessType)
	{
		this.accessType = accessType;
	}

	public String getFaBeanId()
	{
		return faBeanId;
	}

	public void setFaBeanId(String faBeanId)
	{
		this.faBeanId = faBeanId;
	}

	public String getBaBeanId()
	{
		return baBeanId;
	}

	public void setBaBeanId(String baBeanId)
	{
		this.baBeanId = baBeanId;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public String getPublicKey()
	{
		return publicKey;
	}

	public void setPublicKey(String publicKey)
	{
		this.publicKey = publicKey;
	}

	public String getAppAttr()
	{
		return appAttr;
	}

	public void setAppAttr(String appAttr)
	{
		this.appAttr = appAttr;
	}

	public String getLocalPort()
	{
		return localPort;
	}

	public void setLocalPort(String localPort)
	{
		this.localPort = localPort;
	}

	public String getDesKey()
	{
		return desKey;
	}

	public void setDesKey(String desKey)
	{
		this.desKey = desKey;
	}

	public String getSigCntBeanId()
	{
		return sigCntBeanId;
	}

	public void setSigCntBeanId(String sigCntBeanId)
	{
		this.sigCntBeanId = sigCntBeanId;
	}

	public String getEchoMsgCd()
	{
		return echoMsgCd;
	}

	public void setEchoMsgCd(String echoMsgCd)
	{
		this.echoMsgCd = echoMsgCd;
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

	public String getAuthMsgCd()
	{
		return authMsgCd;
	}

	public void setAuthMsgCd(String authMsgCd)
	{
		this.authMsgCd = authMsgCd;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getReplyToQ()
	{
		return replyToQ;
	}

	public void setReplyToQ(String replyToQ)
	{
		this.replyToQ = replyToQ;
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
