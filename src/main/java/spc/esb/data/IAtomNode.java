package spc.esb.data;

public interface IAtomNode extends INode
{
	Object getValue();

	int intValue();

	void set(int v);

	void set(short v);

	long longValue();

	void set(long v);

	byte[] byteValue();

	void set(byte[] v);

	double doubleValue();

	void set(double v);

	void set(float v);

	String stringValue();

	void set(String v);

	boolean isBoolean();

	boolean isNumber();

	boolean isInteger();

	boolean isLong();

	boolean isDouble();

	boolean isBytes();

	boolean isString();

	boolean booleanValue();

	void set(boolean v);
}
