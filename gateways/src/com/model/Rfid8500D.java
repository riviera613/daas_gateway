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
	
	public static final String CARD_ID = "card_id";
	public static final String START_POS = "start_pos";
	public static final String WRITE_WORD = "write_word";
	public static final String ERASE_LENGTH = "erase_length";

	public Rfid8500D(String id, ChannelHandlerContext ctx) {
		super(id, ctx);
	}

	@Override
	public String control(String type) {
		switch(type) {
		case SWITCH_ON: return switchOn();
		case SWITCH_OFF: return switchOff();
		default: return ERROR;
		}
	}

	@Override
	public String control(String type, JSONObject params) {
		switch(type) {
		case WRITE: return writeInfo(params.getString(CARD_ID), params.getInt(START_POS), params.getString(WRITE_WORD));
		case ERASE: return eraseInfo(params.getString(CARD_ID), params.getInt(START_POS), params.getInt(ERASE_LENGTH));
		default: return ERROR;
		}
	}

	@Override
	public void handler(String msg) {
		char []buf = msg.toCharArray();
		if(buf[2] == 0x10 || buf[2] == 0x11) {
			int length=(int)buf[1] + 2;
			String cardId = msg.substring(8, length * 2 - 2);
			System.out.println("Card ID: " + cardId);
			
		} else {
			System.out.println("error msg");
		}
	}
	
	public String switchOn() {
		StringBuffer buf = new StringBuffer();
		buf.append((char)0xAA);
		buf.append((char)0x03);
		buf.append((char)0x11);
		buf.append((char)0x01);
		buf.append((char)0x55);
		ctx.writeAndFlush(buf.toString());
		return SUCCESS;
	}
	
	public String switchOff() {
		StringBuffer buf = new StringBuffer();
		buf.append((char)0xAA);
		buf.append((char)0x02);
		buf.append((char)0x12);
		buf.append((char)0x55);
		ctx.writeAndFlush(buf.toString());
		return SUCCESS;
	}
	
	public String writeInfo(String cardId, int startPos, String startWord) {
		return UNCOMPLETED;
	}
	
	public String eraseInfo(String cardId, int startPos, int eraseLength) {
		return UNCOMPLETED;
	}
}
