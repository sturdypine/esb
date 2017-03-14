package spc.esb.data;

public interface IBlobMsgHeader
{
	byte[] toMsg(boolean containXml);

	boolean containXML();
	
	void setSndDt(String sndDt);
	
	void setSndTm(String sndTm);
	
	void setSeqNb(String seqNb);
	
	void setRcvMbrCd(String rcvMbrCd);
	
	void setSndAppCd(String sndAppCd);
	
	void setSndMbrCd(String sndMbrCd);
	
	ICompositeNode toCNode(ICompositeNode cnode);
	
	IBlobMsgHeader newInstance();
}
