package com.device.base;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractDevice implements Device{
	
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	
	protected String id;
	
	protected ChannelHandlerContext ctx;
	
	public AbstractDevice(String id) {
		this.id = id;
	}
	
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
}
