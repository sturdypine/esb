package spc.esb.data.sig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMsgSigContent implements MsgSigContent {
	protected Logger log = LoggerFactory.getLogger(getClass());
	protected String name;

	public void init() throws Exception {
		if (MsgSigContent.SIG.containsKey(name))
			log.warn("MsgSigContent(" + name + ") repeated!!!");
		MsgSigContent.SIG.put(name, this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
