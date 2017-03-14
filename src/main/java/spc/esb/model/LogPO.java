package spc.esb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import spc.webos.util.StringX;

@Entity
@Table(name = "esb_log")
public class LogPO implements Serializable
{
	public static final long serialVersionUID = 20100602L;
	// 和物理表对应字段的属性
	@Column
	String msgSn; //
	@Column
	String logPoint; //
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
	String fts; //
	// String refCallTyp; //
	@Column
	String retCd; //
	@Column
	String retDesc; //
	@Column
	String location; //
	@Column
	String traceNo; //
	@Column
	String ext;
	@Column
	String replyToQ;
	@Column
	String replyMsgCd; //
	@Column
	String mbrCd = StringX.EMPTY_STRING; //
	@Column
	String ip; //
	@Column
	String broker;
	@Column
	String eg;
	@Column
	String appCd; //
	@Column
	String tmStamp; //
	@Column
	String tdt;
	String pdd;
	@Column
	String ttm;
	@Column
	String ttm10s;
	@Column
	String ttm1m;
	@Column
	String ttm10m;
	@Column
	String ttm1h;
	@Id
	@Column
	Long seq;
	@Column
	String biz1;
	@Column
	String biz2;
	@Column
	String biz3;
	@Column
	String biz4;
	@Column
	String biz5;
	@Column
	String biz6;
	@Column
	String biz7;
	@Column
	String biz8;
	@Column
	String biz9;
	@Column
	Integer orignalLen;

	// 2012-06-12 增加BPL信息
	String bplPSN;
	String bplNode;
	String bplFailAbort;
	String bplPSndAppCd;
	String bplPSndMbrCd;

	public String getMsgSn()
	{
		return msgSn;
	}

	public void setMsgSn(String msgSn)
	{
		this.msgSn = msgSn;
	}

	public String getLogPoint()
	{
		return logPoint;
	}

	public void setLogPoint(String logPoint)
	{
		this.logPoint = logPoint;
	}

	public String getMsgCd()
	{
		return msgCd;
	}

	public void setMsgCd(String msgCd)
	{
		this.msgCd = msgCd;
	}

	public String getTraceNo()
	{
		return traceNo;
	}

	public void setTraceNo(String traceNo)
	{
		this.traceNo = traceNo;
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

	public String getReplyMsgCd()
	{
		return replyMsgCd;
	}

	public void setReplyMsgCd(String replyMsgCd)
	{
		this.replyMsgCd = replyMsgCd;
	}

	public String getFts()
	{
		return fts;
	}

	public void setFts(String fts)
	{
		this.fts = fts;
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

	public String getExt()
	{
		return ext;
	}

	public void setExt(String ext)
	{
		this.ext = ext;
	}

	public String getReplyToQ()
	{
		return replyToQ;
	}

	public void setReplyToQ(String replyToQ)
	{
		this.replyToQ = replyToQ;
	}

	public String getMbrCd()
	{
		return mbrCd;
	}

	public void setMbrCd(String mbrCd)
	{
		this.mbrCd = mbrCd;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
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

	public String getTdt()
	{
		return tdt;
	}

	public void setTdt(String tdt)
	{
		this.tdt = tdt;
	}

	public String getPdd()
	{
		return pdd;
	}

	public void setPdd(String pdd)
	{
		this.pdd = pdd;
	}

	public String getTtm()
	{
		return ttm;
	}

	public void setTtm(String ttm)
	{
		this.ttm = ttm;
	}

	public String getTtm10s()
	{
		return ttm10s;
	}

	public void setTtm10s(String ttm10s)
	{
		this.ttm10s = ttm10s;
	}

	public String getTtm1m()
	{
		return ttm1m;
	}

	public void setTtm1m(String ttm1m)
	{
		this.ttm1m = ttm1m;
	}

	public String getTtm10m()
	{
		return ttm10m;
	}

	public void setTtm10m(String ttm10m)
	{
		this.ttm10m = ttm10m;
	}

	public String getTtm1h()
	{
		return ttm1h;
	}

	public void setTtm1h(String ttm1h)
	{
		this.ttm1h = ttm1h;
	}

	public Long getSeq()
	{
		return seq;
	}

	public void setSeq(Long seq)
	{
		this.seq = seq;
	}

	public String getBiz1()
	{
		return biz1;
	}

	public void setBiz1(String biz1)
	{
		this.biz1 = biz1;
	}

	public String getBiz2()
	{
		return biz2;
	}

	public void setBiz2(String biz2)
	{
		this.biz2 = biz2;
	}

	public String getBiz3()
	{
		return biz3;
	}

	public void setBiz3(String biz3)
	{
		this.biz3 = biz3;
	}

	public String getBiz4()
	{
		return biz4;
	}

	public void setBiz4(String biz4)
	{
		this.biz4 = biz4;
	}

	public String getBiz5()
	{
		return biz5;
	}

	public void setBiz5(String biz5)
	{
		this.biz5 = biz5;
	}

	public String getBiz6()
	{
		return biz6;
	}

	public void setBiz6(String biz6)
	{
		this.biz6 = biz6;
	}

	public String getBiz7()
	{
		return biz7;
	}

	public void setBiz7(String biz7)
	{
		this.biz7 = biz7;
	}

	public String getBiz8()
	{
		return biz8;
	}

	public void setBiz8(String biz8)
	{
		this.biz8 = biz8;
	}

	public String getBiz9()
	{
		return biz9;
	}

	public void setBiz9(String biz9)
	{
		this.biz9 = biz9;
	}

	public Integer getOrignalLen()
	{
		return orignalLen;
	}

	public void setOrignalLen(Integer orignalLen)
	{
		this.orignalLen = orignalLen;
	}

	public String getBplPSN()
	{
		return bplPSN;
	}

	public void setBplPSN(String bplPSN)
	{
		this.bplPSN = bplPSN;
	}

	public String getBplNode()
	{
		return bplNode;
	}

	public void setBplNode(String bplNode)
	{
		this.bplNode = bplNode;
	}

	public String getBplFailAbort()
	{
		return bplFailAbort;
	}

	public void setBplFailAbort(String bplFailAbort)
	{
		this.bplFailAbort = bplFailAbort;
	}

	public String getBplPSndAppCd()
	{
		return bplPSndAppCd;
	}

	public void setBplPSndAppCd(String bplPSndAppCd)
	{
		this.bplPSndAppCd = bplPSndAppCd;
	}

	public String getBplPSndMbrCd()
	{
		return bplPSndMbrCd;
	}

	public void setBplPSndMbrCd(String bplPSndMbrCd)
	{
		this.bplPSndMbrCd = bplPSndMbrCd;
	}
}
