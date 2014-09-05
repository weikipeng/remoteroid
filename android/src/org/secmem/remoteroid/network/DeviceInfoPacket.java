package org.secmem.remoteroid.network;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.util.*;

public class DeviceInfoPacket extends Packet{	
	
	private int widthResolution;
	private int heightResolution;
	
	public DeviceInfoPacket(DisplayMetrics dm){
		widthResolution = dm.widthPixels;
		heightResolution = dm.heightPixels;
		
		byte[] payload = String.format("%4d%4d", widthResolution, heightResolution).getBytes(); 
		setHeader(new PacketHeader(OpCode.DEVICEINFO_SEND, payload.length));
		setPayload(payload);		
	}
	
	public int getWidthResolution() {
		return widthResolution;
	}
	public void setWidthResolution(int widthResolution) {
		this.widthResolution = widthResolution;
	}
	public int getHeightResolution() {
		return heightResolution;
	}
	public void setHeightResolution(int heightResolution) {
		this.heightResolution = heightResolution;
	}	
}
