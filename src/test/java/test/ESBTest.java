package test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;

import spc.esb.common.service.ESBInfoService;
import spc.esb.common.service.MsgDefService;
import spc.esb.common.service.SignatureService;
import spc.esb.common.service.WsdlService;
import spc.esb.converter.JsonCallMsgConverter;
import spc.esb.core.service.ESBService;
import spc.esb.data.AtomNode;
import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.endpoint.Endpoint;
import spc.webos.endpoint.EndpointFactory;
import spc.webos.endpoint.Executable;
import spc.webos.endpoint.HttpEndpoint;
import spc.webos.mq.MQ;
import spc.webos.mq.MQ.Future;
import spc.webos.service.seq.UUID;
import spc.webos.util.JsonUtil;
import spc.webos.util.LogUtil;
import spc.webos.util.SpringUtil;
import spc.webos.util.StringX;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:META-INF/spring/*.xml")
public class ESBTest
{
	JsonCallMsgConverter converter;
	@Resource
	ESBService esbService;
	@Resource
	MsgDefService msgDefService;
	@Resource
	WsdlService wsdlService;
	@Resource
	ESBInfoService esbInfoService;
	@Resource
	UUID uuid;
	// @Resource
	MQ mq;
	@Resource
	SignatureService signatureService;

	@Test
	public void sig() throws Exception
	{
		Message msg = new Message();
		msg.setMsgCd("CMIS.BRH10.01");
		msg.setInRequest("applicant/cardNum", "3242342342");
		msg.setInRequest("applicant/mobilePhone", "18600990099");
		msg.setInRequest("plyInfo/plyNO", "12222232");
		msg.setInRequest("plyInfo/loanAmt", "1000.22");
		msg.setInRequest("plyInfo/insureAmt", "2000");
		System.out.println(msg);
		String sig = signatureService.sig(msg, "CMIS", "12".getBytes());
		System.out.println("sig:" + sig);
	}

	@Test
	public void wsdl() throws Exception
	{
		String reqMsgCd = "CMIS.BRH10.01";
		System.out.println(wsdlService.wsdl(reqMsgCd));
		Socket s = null;
		// s.shutdownInput();
		//
		// System.out.println(wsdlService.schema(reqMsgCd));
		//
		// System.out.println(wsdlService.schema("SSO.000000011.01"));
		// System.out.println(wsdlService.sample(reqMsgCd, 1) + "\n\n\n");
		// System.out.println(wsdlService.sample(reqMsgCd, 2) + "\n\n\n");
		// System.out.println(wsdlService.sample(reqMsgCd, 1));
	}

	@Test
	public void esb() throws Exception
	{
		IMessage msg = msg();
		IMessage response = esbService.sync(msg);
		System.out.println(response);
	}

	@Test
	public void startup() throws Exception
	{
		System.out.println("Start....");
		Thread.sleep(100000000000l);
	}

	@Test
	public void json() throws Exception
	{
		IMessage msg = msg();
		System.out.println(msg.toString());
		// System.out.println(JsonUtil.obj2json(msg.getTransaction(),
		// AtomNode.class));
		// System.out.println("JSC:" + new String(converter.serialize(msg)));
		// msg.setInRequest("map", null);

		converter.serialize(msg);
		Executable exe = new Executable();
		exe.reqmsg = msg;
		Endpoint endpoint = EndpointFactory.getInstance()
				.getEndpoint("jscall:demo.DemoService.save");
		endpoint.execute(exe);
		System.out.println("body:" + msg.getInLocal(JsonCallMsgConverter.RESPONSE_BODY_KEY));
		msg = converter.deserialize(null, msg);
		System.out.println(new String(SOAPConverter.getInstance().serialize(msg)));

		// Thread.sleep(100000l);
	}

	public static class User
	{
		String name;
		int age;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public int getAge()
		{
			return age;
		}

		public void setAge(int age)
		{
			this.age = age;
		}
	}

	@Test
	public void mq() throws Exception
	{
		LogUtil.setTraceNo("xxxx", "mq:esb", true);
		System.out.println("appCode:" + SpringUtil.APPCODE);
		Map request = new HashMap();
		request.put("name", "brh");
		Map map = new HashMap();
		map.put("age", 19);
		map.put("name", "name");
		request.put("map", map);

		for (int i = 0; i < 1; i++)
		{
			// try {
			// User user = mq.sync("ESB_JSON", "SSO.000000010.01", request, new
			// Future<User>(60000) {
			// });
			// System.out.println(user.getName() + " ::: " + user.getAge());
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			try
			{
				User user = mq.sync("ESB_JSON", "SSO.000000010.01",
						"{name:'brh',map:{age:16,name:'esb'}}", new Future<User>(60000)
						{
						});
				System.out.println(user.getName() + " ::: " + user.getAge());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		Thread.sleep(10 * 1000l);
	}

	@Test
	public void http_soap() throws Exception
	{
		IMessage msg = msg();
		HttpEndpoint http = new HttpEndpoint("http://localhost:9111/ws/" + msg.getMsgCd());
		msg.setHeader(null);
		Executable exe = new Executable();
		exe.reqHttpHeaders = new HashMap<>();
		exe.reqHttpHeaders.put("sndAppCd", "ESB");
		// exe.correlationID = msg.getMsgSn().getBytes();
		exe.request = msg.toByteArray(false);
		System.out.println("request:" + new String(exe.request));
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println(new String(exe.response));
		}
		Thread.sleep(5000);
	}

	@Test
	public void tcp_soap() throws Exception
	{
		Endpoint endpoint = EndpointFactory.getInstance()
				.getEndpoint("tcp://localhost:9999?hdrLen=4&hdrLenBinary=true");
		IMessage msg = msg();
		Executable exe = new Executable();
		exe.correlationID = msg.getMsgSn().getBytes();
		exe.request = msg.toByteArray(false);
		for (int i = 0; i < 1; i++)
		{
			endpoint.execute(exe);
			System.out.println(new String(exe.response));
		}
	}

	@Test
	public void http_json() throws Exception
	{
		IMessage msg = msg();
		try (HttpEndpoint http = new HttpEndpoint("http://localhost:9111/json/" + msg.getMsgCd()))
		{
			msg.setHeader(null);
			Executable exe = new Executable();
			exe.reqHttpHeaders = new HashMap<>();
			exe.reqHttpHeaders.put("sndAppCd", "ESB");
			// exe.correlationID = msg.getMsgSn().getBytes();
			exe.request = JsonUtil.obj2json(msg.getTransaction(), AtomNode.class).getBytes();
			System.out.println("request:" + new String(exe.request));

			for (int i = 0; i < 1; i++)
			{
				http.execute(exe);
				System.out.println("-------------------------------------------------------");
				System.out.println(new String(exe.response));
			}
		}
	}

	@Test
	public void post_rest() throws Exception
	{
		IMessage msg = msg();
		try (HttpEndpoint http = new HttpEndpoint("http://localhost:9111/api/" + msg.getMsgCd()))
		{
			msg.setHeader(null);
			Executable exe = new Executable();
			exe.reqHttpHeaders = new HashMap<>();
			exe.reqHttpHeaders.put("sndAppCd", "ESB");
			// exe.correlationID = msg.getMsgSn().getBytes();
			exe.request = JsonUtil.obj2json(msg.getBody(), AtomNode.class).getBytes();
			System.out.println("request:" + new String(exe.request));

			for (int i = 0; i < 1; i++)
			{
				http.execute(exe);
				System.out.println("-------------------------------------------------------");
				System.out.println(new String(exe.response));
			}
		}
		Thread.sleep(100000000l);
	}

	@Test
	public void jscall() throws Exception
	{
		HttpEndpoint http = new HttpEndpoint("http://localhost:6210/api/demo/sayHello");
		Executable exe = new Executable();
		exe.request = new Gson().toJson(Arrays.asList("cjs")).getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println("------------------------JSCall response--------------------------");
			System.out.println(new String(exe.response));
		}
	}

	@Test
	public void restful() throws Exception
	{
		HttpEndpoint http = new HttpEndpoint("http://localhost:6210/api/demo/sayHello$2");
		Executable exe = new Executable();
		exe.request = "{name:'chenjs',age:18}".getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println(
					"------------------------JSCall $2 map response--------------------------");
			System.out.println(new String(exe.response));
		}

		http = new HttpEndpoint("http://localhost:6210/api/demo/sayHello$1");
		exe = new Executable();
		exe.request = "{name:'chenjs'}".getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println(
					"------------------------JSCall $1 map response--------------------------");
			System.out.println(new String(exe.response));
		}

		http = new HttpEndpoint("http://localhost:6210/xml/demo/sayHello$2");
		exe = new Executable();
		exe.request = "<root><name>chenjs</name><age>18</age></root>".getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out
					.println("------------------------XML Call response--------------------------");
			System.out.println(new String(exe.response));
		}

		http = new HttpEndpoint("http://localhost:6210/ws/demo/sayHello$2");
		exe = new Executable();
		exe.request = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.8f8.com/esb/\" xmlns:esb=\"http://www.8f8.com/esb/\">\n<soap:Body><root><name>chenjs</name><age>18</age></root></soap:Body>\n</soap:Envelope>"
				.getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out
					.println("------------------------WS Call response--------------------------");
			System.out.println(new String(exe.response));
		}

		http = new HttpEndpoint("http://localhost:6210/api/demo/save$1");
		exe = new Executable();
		exe.request = "{p:{name:'chenjs',age:20}}".getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println(
					"------------------------Json 1 parameter Call response--------------------------");
			System.out.println(new String(exe.response));
		}

		http = new HttpEndpoint("http://localhost:6210/api/demo/save$$1");
		exe = new Executable();
		exe.request = "{name:'chenjs',age:20}".getBytes();
		for (int i = 0; i < 1; i++)
		{
			http.execute(exe);
			System.out.println(
					"------------------------Json 1 parameter Flat Call response--------------------------");
			System.out.println(new String(exe.response));
		}
	}

	public static IMessage msg() throws Exception
	{
		IMessage msg = new Message();
		msg.setMsgCd("SSO.000000010.01");
		msg.setSn("0000000198");
		msg.setSndAppCd("ESB");
		msg.setSndDt("20160922");
		msg.setSndTm("090909009");

		// msg.setInRequest("name", "xxPO");
		// msg.setInRequest("map/name", "chenjs");
		// msg.setInRequest("map/pwd", "pwd");
		// msg.setInRequest("map/age", "22");

		return msg;
	}

	@Test
	public void vo() throws Exception
	{
		String pkg = "com.brh";
		String apiDir = "/Users/chenjs/Downloads/";
		String implDir = "/Users/chenjs/Downloads/";
		wsdlService.schema2java("PTP", null, pkg, apiDir, implDir);
	}

	public static void main(String[] args) throws Exception
	{
		List<Object> list = new ArrayList<>();
		list.add(null);
		System.out.println(list.size());

		System.out.println(StringX.str2xml(
				"<?xml version=\"1.0\" encoding=\"utf-8\"?><PACKET><Head><SystemCode>BRH</SystemCode><FuncCode>BRH30</FuncCode><FuncName>鍊熸椤圭洰淇℃伅鎺ㄩ�佽姹�</FuncName><Operator>AB000001</Operator><TransDate>2017-01-11 11:17:53</TransDate></Head><Body><PlyNo>123</PlyNo><CavNo>456</CavNo><CheckResult>00</CheckResult></Body></PACKET>",
				false));
	}
}
