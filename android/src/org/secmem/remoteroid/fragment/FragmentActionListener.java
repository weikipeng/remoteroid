package org.secmem.remoteroid.fragment;

public interface FragmentActionListener {
	public void onConnectRequested(String ipAddress);
	public void onDisconnectRequested();
	public void onDriverInstalled();
}
