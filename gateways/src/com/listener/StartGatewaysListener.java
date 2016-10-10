package com.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;

/**
 * 启动监听
 * @author Riviera
 *
 */
public class StartGatewaysListener extends ContextLoaderListener{

	Thread deviceMsgHandlerThread;
	
	public static Properties idProp = new Properties();
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		
		File file = new File(event.getServletContext().getRealPath(".") + "/WEB-INF/classes/com/device/deviceId.properties");
		try {
			idProp.load(new FileInputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deviceMsgHandlerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				//必须在子线程中启动netty服务端，直接在主线程启动会阻塞主线程
				EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		        EventLoopGroup workerGroup = new NioEventLoopGroup();
		        try {
		            ServerBootstrap b = new ServerBootstrap();
		            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		            .handler(new LoggingHandler(LogLevel.INFO))
		            .childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new StringDecoder());
							ch.pipeline().addLast(new StringEncoder());
							ch.pipeline().addLast(new DeviceMsgHandler());
						}   	 
		             });
		            
		            //开放端口供客户端访问。
		            //必须关闭Future，否则程序会自动结束
		            b.bind(DeviceMsgHandler.PORT).sync().channel().closeFuture().sync();
		        } catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
		            bossGroup.shutdownGracefully();
		            workerGroup.shutdownGracefully();
		        }
			}
		});
		
		deviceMsgHandlerThread.start();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		super.contextDestroyed(event);
		if(deviceMsgHandlerThread != null)
			deviceMsgHandlerThread.interrupt();
	}
	
/*	public static void main(String[] argv)
	{
		File file = new File("./src/com/device/deviceId.properties");
		try {
			idProp = new Properties();
			idProp.load(new FileInputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(idProp.get("067b2303"));
	}*/
}
