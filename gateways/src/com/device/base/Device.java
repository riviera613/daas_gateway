package com.device.base;

import org.json.JSONObject;

/**
 * 设备接口
 * @author Riviera
 *
 */
public interface Device {
	
	/**
	 * 无参数控制接口
	 * @param type	方法名
	 * @return
	 */
	public String control(String type);

	/**
	 * 有参数控制接口
	 * @param type		方法名
	 * @param params	方法参数
	 * @return
	 */
	public String control(String type, JSONObject params);
	
	/**
	 * 上行响应接口
	 * @param msg	接收到的消息
	 */
	public void handler(String msg);
	
	/**
	 * 设备主动下线
	 */
	public void offline();
}
