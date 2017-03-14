package spc.esb.data.iso8583;

public interface IISO8583MessageConverter
{
	byte[] serialize(ISO8583Message msg) throws Exception;

	void deserialize(ISO8583Message msg, byte[] buf8583, int offset) throws Exception;
}
