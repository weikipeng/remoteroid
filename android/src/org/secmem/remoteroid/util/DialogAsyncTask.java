package org.secmem.remoteroid.util;

import org.secmem.remoteroid.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

public abstract class DialogAsyncTask<T,U,V> extends AsyncTask<T,U,V> {
	private Activity activity;
	private ProgressDialog dialog;
	private String progressMessage;
	private boolean finishOnCancel = false;
	
	public DialogAsyncTask(Activity context){
		this.activity = context;
		this.progressMessage = (String)context.getText(R.string.please_wait);
	}
	
	public void setDialogMessage(String message){
		this.progressMessage = message;
	}
	
	public DialogAsyncTask<T, U, V> setFinishOnCancel(boolean cancel){
		this.finishOnCancel = cancel;
		return this;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(progressMessage!=null){
			dialog = new ProgressDialog(activity);
			dialog.setMessage(progressMessage);
			dialog.setIndeterminate(true);
			if(finishOnCancel){
				dialog.setOnCancelListener(new OnCancelListener(){
	
					@Override
					public void onCancel(DialogInterface dialog) {
						activity.finish();
					}
					
				});
			}
			dialog.show();
		}
	}

	@Override
	protected void onPostExecute(V result) {
		super.onPostExecute(result);
		if(progressMessage!=null){
			dialog.dismiss();
		}
	}

	

	

}
