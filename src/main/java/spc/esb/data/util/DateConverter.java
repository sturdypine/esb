package spc.esb.data.util;

import java.text.SimpleDateFormat;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.model.MsgSchemaPO;
import spc.webos.exception.AppException;

public class DateConverter extends AtomConverter
{
	protected String esbFormat;
	protected String format;

	public IAtomNode converter(IMessage msg, IAtomNode src, MsgSchemaPO struct, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode)
	{
		String dt = src.stringValue();
		if (dt.length() == 0) return src;

		try
		{
			return esb2rcv ? new AtomNode(new SimpleDateFormat(format).format(new SimpleDateFormat(
					esbFormat).parse(dt)), src.getExt()) : new AtomNode(new SimpleDateFormat(
					esbFormat).format(new SimpleDateFormat(format).parse(dt)), src.getExt());
		}
		catch (Exception e)
		{
			throw new AppException(ESBRetCode.MSG_STRING_FORMAT, e, esb2rcv ? new Object[] { dt,
					esbFormat, format } : new Object[] { dt, format, esbFormat });
		}
	}

	public void setEsbFormat(String esbFormat)
	{
		this.esbFormat = esbFormat;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}
}
