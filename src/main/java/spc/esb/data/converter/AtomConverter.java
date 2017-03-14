package spc.esb.data.converter;

/**
 * 原子节点转换抽象类
 * 
 * @author spc
 * 
 */
public abstract class AtomConverter implements IAtomConverter
{
	protected String name;

	public void init()
	{
		if (name != null) CONVERTERS.put(name, this);
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
