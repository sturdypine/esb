package spc.esb.data.validator;

import java.math.BigDecimal;

import org.springframework.validation.Errors;

import spc.esb.constant.ESBRetCode;
import spc.esb.data.IMessage;
import spc.esb.model.MsgValidatorPO;
import spc.webos.util.StringX;

/**
 * 数字比较器
 * 
 * @author spc
 * 
 */
public class NumberCompareCValidator extends AbstractCompositeValidator
{
	public void validate(IMessage msg, MsgValidatorPO msgValidatorVO, Errors errors)
	{
		String val1 = StringX.null2emptystr(getNode(msg, msgValidatorVO.getPath1()));
		String val2 = StringX.null2emptystr(getNode(msg, msgValidatorVO.getPath2()));
		if (StringX.nullity(val1) || StringX.nullity(val2)) return; // 如果两个数字不存在则不比较
		BigDecimal v1 = new BigDecimal(val1);
		BigDecimal v2 = new BigDecimal(val2);
		// System.out.println(msgValidatorVO.getPath1()+"x "+val1+" - "+val2);
		if (compare(v1, v2)) reject(
				errors,
				msgValidatorVO.getPath1(),
				new Object[] {
						StringX.nullity(msgValidatorVO.getName1()) ? msgValidatorVO.getPath1()
								: msgValidatorVO.getName1(),
						val1,
						StringX.nullity(msgValidatorVO.getName2()) ? msgValidatorVO.getPath2()
								: msgValidatorVO.getName2(), val2 }, msgValidatorVO);
	}

	protected boolean compare(BigDecimal v1, BigDecimal v2)
	{
		if (target == 0) return v1.compareTo(v2) == 1;
		if (target == 1) return v1.compareTo(v2) != -1;
		if (target == 2) return v1.compareTo(v2) == -1;
		return v1.compareTo(v2) != 1;
	}

	public void init() throws Exception
	{
		if (StringX.nullity(name))
		{
			if (target == 0) name = "numgt";
			else if (target == 1) name = "numget";
			else if (target == 2) name = "numlt";
			else name = "numlet";
		}
		if (StringX.nullity(msgFormat))
		{
			String msg = "\u5C0F\u4E8E\u7B49\u4E8E"; // 小于等于
			if (target == 0) msg = "\u5927\u4E8E"; // 大于
			else if (target == 1) msg = "\u5927\u4E8E\u7B49\u4E8E"; // 大于等于
			else if (target == 2) msg = "\u5C0F\u4E8E"; // 小于
			msgFormat = StringX.utf82str("{0}({1}) \u5FC5\u987B" + msg + " {2}({3})");
		}
		super.init();
	}

	public NumberCompareCValidator()
	{
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
	}

	protected int target; // 比较目标数值, 0:>，1:>=，2:<，3:<=

	public int getTarget()
	{
		return target;
	}

	public void setTarget(int target)
	{
		this.target = target;
	}
}
