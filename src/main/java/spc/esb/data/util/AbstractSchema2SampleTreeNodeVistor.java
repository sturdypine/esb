package spc.esb.data.util;

import spc.webos.util.tree.ITreeNodeVistor;
import spc.webos.util.tree.TreeNode;

public abstract class AbstractSchema2SampleTreeNodeVistor implements ITreeNodeVistor
{
	protected StringBuilder buf = new StringBuilder();
	protected int level;

	public String sample(TreeNode tnode)
	{
		buf.setLength(0);
		tnode.dfsTraverse(this);
		return buf.toString();
	}

	protected String pretty()
	{
		StringBuffer buf = new StringBuffer();
		buf.append('\n');
		for (int i = 0; i < level; i++)
			buf.append('\t');
		return buf.toString();
	}

	public void clear()
	{
		buf.setLength(0);
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public String toString()
	{
		return buf.toString();
	}
}
