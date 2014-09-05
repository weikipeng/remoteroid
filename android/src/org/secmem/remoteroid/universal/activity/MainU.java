package org.secmem.remoteroid.universal.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.activity.NotificationReceiverSettings;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.universal.fragment.ConnectFragmentU;
import org.secmem.remoteroid.universal.fragment.ConnectedFragmentU;
import org.secmem.remoteroid.universal.listener.ConnectFragmentListenerU;
import org.secmem.remoteroid.universal.listener.ConnectedFragmentListenerU;
import org.secmem.remoteroid.universal.service.IRemoteroidU;
import org.secmem.remoteroid.universal.service.RemoteroidServiceU;
import org.secmem.remoteroid.util.Util;

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

public class MainU extends SherlockFragmentActivity implements ConnectFragmentListenerU, ConnectedFragmentListenerU{
	private static final String TAG = "MainU";
	
	private IntentFilter broadcastFilter;
	private IRemoteroidU remoteroidSvc;
	private ServiceConnection connection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			remoteroidSvc = IRemoteroidU.Stub.asInterface(service);
			try{
				remoteroidSvc.requestBroadcastConnectionState();
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			remoteroidSvc = null;
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Add this LayoutParams to wake up application from Push message
		// while device is in sleep mode
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | 
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    // Set Action bar color
	    getSupportActionBar().setBackgroundDrawable(
	    		getResources().getDrawable(R.drawable.bg_red));
	    
	    
	    // Prepare Intent filter
	    broadcastFilter = new IntentFilter();
	    broadcastFilter.addAction(RemoteroidIntent.ACTION_CONNECTED);
	    broadcastFilter.addAction(RemoteroidIntent.ACTION_CONNECTION_FAILED);
	    broadcastFilter.addAction(RemoteroidIntent.ACTION_DISCONNECTED);
	}


	@Override
	protected void onStart(){
		super.onStart();
		
		if(!Util.Services.isServiceAliveU(getApplicationContext())){
			Util.Services.startRemoteroidServiceU(getApplicationContext());
		}
		// Register a BroadcastReceiver to receive 
		// broadcast message regarding connection status
		registerReceiver(connectionStateReceiver, broadcastFilter);
		bindService(new Intent(this, RemoteroidServiceU.class), 
					connection, Context.BIND_AUTO_CREATE);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		// Unregister BroadcastReceiver
		unregisterReceiver(connectionStateReceiver);
		unbindService(connection);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// Typically this method is called when remote-connect message has arrived
		// while user already started Remoteroid application.
		String serverIp = intent.getStringExtra(RemoteroidIntent.EXTRA_IP_ADDESS);
		if(remoteroidSvc!=null){
			try{
				if(!remoteroidSvc.isCommandConnected()){
					setSupportProgressBarIndeterminateVisibility(true);
					remoteroidSvc.connectCommand(serverIp);
				}else{
					Log.e(TAG, "Cannot make a connection while client is connected to server!");
				}
			}catch(RemoteException e){
				e.printStackTrace();
			}
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
			startActivity(new Intent(this, NotificationReceiverSettings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private BroadcastReceiver connectionStateReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			setSupportProgressBarIndeterminateVisibility(false);
			
			String action = intent.getAction();
			if(action.equals(RemoteroidIntent.ACTION_CONNECTED)){
				// The client has connected to server.
				// Need to replace ConnectFragmentU to ConnectedFragmentU
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, 
							new ConnectedFragmentU().setListener(MainU.this)).commitAllowingStateLoss();
				
			}else if(action.equals(RemoteroidIntent.ACTION_CONNECTION_FAILED)){
				// The client failed to connect server.
				// ConnectFragmentU should revert its component's state to
				// original state which makes user retry its connection.
				//connectFragment.resetComponentState();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, 
						new ConnectFragmentU().setListener(MainU.this)).commitAllowingStateLoss();
			
				// Show a message to user that connection has failed
				Toast.makeText(getApplicationContext(), 
						R.string.connection_with_server_has_interrupted, Toast.LENGTH_SHORT).show();
				
			}else if(action.equals(RemoteroidIntent.ACTION_DISCONNECTED)){
				// The client has been disconnected from server.
				// Current fragment needs to be replaced to ConnectFragment
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.container, 
							new ConnectFragmentU().setListener(MainU.this)).commitAllowingStateLoss();
			}
		}
	};
	
	/* From interface ConnectFragmentListenerU */
	@Override
	public void onConnectionRequested(String ipAddress) {
		// Cross-check that the service is ready to use or not
		if(remoteroidSvc!=null){
			try{
				setSupportProgressBarIndeterminateVisibility(true);
				remoteroidSvc.connectCommand(ipAddress);
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}
	}

	/* From interface ConnectedFragmentListenerU */
	@Override
	public void onDisconnectRequested() {
		// Cross-check that the service is ready to use or not
		if(remoteroidSvc!=null){
			try{
				setSupportProgressBarIndeterminateVisibility(true);
				remoteroidSvc.disconnect();
			}catch(RemoteException e){
				e.printStackTrace();
			}
		}
	}
}
