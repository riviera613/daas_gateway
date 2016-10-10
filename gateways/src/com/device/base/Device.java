package com.device.base;

import io.netty.channel.ChannelHandlerContext;

import org.json.JSONObject;

public interface Device {
	
	public void setCtx(ChannelHandlerContext ctx);
	
	public String control(String type);

	public String control(String type, JSONObject params);
	
	public void handler(String msg);
}
