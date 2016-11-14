package com.model;

import java.io.IOException;

import org.json.JSONObject;

import com.model.base.AbstractDevice;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

/**
 * 无人车，通过Nrf24l01芯片通信
 * @author Riviera
 * 不知道代码还能不能用了
 */
public class Car extends AbstractDevice {
	
	public static final String FRONT = "front";
	public static final String BACK = "back";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String STOP = "stop";
	
	public static final String DISTANCE = "distance";
	public static final String ANGEL = "angel";
	
	private static final GpioController gpio = GpioFactory.getInstance();
	private static final GpioPinDigitalOutput ce = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, "MyLED", PinState.LOW);
	private SpiDevice spi = null;

	public Car(String id) {
		super(id);
		try {
			initNrf24l01();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String control(String type) {
		byte[] data = {0, 0};
		switch(type) {
		case FRONT: data[0] = 1; break;
		case BACK: data[0] = 2; break;
		case LEFT: data[0] = 4; break;
		case RIGHT: data[0] = 5; break;
		case STOP: data[0] = 3; break;
		default: return ERROR;
		}
		try {
			sendData(data);
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	@Override
	public String control(String type, JSONObject params) {
		byte[] data = {0, 0};
		switch(type) {
		case FRONT: 
			data[0] = 1;
			data[1] = (byte)params.getInt(DISTANCE);
			break;
		case BACK: 
			data[0] = 2; 
			data[1] = (byte)params.getInt(DISTANCE);
			break;
		case LEFT: 
			data[0] = 4; 
			data[1] = (byte)params.getInt(ANGEL);
			break;
		case RIGHT: 
			data[0] = 5; 
			data[1] = (byte)params.getInt(ANGEL);
			break;
		default: return ERROR;
		}
		try {
			sendData(data);
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}

	@Override
	public void handler(String msg) {

	}
	
	private void initNrf24l01() throws InterruptedException, IOException
	{
		byte[] sendaddress = { 48, 52, 67, 16, 16, 1 };
		byte[] reiecveaddress = { 42, 52, 67, 16, 16, 1 };
		byte[] enableAA = { 33, 1 };
		byte[] enableADDR = { 34, 1 };
		byte[] setupRETR = { 36, 10 };
		byte[] rfCH = { 37, 40 };
		byte[] rfSetup = { 38, 7 };
		byte[] rxPwP0 = { 49, 2 }; 
		
		this.spi = SpiFactory.getInstance(SpiChannel.CS0, 1000000, SpiDevice.DEFAULT_SPI_MODE);
		ce.low();
		this.spi.write(sendaddress);
		this.spi.write(reiecveaddress);
		this.spi.write(enableAA);
		this.spi.write(enableADDR);
		this.spi.write(setupRETR);
		this.spi.write(rfCH);
		this.spi.write(rfSetup);
		this.spi.write(rxPwP0);
	}
	
	public void sendData(byte[] arg) throws InterruptedException, IOException
	{
		byte[] clear = { 39, -1 };
		byte nop = -1;
		byte[] data = new byte[3];
		byte[] config = { 32, 14 };
		for (int i = 1; i < arg.length + 1; i++) {
			data[i] = arg[(i - 1)];
		}
		data[0] = -96;
		this.spi = SpiFactory.getInstance(SpiChannel.CS0, 1000000, SpiDevice.DEFAULT_SPI_MODE);
		ce.low();
		this.spi.write(data);
		this.spi.write(config);
		ce.high();
		Thread.sleep(1L);
		for (int i = 0; i < 10; i++) {
			Thread.sleep(50L);
			byte[] state = this.spi.write(new byte[] { nop });
			System.out.println(state[0]);
			clear[1] = state[0];
			this.spi.write(clear);
		}
	}
	
	public void readData() throws InterruptedException, IOException {
		byte[] config = { 32, 15 };
		byte[] data = { 97, -1, -1 };
		
		this.spi = SpiFactory.getInstance(SpiChannel.CS0, 1000000, SpiDevice.DEFAULT_SPI_MODE);
		ce.low();
		this.spi.write(config);
		ce.high();
		byte[] data1 = this.spi.write(data);
		System.out.println("rebackdata-->" + data1[0] + " " + data1[1] + " " + data1[2]);
	}
}
