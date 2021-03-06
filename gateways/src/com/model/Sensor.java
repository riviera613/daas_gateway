package com.model;

import io.netty.channel.ChannelHandlerContext;

import org.json.JSONObject;

import com.model.base.AbstractDevice;


/**
 * 传感器结点
 * @author Riviera
 *
 */
public class Sensor extends AbstractDevice {
	
	public static final String TEMPERATURE = "temperature";	//温度值
	public static final String HUMIDITY = "humidity";		//湿度值
	
	private String temp;
	private String humi;
	
	public Sensor(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}

	@Override
	public String control(String type) {
		switch(type) {
		case TEMPERATURE: return getTemp();
		case HUMIDITY: return getHumi();
		default: return ERROR;
		}
	}

	@Override
	public String control(String type, JSONObject params) {
		return ERROR;
	}

	@Override
	public void handler(String msg) {
		temp = msg.substring(6, 8);
		humi = msg.substring(10, 12);
		if(temp.equals("00") && humi.equals("00")) {
			offline();
		}
	}
	
	public String getTemp() {
		return temp;
	}
	
	public String getHumi() {
		return humi;
	}
}
