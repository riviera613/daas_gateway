package com.device.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeviceMap {

	private static Map<String, Device> map = new HashMap<>();
	
	private DeviceMap() {
		
	}
	
	public static Device get(String id) {
		return map.get(id);
	}
		
	public static void put(String id, Device device) {
		map.put(id, device);
	}
	
	public static void remove(Device device) {
		String key = null;
		for(Entry<String, Device> e : map.entrySet())
			if(e.getValue().equals(device))
				key = e.getKey();
		if(key != null)
			map.remove(key);
	}
}
