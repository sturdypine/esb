package spc.esb.converter;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.IMessage;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;

/**
 * 通用的前端报文接受转换器，将接受的二进制放入msg，同时根据接入ip和端口查找发送方系统编号放入sndappcd中。 和
 * DefaultCoreMessageConverter 配合使用
 * 
 * @author chenjs
 * 
 */
public class IPAndPortMsgConverter extends AbstractMsgConverter
{
	public IMessage deserialize(byte[] buf, IMessage msg) throws Exception
	{
		msg.setOriginalBytes(buf);
		String remoteIP = (String) msg.getInLocal(ESBMsgLocalKey.ACCEPTOR_REMOTE_HOST);
		Integer localPort = (Integer) msg.getInLocal(ESBMsgLocalKey.ACCEPTOR_LOCAL_PORT);

		// 根据uri, localport, remoteIP获取身份信息
		msg.setSndAppCd(StringX.nullity(sndAppCd)
				? esbInfoService.getNodeByUriPortIP(localPort, remoteIP).getAppCd() : sndAppCd);
		// 同步调用模式下, 从报文中获取snddt, seqnb字段
		String sndDt = FastDateFormat.getInstance("yyyyMMdd").format(new Date());
		if (!StringX.nullity(sndDt)) msg.setSndDt(sndDt);
		String seqNb = SpringUtil.random(randomSeqNbLen);
		if (!StringX.nullity(seqNb)) msg.setSeqNb(seqNb);
		if (log.isInfoEnabled()) log.info("ip:" + remoteIP + ", port:" + localPort + ", header: "
				+ msg.getHeader().toXml(IMessage.TAG_HEADER, false));
		return msg;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		byte[] response = msg.getOriginalBytes();
		if (response == null)
		{ // 如果original bytes为空，则表示可能MQ Call过程中出现异常
			if (log.isInfoEnabled()) log.info(
					"default response:" + (defaultResponse != null ? defaultResponse.length : 0)
							+ ", header:" + msg.getHeader().toXml(IMessage.TAG_HEADER, true));
			if (defaultResponse != null) return defaultResponse;
		}
		return response;
	}

	protected String sndAppCd; // 如果指定具体发送系统
	protected int randomSeqNbLen = 15;
	protected byte[] defaultResponse; // 错误等情况下默认返回前端的字节，使用base64注入

	public void setSndAppCd(String sndAppCd)
	{
		this.sndAppCd = sndAppCd;
	}

	public void setRandomSeqNbLen(int randomSeqNbLen)
	{
		this.randomSeqNbLen = randomSeqNbLen;
	}

	public byte[] getDefaultResponse()
	{
		return defaultResponse;
	}

	public void setDefaultResponse(byte[] defaultResponse)
	{
		this.defaultResponse = defaultResponse;
	}

	public void setDefaultResponse(String defaultResponse)
	{
		this.defaultResponse = StringX.decodeBase64(defaultResponse);
	}
}
