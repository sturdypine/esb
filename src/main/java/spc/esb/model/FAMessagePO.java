package spc.esb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "esb_famsg")
public class FAMessagePO extends MessagePO
{
	public static final long serialVersionUID = 20110930L;

	@Column
	protected String esbMsgCd;

	public String getEsbMsgCd()
	{
		return esbMsgCd;
	}

	public void setEsbMsgCd(String esbMsgCd)
	{
		this.esbMsgCd = esbMsgCd;
	}
}
