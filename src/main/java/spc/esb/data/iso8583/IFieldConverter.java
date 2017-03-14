package spc.esb.data.iso8583;

public interface IFieldConverter
{
	byte[] vlen(Field f) throws Exception;
	
	void pack(Field f) throws Exception;

	int unpack(byte[] buf, int offset, Field f) throws Exception;
}
