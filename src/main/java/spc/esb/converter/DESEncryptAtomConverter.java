package spc.esb.converter;

import javax.annotation.Resource;

import spc.esb.common.service.ESBInfoService;
import spc.esb.data.AtomNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.util.AtomConverter;
import spc.esb.model.MsgSchemaPO;
import spc.esb.model.NodePO;
import spc.webos.util.CipherUtil;
import spc.webos.util.StringX;

/**
 * 使用DES算法进行转加密原子转换节点
 * 
 * @author chenjs
 * 
 */
public class DESEncryptAtomConverter extends AtomConverter
{
	public IAtomNode converter(IMessage msg, IAtomNode src, MsgSchemaPO schema, boolean esb2rcv,
			ICompositeNode pnode, String path, ICompositeNode tpnode) throws Exception
	{
		if (src == null) return null;
		NodePO sndNodeVO = esbInfoService.getNode(msg.getSndNodeApp());
		NodePO rcvNodeVO = esbInfoService.getNode(msg.getRcvNodeApp());
		if (sndNodeVO == null)
		{
			log.warn("sndNodeVO is null by:" + msg.getSndNodeApp());
			return src;
		}
		if (rcvNodeVO == null)
		{
			log.warn("rcvNodeVO is null by:" + msg.getRcvNodeApp());
			return src;
		}
		byte[] plain = CipherUtil.desDecrypt(src.byteValue(),
				StringX.decodeBase64(sndNodeVO.getDesKey().getBytes()));
		byte[] encrypt = CipherUtil.desEncrypt(plain,
				StringX.decodeBase64(rcvNodeVO.getDesKey().getBytes()));
		if (log.isDebugEnabled())
			log.debug("src:" + src + ", plain: " + new String(StringX.encodeBase64(plain))
					+ ", encrypt: " + new String(StringX.encodeBase64(encrypt)));
		return new AtomNode(encrypt);
	}

	public DESEncryptAtomConverter()
	{
		name = "DESEncrypt";
	}

	@Resource
	protected ESBInfoService esbInfoService;

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}
}
