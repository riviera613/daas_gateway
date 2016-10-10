package com.device.base;

import com.listener.CtxDeviceMap;

import io.netty.channel.ChannelHandlerContext;

/**
 * 设备的抽象类
 * @author Riviera
 *
 */
public abstract class AbstractDevice implements Device{
	
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";
	
	protected String id;
	
	protected ChannelHandlerContext ctx = null;
	
	public AbstractDevice(String id) {
		this(id, null);
	}
	
	public AbstractDevice(String id, ChannelHandlerContext ctx) {
		this.id = id;
		this.ctx = ctx;
	}
	
	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	public void offline() {
		DeviceMap.remove(id);
		if(ctx != null) {
			CtxDeviceMap.remove(ctx);
		}
	}
}
