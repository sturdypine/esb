<#assign service=_stringx.replaceAll(reqMsgCd,".", "")>
<#assign company="www.w3.org">
<wsdl:definitions targetNamespace="http://${company}/esb/"
	xmlns:esb="http://${company}/esb/" xmlns:soap11="http://schemas.xmlsoap.org/soap/envelope/"
	xmlns:soap12="http://www.w3.org/2003/05/soap-envelope" xmlns:soapenc11="http://schemas.xmlsoap.org/soap/encoding/"
	xmlns:soapenc12="http://www.w3.org/2003/05/soap-encoding" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
	xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:xs="http://www.w3.org/2001/XMLSchema">
${"<!--"}
date:${.now?string("yyyy-MM-dd")}
<#if serviceVO?exists>
serviceId:${serviceVO.serviceId?default('')}
ver:${serviceVO.ver?default('')}
name:${serviceVO.name?default('')}
publishDt:${serviceVO.publishDt?default('')}
effectDt:${serviceVO.effectDt?default('')}
</#if>
${"-->"}
	<wsdl:types>
		<xs:schema attributeFormDefault="qualified" elementFormDefault="qualified" targetNamespace="http://${company}/esb/">
			<xs:complexType name="ESB_STATUS">
				<xs:all>
					<xs:element name="retCd" type="xs:string" minOccurs="1"
						maxOccurs="1" />
					<xs:element name="location" type="xs:string"
						minOccurs="0" maxOccurs="1" />
					<xs:element name="appCd" type="xs:string" minOccurs="1"
						maxOccurs="1" />
					<xs:element name="desc" type="xs:string" minOccurs="0"
						maxOccurs="1" />
					<xs:element name="ip" type="xs:string" minOccurs="0"
						maxOccurs="1" />
					<xs:element name="traceNo" type="xs:string" minOccurs="0"
						maxOccurs="1" />
				</xs:all>
			</xs:complexType>
			${request?default('')}
			${response?default('')}
<#if !flat>
			<xs:element name="${reqCd}" minOccurs="1" maxOccurs="1">
			<xs:complexType>
				<xs:all>${reqMethod?default('')}
				</xs:all>
			</xs:complexType>
			</xs:element>
			<xs:element name="${resCd}" minOccurs="0" maxOccurs="1">
			<xs:complexType>
				<xs:all>${resMethod?default('')}
				</xs:all>
			</xs:complexType>
			</xs:element>
</#if>
		</xs:schema>
	</wsdl:types>
	
	<wsdl:message name="HEADERS">
		<wsdl:part name="sndAppCd" type="xs:string" />
		<wsdl:part name="sndDt" type="xs:string" />
		<wsdl:part name="sndTm" type="xs:string" />
		<wsdl:part name="seqNb" type="xs:string" />
		<wsdl:part name="msgCd" type="xs:string" />
		<wsdl:part name="callTyp" type="xs:string" />
		<wsdl:part name="replyToQ" type="xs:string" />
		<wsdl:part name="rcvAppCd" type="xs:string" />
		<wsdl:part name="refSeqNb" type="xs:string" />
		<wsdl:part name="refMsgCd" type="xs:string" />
		<wsdl:part name="refSndAppCd" type="xs:string" />
		<wsdl:part name="refSndDt" type="xs:string" />
		<wsdl:part name="signature" type="xs:string" />
		<wsdl:part name="ext" type="xs:string" />
		<wsdl:part name="status" type="esb:ESB_STATUS" />
	</wsdl:message>
<#if flat>
	<wsdl:message name="${reqMsgCd}">${reqMessage}
	</wsdl:message>
	<wsdl:message name="${resMsgCd}">${resMessage}
	</wsdl:message>
<#else>
	<wsdl:message name="${reqMsgCd}">
		<wsdl:part name="${reqCd}" element="esb:${reqCd}"/>
	</wsdl:message>
	<wsdl:message name="${resMsgCd}">
		<wsdl:part name="${resCd}" element="esb:${resCd}"/>
	</wsdl:message>
</#if>
	<wsdl:portType name="${reqMsgCd}">
		<wsdl:operation name="${reqMsgCd}">
			<wsdl:input message="esb:${reqMsgCd}" />
			<wsdl:output message="esb:${resMsgCd}" />
		</wsdl:operation>
	</wsdl:portType>
  	
	<wsdl:binding name="${reqMsgCd}" type="esb:${reqMsgCd}">
		<wsdlsoap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
		<wsdl:operation name="${reqMsgCd}">
			<wsdlsoap:operation soapAction="${reqMsgCd}"/>
			<wsdl:input>
				<wsdlsoap:header message="esb:HEADERS" part="sndAppCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="sndDt" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="sndTm" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="seqNb" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="msgCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="callTyp" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="replyToQ" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="ext" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="signature" use="literal" />
				<wsdlsoap:body use="literal" />
			</wsdl:input>
			<wsdl:output>
				<wsdlsoap:header message="esb:HEADERS" part="sndAppCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="sndDt" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="sndTm" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="seqNb" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="msgCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="callTyp" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="replyToQ" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="rcvAppCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="refSeqNb" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="refMsgCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="refSndAppCd" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="refSndDt" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="ext" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="signature" use="literal" />
				<wsdlsoap:header message="esb:HEADERS" part="status" use="literal" />
				<wsdlsoap:body use="literal" />
			</wsdl:output>
		</wsdl:operation>
	</wsdl:binding>
  
	<wsdl:service name="${reqMsgCd}">
		<wsdl:port binding="esb:${reqMsgCd}" name="${reqMsgCd}">
			<wsdlsoap:address location="${_conf['esb.ws.location']!'http://ESB_WS:8888/'}ws/${reqMsgCd}"/>
		</wsdl:port>
	</wsdl:service>
</wsdl:definitions>
