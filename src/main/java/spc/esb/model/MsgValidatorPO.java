package spc.esb.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import spc.webos.util.JsonUtil;

/**
 * genarated by sturdypine.chen Email: sturdypine@gmail.com description:
 */
public class MsgValidatorPO implements Serializable
{
	public static final long serialVersionUID = 20091211L;
	// 和物理表对应字段的属性
	Integer seq; // 主键
	String msgCd; // 
	String validator; // 
	String path1; // 
	String name1; // 
	String path2; // 
	String name2; // 
	String path3; // 
	String name3; // 
	String path4; // 
	String name4; // 
	String remark; // 
	String errCd; // 
	String msgFormat; // 
	String disable; //

	// 和此VO相关联的其他VO属性

	// 和此VO相关联的其他简单Sql属性
	// Note: 如果关联的Sql对象为String, Inegter...等Java final class时， 只能使用Object对象，
	// 访问时候只能通过
	// Object的toString()方法来使用。
	public static final String TABLE = "esb_msgvalidator";
	public static final String[] BLOB_FIELD = null;
	public static final String SEQ_NAME = "seq";

	public MsgValidatorPO()
	{
	}

	public void setPrimary(Integer seq)
	{
		this.seq = seq;
	}

	public String primary(String delim)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(this.seq);
		return buf.toString();
	}

	public Map primary()
	{
		Map m = new HashMap();
		m.put("seq", seq);
		return m;
	}

	public String table()
	{
		return TABLE;
	}

	public String[] blobFields()
	{
		return BLOB_FIELD;
	}

//	public String getKeyName()
//	{
//		return SEQ_NAME;
//	}
//
//	public Serializable getKey()
//	{
//		return seq;
//	}

	// set all properties to NULL
	public void setNULL()
	{
		this.seq = null;
		this.msgCd = null;
		this.validator = null;
		this.path1 = null;
		this.name1 = null;
		this.path2 = null;
		this.name2 = null;
		this.path3 = null;
		this.name3 = null;
		this.path4 = null;
		this.name4 = null;
		this.remark = null;
		this.errCd = null;
		this.msgFormat = null;
		this.disable = null;
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof MsgValidatorPO)) return false;
		MsgValidatorPO obj = (MsgValidatorPO) o;
		if (!seq.equals(obj.seq)) return false;
		if (!msgCd.equals(obj.msgCd)) return false;
		if (!validator.equals(obj.validator)) return false;
		if (!path1.equals(obj.path1)) return false;
		if (!name1.equals(obj.name1)) return false;
		if (!path2.equals(obj.path2)) return false;
		if (!name2.equals(obj.name2)) return false;
		if (!path3.equals(obj.path3)) return false;
		if (!name3.equals(obj.name3)) return false;
		if (!path4.equals(obj.path4)) return false;
		if (!name4.equals(obj.name4)) return false;
		if (!remark.equals(obj.remark)) return false;
		if (!errCd.equals(obj.errCd)) return false;
		if (!msgFormat.equals(obj.msgFormat)) return false;
		if (!disable.equals(obj.disable)) return false;
		return true;
	}

	// 只对主键进行散列
	public int hashCode()
	{
		long hashCode = getClass().hashCode();
		if (seq != null) hashCode += seq.hashCode();
		return (int) hashCode;
	}

	public int compareTo(Object o)
	{
		return -1;
	}

	// set all properties to default value...
	public void init()
	{
	}

	public Integer getSeq()
	{
		return seq;
	}

	public void setSeq(Integer seq)
	{
		this.seq = seq;
	}

	public String getMsgCd()
	{
		return msgCd;
	}

	public void setMsgCd(String msgCd)
	{
		this.msgCd = msgCd;
	}

	public String getValidator()
	{
		return validator;
	}

	public void setValidator(String validator)
	{
		this.validator = validator;
	}

	public String getPath1()
	{
		return path1;
	}

	public void setPath1(String path1)
	{
		this.path1 = path1;
	}

	public String getName1()
	{
		return name1;
	}

	public void setName1(String name1)
	{
		this.name1 = name1;
	}

	public String getPath2()
	{
		return path2;
	}

	public void setPath2(String path2)
	{
		this.path2 = path2;
	}

	public String getName2()
	{
		return name2;
	}

	public void setName2(String name2)
	{
		this.name2 = name2;
	}

	public String getPath3()
	{
		return path3;
	}

	public void setPath3(String path3)
	{
		this.path3 = path3;
	}

	public String getName3()
	{
		return name3;
	}

	public void setName3(String name3)
	{
		this.name3 = name3;
	}

	public String getPath4()
	{
		return path4;
	}

	public void setPath4(String path4)
	{
		this.path4 = path4;
	}

	public String getName4()
	{
		return name4;
	}

	public void setName4(String name4)
	{
		this.name4 = name4;
	}

	public String getRemark()
	{
		return remark;
	}

	public void setRemark(String remark)
	{
		this.remark = remark;
	}

	public String getErrCd()
	{
		return errCd;
	}

	public void setErrCd(String errCd)
	{
		this.errCd = errCd;
	}

	public String getMsgFormat()
	{
		return msgFormat;
	}

	public void setMsgFormat(String msgFormat)
	{
		this.msgFormat = msgFormat;
	}

	public String getDisable()
	{
		return disable;
	}

	public void setDisable(String disable)
	{
		this.disable = disable;
	}

	public void set(MsgValidatorPO vo)
	{
		this.seq = vo.seq;
		this.msgCd = vo.msgCd;
		this.validator = vo.validator;
		this.path1 = vo.path1;
		this.name1 = vo.name1;
		this.path2 = vo.path2;
		this.name2 = vo.name2;
		this.path3 = vo.path3;
		this.name3 = vo.name3;
		this.path4 = vo.path4;
		this.name4 = vo.name4;
		this.remark = vo.remark;
		this.errCd = vo.errCd;
		this.msgFormat = vo.msgFormat;
	}

	public static long getSerialVersionUID()
	{
		return serialVersionUID;
	}

	public StringBuffer toJson()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(JsonUtil.obj2json(this));
		return buf;
//		
//		StringBuffer buf = new StringBuffer();
//		buf.append('{');
//		if (seq != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("seq:'");
//			buf.append(seq);
//			buf.append('\'');
//		}
//		if (msgCd != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("msgCd:'");
//			buf.append(msgCd);
//			buf.append('\'');
//		}
//		if (validator != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("validator:'");
//			buf.append(validator);
//			buf.append('\'');
//		}
//		if (path1 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("path1:'");
//			buf.append(path1);
//			buf.append('\'');
//		}
//		if (name1 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("name1:'");
//			buf.append(name1);
//			buf.append('\'');
//		}
//		if (path2 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("path2:'");
//			buf.append(path2);
//			buf.append('\'');
//		}
//		if (name2 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("name2:'");
//			buf.append(name2);
//			buf.append('\'');
//		}
//		if (path3 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("path3:'");
//			buf.append(path3);
//			buf.append('\'');
//		}
//		if (name3 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("name3:'");
//			buf.append(name3);
//			buf.append('\'');
//		}
//		if (path4 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("path4:'");
//			buf.append(path4);
//			buf.append('\'');
//		}
//		if (name4 != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("name4:'");
//			buf.append(name4);
//			buf.append('\'');
//		}
//		if (remark != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("remark:'");
//			buf.append(remark);
//			buf.append('\'');
//		}
//		if (errCd != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("errCd:'");
//			buf.append(errCd);
//			buf.append('\'');
//		}
//		if (msgFormat != null)
//		{
//			if (buf.length() > 2) buf.append(',');
//			buf.append("msgFormat:'");
//			buf.append(msgFormat);
//			buf.append('\'');
//		}
//		buf.append('}');
//		return buf;
	}

	public void afterLoad()
	{
		// TODO Auto-generated method stub

	}

	public void beforeLoad()
	{
		// TODO Auto-generated method stub

	}

	public void setManualSeq(Long seq)
	{

	}

	public void destory()
	{

	}

	public String toString()
	{
		StringBuffer buf = new StringBuffer(128);
		buf.append(getClass().getName() + "(serialVersionUID=" + serialVersionUID + "):");
		buf.append(toJson());
		return buf.toString();
	}
}
