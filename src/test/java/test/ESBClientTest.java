package test;

import java.util.HashMap;

import org.junit.Test;

import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.webos.endpoint.Executable;
import spc.webos.endpoint.HttpEndpoint;

public class ESBClientTest {
	@Test
	public void http_soap() throws Exception {
		IMessage msg = msg();
		try (HttpEndpoint http = new HttpEndpoint("http://127.0.0.1:9111/ws/" + msg.getMsgCd())) {
			msg.setHeader(null);
			Executable exe = new Executable();
			exe.reqHttpHeaders = new HashMap<>();
			exe.reqHttpHeaders.put("sndAppCd", "ESB");
			// exe.correlationID = msg.getMsgSn().getBytes();
			exe.request = msg.toByteArray(false);
			System.out.println("request:" + new String(exe.request));
			for (int i = 0; i < 1; i++) {
				http.execute(exe);
				System.out.println(new String(exe.response));
			}
		}
	}

	public static IMessage msg() throws Exception {
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

//		msg.setRequest((ICompositeNode) NodeConverterFactory.getInstance().unpack(ssomsg(), null));
		return msg;
	}
}
