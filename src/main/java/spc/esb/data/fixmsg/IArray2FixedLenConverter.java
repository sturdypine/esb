package spc.esb.data.fixmsg;

import spc.esb.data.IArrayNode;
import spc.webos.util.tree.TreeNode;

/**
 * 将XML中的数组节点变成定长报文
 * 
 * @author spc
 * 
 */
public interface IArray2FixedLenConverter
{
	void pack(byte[] fixedLen, int offset, IArrayNode value, TreeNode struct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception;

	IArrayNode unpack(byte[] fixedLen, int offset, TreeNode struct,
			IAtom2FixedLenConverter atom2fixedLen, IArray2FixedLenConverter array2fixedLen,
			String charset) throws Exception;
}
