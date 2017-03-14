package spc.esb.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.webos.util.StringX;

public abstract class AbstractNodeConverter implements INodeConverter
{
	protected String name;
	protected Logger log = LoggerFactory.getLogger(getClass());

	public void init()
	{
		if (!StringX.nullity(name))
		{
			if (CONVERTERS.containsKey(name)) log.warn("CONVERTER(" + name + ") has bean exsit!!!");
			CONVERTERS.put(name, this);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}