package spc.esb.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEncryptSig implements Encrypt, Signature {
	public void init() throws Exception {
		if (Encrypt.ENCRYPTS.containsKey(name))
			log.warn("Encrypt(" + name + ") repeated!!!");
		Encrypt.ENCRYPTS.put(name, this);
		if (Signature.SIGS.containsKey(name))
			log.warn("Signature(" + name + ") repeated!!!");
		Signature.SIGS.put(name, this);
	}

	protected Logger log = LoggerFactory.getLogger(getClass());
	protected String name;

	public void setName(String name) {
		this.name = name;
	}
}
