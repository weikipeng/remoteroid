package org.secmem.remoteroid;
import java.util.List;

interface IRemoteroid{
	String getConnectionStatus();
	boolean isConnected();
	void connect(String ipAddress);
	void disconnect();
	void onNotificationCatched(String notificationText, long when,int type);
	void onSendFile(in List<String> pathlist);
	
	void requestFragmentBeShown();
}