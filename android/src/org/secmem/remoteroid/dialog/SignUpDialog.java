package org.secmem.remoteroid.dialog;

import java.io.IOException;
import java.net.MalformedURLException;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.data.Device;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.util.HongUtil;
import org.secmem.remoteroid.util.Pref;
import org.secmem.remoteroid.web.RemoteroidWeb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SignUpDialog {
	
	static ProgressDialog mProgress;
	
	public static void ShowDialog(final Context context) {
		final LinearLayout linear = (LinearLayout) View.inflate(context, R.layout.dialog_sign_up, null);
		new AlertDialog.Builder(context).setTitle(context.getString(R.string.dialog_sign_up_title)).setIcon(R.drawable.ic_launcher).setView(linear)
				.setPositiveButton("Account", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText edtEmail = (EditText) linear.findViewById(R.id.dialog_sign_up_edt_email);
						EditText edtPw = (EditText) linear.findViewById(R.id.dialog_sign_up_edt_pw);

						if (edtEmail.getText().length() == 0 || edtPw.getText().length() == 0) {
							if (edtEmail.getText().length() == 0)
								HongUtil.makeToast(context, context.getString(R.string.dialog_sign_up_input_email));
							else
								HongUtil.makeToast(context, context.getString(R.string.dialog_sign_up_input_pwd));
							ShowDialog(context);
						} else {
							new SignUpAsync(context).execute(edtEmail.getText().toString(), edtPw.getText().toString());
						}

					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	public static class SignUpAsync extends AsyncTask<String, Void, Integer> {
		
		private Context context;
		
		public SignUpAsync(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgress = new ProgressDialog(this.context);
			mProgress.setTitle("Loading...");
			mProgress.setMessage("Sign Up.......");
			mProgress.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			String email = params[0];
			String pw = params[1];
			Response response = null;
			try {
				response = RemoteroidWeb.addAccount(email, pw).sendRequest();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null && response.isSucceed()) {
				Account account = response.getPayloadAsAccount();
				Pref.setMyPreferences(Pref.Account.EMAIL, account.getEmail(), context);
				Pref.setMyPreferences(Pref.Account.PASSWORD, pw, context);
				Pref.setMyPreferences(Pref.Account.SECURITY_PASSWORD, account.getPassword(), context);
			}

			return (response != null && response.isSucceed()) ? Codes.Result.OK : response.getErrorCode();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			mProgress.dismiss();
			if (result == Codes.Result.OK) {
				// HongUtil.makeToast(context, "Success.");
				new AddDeviceAsync(context).execute();
			} else {
				if (result == Codes.Error.Account.DUPLICATE_EMAIL) {
					HongUtil.makeToast(context, "email is duplicate");
				} else if (result == Codes.Error.Account.AUTH_FAILED) {
					HongUtil.makeToast(context, "Auth failed..");
				}
				ShowDialog(this.context);
			}

		}
	}

	public static class AddDeviceAsync extends AsyncTask<String, Void, Integer> {
		
		private Context context;
		
		public AddDeviceAsync(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			SystemClock.sleep(300);
			mProgress.setTitle("Loading...");
			mProgress.setMessage("Setting Device Info.......");
			mProgress.show();
		}

		@Override
		protected Integer doInBackground(String... params) {

			Response response = null;
			String email = Pref.getMyPreferences(Pref.Account.EMAIL, context);
			String pwd = Pref.getMyPreferences(Pref.Account.SECURITY_PASSWORD, context);
			String reg = Pref.getMyPreferences(Pref.GCM.KEY_GCM_REGISTRATION, context);
			String uuid = HongUtil.getDeviceId(this.context);

			try {
				response = RemoteroidWeb.addDevice(Build.MODEL, email, pwd, reg, uuid).sendRequest();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (response != null && response.isSucceed()) {
				Device device= response.getPayloadAsDevice();
				Pref.setMyPreferences(Pref.Device.UUID, device.getUUID(), context);
			}

			return (response != null && response.isSucceed()) ? Codes.Result.OK : response.getErrorCode();
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			mProgress.dismiss();
			if (result == Codes.Result.OK) {
				Pref.setMyPreferences(Pref.Authentication.IS_ADD_DEVICE, true, context);
				HongUtil.makeToast(context, "Success.");
			} else {
				if (result == Codes.Error.Device.DUPLICATE_NAME) {
					HongUtil.makeToast(context, "Email is duplicate");
				} else if (result == Codes.Error.Device.DEVICE_NOT_FOUND) {
					HongUtil.makeToast(context, "Device not found");
				}
			}
		}

	}
}
