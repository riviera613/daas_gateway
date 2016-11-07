package com.model;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

import com.model.base.AbstractDevice;

/**
 * RFID读写器
 * @author Riviera
 * 原设备已经没了
 *
 */
public class Rfid8500D extends AbstractDevice{
	
	public static final String SWITCH_ON = "switch_on";		//打开
	public static final String SWITCH_OFF = "switch_off";	//关闭
	public static final String WRITE = "write";				//写数据
	public static final String ERASE = "erase";				//擦除数据

	public Rfid8500D(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}

	@Override
	public String control(String type) {
		StringBuffer buf = new StringBuffer();
		if(type.equals(Rfid8500D.SWITCH_ON)) {
			buf.append(0xAA);
			buf.append(0x03);
			buf.append(0x11);
			buf.append(0x01);
			buf.append(0x55);
		} else if(type.equals(Rfid8500D.SWITCH_OFF)) {
			buf.append(0xAA);
			buf.append(0x02);
			buf.append(0x12);
			buf.append(0x55);			
		}
		else
			return AbstractDevice.ERROR;
		
		ctx.writeAndFlush(buf.toString().toCharArray());
		return AbstractDevice.SUCCESS;
	}

	@Override
	public String control(String type, JSONObject params) {
		StringBuffer buf = new StringBuffer();
		if(type.equals(Rfid8500D.WRITE)) {

		} else if(type.equals(Rfid8500D.ERASE)) {
			
		}
		
		ctx.writeAndFlush(buf.toString().toCharArray());
		return AbstractDevice.SUCCESS;
	}

	@Override
	public void handler(String msg) {
		char []buf = msg.toCharArray();
		if(buf[2] == 0x10 || buf[2] == 0x11) {  //读取卡号
			int length=(int)buf[1] + 2;
			String cardId = msg.substring(8, length * 2 - 2);
			System.out.println("Card ID: " + cardId);
			
		} else {
			System.out.println("error msg");
		}
	}
}
