package org.secmem.remoteroid.receiver;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMBroadcastReceiverImpl extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		// Override this method to use GCMIntentService on .service subpackage
		return context.getPackageName()+".service.GCMIntentService";
	}

}
