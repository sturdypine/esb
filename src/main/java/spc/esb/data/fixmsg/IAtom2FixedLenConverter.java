package spc.esb.data.fixmsg;

import spc.esb.data.IAtomNode;
import spc.esb.data.INode;
import spc.esb.model.MsgSchemaPO;

public interface IAtom2FixedLenConverter
{
	/**
	 * 将一个具体的字段值写入到定长报文中。比如由于不同系统对数字类型的补齐规则可能不一样。
	 * 
	 * @param fixedLen
	 * @param start
	 * @param len
	 * @param value
	 */
	void pack(byte[] fixedLen, int offset, IAtomNode value, MsgSchemaPO struct, String charset)
			throws Exception;

	/**
	 * 将定长报文中的某个字段提取变成一个INode节点
	 * 
	 * @param fixedLen
	 * @param struct
	 * @return
	 * @throws Exception
	 */
	INode unpack(byte[] fixedLen, int offset, MsgSchemaPO struct, String charset) throws Exception;

	final static String DEFAULT_NUM_VALUE = "0";
	static final String TO_RIGHT = "R"; // 有些定长需要左边补空格，放在报文规范的ext1里面,默认是右边补空格
	static final String TRIM = "T"; // 是否trim
	static final String KICK = "K"; // 踢掉内容中的空格 added by chenjs 2011-11-22
}
