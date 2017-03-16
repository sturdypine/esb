package spc.esb.common.service;

import spc.esb.data.IMessage;

/**
 * 签名服务接口
 * 
 * 
 * 
 */
public interface SignatureService
{
	// 签名方法，返回签名后的签名串
	// msg表示当前报文
	// node表示接收该签名的系统，sig方法需要获取node所要求的摘要方法和签名方法来进行签名
	// srcBytes表示默认的待签名内容
	String sig(IMessage msg, String nodeApp, byte[] srcBytes) throws Exception;

	// 核签方法，返回核签是否成功
	// msg表示当前报文
	// node表示发起该签名的系统，unsig方法需要获取node所使用的摘要方法和签名方法来完成核签
	// srcBytes表示默认的待核签内容
	// signature表示待核签的签名串
	boolean unsig(IMessage msg, String nodeApp, byte[] srcBytes, String signature) throws Exception;

	// final static String SIG_MODE_BODY = "0"; // 基于xml的body标签到body标签的签名模式
	// final static String SIG_MODE_ELEMENT = "1"; // 基于具体xml的样本内容中的签名要素签名

	boolean isUnsig(IMessage msg); // 判断是否需要验签，根据报文属性和ext/unsign节点判断
}
