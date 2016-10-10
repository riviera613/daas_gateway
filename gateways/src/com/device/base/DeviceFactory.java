package com.device.base;

import io.netty.channel.ChannelHandlerContext;

import com.device.*;

/**
 * 生成设备实例的工厂
 * @author Riviera
 *
 */
public class DeviceFactory {

	public static Device getInstance(String name, String id) {
		return getInstance(name, id, null);
	}
	
	public static Device getInstance(String name, String id, ChannelHandlerContext ctx) {
		if(name.equals("SensorSink"))
			return new SensorSink(id, ctx);
		else if(name.equals("Sensor"))
			return new Sensor(id, ctx);
		else
			return null;
	}
}
