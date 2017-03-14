<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
<xs:element name="${msgCd}">
<xs:complexType><xs:sequence>
	<xs:element name="Header" minOccurs="1" maxOccurs="1">
		<xs:complexType>
			<xs:sequence>
				<xs:element name="sndAppCd" type="xs:string" minOccurs="1" maxOccurs="1" />
				<xs:element name="sndDt" type="xs:string" minOccurs="1" maxOccurs="1" />
				<xs:element name="sndTm" type="xs:string" minOccurs="1" maxOccurs="1" />
				<xs:element name="seqNb" type="xs:string" minOccurs="1" maxOccurs="1" />
				<xs:element name="msgCd" type="xs:string" minOccurs="1" maxOccurs="1" />
				<xs:element name="replyToQ" type="xs:string" minOccurs="0" maxOccurs="1" />
				<#if response>
				<xs:element name="rcvAppCd" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:element name="refSeqNb" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:element name="refMsgCd" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:element name="refSndAppCd" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:element name="refSndDt" type="xs:string" minOccurs="0" maxOccurs="1" />
				</#if> 
				<xs:element name="ext" type="xs:string" minOccurs="0" maxOccurs="1" />
				<xs:element name="signature" maxOccurs="1" minOccurs="0" nillable="true" type="xs:string" />
				<#if response>
				<xs:element name="status" maxOccurs="1" minOccurs="0" nillable="true">
					<xs:complexType>
						<xs:sequence>
							<xs:element name="retCd" type="xs:string" minOccurs="1" maxOccurs="1" />
							<xs:element name="location" type="xs:string" minOccurs="0" maxOccurs="1" />
							<xs:element name="mbrCd" type="xs:string" minOccurs="0" maxOccurs="1" />
							<xs:element name="appCd" type="xs:string" minOccurs="1" maxOccurs="1" />
							<xs:element name="desc" type="xs:string" minOccurs="0" maxOccurs="1" />
							<xs:element name="ip" type="xs:string" minOccurs="0" maxOccurs="1" />
						</xs:sequence>
					</xs:complexType>
				</xs:element>
				</#if> 
			</xs:sequence>
		</xs:complexType>
	</xs:element>
	${schema!('')}
</xs:sequence></xs:complexType>
</xs:element>
</xs:schema>
