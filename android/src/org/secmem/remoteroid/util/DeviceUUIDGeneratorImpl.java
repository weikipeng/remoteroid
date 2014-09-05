package org.secmem.remoteroid.util;

import org.secmem.remoteroid.BuildConfig;
import org.secmem.remoteroid.lib.util.DeviceUUIDGenerator;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceUUIDGeneratorImpl extends DeviceUUIDGenerator {
	private static final String TAG = "DeviceUUIDGeneratorImpl";
	private Context context;
	
	public DeviceUUIDGeneratorImpl(Context context){
		this.context = context;
	}

	@Override
	public String generate() {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tm.getDeviceId();
		if(uuid==null){
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			String mac = wifiManager.getConnectionInfo().getMacAddress();
			if(mac==null)
				throw new IllegalStateException();
			if(BuildConfig.DEBUG)
				Log.d(TAG, "Generating UUID with Wifi MAC : "+mac);
			return mac;
		}
		if(BuildConfig.DEBUG)
			Log.d(TAG, "Generating UUID with IMEI/ESN : "+uuid);
		return uuid;
	}

}
