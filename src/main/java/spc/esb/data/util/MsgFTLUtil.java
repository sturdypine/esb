package spc.esb.data.util;

import java.util.Map;

import spc.esb.constant.ESBCommon;
import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.webos.util.FTLUtil;

public class MsgFTLUtil extends FTLUtil
{
	public static Map model(Map root, IMessage msg)
	{
		root = model(root);
		if (msg == null) return root;
		ICompositeNode transaction = msg.getTransaction();
		ICompositeNode body = transaction.findComposite(IMessage.TAG_BODY,
				new CompositeNode());
//		ICompositeNode response = transaction.findComposite(IMessage.PATH_RESPONSE,
//				new CompositeNode());
		root.put(ESBCommon.MODEL_MSG, transaction);
		root.put(ESBCommon.MODEL_XML, transaction);
		root.put(ESBCommon.MODEL_BODY, body);
//		root.put(Common.MODEL_MSG_RESPONSE, response);
		root.put(ESBCommon.MODEL_MSG_OBJ, msg);
		root.put(ESBCommon.MODEL_MSG_SN, msg.getMsgSn());
		root.put(ESBCommon.MODEL_MSG_CD, msg.getMsgCd());
		root.put(ESBCommon.MODEL_MSG_LOCAL, msg.getLocal());
		root.put(ESBCommon.MODEL_MSG_ATTR, msg.getAttr());
		root.put(ESBCommon.MODEL_MSG_LOCAL_BPL_VARS,
				msg.getInLocal(ESBMsgLocalKey.LOCAL_BPL_VARIABLES));
		IMessage pmsg = (IMessage) msg.getInLocal(ESBMsgLocalKey.LOCAL_PARENT_MSG);
		if (pmsg != null)
		{
			root.put(ESBCommon.MODEL_PARENT_MSG, pmsg.getTransaction());
			root.put(ESBCommon.MODEL_MSG_PREQUEST, pmsg.getRequest());
			root.put(ESBCommon.MODEL_MSG_PRESPONSE, pmsg.getResponse());
		}
		return root;
	}
}
