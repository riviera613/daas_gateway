package com.utils;

import io.netty.channel.ChannelHandlerContext;

import com.model.*;
import com.model.base.Device;

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
		
		if(name.equals("Sensor"))
			return new Sensor(id, ctx);
		
		if(name.equals("Rfid8500D"))
			return new Rfid8500D(id, ctx);
		
		if(name.equals("IR001"))
			return new IR001(id, ctx);
		
		if(name.equals("ARDrone"))
			return new ARDrone(id);
		
		return null;
	}
}
