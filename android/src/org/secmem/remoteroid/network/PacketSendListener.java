package org.secmem.remoteroid.network;

public interface PacketSendListener {
	public void onPacketSent();
	public void onSendFailed();

}
