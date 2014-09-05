package org.secmem.remoteroid.network;

public interface ScreenTransmissionListener {
	public void onScreenTransferRequested();
	public void onScreenTransferStopRequested();
	public void onScreenTransferInterrupted();
}
