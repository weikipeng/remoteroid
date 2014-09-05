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

package org.secmem.remoteroid.fragment;

import java.util.regex.Pattern;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.fragment.InterfaceFragment;
import org.secmem.remoteroid.util.Util;
import org.secmem.remoteroid.util.Util.Connection;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AuthenticateFragment extends InterfaceFragment<FragmentActionListener> {
	
	private EditText mEdtIpAddr;
	private Button mBtnConnect;

	private boolean isIpValid=false;
	
	public AuthenticateFragment(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_authenticate, null);
		
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mEdtIpAddr = (EditText)view.findViewById(R.id.edt_fragment_authenticate_ip_address);
		mBtnConnect = (Button)view.findViewById(R.id.btn_fragment_authenticate_connect);
		
		mEdtIpAddr.addTextChangedListener(new TextWatcher(){

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
				if(Pattern.matches("^(([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])\\.){3}([2][5][0-5]|[2][0-4][0-9]|[0-1][0-9][0-9]|[0-9][0-9]|[0-9])$", s))
					isIpValid = true;
				else
					isIpValid = false;
				mBtnConnect.setEnabled(isIpValid);
			}
			
		});
		
		// Load last server ip address from preferences
		mEdtIpAddr.setText(Connection.getLastServerIpAddress(getActivity()));
		
		mBtnConnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Util.Connection.setLastServerIpAddress(getActivity(), mEdtIpAddr.getText().toString());
				getListener().onConnectRequested(mEdtIpAddr.getText().toString());
				mBtnConnect.setEnabled(false);
				mBtnConnect.setText(R.string.connecting);
			}
		});

		
	}

}
