package com.model;

import io.netty.channel.ChannelHandlerContext;

import org.json.JSONObject;

import com.model.base.AbstractDevice;

/**
 * IR001红外线学习器
 * @author Riviera
 *
 */
public class IR001 extends AbstractDevice {
	
	public static final String STUDY = "study";	//学习红外信号
	public static final String SEND = "send";	//发射红外信号
	
	public static final String CODE = "code";	//红外信号的存储代码

	public IR001(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}

	@Override
	public String control(String type) {
		return AbstractDevice.ERROR;
	}

	@Override
	public String control(String type, JSONObject params) {
		StringBuffer buf = new StringBuffer();
		if(type == IR001.STUDY) {
			int code = params.getInt(IR001.CODE);
			buf.append(0x88);
			buf.append(code);
			buf.append(0x00);
			buf.append(0x00);
			buf.append(0x88 ^ code ^ 0x00 ^ 0x00);
		} else if(type == IR001.SEND) {
			int code = params.getInt(IR001.CODE);
			buf.append(0x86);
			buf.append(code);
			buf.append(0x00);
			buf.append(0x00);
			buf.append(0x86 ^ code ^ 0x00 ^ 0x00);
		} else {
			return AbstractDevice.ERROR;
		}
		return AbstractDevice.SUCCESS;
	}

	@Override
	public void handler(String msg) {
		
	}

}
