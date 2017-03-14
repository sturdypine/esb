package spc.esb.data;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 默认的报文在表示数组是<array><v>a</v><v>b</v></array>
 * 很多xml报文在数组上表示是<array>a</array><array></array>
 * 
 * @author spc
 * 
 */
public class Array2Node2XML extends DefaultNode2XML
{
	public void array(OutputStream os, IArrayNode anode, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException
	{
		path.add(tag);
		for (int i = 0; i < anode.size(); i++)
		{
			INode node = (INode) anode.getNode(i);
			if (node == null) continue;
			if (i > 0 && pretty)
			{ // for pretty
				os.write('\n');
				for (int j = 0; j < (path.size() - 2); j++)
					os.write('\t');
			}
			node2xml(os, node, ns, tag, pretty, root, parent, path, attribute, index);
		}
		path.remove(path.size() - 1);
	}

	public void node2xml(OutputStream os, INode node, String ns, String tag, boolean pretty,
			ICompositeNode root, ICompositeNode parent, List path, Map attribute, int index)
			throws IOException
	{
		if (node.isNull()) return;
		if (!(node instanceof IArrayNode)) startElement(os, node, ns, tag, pretty, root, parent,
				path, attribute);

		if (node instanceof IArrayNode) array(os, (IArrayNode) node, ns, tag, pretty, root, parent,
				path, attribute, index);
		else if (node instanceof ICompositeNode) map(os, (ICompositeNode) node, ns, tag, pretty,
				root, parent, path, attribute, index);
		else atom(os, (IAtomNode) node, ns, tag, pretty, root, parent, path, attribute, index);

		if (pretty && (node instanceof ICompositeNode))
		{ // for pretty
			os.write('\n');
			for (int j = 0; j < (path.size() - 2); j++) // -1 变为 -2
				os.write('\t');
		}
		if (!(node instanceof IArrayNode)) endElement(os, node, ns, tag, pretty, root, path,
				attribute);
	}

	final static Array2Node2XML A2N2X = new Array2Node2XML();

	public static INode2XML getInstance()
	{
		return A2N2X;
	}
}
