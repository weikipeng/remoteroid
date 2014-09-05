package org.secmem.remoteroid.network;

public interface PacketListener {
	public void onPacketReceived(Packet packet);
	public void onInterrupt();
}
