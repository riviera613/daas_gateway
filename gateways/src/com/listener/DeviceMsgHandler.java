package com.listener;

import com.model.base.Device;
import com.utils.DeviceFactory;
import com.utils.DeviceMap;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty的服务器端
 * @author Riviera
 *
 */
public class DeviceMsgHandler extends SimpleChannelInboundHandler<String>{

	public final static int PORT = 8150;
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		System.out.println("Register from " + ctx.channel().hashCode());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		/*
		 * 可能收到两类报文：登入登出报文，或设备自定报文
		 * 
		 * 登入登出报文格式：{报文类型}#{设备类型}#{设备识别id}
		 * 
		 * 报文类型：login / logout
		 * 设备类型：usb
		 * 设备识别id：usb设备对应的就是32位的usb_id 
		 */
		
		//登入报文
		if(msg.indexOf("login") == 0)
		{
			String[] msgInfo = msg.split("#");
			String type = msgInfo[1];
			//usb设备接入
			if(type.equals("usb"))
			{
				String usbId = msgInfo[2];
				String[] info = (String[]) ((String) StartGatewaysListener.idProp.get(usbId)).split("#");
				if(info.length >= 2)
				{
					String id = info[0];
					String deviceName = info[1];
					Device device = DeviceFactory.getInstance(deviceName, id, ctx);
					DeviceMap.put(id, device);
					CtxDeviceMap.put(ctx, device);
					ctx.writeAndFlush("success");
				}
				else
					ctx.writeAndFlush("error");
			}
		}
		
		//登出报文
		else if(msg.indexOf("logout") == 0)
		{
			Device device = CtxDeviceMap.get(ctx);
			device.offline();
			device = null;
		}
		
		//设备自定报文
		else
		{
			Device device = CtxDeviceMap.get(ctx);
			if(device != null)
				device.handler(msg);
			else
				System.out.println("Error");
			device = null;
		}
			
	}
}
