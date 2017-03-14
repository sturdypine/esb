package spc.esb.data.validator;

import spc.esb.constant.ESBRetCode;
import spc.webos.util.RegExp;
import spc.webos.util.StringX;

/**
 * esb时间格式校验, hhmmss
 * 
 * @author spc
 * 
 */
public class TimeValidator extends RegExpValidator
{
	public TimeValidator()
	{
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
		msgFormat = StringX.utf82str("{0}({1}) \u4E0D\u662F\u5408\u6CD5\u65F6\u95F4");
		pattern = RegExp.TIME_2;
		name = "time";
	}
}
