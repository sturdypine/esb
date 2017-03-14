package spc.esb.data.sig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAtomNode2SigContent implements AtomNode2SigContent
{
	protected Logger log = LoggerFactory.getLogger(getClass());

	public void init() throws Exception
	{
		if (AtomNode2SigContent.SIGS.containsKey(name)) log.warn("IAtomNodeSigContent(" + name
				+ ") repeated!!!");
		AtomNode2SigContent.SIGS.put(name, this);
	}

	protected String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
