package spc.esb.data.validator;

import java.util.List;

import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.FieldError;

import spc.esb.data.ArrayNode;
import spc.esb.data.CompositeNode;
import spc.esb.data.IArrayNode;
import spc.esb.data.IAtomNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;

public class MessageErrors extends AbstractBindingResult
{
	IMessage msg;
	private static final long serialVersionUID = 1L;

	public MessageErrors(IMessage msg)
	{
		super(msg.getMsgSn() + '/' + msg.getMsgCd());
		this.msg = msg;
	}

	public ICompositeNode toCNode()
	{
		CompositeNode cnode = new CompositeNode();
		List errors = getAllErrors();
		if (errors == null) return cnode;
		cnode.put("errorCount", new Integer(errors.size()));
		List errs = new ArrayNode(errors.size());
		for (int i = 0; i < errors.size(); i++)
		{
			FieldError error = (FieldError) errors.get(i);
			CompositeNode errinf = new CompositeNode();
			String path = error.getField().replace('.', '/');
			String name = path;
			int index = path.indexOf('|');
			if (index > 0)
			{
				name = path.substring(index + 1);
				path = path.substring(0, index);
			}
			errinf.put("path", path);
			errinf.put("name", name);
			Object value = error.getRejectedValue();
			if (value instanceof IAtomNode) errinf.put("value", value); // 只在原子节点的情况下返回整个值
			errinf.put("code", error.getCode());
			Object[] args = error.getArguments();
			errinf.put(
					"desc",
					SpringUtil.getMessage(SpringUtil.RETCD_PATH + error.getCode(), args,
							error.getDefaultMessage(), null));
			errs.add(errinf);
		}
		cnode.put("errors", errs);
		return cnode;
	}

	public String getErrDesc()
	{
		ICompositeNode cnode = toCNode();
		IArrayNode errs = cnode.findArray("errors", null);
		if (errs == null) return StringX.EMPTY_STRING;
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < errs.size(); i++)
		{
			CompositeNode errinf = (CompositeNode) errs.get(i);
			// buf.append(StringX.null2emptystr(errinf.get("name")));
			buf.append(StringX.null2emptystr(errinf.get("desc")));
			// buf.append("(");
			// buf.append(StringX.null2emptystr(errinf.get("path")));
			// buf.append(")");
			buf.append('\n');
		}
		return buf.toString();
	}

	protected Object getActualFieldValue(String field)
	{
		int index = field.indexOf('|');
		if (index > 0) field = field.substring(0, index);
		return msg.getTransaction().find(field.replace('.', '/'));
	}

	public Object getTarget()
	{
		return msg;
	}
}
