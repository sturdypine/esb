package spc.esb.data.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.data.Array2Node2XML;
import spc.esb.data.CompositeNode;
import spc.esb.data.ICompositeNode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.INode2XML;
import spc.esb.data.Message;
import spc.esb.data.SequenceCompositeNode;
import spc.webos.constant.Common;
import spc.webos.util.JsonUtil;
import spc.webos.util.StringX;

public class SOAPConverter implements MessageConverter
{
	protected SaxHandler handler = DefaultSaxHandler.getInstance();
	protected String charset = Common.CHARSET_UTF8;
	protected boolean emptyTag = true;
	protected boolean cdata = true;
	protected boolean pretty = false;
	protected boolean serializeAll = false;
	// // added by chenjs 2011-09-01序列化时删除transaction中的local变量
	// protected boolean removeLocal = false;
	// // added by chenjs 2012-01-01序列化时删除originalBytes标签
	// protected boolean removeOriginalBytes = true;
	protected boolean sequence = false;
	protected boolean unvalidXMLEx = true; // 不合法的XML直接异常退出, 否则部分解析后设置Local临时变量
	protected INode2XML node2xml = Array2Node2XML.getInstance(); // 900,
																	// 直接是第二种数组方式
	// protected String soapHeaderTag = "Header";
	// protected String soapBodyTag = "Body";
	public static int SOAP_TEST_LEN = 100; // soap 报文检测头长度
	public static int JSON_TEST_LEN = 10; // JSON 报文检测头长度
	protected boolean usingNodeNS; // 使用节点自带的ns
	protected String ns; // = "esb" // 减少报文空间，所有要素使用默认命名空间
	protected boolean soap = true; // 严格soap报文，带namesapce, 默认是XML
	protected boolean flat = true; // soap报文中Body是否扁平，不扁平则带有报文编号
	final String STR_SOAP_ROOT_START_TAG = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.w3.org/esb/\" xmlns:esb=\"http://www.w3.org/esb/\">\n<soap:Header>";
	final byte[] SOAP_ROOT_START_TAG = STR_SOAP_ROOT_START_TAG.getBytes();
	final String STR_SOAP_MID = "</soap:Header>\n<soap:Body>";
	final byte[] SOAP_MID = STR_SOAP_MID.getBytes();
	final String STR_SOAP_ROOT_END_TAG = "</soap:Body>\n</soap:Envelope>";
	final byte[] SOAP_ROOT_END_TAG = STR_SOAP_ROOT_END_TAG.getBytes();

	protected Logger log = LoggerFactory.getLogger(getClass());

	public SOAPConverter()
	{
	}

	public SOAPConverter(boolean soap)
	{
		this.soap = soap;
	}

	public SOAPConverter(boolean soap, String ns, boolean serializeAll)
	{
		this.soap = soap;
		this.ns = ns;
		this.serializeAll = serializeAll;
	}

	public String getContentType()
	{
		return Common.FILE_XML_CONTENTTYPE;
	}

	public void setHandler(SaxHandler handler)
	{
		this.handler = handler;
	}

	static ThreadLocal SAX_PARSER = new ThreadLocal();

	static SAXParser newSAXParser()
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(false);
			// factory.setXIncludeAware(false);
			return factory.newSAXParser();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static XMLReader getXMLReader(SaxHandler handler)
	{
		XMLReader parser = null;
		try
		{
			// SAXParserFactory factory = SAXParserFactory.newInstance();
			// factory.setValidating(false);
			// factory.setNamespaceAware(false);
			// // factory.setXIncludeAware(false);
			// SAXParser saxParser = factory.newSAXParser();
			// 2012-07-08 构造SAXParser对象平均需要1.5ms, 使用ThreadLocal变量缓存当前线程的解析对象
			if (SAX_PARSER.get() == null) SAX_PARSER.set(newSAXParser());
			parser = ((SAXParser) SAX_PARSER.get()).getXMLReader();
			// parser.setDTDHandler(handler);
			// parser.setEntityResolver(handler);
			parser.setErrorHandler(handler);
			parser.setContentHandler(handler);
		}
		catch (Exception e)
		{
			throw new RuntimeException("No valid sax parser!", e);
		}
		return parser;
	}

	public INode unpack(Object obj)
	{
		InputStream is = (InputStream) obj;
		ICompositeNode root = root();
		handler.setRoot(root);
		try
		{
			getXMLReader(handler).parse(new InputSource(is));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Parse xml error!", e);
		}
		return root;
	}

	protected ICompositeNode root()
	{
		return sequence ? new SequenceCompositeNode() : new CompositeNode();
	}

	public void serialize(IMessage msg, OutputStream os) throws Exception
	{
		if (soap)
		{
			os.write(SOAP_ROOT_START_TAG);
			ICompositeNode hdr = msg.getHeader();
			hdr.toXml(os, ns, null, pretty, node2xml, new HashMap());
			os.write(SOAP_MID);

			ICompositeNode body = msg.getBody();
			if (body != null)
			{
				ICompositeNode nbody = body;
				if (!flat)
				{ // 非扁平模式，需要增加一个报文编号
					nbody = new CompositeNode(); //
					String m = StringX.replaceAll(msg.getMsgCd(), ".", "");
					if (!body.containsKey(m)) nbody.set(m, body); // soap报文提供方法名信息
				}
				nbody.toXml(os, ns, null, pretty, node2xml, new HashMap());
			}
			os.write(SOAP_ROOT_END_TAG);
		}
		else
		{
			if (serializeAll)
			{
				CompositeNode transaction = new CompositeNode(msg.getTransaction());
				transaction.setExt(null); // clear namespace
				new Message(transaction).toXml(os, pretty, node2xml, serializeAttr());
			}
			else
			{
				Message m = new Message();
				m.setBody(msg.getBody());
				m.setHeader(msg.getHeader());
				m.toXml(os, pretty, node2xml, serializeAttr());
			}
		}
	}

	protected Map serializeAttr()
	{
		Map attr = new HashMap();
		if (!cdata) attr.put(INode2XML.ATTR_KEY_NO_CDATA, Boolean.TRUE);
		if (!emptyTag) attr.put(INode2XML.ATTR_KEY_NO_EMPTY_TAG, Boolean.TRUE);

		if (usingNodeNS) attr.put(INode2XML.USING_NODE_NS, Boolean.TRUE);
		return attr;
	}

	public byte[] serialize(IMessage msg) throws Exception
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serialize(msg, baos);
		return baos.toByteArray();
	}

	public IMessage deserialize(byte[] buf) throws Exception
	{
		return deserialize(buf, null);
	}

	public IMessage deserialize(byte[] buf, IMessage msg) throws Exception
	{
		// modified by chenjs 2012-01-17 支持SOAP报文解析
		IMessage nmsg = isJSON(buf) ? deserializeJSON(buf, msg)
				: deserialize(buf, 0, buf.length, msg);
		// nmsg.init();
		nmsg.setInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES, buf);
		return nmsg;
	}

	public IMessage deserializeJSON(byte[] buf, IMessage msg) throws Exception
	{
		String json = new String(buf, charset);
		try
		{
			return new Message(new CompositeNode((Map) JsonUtil.json2obj(json)));
		}
		catch (Exception e)
		{
			log.warn("json:" + json + ",e:" + e);
			throw e;
		}
	}

	public static boolean isJSON(byte[] xml)
	{
		// byte[] hdr = new byte[xml.length < JSON_TEST_LEN ? xml.length :
		// JSON_TEST_LEN];
		// System.arraycopy(xml, 0, hdr, 0, hdr.length);
		return StringX
				.trim(new String(xml, 0, xml.length < JSON_TEST_LEN ? xml.length : JSON_TEST_LEN))
				.startsWith("{");
	}

	public IMessage deserialize(byte[] buf, int offset, int len) throws Exception
	{
		return deserialize(buf, offset, len, null);
	}

	// 2012-08-16 增加unvalidXMLEx变量来控制解析是直接异常，还是部分解析
	public IMessage deserialize(byte[] buf, int offset, int len, IMessage reqmsg) throws Exception
	{
		ICompositeNode transaction = root();
		IMessage msg = null;
		try
		{
			deserialize2composite(buf, offset, len, transaction);
			msg = new Message(transaction);
			if (!flat)
			{ // 去掉Body报文里面的msgCd信息, 取第一个元素
				ICompositeNode body = msg.getBody();
				if (body.size() > 0)
				{
					ICompositeNode nb = body.findComposite(body.keys().next().toString(), null);
					if (nb != null) msg.setBody(nb);
				}
			}
		}
		catch (Exception e)
		{
			String strxml = new String(buf, offset, len, charset);
			log.warn("fail to parse strxml: " + strxml + "\nbase64:" + StringX.base64(buf));
			if (unvalidXMLEx) throw e;

			if (StringX.containUnvalidXmlChar(strxml))
			{ // 删除不合法字符
				byte[] nbuf = StringX.removeUnvalidXmlChar(strxml).getBytes(Common.CHARSET_UTF8);
				try
				{
					transaction = root();
					deserialize2composite(nbuf, 0, nbuf.length, transaction);
					msg = new Message(transaction);
					msg.setInLocal(ESBMsgLocalKey.LOCAL_UNVALID_XML_CHAR, Common.YES);
					msg.setInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES, buf);
					return msg;
				}
				catch (Exception ee)
				{
					log.warn("fail to parse xml after removeUnvalidXmlChar!!!");
				}
			}
			msg = new Message(transaction);
			msg.setInLocal(ESBMsgLocalKey.LOCAL_UNVALID_XML, Common.YES);
		}
		// msg.init();
		msg.setInLocal(ESBMsgLocalKey.LOCAL_ORIGINAL_REQ_BYTES, buf);
		return msg;
	}

	public ICompositeNode deserialize2composite(byte[] buf, int offset, int len) throws Exception
	{
		return deserialize2composite(buf, offset, len, root());
	}

	public ICompositeNode deserialize2composite(byte[] buf, int offset, int len,
			ICompositeNode root) throws Exception
	{
		InputStream is = new ByteArrayInputStream(buf, offset, len);
		handler.setRoot(root);
		try
		{
			InputSource input = new InputSource(is);
			input.setEncoding(charset); // modified by chenjs 2010-11-20。
			// 提供一个字符集的参数，用于指定输入流的字符集，默认为utf-8
			getXMLReader(handler).parse(input);
		}
		catch (Exception e)
		{
			log.warn("Parse xml error: offset: " + offset + ", len: " + len + ", base64 buf: ["
					+ new String(StringX.encodeBase64(buf)) + "]\n\tstr:" + new String(buf));
			throw e;
		}
		return root;
	}

	public ICompositeNode deserialize2composite(byte[] buf, ICompositeNode root) throws Exception
	{
		return deserialize2composite(buf, 0, buf.length, root);
	}

	public ICompositeNode deserialize2composite(byte[] buf) throws Exception
	{
		return deserialize2composite(buf, 0, buf.length);
	}

	public ICompositeNode deserialize2composite(InputStream is) throws Exception
	{
		return deserialize2composite(is, root());
	}

	public ICompositeNode deserialize2composite(InputStream is, ICompositeNode root)
			throws Exception
	{
		handler.setRoot(root);
		InputSource input = new InputSource(is);
		input.setEncoding(charset); // modified by chenjs 2010-11-20。
		// 提供一个字符集的参数，用于指定输入流的字符集，默认为utf-8
		getXMLReader(handler).parse(input);
		return root;
	}

	public IMessage deserialize(InputStream is) throws Exception
	{
		IMessage msg = new Message(deserialize2composite(is));
		// msg.init();
		return msg;
	}

	public static SOAPConverter getInstance()
	{
		return XML_CONVERTER;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	public void setSoap(boolean soap)
	{
		this.soap = soap;
	}

	public boolean isEmptyTag()
	{
		return emptyTag;
	}

	public void setEmptyTag(boolean emptyTag)
	{
		this.emptyTag = emptyTag;
	}

	public boolean isCdata()
	{
		return cdata;
	}

	public void setCdata(boolean cdata)
	{
		this.cdata = cdata;
	}

	public String getCharset()
	{
		return charset;
	}

	public boolean isPretty()
	{
		return pretty;
	}

	public void setPretty(boolean pretty)
	{
		this.pretty = pretty;
	}

	public boolean isSerializeAll()
	{
		return serializeAll;
	}

	public void setSerializeAll(boolean serializeAll)
	{
		this.serializeAll = serializeAll;
	}

	public void setNs(String ns)
	{
		this.ns = ns;
	}

	public void setUsingNodeNS(boolean usingNodeNS)
	{
		this.usingNodeNS = usingNodeNS;
	}

	public boolean isSequence()
	{
		return sequence;
	}

	public void setSequence(boolean sequence)
	{
		this.sequence = sequence;
	}

	public boolean isFlat()
	{
		return flat;
	}

	public void setFlat(boolean flat)
	{
		this.flat = flat;
	}

	public void setNode2xml(INode2XML node2xml)
	{
		this.node2xml = node2xml;
	}

	public void setUnvalidXMLEx(boolean unvalidXMLEx)
	{
		this.unvalidXMLEx = unvalidXMLEx;
	}

	static SOAPConverter XML_CONVERTER = new SOAPConverter();
}
