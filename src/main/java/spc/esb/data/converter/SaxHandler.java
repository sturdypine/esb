package spc.esb.data.converter;

import org.xml.sax.helpers.DefaultHandler;

import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;

public abstract class SaxHandler extends DefaultHandler
{
	public void start()
	{
		setRoot(new CompositeNode());
	}

	public abstract void setRoot(ICompositeNode root);

	public abstract ICompositeNode root();
}
