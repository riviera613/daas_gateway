package com.test.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gateways proxy
 * @author Riviera
 *
 */
public class Gateways {

	/**
	 * list of devices in this gateways
	 */
	private List<Device> devices;
	
	/**
	 * a map of deviceId -> pos in devices list
	 */
	private Map<String, Integer> devicesMap;
	
	public Gateways() {
		devices = new ArrayList<>();
		devicesMap = new HashMap<>();
	}
	
	public List<Device> getDevices() {
		return devices;
	}
	
	/**
	 * get max timestamp of all device data
	 * @return
	 */
	public long getMaxTimeStamp() {
		long max = 0;
		for(Device device : devices)
			max = Math.max(max, device.maxTimestamp());
		return max;
	}
	
	/**
	 * get data of a device
	 * @param deviceId
	 * @param dataName
	 * @return
	 */
	public Object getData(String deviceId, String dataName) {
		if(devicesMap.containsKey(deviceId)) {
			int pos = devicesMap.get(deviceId);
			return devices.get(pos).getData(dataName);
		} else {
			return null;
		}
	}
	
	/**
	 * set data of a device
	 * @param deviceId
	 * @param dataName
	 * @param dataValue
	 * @return
	 */
	public void setData(String deviceId, String dataName, Object dataValue) {
		if(devicesMap.containsKey(deviceId)) {
			int pos = devicesMap.get(deviceId);
			devices.get(pos).setData(dataName, dataValue);
		}
	}
	
	/**
	 * open a device by pos
	 * @param pos
	 */
	public void openDevice(int pos) {
		devices.get(pos).open();
	}
	
	/**
	 * open a device by device id
	 * @param deviceId
	 */
	public void openDevice(String deviceId) {
		if(devicesMap.containsKey(deviceId))
			openDevice(devicesMap.get(deviceId));
		else {
			devices.add(new Device(deviceId));
			devicesMap.put(deviceId, devices.size() - 1);
		}
	}
	
	/**
	 * stop a device by pos
	 * @param pos
	 */
	public void stopDevice(int pos) {
		devices.get(pos).stop();
	}
	
	/**
	 * stop a device by device id
	 * @param deviceId
	 */
	public void stopDevice(String deviceId) {
		if(devicesMap.containsKey(deviceId))
			stopDevice(devicesMap.get(deviceId));
	}
	
	/**
	 * add a new device
	 * @param device
	 * @return add success or fail
	 */
	public boolean addDevice(Device device) {
		//cannot add exist device
		if(!devicesMap.containsKey(device.getId())) {
			devices.add(device);
			devicesMap.put(device.getId(), devices.size() - 1);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * get a bitmap of existing devices
	 * @return	'0' means stopped and '1' means actived
	 */
	public String getBitmap() {
		if(devices.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for(Device d : devices)
			sb.append(d.isActive() ? '1' : '0');
		return sb.toString();
	}
	
	/**
	 * reset this gateways proxy
	 */
	public void reset() {
		devices.clear();
		devicesMap.clear();
	}
}
