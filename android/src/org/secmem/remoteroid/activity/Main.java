/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.IRemoteroid;
import org.secmem.remoteroid.R;
import org.secmem.remoteroid.fragment.AuthenticateFragment;
import org.secmem.remoteroid.fragment.ConnectedFragment;
import org.secmem.remoteroid.fragment.DriverInstallationFragment;
import org.secmem.remoteroid.fragment.FragmentActionListener;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.service.RemoteroidService;
import org.secmem.remoteroid.service.RemoteroidService.ServiceState;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Main extends SherlockFragmentActivity implements
		FragmentActionListener {
	
	private static final String TAG = "Main";
	
	private IRemoteroid mRemoteroidSvc;
	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRemoteroidSvc = IRemoteroid.Stub.asInterface(service);
			
			try {
				mRemoteroidSvc.requestFragmentBeShown();
				
		        // Remote connected requested?
		        if(getIntent()!=null){
		        	String action = getIntent().getAction();
		        	if(action!=null && action.equals(RemoteroidIntent.ACTION_REMOTE_CONNECT)){
		        		String serverIp = getIntent().getStringExtra(RemoteroidIntent.EXTRA_IP_ADDESS);
		        		
		        		// Connect to server when client is not connected to server
		        		if(!mRemoteroidSvc.isConnected()){
		        			Log.i(TAG, "Remote-connect requested to "+serverIp);
		        			onConnectRequested(serverIp);
		        		}else{
		        			Log.e(TAG, "Client already connected to server!");
		        		}
		        	}
		        }
		        
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mRemoteroidSvc = null;
		}

	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
		// Typically this method is called when remote-connect message has arrived
		// while user already started Remoteroid application.
		// Turn screen on
		Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
        
    }

    
    public void onStart(){
    	super.onStart();
        bindService(new Intent(this, RemoteroidService.class), conn, Context.BIND_AUTO_CREATE);
        
    }

	@Override
	protected void onResume() {
		super.onResume();
		// Register receiver to get broadcast from service
		IntentFilter filter = new IntentFilter();
	    filter.addAction(RemoteroidIntent.ACTION_SHOW_CONNECT_FRAGMENT);
	    filter.addAction(RemoteroidIntent.ACTION_SHOW_CONNECTED_FRAGMENT);
	    filter.addAction(RemoteroidIntent.ACTION_SHOW_DRIVER_INSTALLATION_FRAGMENT);
	    filter.addAction(RemoteroidIntent.ACTION_CONNECTED);
	    filter.addAction(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED);
	    filter.addAction(RemoteroidIntent.ACTION_CONNECTION_FAILED);
	    filter.addAction(RemoteroidIntent.ACTION_DISCONNECTED);
	    filter.addAction(RemoteroidIntent.ACTION_INTERRUPTED);
	    
	    registerReceiver(serviceConnReceiver, filter);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		unregisterReceiver(serviceConnReceiver);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(mRemoteroidSvc!=null)
			unbindService(conn);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			ServiceState state = ServiceState.valueOf(mRemoteroidSvc.getConnectionStatus());
			switch(state){
			case IDLE:
				ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
				am.killBackgroundProcesses(getPackageName());
				break;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG, "onNetIntent() at Main()");
        // Remote connected requested?
		String serverIp = intent.getStringExtra(RemoteroidIntent.EXTRA_IP_ADDESS);
		try{
			if(mRemoteroidSvc!=null && !mRemoteroidSvc.isConnected()){
				Log.i(TAG, "Remote-connection requested to "+serverIp);
				onConnectRequested(serverIp);
			}else{
				Log.e(TAG, "Client already connected to server!");
			}
		}catch(RemoteException e){
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_preferences:
			startActivityForResult(new Intent(this, NotificationReceiverSettings.class), 0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private BroadcastReceiver serviceConnReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			setSupportProgressBarIndeterminateVisibility(false);
			
			if(RemoteroidIntent.ACTION_CONNECTED.equals(action)
					|| RemoteroidIntent.ACTION_SHOW_CONNECTED_FRAGMENT.equals(action)){
				// Show connected fragment
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, 
						new ConnectedFragment().setListener(Main.this)).commitAllowingStateLoss();
				
			}else if(RemoteroidIntent.ACTION_DEVICE_OPEN_FAILED.equals(action) 
					|| RemoteroidIntent.ACTION_DISCONNECTED.equals(action)
					|| RemoteroidIntent.ACTION_INTERRUPTED.equals(action)
					|| RemoteroidIntent.ACTION_SHOW_CONNECT_FRAGMENT.equals(action)){
				// Show connect fragment
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, 
						new AuthenticateFragment().setListener(Main.this)).commitAllowingStateLoss();
			}else if(RemoteroidIntent.ACTION_CONNECTION_FAILED.equals(action)){
				Toast.makeText(getApplicationContext(), R.string.connection_with_server_has_interrupted, Toast.LENGTH_SHORT).show();
				// Show connect fragment
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, 
						new AuthenticateFragment().setListener(Main.this)).commitAllowingStateLoss();
			}else if(RemoteroidIntent.ACTION_SHOW_DRIVER_INSTALLATION_FRAGMENT.equals(action)){
				// Show driver installation fragment
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, 
						new DriverInstallationFragment().setListener(Main.this)).commitAllowingStateLoss();
			}
        }
        
	};

	@Override
	public void onConnectRequested(String ipAddress) {
		try {
			setSupportProgressBarIndeterminateVisibility(true);
			mRemoteroidSvc.connect(ipAddress);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	
	
	@Override
	public void onDisconnectRequested() {
		try {
			setSupportProgressBarIndeterminateVisibility(true);
			mRemoteroidSvc.disconnect();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDriverInstalled() {
		// Proceed to authenticate fragment
		// Show connect fragment
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.container, 
				new AuthenticateFragment().setListener(Main.this)).commitAllowingStateLoss();
		
	}


	
}