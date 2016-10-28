package com.listener;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

import com.model.base.Device;

public class CtxDeviceMap {

	private static Map<ChannelHandlerContext, Device> map = new HashMap<>();
	
	private CtxDeviceMap() {
		
	}
	
	public static Device get(ChannelHandlerContext ctx) {
		return map.get(ctx);
	}
	
	public static void put(ChannelHandlerContext ctx, Device device) {
		map.put(ctx, device);
	}
	
	public static void remove(ChannelHandlerContext ctx) {
		map.remove(ctx);
	}
}
