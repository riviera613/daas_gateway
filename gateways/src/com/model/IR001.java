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
		return ERROR;
	}

	@Override
	public String control(String type, JSONObject params) {
		switch(type) {
		case STUDY: return studySignal(params.getInt(CODE));
		case SEND: return sendSignal(params.getInt(CODE));
		default: return ERROR;
		}
	}

	@Override
	public void handler(String msg) {
		
	}
	
	public String studySignal(int code) {
		String buf = "";
		buf += (char)0x88;
		buf += (char)code;
		buf += (char)0x00;
		buf += (char)0x00;
		buf += (char)(0x88 ^ code ^ 0x00 ^ 0x00);
		ctx.writeAndFlush(buf.toString());
		return SUCCESS;
	}
	
	public String sendSignal(int code) {
		StringBuffer buf = new StringBuffer();
		buf.append((char)0x86);
		buf.append((char)code);
		buf.append((char)0x00);
		buf.append((char)0x00);
		buf.append((char)(0x86 ^ code ^ 0x00 ^ 0x00));
		ctx.writeAndFlush(buf.toString());
		return SUCCESS;
	}
}
