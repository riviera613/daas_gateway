package com.device.base;

import com.device.*;

public class DeviceFactory {

	public static Device getInstance(String name, String id) {
		if(name.equals("SensorSink"))
			return new SensorSink(id);
		else if(name.equals("Sensor"))
			return new Sensor(id);
		else
			return null;
	}
}
