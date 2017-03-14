package spc.esb.converter;

import java.io.ByteArrayOutputStream;

import spc.esb.data.IMessage;
import spc.esb.data.converter.MessageConverter;

/*
 * 抽象定义报文转换类
 * chenjs
 */
public abstract class AbstractMsgConverter extends BaseMsgConverter implements
		MessageConverter
{
	// 获取请求报文，可能遇到同步和异步两种模式，如果是同步模式msg就是请求报文，如果是异步模式需要从数据库恢复
	protected IMessage getRequestMsg(byte[] buf, IMessage msg) throws Exception
	{
		return msg;
	}

	public IMessage deserialize(byte[] buf, int offset, int len) throws Exception
	{
		return deserialize(buf, offset, len, null);
	}

	public IMessage deserialize(byte[] buf, int offset, int len, IMessage msg) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(buf, offset, len);
		return deserialize(baos.toByteArray(), msg);
	}

	public IMessage deserialize(byte[] buf) throws Exception
	{
		return deserialize(buf, null);
	}

	protected boolean inMB = true; // 2012-01-25 chenjs 是否MB内置运行模式,
									// 默认适配器都是内置运行模式

	public boolean isInMB()
	{
		return inMB;
	}

	public void setInMB(boolean inMB)
	{
		this.inMB = inMB;
	}
}
