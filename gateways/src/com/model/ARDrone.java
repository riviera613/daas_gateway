package com.model;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.json.JSONObject;

import com.model.base.AbstractDevice;

/**
 * ARDrone无人机
 * @author Riviera
 * 不知道代码还能不能用了
 */
public class ARDrone extends AbstractDevice {
	
	public static final String TAKEOFF = "takeoff";
	public static final String EMERGENCY = "emergency";
	public static final String LAND = "land";
	public static final String STOP = "stop";
	
	public static final String FRONT = "front";
	public static final String BACK = "back";
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String HOVER = "hover";
	
	public static final String SET_SPEED = "set_speed";
	public static final String BATTERY = "battery";
	public static final String VIDEO_START = "video_start";
	public static final String VIDEO_STOP = "video_stop";
	
	public static final String SPEED = "speed";
	
	static final int NAVDATA_PORT = 5554;	//接收状态信息的端口
    static final int VIDEO_PORT = 5555;		//接收视频流的端口
    static final int AT_PORT = 5556;		//发送控制命令的端口
    static final int INTERVAL = 30;			//两次命令之间的间隔（ms）
    static final int TIMEOUT = 5000;		//超时重传限界（ms）
    
    InetAddress ipAddr;					//飞机的IP地址
    int seq = 1;						//当前指令序列号		
    int lastSeq = 1;					//上一条指令的序列号
    float speed = (float)0.1;			//速度
    boolean connect = false;			//飞机是否已与网关建立连接
    String videoFilename;				//视频文件名
    byte[] navdata = new byte[1024];	//接收到的数据
    
    DatagramSocket socketAT;			//AT端口对应socket
    DatagramSocket socketData;			//NAVDATA端口对应socket
    ATWatchDog atWatchDogThread;		//定时发送AT-WatchDog的子线程
    Navdata navdataThread;				//定时接收navdata的子线程
    Video videoThread;					//接收视频流的线程

	public ARDrone(String id) {
		super(id);
		try {
			initARDrone("192.168.1.1");
		} catch (UnknownHostException | SocketException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public String control(String type) {
		switch(type) {
		case TAKEOFF: sendAT("AT*REF=", ",290718208"); break;
		case EMERGENCY: sendAT("AT*REF=", ",290717952"); break;
		case LAND: sendAT("AT*REF=", ",290717696"); break;
		case STOP: 
			sendAT("AT*REF=", ",290717696");
			atWatchDogThread.interrupt();
			navdataThread.interrupt();
			while(atWatchDogThread.getState()!=State.TERMINATED || navdataThread.getState()!=State.TERMINATED);
			socketAT.close();
			break;
		case FRONT: sendAT("AT*PCMD=", ",1,0,"+floatToInt(speed)+",0,0"); break;
		case BACK: sendAT("AT*PCMD=", ",1,0,"+floatToInt(-speed)+",0,0"); break;
		case UP: sendAT("AT*PCMD=", ",1,0,0,"+floatToInt(speed)+",0"); break;
		case DOWN: sendAT("AT*PCMD=", ",1,0,0,"+floatToInt(-speed)+",0"); break;
		case LEFT: sendAT("AT*PCMD=", ",1,0,0,0,"+floatToInt(-speed)); break;
		case RIGHT: sendAT("AT*PCMD=", ",1,0,0,0,"+floatToInt(speed)); break;
		case HOVER: sendAT("AT*PCMD=", ",1,0,0,0,0"); break;
		case BATTERY: return String.valueOf(navdata[24]); 
		case VIDEO_START:
			videoFilename = "video.h264";
	    	videoThread = new Video();
	    	videoThread.start();
	    	break;
		case VIDEO_STOP: videoThread.interrupt(); break;
		default: return ERROR;
		}
		return SUCCESS;
	}

	@Override
	public String control(String type, JSONObject params) {
		switch(type) {
		case SET_SPEED:
			float new_speed = (float)params.getDouble(SPEED);
			speed = (new_speed > 0.99 || new_speed < -0.99) ? 0 : new_speed;
			break;
		default: return ERROR;
		}
		return SUCCESS;
	}

	@Override
	public void handler(String msg) {

	}
	
	private void initARDrone(String ip) throws UnknownHostException, SocketException, InterruptedException {
    	ipAddr = InetAddress.getByName(ip);
    	connect = true;
    	
    	socketAT = new DatagramSocket(AT_PORT);	//建立socket连接
    	socketAT.setSoTimeout(TIMEOUT);
    	socketData = new DatagramSocket(NAVDATA_PORT);
    	socketData.setSoTimeout(TIMEOUT);

    	sendInitialAT();						//发送初始化信息	
		
    	atWatchDogThread = new ATWatchDog();	//开启子线程
		atWatchDogThread.start();
		navdataThread = new Navdata();
		navdataThread.start();
	}
	
    /**
     * 发送一条AT指令
     * @param atType	指令类型
     * @param atParam	指令参数
     * 为保证AT指令的序列号严格+1递增，需要在带锁的send方法内部组装指令
     */
    private synchronized void sendAT(String atType, String atParam) {
    	if(socketAT.isClosed())
    		return;
    	String at = atType + String.valueOf(seq) + atParam;
    	byte[] buf = (at + "\r").getBytes();
    	try {
			socketAT.send(new DatagramPacket(buf, buf.length, ipAddr, AT_PORT));
	    	System.out.println("send " + buf.length + " :" + at);
			seq++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	/**
	 * 发送初始化信息
	 * @throws InterruptedException
	 */
    private void sendInitialAT() throws InterruptedException
    {
    	seq = 1;
    	Thread.sleep(INTERVAL);
		sendAT("AT*MISC=", ",2,20,2000,3000");
		Thread.sleep(INTERVAL);
		sendAT("AT*REF=", ",290717696");
		Thread.sleep(INTERVAL);
		sendAT("AT*COMWDG=", "");
		Thread.sleep(INTERVAL);
		sendAT("AT*CONFIG=", ",\"control:altitude_max\",\"2000\"");	//altitude max 2m
		Thread.sleep(INTERVAL);
		sendAT("AT*CONFIG=", ",\"control:control_level\",\"0\"");	//0:BEGINNER, 1:ACE, 2:MAX
		Thread.sleep(INTERVAL);
		sendAT("AT*CONFIG=", ",\"general:navdata_demo\",\"TRUE\"");
		Thread.sleep(INTERVAL);
		sendAT("AT*CONFIG=", ",\"general:video_enable\",\"TRUE\"");
		Thread.sleep(INTERVAL);
		sendAT("AT*CONFIG=", ",\"pic:ultrasound_freq\",\"8\"");
		Thread.sleep(INTERVAL);
		sendAT("AT*FTRIM=", "");
		Thread.sleep(INTERVAL);
		sendAT("AT*REF=", ",290717696");
		Thread.sleep(INTERVAL);
		sendAT("AT*PCMD=", ",0,0,0,0,0");
		Thread.sleep(INTERVAL);
    }
    
    /**
     * 在发送AT指令时调用，浮点数转为整数编码
     * @param f	浮点数
     * @return	整数编码
     */
    private int floatToInt(float f)
    {
    	ByteBuffer bb = ByteBuffer.allocate(4);
    	bb.asFloatBuffer().put(0, f);
    	return bb.asIntBuffer().get(0);
    }
	
	/**
     * ATWatchDog
     * @author Riviera
     * 定时向ARDrone发送数据，防止连接丢失
     */
    private class ATWatchDog extends Thread
    {
    	@Override
    	public void run()
    	{
    		while(!isInterrupted())
    		{
    			try {
    				sleep(INTERVAL);
    				if(seq == lastSeq)
    					sendAT("AT*COMWDG=", "");
    				lastSeq = seq;
    				sleep(INTERVAL);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    				break;
    			}   			
    		}
    			
    	}
    }
    
    /**
     * Navdata
     * @author Riviera
     * 接收ARDrone传送来的数据
     */
    private class Navdata extends Thread
    {
    	@Override
    	public void run()
    	{
    		byte[] buf = {0x01, 0x00, 0x00, 0x00};
    		DatagramPacket dp = new DatagramPacket(buf, buf.length, ipAddr, NAVDATA_PORT);
    		try {
    			//发送初始信息，启动数据传输
				socketData.send(dp);
				sendAT("AT*CONFIG=", ",\"general:navdata_demo\",\"TRUE\"");
				
				DatagramPacket dataPacket = new DatagramPacket(navdata, navdata.length); 
				while(!isInterrupted())
				{
					try {
						if(!connect)	//如果当前状态为未连接，则需要重新发送初始信息
						{
							sendInitialAT();
							socketData.send(dp);
							sendAT("AT*CONFIG=", ",\"general:navdata_demo\",\"TRUE\"");
						}
						socketData.receive(dataPacket);
						if(!connect)	//接收到navdata代表平台与飞机连接成功，置标志位为true
							connect = true;
						sleep(INTERVAL);
					} catch (SocketTimeoutException e) {
						// TODO Auto-generated catch block
						System.out.println("Navdata Timeout.");
						connect = false;	//若超时，则认为飞机与平台断开连接，置标志位为false
						continue;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Video
     * @author Riviera
     * 接收视频流
     */
    private class Video extends Thread
    {
    	@Override
    	public void run()
    	{
    		Socket socketVideo = null;
    		try {
    			socketVideo = new Socket(ipAddr,VIDEO_PORT);	//建立TCP连接后ARDrone才会开始传输视频流
				InputStream in = socketVideo.getInputStream();	//打开TCP输入流
				DataOutputStream h264 = new DataOutputStream(new FileOutputStream("test.h264"));
				while(!isInterrupted())
				{
					byte[] PaVE = new byte[76];	//每一帧前有一个报文头，长度为76字节	
					in.read(PaVE);
					int length = (PaVE[8] & 0xff) + (PaVE[9] & 0xff) * 256 + (PaVE[10] & 0xff) * 256 * 256 + (PaVE[11] & 0xff) * 256 * 256 * 256;
														//从PaVE的信息中取得后面一帧的长度
					if(length > 64000 || length < 0)
						continue;						//若获取到错误的长度则抛弃这一帧
					byte[] frame = new byte[length];	//创建一个长度合适的byte数组接收一帧视频
					in.read(frame);						//接收一帧图像
					h264.write(frame, 0, frame.length);	//向视频文件中写入数据
					sleep(34);							//30FPS
				}
				h264.close();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if(socketVideo != null)
					try {
						socketVideo.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
    	}
    }

}
