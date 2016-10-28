package com.model;

import io.netty.channel.ChannelHandlerContext;

import org.json.JSONObject;

import com.model.base.AbstractDevice;
import com.model.base.Device;
import com.utils.DeviceFactory;
import com.utils.DeviceMap;


/**
 * 传感器汇聚结点
 * @author Riviera
 *
 */
public class SensorSink extends AbstractDevice {
	
	public SensorSink(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}
	
	@Override
	public String control(String type) {
		return AbstractDevice.ERROR;
	}

	@Override
	public String control(String type, JSONObject params) {
		return AbstractDevice.ERROR;
	}

	@Override
	public void handler(String msg) {
		String sensorId = id + "_" + msg.substring(0, 5);
		Device sensor = DeviceMap.get(sensorId);
		if(sensor == null) {
			sensor = DeviceFactory.getInstance("Sensor", sensorId);
			DeviceMap.put(sensorId, sensor);
		}
		sensor.handler(msg);
	}
}
