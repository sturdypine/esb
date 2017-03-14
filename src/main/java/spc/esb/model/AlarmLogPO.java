package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.util.StringX;

@Entity
@Table(name = "esb_alarm")
public class AlarmLogPO implements Serializable
{
	public static final long serialVersionUID = 20100730L;
	// 和物理表对应字段的属性
	@Column
	String msgSn; //
	@Column
	String msgCd; //
	@Column
	String lang;
	@Column
	String sndMbrCd = StringX.EMPTY_STRING; //
	@Column
	String sndAppCd; //
	@Column
	String sndDt; //
	@Column
	String seqNb; //
	@Column
	String sndTm; //
	@Column
	String callTyp; //
	@Column
	String rcvMbrCd = StringX.EMPTY_STRING; //
	@Column
	String rcvAppCd; //
	@Column
	String rcvAppSn; //
	@Column
	String refRcvAppSn; //
	@Column
	String refMsgCd; //
	@Column
	String refSndMbrCd = StringX.EMPTY_STRING; //
	@Column
	String refSndAppCd; //
	@Column
	String refSndDt; //
	@Column
	String refSeqNb; //
	@Column
	String refCallTyp; //
	@Column
	String retCd; //
	@Column
	String retDesc; //
	@Column
	String location; //
	@Column
	String traceNo; //
	@Column
	String broker;
	@Column
	String eg;
	@Column
	String mbrCd = StringX.EMPTY_STRING; //
	@Column
	String ip = StringX.EMPTY_STRING; //
	@Column
	String appCd; //
	@Column
	String tmStamp; //
	@Id
	@Column
	Long seq;

	public String getMsgSn()
	{
		return msgSn;
	}

	public void setMsgSn(String msgSn)
	{
		this.msgSn = msgSn;
	}

	public String getMsgCd()
	{
		return msgCd;
	}

	public void setMsgCd(String msgCd)
	{
		this.msgCd = msgCd;
	}

	public String getLang()
	{
		return lang;
	}

	public void setLang(String lang)
	{
		this.lang = lang;
	}

	public String getSndMbrCd()
	{
		return sndMbrCd;
	}

	public void setSndMbrCd(String sndMbrCd)
	{
		this.sndMbrCd = sndMbrCd;
	}

	public String getSndAppCd()
	{
		return sndAppCd;
	}

	public void setSndAppCd(String sndAppCd)
	{
		this.sndAppCd = sndAppCd;
	}

	public String getSndDt()
	{
		return sndDt;
	}

	public void setSndDt(String sndDt)
	{
		this.sndDt = sndDt;
	}

	public String getSeqNb()
	{
		return seqNb;
	}

	public void setSeqNb(String seqNb)
	{
		this.seqNb = seqNb;
	}

	public String getSndTm()
	{
		return sndTm;
	}

	public void setSndTm(String sndTm)
	{
		this.sndTm = sndTm;
	}

	public String getCallTyp()
	{
		return callTyp;
	}

	public void setCallTyp(String callTyp)
	{
		this.callTyp = callTyp;
	}

	public String getRcvMbrCd()
	{
		return rcvMbrCd;
	}

	public void setRcvMbrCd(String rcvMbrCd)
	{
		this.rcvMbrCd = rcvMbrCd;
	}

	public String getRcvAppCd()
	{
		return rcvAppCd;
	}

	public void setRcvAppCd(String rcvAppCd)
	{
		this.rcvAppCd = rcvAppCd;
	}

	public String getRcvAppSn()
	{
		return rcvAppSn;
	}

	public void setRcvAppSn(String rcvAppSn)
	{
		this.rcvAppSn = rcvAppSn;
	}

	public String getRefRcvAppSn()
	{
		return refRcvAppSn;
	}

	public void setRefRcvAppSn(String refRcvAppSn)
	{
		this.refRcvAppSn = refRcvAppSn;
	}

	public String getRefMsgCd()
	{
		return refMsgCd;
	}

	public void setRefMsgCd(String refMsgCd)
	{
		this.refMsgCd = refMsgCd;
	}

	public String getRefSndMbrCd()
	{
		return refSndMbrCd;
	}

	public void setRefSndMbrCd(String refSndMbrCd)
	{
		this.refSndMbrCd = refSndMbrCd;
	}

	public String getRefSndAppCd()
	{
		return refSndAppCd;
	}

	public void setRefSndAppCd(String refSndAppCd)
	{
		this.refSndAppCd = refSndAppCd;
	}

	public String getRefSndDt()
	{
		return refSndDt;
	}

	public void setRefSndDt(String refSndDt)
	{
		this.refSndDt = refSndDt;
	}

	public String getRefSeqNb()
	{
		return refSeqNb;
	}

	public void setRefSeqNb(String refSeqNb)
	{
		this.refSeqNb = refSeqNb;
	}

	public String getRefCallTyp()
	{
		return refCallTyp;
	}

	public void setRefCallTyp(String refCallTyp)
	{
		this.refCallTyp = refCallTyp;
	}

	public String getRetCd()
	{
		return retCd;
	}

	public void setRetCd(String retCd)
	{
		this.retCd = retCd;
	}

	public String getRetDesc()
	{
		return retDesc;
	}

	public void setRetDesc(String retDesc)
	{
		this.retDesc = retDesc;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getBroker()
	{
		return broker;
	}

	public void setBroker(String broker)
	{
		this.broker = broker;
	}

	public String getEg()
	{
		return eg;
	}

	public void setEg(String eg)
	{
		this.eg = eg;
	}

	public String getMbrCd()
	{
		return mbrCd;
	}

	public void setMbrCd(String mbrCd)
	{
		this.mbrCd = mbrCd;
	}

	public String getTraceNo()
	{
		return traceNo;
	}

	public void setTraceNo(String traceNo)
	{
		this.traceNo = traceNo;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getAppCd()
	{
		return appCd;
	}

	public void setAppCd(String appCd)
	{
		this.appCd = appCd;
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
