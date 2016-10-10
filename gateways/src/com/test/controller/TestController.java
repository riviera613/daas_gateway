package com.test.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.model.Data;
import com.test.model.Device;
import com.test.model.Gateways;
import com.test.util.BitmapUtil;

@Controller
@RequestMapping("/test")
public class TestController {
	
	private Gateways gateways;
	
	public TestController() {
		gateways = new Gateways();
	}

	/**
	 * get update info of devices
	 * @param timestamp
	 * @param remoteBitmap
	 * @return
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public String getUpdate(@RequestParam("timestamp") Long timestamp, @RequestParam("bitmap") String remoteBitmap) {
		JSONObject result = new JSONObject();
		
		//compare remote and local bitmap
		remoteBitmap = BitmapUtil.decompress(remoteBitmap);
		String localBitmap = gateways.getBitmap();
		Map<String, List<Integer>> compareResult = BitmapUtil.compare(remoteBitmap, localBitmap);
		
		//get devices list
		List<Device> devices = gateways.getDevices();
		
		//add state changed device
		if(compareResult.get("open").size() > 0)
			result.put("openDevices", buildPosArray(compareResult, "open", devices));
		if(compareResult.get("stop").size() > 0)
			result.put("stopDevices", buildPosArray(compareResult, "stop", devices));
		if(compareResult.get("new").size() > 0) {
			result.put("newDevices", buildIdArray(compareResult, "new", devices));
				JSONArray newStoppedDevices = new JSONArray();
				List<Integer> newDevicesPos = compareResult.get("new");
				for(Integer pos : newDevicesPos)
					if(!gateways.getDevices().get(pos).isActive())
						newStoppedDevices.put(gateways.getDevices().get(pos).getId());
				if(newStoppedDevices.length() > 0)
					result.put("newStoppedDevices", newStoppedDevices);
		}
		
		//add new timestamp
		if(timestamp < gateways.getMaxTimeStamp())
			result.put("timestamp", gateways.getMaxTimeStamp());
		
		//add changed data
		JSONArray updateData = new JSONArray();
		for(Device device : devices) {
			JSONObject deviceJson = new JSONObject();
			deviceJson.put("deviceId", device.getId());
			JSONArray dataJsons = new JSONArray();
			for(Entry<String, Data> e : device.getData().entrySet())
				if(e.getValue().getTimestamp() > timestamp) {
					JSONObject dataJson = new JSONObject();
					dataJson.put("dataName", e.getKey());
					dataJson.put("dataValue", e.getValue().getValue());
					dataJsons.put(dataJson);
				}
			if(dataJsons.length() > 0) {
				deviceJson.put("data", dataJsons);
				updateData.put(deviceJson);
			}
		}
		if(updateData.length() > 0)
			result.put("updateData", updateData);
		return result.toString();
	}
	
	/**
	 * get all info of devices
	 * @return
	 */
	@RequestMapping(value = "/devices", method = RequestMethod.GET)
	@ResponseBody
	public String get() {
		JSONArray json = new JSONArray();
		for(Device device : gateways.getDevices()) {
			Map<String, Object> infoMap = device.infoMap();
			if(infoMap != null)
				json.put(new JSONObject(infoMap));
		}
		String res = json.toString();
		System.out.println("Node num:\t" + gateways.getDevices().size());
		System.out.println("Get length:\t" + res.length());
		return res;
	}
	
	/**
	 * clean up all data in this gateways
	 * @return
	 */
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	@ResponseBody
	public String reset() {
		gateways.reset();
		return "success";
	}
	
	private JSONArray buildPosArray(Map<String, List<Integer>> compareResult, String state, List<Device> devices) {
		List<Integer> bits = compareResult.get(state);
		JSONArray devicesId = new JSONArray();
		for(Integer pos : bits)
			devicesId.put(pos);
		return devicesId;
	}
	
	private JSONArray buildIdArray(Map<String, List<Integer>> compareResult, String state, List<Device> devices) {
		List<Integer> bits = compareResult.get(state);
		JSONArray devicesId = new JSONArray();
		for(Integer pos : bits)
			devicesId.put(devices.get(pos).getId());
		return devicesId;
	}
	
	/************************little data test*************************/
	
	@RequestMapping(value = "/open", method = RequestMethod.POST)
	@ResponseBody
	public String openDevice(@RequestParam("deviceId") String deviceId) {
		gateways.openDevice(deviceId);
		return "success";
	}

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	@ResponseBody
	public String stopDevice(@RequestParam("deviceId") String deviceId) {
		gateways.stopDevice(deviceId);
		return "success";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public String setData(@RequestParam("deviceId") String deviceId, @RequestParam("dataName") String dataName,
							@RequestParam("dataValue") String dataValue) {
		gateways.setData(deviceId, dataName, dataValue);
		return "success";
	}
	
	/******************************************************************/
	
	/***************************big data test**************************/
	
	/**
	 * add a batch of devices
	 * @param num
	 * @return
	 */
	@RequestMapping(value = "/large/add", method = RequestMethod.POST)
	@ResponseBody
	public String addDevices(@RequestParam("num") Integer num) {
		Random random = new Random(System.currentTimeMillis());
		int start = gateways.getDevices().size();
		for(int i = start; i < start + num; i++) {
			String deviceId = String.valueOf(i);
			while(deviceId.length() < 4)
				deviceId = "0" + deviceId;
			Device device = new Device(deviceId);
			device.setData("temperature", String.valueOf(random.nextInt(30)));
			device.setData("humidity", String.valueOf(random.nextInt(100)));
			gateways.addDevice(device);
		}
		return "success";
	}
	
	/**
	 * switch a batch of devices
	 * @param num
	 * @return
	 */
	@RequestMapping(value = "/large/switch", method = RequestMethod.POST)
	@ResponseBody
	public String switchDevices(@RequestParam("num") Integer num) {
		List<Device> devices = gateways.getDevices();
		List<Device> seq = getRandomSeq(devices, num);
		Random random = new Random(System.currentTimeMillis());
		for(Device device : seq) {
			if(device.isActive())
				device.stop();
			else {
				device.open();
				device.setData("temperature", String.valueOf(random.nextInt(30)));
				device.setData("humidity", String.valueOf(random.nextInt(100)));
			}
		}
		return "success";
	}
	
	/**
	 * update data in a batch of devices
	 * @param num
	 * @return
	 */
	@RequestMapping(value = "/large/update", method = RequestMethod.POST)
	@ResponseBody
	public String updateDevicesData(@RequestParam("num") Integer num) {
		List<Device> devices = gateways.getDevices();
		List<Device> seq = getRandomSeq(devices, num);
		Random random = new Random(System.currentTimeMillis());
		for(Device device : seq) {
			device.setData("temperature", String.valueOf(random.nextInt(30)));
			device.setData("humidity", String.valueOf(random.nextInt(100)));
		}
		return "success";
	}
	
	/**
	 * get a random subseq of a devices list
	 * @param source
	 * @param len
	 * @return
	 */
	private List<Device> getRandomSeq(List<Device> source, int len) {
		if(len > source.size())
			len = source.size();
		List<Device> output = new ArrayList<>();
		Random random = new Random(System.currentTimeMillis());
		for(int i = 0; i < len; i++)
			output.add(source.get(i));
		for(int i = len + 1; i < source.size(); i++) {
			int j = random.nextInt(i + 1);
			if(j < len)
				output.set(j, source.get(i));
		}
		return output;
	}
	
	/******************************************************************/
}
