package com.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.model.base.Device;

/**
 * 设备id -> 设备实例的映射
 * @author Riviera
 *
 */
public class DeviceMap {

	public static Map<String, Device> map = new ConcurrentHashMap<>();
	
	private DeviceMap() {
		
	}
	
	public static Device get(String id) {
		return map.get(id);
	}
		
	public static void put(String id, Device device) {
		map.put(id, device);
	}
	
	public static void remove(String deviceId) {
		map.remove(deviceId);
	}
}
