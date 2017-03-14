package spc.esb.server.netty;

import javax.annotation.Resource;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import spc.esb.constant.ESBMsgLocalKey;
import spc.esb.core.service.ESBService;
import spc.esb.data.IMessage;
import spc.esb.data.Message;
import spc.esb.data.converter.MessageConverter;
import spc.esb.data.converter.SOAPConverter;
import spc.webos.config.AppConfig;
import spc.webos.constant.Config;
import spc.webos.server.netty.AbstractNettyServer;
import spc.webos.util.StringX;

public class TcpNettyServer extends AbstractNettyServer
{
	public TcpNettyServer()
	{
	}

	public TcpNettyServer(int port)
	{
		this.port = port;
	}

	public void bootstrap() throws Exception
	{
		final TcpNettyServer server = this;
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>()
				{
					public void initChannel(SocketChannel ch) throws Exception
					{
						if (idleTimeout > 0)
							ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, idleTimeout));
						ch.pipeline().addLast("readtimeout", new ReadTimeoutHandler(readTimeout));
						ch.pipeline().addLast("writetimeout",
								new WriteTimeoutHandler(writeTimeout));
						ch.pipeline().addLast("decoder",
								new LengthFieldBasedFrameDecoder(maxContentLength, 0, 4, 0, 4));
						ch.pipeline().addLast("encoder", new LengthFieldPrepender(4));
						ch.pipeline().addLast(new TcpInboundHandler(server));
					}
				}).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	@Resource
	protected ESBService esbService;
	protected MessageConverter converter = new SOAPConverter();

	public void setEsbService(ESBService esbService)
	{
		this.esbService = esbService;
	}

	public void setConverter(MessageConverter converter)
	{
		this.converter = converter;
	}

	class TcpInboundHandler extends SimpleChannelInboundHandler<Object>
	{
		TcpNettyServer server;

		public TcpInboundHandler(TcpNettyServer server)
		{
			this.server = server;
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception
		{
			ByteBuf buf = (ByteBuf) obj;
			byte[] request = new byte[buf.capacity()];
			buf.readBytes(request);
			String remoteAddress = ctx.channel().remoteAddress().toString();
			server.log.info("request len:{}, remote:{}", request.length, remoteAddress);
			boolean trace = AppConfig.getInstance()
					.getProperty(Config.app_trace_tcp + server.getPort(), false);
			if (trace) server.log.info("request base64:{}", StringX.base64(request));
			else if (server.log.isDebugEnabled())
				server.log.debug("request base64:{}", StringX.base64(request));
			IMessage reqmsg = new Message();
			reqmsg.setInLocal(ESBMsgLocalKey.ACCEPTOR_LOCAL_PORT, server.port);
			reqmsg.setInLocal(ESBMsgLocalKey.ACCEPTOR_REMOTE_HOST,
					remoteAddress.substring(1, remoteAddress.indexOf(':')));
			IMessage msg = server.converter.deserialize(request, reqmsg);
			msg = server.esbService.sync(msg);
			// 优先使用前端适配器的输出内容
			byte[] response = msg.getOriginalBytes();
			if (response == null) response = server.converter.serialize(msg);
			server.log.info("response close:{}, len:{}", server.shortCnn, response.length);
			if (server.log.isDebugEnabled())
				server.log.debug("response base64:{}", StringX.base64(response));
			ByteBuf resBuf = ctx.alloc().buffer(response.length);
			resBuf.writeBytes(response);
			ctx.write(resBuf);
			ctx.flush();
			if (server.shortCnn) ctx.close();
			if (trace) server.log.info("response base64:{}", StringX.base64(response));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
		{
			server.log.warn("ex: " + server.port, cause);
			ctx.channel().close();
		}
	}
}
