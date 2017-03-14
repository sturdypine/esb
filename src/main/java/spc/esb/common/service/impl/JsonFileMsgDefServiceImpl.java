package spc.esb.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import spc.esb.model.MsgSchemaPO;

/**
 * 容许从文件系统中加载esb报文配置信息。启动无需数据库支持
 * 
 * @author chenjs
 *
 */
public class JsonFileMsgDefServiceImpl extends MsgDefServiceImpl
{
	protected List loadBAMsgCd()
	{
		String json = "['ESB.0000.01','ESB.00000.02']";
		// String json = new
		// String(FileUtil.is2bytes(baMsgCdRes.getInputStream()),
		// Common.CHARSET_UTF8);
		return new Gson().fromJson(json, List.class);
	}

	protected List loadBAMsgSchema()
	{
		String json = "[{msgCd:'xxx'},{msgCd:'yy'}]";
		// String json = new
		// String(FileUtil.is2bytes(baMsgSchemaRes.getInputStream()),
		// Common.CHARSET_UTF8);
		return new Gson().fromJson(json, new TypeToken<List<MsgSchemaPO>>()
		{
		}.getType());
	}

	protected List loadFAMsgCd()
	{
		return new ArrayList();
	}

	protected List loadFAMsgSchema()
	{
		return new ArrayList();
	}

	protected List loadMDMsgCd()
	{
		return new ArrayList();
	}

	protected List loadMDMsgSchema()
	{
		return new ArrayList();
	}

	protected Resource baMsgCdRes;
	protected Resource baMsgSchemaRes;

	public void setBaMsgCdRes(Resource baMsgCdRes)
	{
		this.baMsgCdRes = baMsgCdRes;
	}

	public void setBaMsgSchemaRes(Resource baMsgSchemaRes)
	{
		this.baMsgSchemaRes = baMsgSchemaRes;
	}
}
