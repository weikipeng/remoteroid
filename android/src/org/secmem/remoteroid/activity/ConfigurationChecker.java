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

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ConfigurationChecker extends SherlockActivity {
	
	private Button btnEnableAccService;
	private boolean isAccEnabled;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration_checker);
		
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_red));
		
		btnEnableAccService = (Button)findViewById(R.id.btn_acc_service);
		
		btnEnableAccService.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Util.Services.launchAccessibilitySettings(ConfigurationChecker.this);
				
			}
			
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		isAccEnabled = Util.Services.isAccessibilityServiceEnabled(this);
		
		if(isAccEnabled){
			btnEnableAccService.setEnabled(false);
			btnEnableAccService.setText(R.string.enabled);
		}		
		invalidateOptionsMenu(); // Update 'Done' menu availability
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.configuration_checker, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.getItem(0).setEnabled(isAccEnabled);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.proceed:
			finish();
			startActivity(new Intent(this, Main.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
