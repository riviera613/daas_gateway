package com.controller;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.device.base.DeviceMap;

@Controller
@RequestMapping("/service")
public class DeviceController {

	@RequestMapping(value="/{id}/{type}", method=RequestMethod.GET)
	@ResponseBody
	public String control(@PathVariable("id") String id, @PathVariable("type") String type) {
		System.out.println("control: " + id + " " + type);
		return DeviceMap.get(id).control(type);
	}
	
	@RequestMapping(value="/{id}/{type}/{params}", method=RequestMethod.GET)
	@ResponseBody
	public String control(@PathVariable("id") String id, @PathVariable("type") String type, 
							@PathVariable("params") String params) {
		System.out.println("control: " + id + " " + type + " " + params);
		return DeviceMap.get(id).control(type, new JSONObject(params));
	}
}
