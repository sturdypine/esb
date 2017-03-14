package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.util.StringX;

@Entity
@Table(name = "esb_fvmapping")
public class FvMappingPO implements Serializable
{
	public static final long serialVersionUID = 20090722L;
	// 和物理表对应字段的属性
	@Id
	@Column
	String fvMapId; // 主键
	@Id
	@Column
	String esbFv; //
	@Id
	@Column
	String mbrCd = StringX.EMPTY_STRING; //
	@Id
	@Column
	String appCd; //

	@Column
	String appFv; //
	@Column
	String fvDesc; //
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

	public String getFvMapId()
	{
		return fvMapId;
	}

	public void setFvMapId(String fvMapId)
	{
		this.fvMapId = fvMapId;
	}

	public String getEsbFv()
	{
		return esbFv;
	}

	public void setEsbFv(String esbFv)
	{
		this.esbFv = esbFv;
	}

	public String getMbrCd()
	{
		return mbrCd;
	}

	public void setMbrCd(String mbrCd)
	{
		this.mbrCd = mbrCd;
	}

	public String getAppCd()
	{
		return appCd;
	}

	public void setAppCd(String appCd)
	{
		this.appCd = appCd;
	}

	public String getAppFv()
	{
		return appFv;
	}

	public void setAppFv(String appFv)
	{
		this.appFv = appFv;
	}

	public String getFvDesc()
	{
		return fvDesc;
	}

	public void setFvDesc(String fvDesc)
	{
		this.fvDesc = fvDesc;
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
