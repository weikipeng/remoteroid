package org.secmem.remoteroid.universal.fragment;

import java.util.regex.Pattern;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.listener.ConnectFragmentListenerU;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectFragmentU extends InterfaceFragment<ConnectFragmentListenerU> {
	
	EditText edtIpAddress;
	Button btnConnect;
	
	public ConnectFragmentU(){
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_authenticate, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		edtIpAddress = (EditText)view.findViewById(R.id.edt_fragment_authenticate_ip_address);
		btnConnect = (Button)view.findViewById(R.id.btn_fragment_authenticate_connect);
		
		edtIpAddress.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if(Pattern.matches("^(([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])\\.){3}([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])$", s)){
					btnConnect.setEnabled(true);
				}else{
					btnConnect.setEnabled(false);
				}
			}
			
		});
		edtIpAddress.setText("192.168.0.26");
		
		btnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				btnConnect.setText(getText(R.string.connecting));
				btnConnect.setEnabled(false);
				edtIpAddress.setEnabled(false);
				getListener().onConnectionRequested(edtIpAddress.getText().toString());
			}
			
		});
	}
	
	public void resetComponentState(){
		getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run(){
				btnConnect.setEnabled(true);
				btnConnect.setText(getText(R.string.connect));
				edtIpAddress.setEnabled(true);
			}
		});
	}
	
}
