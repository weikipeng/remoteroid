package org.secmem.remoteroid.service;

import org.secmem.remoteroid.activity.RemoteConnectRedirector;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.data.WakeupMessage;
import org.secmem.remoteroid.util.RegisterDeviceToRemoteroidTask;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {
	
	public static final String SENDER_ID = "816046818963";

	@Override
	protected void onError(Context context, String errorId) {
		
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		// A push message has arrived.
		Intent connIntent = new Intent(context, RemoteConnectRedirector.class);
		connIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		connIntent.putExtra(RemoteroidIntent.EXTRA_IP_ADDESS, intent.getStringExtra(WakeupMessage.IP_ADDRESS));
		startActivity(connIntent);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		// Registration completed.
		// Now, we should send registration ID to server.
		new RegisterDeviceToRemoteroidTask(context, registrationId).execute();
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		// Device now unregistered.
		// We should delete this device from server.
		
	}

}
