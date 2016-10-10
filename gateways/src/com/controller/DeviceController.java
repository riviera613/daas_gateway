package com.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.device.base.DeviceMap;

/**
 * 设备对外的服务接口
 * @author Riviera
 *
 */
@Controller
@RequestMapping("/service")
public class DeviceController {

	/**
	 * 无参数服务
	 * @param id	设备id   
	 * @param type	服务名
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}", method=RequestMethod.GET)
	@ResponseBody
	public String control(@PathVariable("id") String id, @PathVariable("type") String type) {
		System.out.println("control: " + id + " " + type);
		return DeviceMap.get(id).control(type);
	}
	
	/**
	 * 有参数服务
	 * @param id		设备id
	 * @param type		服务名
	 * @param params	服务参数，JSON字符串格式
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}/{params}", method=RequestMethod.GET)
	@ResponseBody
	public String control(@PathVariable("id") String id, @PathVariable("type") String type, 
							@PathVariable("params") String params) {
		System.out.println("control: " + id + " " + type + " " + params);
		return DeviceMap.get(id).control(type, new JSONObject(params));
	}
}
