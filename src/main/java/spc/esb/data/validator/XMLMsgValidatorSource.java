package spc.esb.data.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import spc.esb.data.ArrayNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.MessageSchema;
import spc.esb.data.INode;
import spc.esb.data.converter.SOAPConverter;
import spc.esb.model.MsgSchemaPO;
import spc.esb.model.MsgValidatorPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * xml格式配置报文的验证信息
 * 
 * @author spc
 * 
 */
public class XMLMsgValidatorSource implements MessageSchema
{
	public final static Logger log = LoggerFactory.getLogger(XMLMsgValidatorSource.class);
	protected Resource dir;
	protected Resource cdir; // 组合验证的配置信息
	protected Map source;
	protected Map csource;

	public TreeNode getMsgSchema(String name)
	{
		return source == null ? null : (TreeNode) source.get(name);
	}

	public List getMsgValidator(String name)
	{
		return csource == null ? null : (List) csource.get(name);
	}

	public void init() throws Exception
	{
		refresh();
	}

	public void refresh() throws Exception
	{
		source = loadMsgStruct();
		csource = loadMsgValidator();
	}

	private Map loadMsgStruct() throws Exception
	{
		if (dir == null) return null;
		if (log.isInfoEnabled()) log.info("loadMsgStruct dir: " + dir.getFilename());
		Map source = new HashMap();
		File file = dir.getFile();
		File[] files = file.listFiles();
		for (int i = 0; files != null && i < files.length; i++)
		{
			String fileName = files[i].getName();
			if (!(fileName.endsWith(".xml"))) continue;
			if (log.isInfoEnabled()) log.info("xmlfile: " + fileName);
			IMessage msg = null;
			String ns = fileName.substring(0, fileName.length() - 4);
			InputStream is = new FileInputStream(files[i]);
			try
			{
				msg = SOAPConverter.getInstance().deserialize(is);
			}
			catch (Exception e)
			{
				log.error("load file:" + files[i], e);
				throw e;
			}
			finally
			{
				is.close();
			}

			// 将验证报文变为MsgStruct的tree node模式
			TreeNode root = create(IMessage.TAG_ROOT, msg.getTransaction(), new TreeNode());
			source.put(ns, root);
		}
		return source;
	}

	private Map loadMsgValidator() throws Exception
	{
		if (null == cdir) return null;
		Map csource = new HashMap();
		if (log.isInfoEnabled()) log.info("loadMsgValidator dir: " + cdir.getFilename());
		File file = cdir.getFile();
		File[] files = file.listFiles();
		for (int i = 0; files != null && i < files.length; i++)
		{
			String fileName = files[i].getName();
			if (!(fileName.endsWith(".xml"))) continue;
			if (log.isInfoEnabled()) log.info("xmlfile: " + fileName);
			ICompositeNode validators = null;
			String ns = fileName.substring(0, fileName.length() - 4);
			InputStream is = new FileInputStream(files[i]);
			try
			{
				validators = SOAPConverter.getInstance().deserialize2composite(is);
			}
			catch (Exception e)
			{
				log.error("load file:" + files[i], e);
				throw e;
			}
			finally
			{
				is.close();
			}

			// 将验证报文变为MsgStruct的tree node模式
			List cv = new ArrayList();
			IArrayNode settings = validators.findArray("setting", new ArrayNode());
			for (int j = 0; j < settings.size(); j++)
				cv.add(((ICompositeNode) settings.get(j)).toObject(new MsgValidatorPO()));
			csource.put(ns, cv);
		}
		return csource;
	}

	public void setDir(Resource dir)
	{
		this.dir = dir;
	}

	public void setCdir(Resource cdir)
	{
		this.cdir = cdir;
	}

	protected TreeNode create(String name, ICompositeNode cnode, TreeNode root)
	{
		Iterator keys = cnode.keys();
		while (keys.hasNext())
		{
			String key = keys.next().toString();
			INode node = cnode.getNode(key);
			if (node instanceof ICompositeNode && ((ICompositeNode) node).size() == 0) continue;
			TreeNode tnode = create(key, node);
			root.insertChild(tnode);
			if (node instanceof ICompositeNode) create(key, (ICompositeNode) node, tnode);
		}
		return root;
	}

	protected TreeNode create(String name, INode node)
	{
		Map ext = node.getExt();
		TreeNode tnode = new TreeNode();
		MsgSchemaPO struct = new MsgSchemaPO();
		if (ext != null && ext.size() > 0)
		{
			CompositeNode cnode = new CompositeNode();
			cnode.set(ext);
			struct = (MsgSchemaPO) cnode.toObject(struct);
		}
		struct.setEsbName(name);
		if (StringX.nullity(struct.getFtyp()))
		{
			if (node instanceof IAtomNode) struct.setFtyp(String.valueOf((char) INode.TYPE_STRING));
			else if (node instanceof ICompositeNode) struct.setFtyp(String
					.valueOf((char) INode.TYPE_MAP));
			else struct.setFtyp(String.valueOf((char) INode.TYPE_ARRAY));
		}
		tnode.setTreeNodeValue(struct);
		return tnode;
	}
}
