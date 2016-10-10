package com.device;

import org.json.JSONObject;

import com.device.base.AbstractDevice;
import com.device.base.Device;
import com.device.base.DeviceFactory;
import com.device.base.DeviceMap;

public class SensorSink extends AbstractDevice {
	
	public SensorSink(String id) {
		super(id);
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
		String sensorId = id + "#" + msg.substring(0, 5);
		Device sensor = DeviceMap.get(sensorId);
		if(sensor == null) {
			sensor = DeviceFactory.getInstance("Sensor", sensorId);
			DeviceMap.put(sensorId, sensor);
		}
		sensor.handler(msg);
	}
}
