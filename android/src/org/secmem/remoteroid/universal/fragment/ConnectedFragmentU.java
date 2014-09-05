package org.secmem.remoteroid.universal.fragment;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.listener.ConnectedFragmentListenerU;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ConnectedFragmentU extends InterfaceFragment<ConnectedFragmentListenerU> {
	private Button btnDisconnect;
	
	public ConnectedFragmentU(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_connected, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		btnDisconnect = (Button)view.findViewById(R.id.btn_fragment_connected_disconnect);
		btnDisconnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getListener().onDisconnectRequested();
			}
			
		});
	}
	
	
}
