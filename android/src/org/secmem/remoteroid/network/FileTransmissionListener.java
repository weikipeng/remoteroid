package org.secmem.remoteroid.network;

import java.io.File;
import java.util.ArrayList;

public interface FileTransmissionListener{
	public void onFileInfoReceived(String fileName, long size);
	public void onReadyToSend(ArrayList<File> filesToSend);
	public void onSendFileInfo(File file);
	public void onFileSent(File file);
	public void onFileTransferInterrupted();
	public void onFileTransferSucceeded();
	public void onAllFileTransferSucceeded();
}
