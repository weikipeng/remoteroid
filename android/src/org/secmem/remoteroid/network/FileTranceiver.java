package org.secmem.remoteroid.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.secmem.remoteroid.BuildConfig;
import org.secmem.remoteroid.data.CommunicateInfo;
import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.util.Log;

public class FileTranceiver extends PacketSender{
	private static final String TAG = "FileTranceiver";
	FileReceiver fileReceiver;
	FileSender fileSender;
	
	public FileTranceiver(OutputStream stream, FileTransmissionListener listener){
		super(stream);
		fileReceiver = new FileReceiver(listener);
		fileSender = new FileSender(listener);
	}
	
	public void receiveFileInfo(Packet packet){
		fileReceiver.receiveFileInfo(packet);
	}
	
	public void receiveFileData(Packet packet){
		fileReceiver.receiveFileData(packet);
	}
	public void closeFile(){
		fileSender.DeleteFileList();
		fileReceiver.closeFile();
	}
	
	public void sendFileList(ArrayList<File> fileList) throws IOException{
		fileSender.setFilesToSend(fileList);
	}
	
	public void sendFileInfo(){
		fileSender.SendFileInfo();
	}
	
	public void sendFileData(){
		fileSender.SendFileData();
	}
	
	public void transferStopRequested(){
		fileSender.transferStopRequested();
	}
	
	public void cancelFile() {
		File file = fileReceiver.getFile();
		if(file !=null && file.exists())
			file.delete();
	}
	
	/**
	 * FileReceiver receive File Information(file name, size) and file data
	 * And store file to SDCARD
	 * @author ssm
	 */
	class FileReceiver{
		private long totalFileSize;
		private long recvFileSize;
		private File file;

		private FileOutputStream out;
		private FileTransmissionListener mListener;
		
		public FileReceiver(FileTransmissionListener listener){
			this.mListener = listener;
		}
		
		/**
		 * Create file that Received information(file name, size)		   
		 * @param packet
		 */
		private void receiveFileInfo(Packet packet){
			FileinfoPacket fileInfo = FileinfoPacket.parse(packet);
			
			String fileName = fileInfo.getFileName();
			totalFileSize = fileInfo.getFileSize();
			
			mListener.onFileInfoReceived(fileName, totalFileSize);
		
			File absoultePathDir = new File(CommunicateInfo.getCurrentPath());
			if(BuildConfig.DEBUG) 
				Log.i(TAG,"dir = "+absoultePathDir.getAbsolutePath());
			if(!absoultePathDir.exists()){
				absoultePathDir.mkdir();
			}
			file = new File(absoultePathDir+"/"+fileName);
			if(BuildConfig.DEBUG)
				Log.i(TAG,"dir2 = "+file.getAbsolutePath());
			int overlapCheck = 1;
			try{			
				while(!file.createNewFile()){
					//Filename duplicate cheack
					String[] list = fileName.split("\\.");
					String newfileName = list[0]+'-'+overlapCheck+"."+list[1];
					file = new File(absoultePathDir+"/"+newfileName);
					overlapCheck++;
				}			
				out = new FileOutputStream(file);			
				recvFileSize = 0;
						
				//Send to host that ready for receive file
				send(new Packet(OpCode.FILEDATA_REQUESTED, null, 0));
			}catch(IOException e){
				file = null;
				e.printStackTrace();							
			}		
		}
		
		/**
		 * Store file to SDCARD that received file data
		 * @param packet
		 */
		private void receiveFileData(Packet packet){
			byte[] data = packet.getPayload();
			int currentRecvLen = packet.getHeader().getPayloadLength();
			try{
				out.write(data, 0, currentRecvLen);
				recvFileSize += currentRecvLen;
				if(totalFileSize <= recvFileSize){
					out.close();
					out = null;
					file = null;
					mListener.onFileTransferSucceeded();
				}
			}catch(IOException e){
				Log.i("debug_state","Filed receiveFileData");
				e.printStackTrace();
				closeFile();		
			}
		}
		
		void closeFile(){
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {Log.i("debug_state","closeFile");}
				out = null;
			}
		}
		
		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}
	}
	
	class FileTransmitStopException extends Exception{
		
	}
	
	/**
	 * FileSender send file information(file name, size) and file data to host
	 * @author ssm
	 */
	class FileSender{
		private final int 		MAXDATASIZE 			= Packet.MAX_LENGTH-PacketHeader.LENGTH;
		private byte [] 		buffer 					= new byte[MAXDATASIZE];			
		private FileInputStream	in 						= null;
		private ArrayList<File>	fileList				= null;	
		private FileTransmissionListener mListener;
		private boolean isTransfer 						= false;
		
		public FileSender(FileTransmissionListener listener){		
			mListener = listener;
		}
		
		/**
		 * Set fileList for transmit and 
		 * Send to host that ready for send file
		 * @param fileList
		 */
		public void setFilesToSend(ArrayList<File> fileList) throws IOException{
			if(isTransfer)
				return;
			
			this.fileList = fileList;
			
			if(!fileList.isEmpty()){
			
				send(new Packet(OpCode.READY_TO_SEND, null, 0));
				mListener.onReadyToSend(fileList);
			}
		}
		
		/**
		 * Send first file information(Name, Size) of FileList to Host
		 * @throws IOException
		 */
		public void SendFileInfo(){		
			if(fileList.isEmpty()){
				try {
					Log.i("qq","FILETRANSFER_COMPLETE");
					send(new Packet(OpCode.FILETRANSFER_COMPLETE, null, 0));
					isTransfer = false;
					mListener.onAllFileTransferSucceeded();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			File currentFile = fileList.get(0);
			FileinfoPacket fileInfoPacket = new FileinfoPacket(currentFile);
			mListener.onSendFileInfo(currentFile);
			try {
				send(fileInfoPacket);
			} catch (IOException e) {
				e.printStackTrace();
				mListener.onFileTransferInterrupted();
			}		
		}
		
		public void transferStopRequested(){
			isTransfer = false;
		}
		
		public void SendFileData(){
			isTransfer = true;
			new SendFileDataThread().start();		
		}
		
		/**
		 *Send to host that first File data of file list
		 */
		class SendFileDataThread extends Thread{
			public void run(){
				try{
					File file = fileList.remove(0);
					long fileSize = file.length();
					long sentFileSize = 0;
					
					in = new FileInputStream(file);			
					
					while(fileSize > sentFileSize){					
						if(isTransfer == false){
							Log.i("qq","CANCEL FILETRANSFER_COMPLETE");
							send(new Packet(OpCode.FILETRANSFER_COMPLETE, null, 0));
							mListener.onAllFileTransferSucceeded();
							throw new FileTransmitStopException();												
						}							
						
						int iCurrentSendSize =
								(int) ((fileSize - sentFileSize) > MAXDATASIZE ? MAXDATASIZE : (fileSize - sentFileSize));
						in.read(buffer, 0, iCurrentSendSize);	
						
						send(new Packet(OpCode.FILEDATA_RECEIVED, buffer, iCurrentSendSize));
						
						sentFileSize += iCurrentSendSize;						
					}
					mListener.onFileSent(file);
					
					SendFileInfo();
				}catch(FileTransmitStopException e){			
			
				}catch(FileNotFoundException e){
					e.printStackTrace();
					mListener.onFileTransferInterrupted();
				}catch(IOException e){					
					e.printStackTrace();
					mListener.onFileTransferInterrupted();
				}finally{
					if(in != null)
					{
						try{
							in.close();				
						}catch(IOException e){};
						in = null;
					}
				}		
			}
		}
		
		public void DeleteFileList(){
			if(fileList==null)
				return;
			fileList.clear();		
		}
	}	
}
