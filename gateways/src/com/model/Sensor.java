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
	
	public static final String TEMPERATURE = "temperature";
	public static final String HUMIDITY = "humidity";
	
	private String temp;
	private String humi;
	
	public Sensor(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}

	@Override
	public String control(String type) {
		if(type.equals(Sensor.TEMPERATURE))
			return temp;
		else if(type.equals(Sensor.HUMIDITY))
			return humi;
		return AbstractDevice.ERROR;
	}

	@Override
	public String control(String type, JSONObject params) {
		return AbstractDevice.ERROR;
	}

	@Override
	public void handler(String msg) {
		temp = msg.substring(6, 8);
		humi = msg.substring(10, 12);
		if(temp.equals("00") && humi.equals("00")) {
			offline();
		}
	}
}
