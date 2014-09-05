package org.secmem.remoteroid.network;

import java.io.File;
import java.text.*;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

public class FileinfoPacket extends Packet{
	
	public static final int MAX_FILENAME_LENGTH = 100;
	public static final int MAX_FILESIZE_LENGTH = 100;
	private static final int MAX_LENGTH = MAX_FILENAME_LENGTH+MAX_FILESIZE_LENGTH;
	
	private String fileName;
	private long fileSize;
	
	protected FileinfoPacket(){
	}
	
	/**
	 * Generates file info packet.
	 * @param file File to send with
	 */
	public FileinfoPacket(File file){
		if(file==null)
			throw new IllegalStateException();
		
		byte[] payload = new byte[MAX_LENGTH];
		
		byte[] fileName = file.getName().getBytes();
		byte[] fileSize = String.valueOf(file.length()).getBytes();
		
		System.arraycopy(fileName, 0, payload, 0, fileName.length);
		System.arraycopy(fileSize, 0, payload, MAX_FILENAME_LENGTH, fileSize.length);
		
		setHeader(new PacketHeader(OpCode.FILEINFO_RECEIVED, payload.length));
		setPayload(payload);
	}
	
	/**
	 * Parse fileinfo from packet
	 * @param packet
	 * @return
	 * @throws ParseException
	 */
	public static FileinfoPacket parse(Packet packet){
		FileinfoPacket fileInfo = new FileinfoPacket();
		byte[] fileName = new byte[MAX_FILENAME_LENGTH];
		byte[] fileSize = new byte[MAX_FILESIZE_LENGTH];
		byte[] payload = packet.getPayload();
		System.arraycopy(payload, 0, fileName, 0, MAX_FILENAME_LENGTH);
		System.arraycopy(payload, MAX_FILENAME_LENGTH, fileSize, 0, MAX_FILESIZE_LENGTH);
		fileInfo.setFileName(new String(fileName).trim());		
		fileInfo.setFileSize(Long.parseLong(new String(fileSize).trim()));
		
		return fileInfo;
	}
	
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	public void setFileSize(long fileSize){
		this.fileSize = fileSize;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public long getFileSize(){
		return fileSize;
	}
	
}
