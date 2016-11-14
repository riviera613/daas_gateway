package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.service.DeviceService;

/**
 * 设备对外的服务接口
 * @author Riviera
 *
 */
@Controller
@RequestMapping("/service")
public class DeviceController {
	
	@Autowired
	private DeviceService deviceService;

	/**
	 * 无参数GET服务
	 * @param id	设备id   
	 * @param type	服务名
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}", method=RequestMethod.GET)
	@ResponseBody
	public String get(@PathVariable("id") String id, @PathVariable("type") String type) {
		System.out.println("control: " + id + " " + type);
		return deviceService.control(id, type);
	}
	
	/**
	 * 有参数GET服务
	 * @param id		设备id
	 * @param type		服务名
	 * @param params	服务参数，JSON字符串格式
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}/{params}", method=RequestMethod.GET)
	@ResponseBody
	public String get(@PathVariable("id") String id, @PathVariable("type") String type, @PathVariable("params") String params) {
		System.out.println("control: " + id + " " + type + " " + params);
		return deviceService.control(id, type, params);
	}
	
	/**
	 * 无参数POST服务
	 * @param id	设备id   
	 * @param type	服务名
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}", method=RequestMethod.POST)
	@ResponseBody
	public String post(@PathVariable("id") String id, @PathVariable("type") String type) {
		System.out.println("control: " + id + " " + type);
		return deviceService.control(id, type);
	}
	
	/**
	 * 有参数POST服务
	 * @param id		设备id
	 * @param type		服务名
	 * @param params	服务参数，JSON字符串格式
	 * @return
	 */
	@RequestMapping(value="/{id}/{type}/{params}", method=RequestMethod.POST)
	@ResponseBody
	public String post(@PathVariable("id") String id, @PathVariable("type") String type, @PathVariable("params") String params) {
		System.out.println("control: " + id + " " + type + " " + params);
		return deviceService.control(id, type, params);
	}
	
	/**
	 * 查看当前所有在线的设备
	 */
	@RequestMapping(value="/device_map", method=RequestMethod.GET)
	@ResponseBody
	public String getDeviceMap() {
		return deviceService.getDeviceMapStr();
	}
	
	/**
	 * 手动创建一个设备实例
	 * @param id	设备id
	 * @param name	设备实例对应的类名
	 * @return
	 */
	@RequestMapping(value="/create/{id}/{name}", method=RequestMethod.POST)
	@ResponseBody
	public String createDevice(@PathVariable("id") String id, @PathVariable("name") String name) {
		return deviceService.createDevice(id, name) ? "success" : "false";
	}
}
