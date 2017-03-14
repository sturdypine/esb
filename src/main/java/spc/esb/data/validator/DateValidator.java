package spc.esb.data.validator;

import spc.esb.constant.ESBRetCode;
import spc.webos.util.RegExp;
import spc.webos.util.StringX;

/**
 * 验证一个报文中出现的数据是否是日期格式, 日期格式为yyyyHHdd
 * 
 * @author spc
 * 
 */
public class DateValidator extends RegExpValidator
{
	public DateValidator()
	{
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
		msgFormat = StringX.utf82str("{0}({1}) \u4E0D\u662F\u5408\u6CD5\u65E5\u671F");
		pattern = RegExp.DATE_2;
		name = "date";
	}
}
