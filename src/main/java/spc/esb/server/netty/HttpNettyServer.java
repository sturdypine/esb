package spc.esb.server.netty;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import spc.esb.common.service.WsdlService;
import spc.esb.constant.ESBRetCode;
import spc.esb.core.service.ESBService;
import spc.esb.data.IMessage;
import spc.esb.data.converter.MessageConverter;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.config.AppConfig;
import spc.webos.constant.Common;
import spc.webos.constant.Config;
import spc.webos.exception.AppException;
import spc.webos.exception.Status;
import spc.webos.server.netty.AbstractNettyServer;
import spc.webos.server.netty.FullHttpServerInboundHandler;
import spc.webos.util.JsonUtil;
import spc.webos.util.StringX;

public class HttpNettyServer extends AbstractNettyServer
{
	public HttpNettyServer()
	{
	}

	public HttpNettyServer(int port)
	{
		this.port = port;
	}

	public void bootstrap() throws Exception
	{
		final HttpNettyServer server = this;
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>()
				{
					public void initChannel(SocketChannel ch) throws Exception
					{
						ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, idleTimeout));
						ch.pipeline().addLast("readtimeout", new ReadTimeoutHandler(readTimeout));
						ch.pipeline().addLast("writetimeout",
								new WriteTimeoutHandler(writeTimeout));
						ch.pipeline().addLast("decoder", new HttpRequestDecoder()); // inbound:1
						ch.pipeline().addLast("encoder", new HttpResponseEncoder()); // outbound:2
						ch.pipeline().addLast("aggregator",
								new HttpObjectAggregator(maxContentLength)); // inbound:2
						ch.pipeline().addLast("deflater", new HttpContentCompressor()); // outbound:1
						ch.pipeline().addLast("handler", new FullHttpServerInboundHandler(server,
								Common.FILE_XML_CONTENTTYPE)
						{
							protected void doPost(ChannelHandlerContext ctx) throws Exception
							{ // uri:
								// /ws[xml,json,rest,custom]/CMIS.0000.01/sndAppCd/ESB/sndDt/20170208/sndTm/090909009/seqNb/001
								String uri = fullHttpRequest.uri();
								String[] uris = StringX.split(uri, "/");
								Map<String, String> uriParams = StringX.uri2params(uris, 0);
								String msgType = uris[1]; // 第一个必须是报文类型，通过报文类型找解析器，index=0是空字符串
								String signature = fullHttpRequest.headers().get("signature");
								if (StringX.nullity(signature))
									signature = uriParams.get("signature");
								String msgCd = fullHttpRequest.headers().get("msgCd");
								if (StringX.nullity(msgCd)) msgCd = uriParams.get(msgType); // 报文编号直接使用msgType
								String sndAppCd = fullHttpRequest.headers().get("sndAppCd");
								if (StringX.nullity(sndAppCd)) sndAppCd = uriParams.get("sndAppCd");
								String seqNb = fullHttpRequest.headers().get("seqNb");
								if (StringX.nullity(seqNb)) seqNb = uriParams.get("seqNb");
								String sndDt = fullHttpRequest.headers().get("sndDt");
								if (StringX.nullity(sndDt)) sndDt = uriParams.get("sndDt");
								String sndTm = fullHttpRequest.headers().get("sndTm");
								if (StringX.nullity(sndTm)) sndTm = uriParams.get("sndTm");

								byte[] request = new byte[fullHttpRequest.content().capacity()];
								fullHttpRequest.content().readBytes(request);
								log.info(
										"request len:{}, msgType:{}, sndAppCd:{}, msgCd:{}, seqNb:{}",
										request == null ? 0 : request.length, msgType, sndAppCd,
										msgCd, seqNb);
								if (AppConfig.getInstance().getProperty(
										Config.app_trace_tcp + server.getPort(), false))
									log.info("request base64:{}", StringX.base64(request));
								else if (log.isDebugEnabled())
									log.debug("request base64:{}", StringX.base64(request));
								MessageConverter converter = ((HttpNettyServer) server).converters
										.get(msgType);
								IMessage msg = converter.deserialize(request);
								// 如果报文中没有参数则直接使用http header or uri中的信息
								if (StringX.nullity(msg.getMsgCd())) msg.setMsgCd(msgCd);
								if (StringX.nullity(msg.getSndApp()) && !StringX.nullity(sndAppCd))
									msg.setSndAppCd(sndAppCd);
								if (StringX.nullity(msg.getSeqNb()) && !StringX.nullity(seqNb))
									msg.setSeqNb(seqNb);
								if (StringX.nullity(msg.getSndDt()) && !StringX.nullity(sndDt))
									msg.setSndDt(sndDt);
								if (StringX.nullity(msg.getSndTm()) && !StringX.nullity(sndTm))
									msg.setSndTm(sndTm);
								if (StringX.nullity(msg.getSignature())
										&& !StringX.nullity(signature))
									msg.setSignature(signature);
								msg.setOriginalBytes(request); // 设置原始报文内容
								if (!isAuth(msg.getSndApp())) // 检查端口是否支持当前发送方系统
									throw new AppException(ESBRetCode.MSG_SNDAPPCD,
											new String[] { msg.getSndApp() });

								msg = ((HttpNettyServer) server).esbService.sync(msg);

								// 优先使用前端适配器的输出内容
								byte[] buf = msg.getOriginalBytes();
								if (buf == null) buf = converter.serialize(msg);
								if (!StringX.nullity(converter.getContentType()))
									this.contentType = converter.getContentType();
								int status = 0;
								if (msgType.equalsIgnoreCase("api"))
								{ // 如果是rest api请求，状态为失败，则返回555
									Status s = msg.getStatus();
									if (s != null && !s.success()) status = 555;
								}
								writeResponse(ctx.channel(), buf, status);
							}

							protected boolean isAuth(String sndAppCd) throws Exception
							{
								String[] unvalidSndAppCd = ((HttpNettyServer) server).unvalidSndAppCd;
								String[] validSndAppCd = ((HttpNettyServer) server).validSndAppCd;
								if (unvalidSndAppCd != null && unvalidSndAppCd.length > 0
										&& StringX.contain(unvalidSndAppCd, sndAppCd, true))
									return false;
								if (validSndAppCd != null || unvalidSndAppCd.length == 0)
									return true;
								return StringX.contain(validSndAppCd, sndAppCd, true);
							}

							protected void doGet(ChannelHandlerContext ctx) throws Exception
							{ // get方式获取wsdl, schema, sample等信息
								String uri = fullHttpRequest.uri();
								int idx = uri.indexOf('?');
								if (idx > 0) uri = uri.substring(0, idx);
								idx = uri.lastIndexOf('/');
								String msgCd = uri.substring(idx + 1);
								QueryStringDecoder decoder = new QueryStringDecoder(
										fullHttpRequest.uri());
								Map<String, List<String>> parameters = decoder.parameters();
								if (parameters.containsKey("wsdl")) writeResponse(ctx.channel(),
										wsdlService.wsdl(msgCd).getBytes(), 0);
								else if (parameters.containsKey("schema"))
									writeResponse(ctx.channel(),
											wsdlService.schema(msgCd).getBytes(), 0);
								else if (parameters.containsKey("xml")) writeResponse(ctx.channel(),
										wsdlService.sample(msgCd, 0).getBytes(), 0);
								else if (parameters.containsKey("json"))
								{ // json sample
									contentType = Common.FILE_JSON_CONTENTTYPE;
									IMessage msg = SOAPConverter.getInstance()
											.deserialize(wsdlService.sample(msgCd, 0).getBytes());
									writeResponse(ctx.channel(),
											JsonUtil.obj2json(msg.getTransaction()).getBytes(), 0);
								}
								else if (parameters.containsKey("api"))
								{ // json rest sample
									contentType = Common.FILE_JSON_CONTENTTYPE;
									IMessage msg = SOAPConverter.getInstance()
											.deserialize(wsdlService.sample(msgCd, 0).getBytes());
									writeResponse(ctx.channel(),
											JsonUtil.obj2json(msg.getBody()).getBytes(), 0);
								}
								else writeResponse(ctx.channel(),
										"type not in (wsdl, schema, xml, json, api)".getBytes(),
										500);
							}
						}); // inbound:3
					}
				}).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	@Resource
	protected ESBService esbService;
	@Resource
	protected WsdlService wsdlService;
	protected Map<String, MessageConverter> converters;
	protected String[] validSndAppCd;
	protected String[] unvalidSndAppCd;

	public void setEsbService(ESBService esbService)
	{
		this.esbService = esbService;
	}

	public void setWsdlService(WsdlService wsdlService)
	{
		this.wsdlService = wsdlService;
	}

	public void setConverters(Map<String, MessageConverter> converters)
	{
		this.converters = converters;
	}

	public void setValidSndAppCd(String[] validSndAppCd)
	{
		this.validSndAppCd = validSndAppCd;
	}

	public void setUnvalidSndAppCd(String[] unvalidSndAppCd)
	{
		this.unvalidSndAppCd = unvalidSndAppCd;
	}
}
