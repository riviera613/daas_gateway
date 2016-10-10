package com.test.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Device proxy
 * @author Riviera
 *
 */
public class Device {

	/**
	 * device id
	 */
	private String id;
	
	/**
	 * device data, a map of dataName -> dataValue
	 */
	private Map<String, Data> data;
	
	/**
	 * if this device is active, set true, otherwise set false
	 */
	private boolean active;
	
	public Device(String id) {
		this.id = id;
		data = new TreeMap<>();
		active = true;
	}
	
	public String getId() {
		return id;
	}
	
	public Map<String, Data> getData() {
		return data;
	}

	/**
	 * activate this device
	 */
	public void open() {
		active = true;
	}
	
	/**
	 * stop this device
	 */
	public void stop() {
		active = false;
		//clean all data
		data.clear();
	}
	
	/**
	 * check whether this device is active or not
	 * @return
	 */
	public boolean isActive() {
		return active;
	}
	
	/**
	 * get one data
	 * @param dataName
	 * @return
	 */
	public Object getData(String dataName) {
		return data.get(dataName).getValue();
	}
	
	/**
	 * set one data
	 * @param dataName
	 * @param dataValue
	 */
	public void setData(String dataName, Object dataValue) {
		data.put(dataName, new Data(dataValue));
	}
	
	/**
	 * get max timestamp of all data
	 * @return
	 */
	public long maxTimestamp() {
		long max = 0;
		for(Entry<String, Data> e : data.entrySet())
			max = Math.max(max, e.getValue().getTimestamp());
		return max;
	}

	/**
	 * get useful information of this device
	 * @return
	 */
	public Map<String, Object> infoMap() {
		if(active) {
			Map<String, Object> map = new TreeMap<>();
			map.put("id", id);
			List<Map<String, String>> dataList = new ArrayList<>();
			for(Entry<String, Data> e : data.entrySet()) {
				Map<String, String> dataMap = new TreeMap<>();
				dataMap.put("dataName", e.getKey());
				dataMap.put("dataValue", e.getValue().getValue());
				dataList.add(dataMap);
			}
			map.put("data", dataList);
			map.put("active", String.valueOf(active));
			return map;
		} else {
			return null;
		}
	}
}
