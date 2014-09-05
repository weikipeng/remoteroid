package org.secmem.remoteroid.universal.service;

interface IRemoteroidU{
	void requestBroadcastConnectionState();
	boolean isCommandConnected();
	boolean isScreenConnected();
	void connectCommand(in String ipAddress);
	void connectScreen(in String ipAddress);
	void disconnect();
	void disconnectScreen();
	void onNotification(in int notificationType, in String[] args);
}