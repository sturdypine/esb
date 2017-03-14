package spc.esb.data.converter;

import java.util.HashMap;
import java.util.Map;

import spc.esb.data.IMessage;

/**
 * 用于核心, 如mb核心调用的适配器转换抽象类
 * 
 * @author chenjs
 * 
 */
public interface CoreMessageConverter
{
	void app2esb(IMessage msg, boolean request) throws Exception;

	// 返回后端系统的二进制，如果是ESB标准xml则返回NULL，否则返回实际发往后端系统的bytes
	byte[] esb2app(IMessage msg, boolean request) throws Exception;

	Map<String, CoreMessageConverter> CORE_MSG_CVTERS = new HashMap<>(); // 存放所有核心适配器
}
