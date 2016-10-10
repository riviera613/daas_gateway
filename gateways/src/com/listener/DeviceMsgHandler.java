package com.listener;

import com.device.base.Device;
import com.device.base.DeviceFactory;
import com.device.base.DeviceMap;

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
		System.out.println("Recevive message " + msg + " from " + ctx.channel().hashCode());
		if(msg.indexOf("login") == 0)
		{
			String[] msgInfo = msg.split("#");
			String type = msgInfo[1];
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
		
		else if(msg.indexOf("logout") == 0)
		{
			Device device = CtxDeviceMap.get(ctx);
			DeviceMap.remove(device);
			CtxDeviceMap.remove(ctx);
			device = null;
		}
		
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
