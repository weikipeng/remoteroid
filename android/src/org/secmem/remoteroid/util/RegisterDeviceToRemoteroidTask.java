package org.secmem.remoteroid.util;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Response;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class RegisterDeviceToRemoteroidTask extends
		AsyncTask<Void, Void, Response> {
	private Context context;
	private Device device;
	
	public RegisterDeviceToRemoteroidTask(Context context, String gcmRegistrationId){
		this.context = context;
		Account account = Util.Connection.getUserAccount(context);
		device = new Device();
		device.setOwnerAccount(account);
		device.setNickname(Util.Connection.getDeviceNickname(context));
		device.setDeviceUUID(new DeviceUUIDGeneratorImpl(context));
		device.setRegistrationKey(gcmRegistrationId);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		Notification notification = new Notification();
		PendingIntent intent = PendingIntent.getActivity(context, 0, new Intent()/* Null intent*/, 0);
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = context.getString(R.string.registering_device);
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(context, context.getString(R.string.app_name), context.getString(R.string.registering_device), intent);
		
		NotificationManager notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.notify(0, notification);
	}

	@Override
	protected Response doInBackground(Void... params) {
		Request request = Request.Builder.setRequest(API.Device.ADD_DEVICE).setPayload(device).build();
		return request.sendRequest();
	}

	@Override
	protected void onPostExecute(Response result) {
		super.onPostExecute(result);
		NotificationManager notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifManager.cancelAll();
		
		if(result.isSucceed()){
			Toast.makeText(context, R.string.device_registered, Toast.LENGTH_SHORT).show();
			context.sendBroadcast(new Intent(RemoteroidIntent.ACTION_DEVICE_REGISTRATION_COMPLETE));
		}else{
			Toast.makeText(context, R.string.cannot_register_device, Toast.LENGTH_SHORT).show();
			context.sendBroadcast(new Intent(RemoteroidIntent.ACTION_DEVICE_REGISTRATION_FAILED));
		}
		
		
	}

}
