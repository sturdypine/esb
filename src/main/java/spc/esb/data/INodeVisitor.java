package spc.esb.data;

public interface INodeVisitor
{
	boolean visitor(INode node, INode parent, String name);
}
