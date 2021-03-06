package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.intent.RemoteroidIntent;
import org.secmem.remoteroid.lib.api.API;
import org.secmem.remoteroid.lib.api.Codes;
import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.lib.request.Request;
import org.secmem.remoteroid.lib.request.Response;
import org.secmem.remoteroid.util.DialogAsyncTask;
import org.secmem.remoteroid.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class LoginActivity extends SherlockActivity {
	private EditText edtEmail;
	private EditText edtPassword;
	
	private Button btnLogin;
	private TextWatcher watcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(edtEmail.length()!=0 && edtPassword.length()!=0){
				btnLogin.setEnabled(true);
			}else{
				btnLogin.setEnabled(false);
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
		
		edtEmail = (EditText)findViewById(R.id.activity_login_email);
		edtPassword = (EditText)findViewById(R.id.activity_login_password);
		
		edtEmail.addTextChangedListener(watcher);
		edtPassword.addTextChangedListener(watcher);
		
		btnLogin = (Button)findViewById(R.id.activity_login_register);
		btnLogin.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new LoginTask(LoginActivity.this).execute(edtEmail.getText().toString(), edtPassword.getText().toString());
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.register, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		case R.id.register:
			startActivity(new Intent(RemoteroidIntent.ACTION_REGISTER));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class LoginTask extends DialogAsyncTask<String, Void, Response>{

		public LoginTask(Activity context) {
			super(context);
		}

		@Override
		protected Response doInBackground(String... params) {
			Account account = new Account();
			account.setEmail(params[0]);
			account.setPassword(params[1]);
			
			Request request = Request.Builder.setRequest(API.Account.LOGIN).setPayload(account).build();
			return request.sendRequest();
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if(result.isSucceed()){
				// Extract secured password from payload
				Account account = result.getPayloadAsAccount();
				Util.Connection.saveAuthData(getApplicationContext(), account.getEmail(), account.getPassword());
				
				Toast.makeText(getApplicationContext(), R.string.account_has_been_set, Toast.LENGTH_SHORT).show();
				String action = getIntent().getAction();
				if(action==null){
					// if this activity has called with explicit intent,
					// finish this activity and navigate to main
					finish();
					startActivity(new Intent(LoginActivity.this, Main.class));
				}else{
					// if this activity has called with implicit intent,
					// finish just finish this activity.
					finish();
				}
			}else{
				switch(result.getErrorCode()){
				case Codes.Error.Account.AUTH_FAILED:
					Toast.makeText(getApplicationContext(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(getApplicationContext(), R.string.error_occurred_please_try_again, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
		
	}

}
