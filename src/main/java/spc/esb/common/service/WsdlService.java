package spc.esb.common.service;

public interface WsdlService
{
	String sample(String msgCd, int type) throws Exception;

	String wsdl(String msgCd) throws Exception;

	String schema(String msgCd) throws Exception;

	String schema2vo(String msgCd, String pkg) throws Exception;

	void schema2java(String appCd, String category, String basePkg, String apiDir, String implDir)
			throws Exception;

}
