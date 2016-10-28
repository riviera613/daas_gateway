package com.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.utils.DeviceMap;

@Service
public class DeviceService {
	
	public DeviceService() {
		
	}

	public String control(String id, String type) {
		return DeviceMap.get(id).control(type);
	}
	
	public String control(String id, String type, String params) {
		return DeviceMap.get(id).control(type, new JSONObject(params));
	}
	
	public String getDeviceMapStr() {
		return DeviceMap.map.toString();
	}
}
