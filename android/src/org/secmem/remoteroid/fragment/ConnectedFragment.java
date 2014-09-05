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

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.fragment.InterfaceFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConnectedFragment extends InterfaceFragment<FragmentActionListener> {
	
	private TextView tvMessage;
	private ProgressBar prgDisconnectPrg;
	private Button btnDisconnect;
	private ImageView ivCircuitBoard;
	
	private boolean isDisconnectRequested = false;

	public ConnectedFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_connected, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tvMessage = (TextView)view.findViewById(R.id.fragment_connected_msg);
		prgDisconnectPrg = (ProgressBar)view.findViewById(R.id.fragment_connected_progress);
		btnDisconnect = (Button)view.findViewById(R.id.btn_fragment_connected_disconnect);
		ivCircuitBoard = (ImageView)view.findViewById(R.id.circuit_board);
		
		if(savedInstanceState!=null){
			isDisconnectRequested = savedInstanceState.getBoolean("disconnect_requested");
		}
		
		if(isDisconnectRequested){
			ivCircuitBoard.setVisibility(View.INVISIBLE);
			prgDisconnectPrg.setVisibility(View.VISIBLE);
			tvMessage.setText(R.string.disconnecting);
			btnDisconnect.setEnabled(false);
		}else{
			ivCircuitBoard.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.blink_short));
		}
		
		btnDisconnect.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				isDisconnectRequested = false;
				ivCircuitBoard.setVisibility(View.INVISIBLE);
				prgDisconnectPrg.setVisibility(View.VISIBLE);
				tvMessage.setText(R.string.disconnecting);
				getListener().onDisconnectRequested();
			}
			
		});
	}

}
