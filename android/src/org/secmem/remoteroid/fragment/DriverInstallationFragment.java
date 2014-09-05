package org.secmem.remoteroid.fragment;

import java.io.IOException;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.universal.fragment.InterfaceFragment;
import org.secmem.remoteroid.util.CommandLine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DriverInstallationFragment extends InterfaceFragment<FragmentActionListener> {

	private TextView tvMsg;
	private ProgressBar prgProgress;
	private Button btnConfirm;
	private boolean installCompleted = false;
	
	public DriverInstallationFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_driver_installation, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		tvMsg = (TextView)view.findViewById(R.id.driver_installation_msg);
		prgProgress = (ProgressBar)view.findViewById(R.id.driver_installation_progress);
		btnConfirm = (Button)view.findViewById(R.id.driver_installation_confirm);
		
		btnConfirm.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!installCompleted){
					// Install Driver
					new InstallDriverTask().execute();
				}else{
					getListener().onDriverInstalled();
				}
				
			}
			
		});
	}
	
	class InstallDriverTask extends AsyncTask<Void, Void, Integer>{

		private static final int ERR_SECURITY = -1;
		private static final int ERR_IO = -2;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Disable button, change button text, then show progressbar
			btnConfirm.setEnabled(false);
			btnConfirm.setText(R.string.installing);
			prgProgress.setVisibility(View.VISIBLE);
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				CommandLine.copyInputDrivers(getActivity());
				installCompleted = true;
			} catch (SecurityException e) {
				e.printStackTrace();
				return ERR_SECURITY;
				
			} catch (IOException e) {
				e.printStackTrace();
				return ERR_IO;
			}
			return 0;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			switch(result){
			case ERR_SECURITY:
				// Root access denied
				tvMsg.setText(R.string.failed_to_get_root_permission_check_out_whether_device_has_rooted_or_not);
				btnConfirm.setText(R.string.retry);
				break;
			case ERR_IO:
				tvMsg.setText(R.string.failed_to_copy_driver_files);
				btnConfirm.setText(R.string.retry);
				break;
			default: // Driver installation succeed
				tvMsg.setText(R.string.driver_installation_complete);
				btnConfirm.setText(android.R.string.ok);
				break;
			}	
			prgProgress.setVisibility(View.GONE);
			btnConfirm.setEnabled(true);
		}
	}
	
}
