package spc.esb.data.validator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spc.webos.util.FileUtil;
import spc.webos.util.SpringUtil;

/**
 * 根据人行下发的schema用于校验发往人行报文，提前进行检查
 * 
 * @author spc
 * 
 */
public class SchemaValidator
{
	protected String schemaDir; // schema 存放的目录
	protected Map schemas; // 缓存磁盘中schema文件， 用人行交易码作为key
	protected Logger log = LoggerFactory.getLogger(getClass());

	public void init() throws Exception
	{
		// schema文件的是报文编号.xsd格式。.xsd之前为报文编号
		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Map schemas = new HashMap();
		File dir = SpringUtil.getInstance().getResourceLoader().getResource(schemaDir).getFile();
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			String name = file.getName();
			// 利用schema工厂，接收验证文档文件对象生成Schema对象
			schemas.put(name.substring(0, name.lastIndexOf('.')), schemaFactory
					.newSchema(new StreamSource(new ByteArrayInputStream(FileUtil
							.is2bytes(new FileInputStream(file))))));
		}
		this.schemas = schemas;
		log.info("load schemaDir: " + schemaDir + ", size: " + schemas.size());
		if (log.isDebugEnabled()) log.debug("schema is: " + schemas.keySet());
	}

	public boolean validate(String msgCd, byte[] xml) throws Exception
	{
		return validate(msgCd, xml, 0, xml.length);
	}

	public boolean validate(String msgCd, byte[] xml, int offset, int length) throws Exception
	{
		// 建立验证文档文件对象，利用此文件对象所封装的文件进行schema验证
		Schema schema = (Schema) schemas.get(msgCd);
		if (schema == null)
		{
			log.warn("schema is null by " + msgCd + " !!!");
			return false;
		}

		// 通过Schema产生针对于此Schema的验证器，利用GBAInitSchema.xsd进行验证
		Validator validator = schema.newValidator();

		// 得到验证的数据源，就是GBAInit.xml
		Source source = new StreamSource(new ByteArrayInputStream(xml, offset, length));

		// 开始验证，成功输出success!!!，失败输出fail
		validator.validate(source);
		return true;
	}

	public void setSchemaDir(String schemaDir)
	{
		this.schemaDir = schemaDir;
	}
}
