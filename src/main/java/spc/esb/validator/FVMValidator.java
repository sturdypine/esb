package spc.esb.validator;

import javax.annotation.Resource;

import org.springframework.validation.Errors;

import spc.esb.common.service.ESBInfoService;
import spc.esb.constant.ESBRetCode;
import spc.esb.data.IMessage;
import spc.esb.data.INode;
import spc.esb.data.validator.AbstractNodeValidator;
import spc.esb.model.MsgSchemaPO;
import spc.webos.util.StringX;
import spc.webos.util.tree.TreeNode;

/**
 * 
 * @author chenjs
 * 
 */
public class FVMValidator extends AbstractNodeValidator
{
	public FVMValidator()
	{
		errCd = ESBRetCode.MSG_FIELD_VALIDATOR;
		msgFormat = StringX.utf82str("{0}({1})invalid fvmapping({2})");
	}

	public void validate(IMessage msg, String field, INode node, TreeNode tnode, Errors errors)
	{
		MsgSchemaPO schema = (MsgSchemaPO) tnode.getTreeNodeValue();
		if (esbInfoService.getFVM(schema.getFvMapId(), msg.getSndNodeApp(),
				node.toString()) == null)
		{ // fvmapping±Ì√ª≈‰÷√
			String fieldName = StringX.nullity(schema.getFdesc()) ? field : schema.getFdesc();
			reject(msg, field, node, tnode, errors,
					new Object[] { fieldName, node.toString(), schema.getFvMapId() });
		}
	}

	@Resource
	protected ESBInfoService esbInfoService;

	public void setEsbInfoService(ESBInfoService esbInfoService)
	{
		this.esbInfoService = esbInfoService;
	}
}
