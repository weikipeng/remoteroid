package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.util.Util;
import org.secmem.remoteroid.util.Util.Connection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RemoteConnectRedirector extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        intent.setAction(RemoteroidIntent.ACTION_REMOTE_CONNECT);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(RemoteroidIntent.EXTRA_IP_ADDESS, getIntent().getStringExtra(RemoteroidIntent.EXTRA_IP_ADDESS));
        
        if(Util.Connection.getServerType(getApplicationContext()).equals(Connection.SERVER_UNIVERSAL)){
            intent.addCategory(RemoteroidIntent.CATEGORY_UNIVERSAL);
        }else{
        	intent.addCategory(Intent.CATEGORY_DEFAULT);
        }
        
        finish();
        startActivity(intent);
	}

}
