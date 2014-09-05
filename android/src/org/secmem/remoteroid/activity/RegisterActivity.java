package org.secmem.remoteroid.activity;

import org.secmem.remoteroid.R;
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
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class RegisterActivity extends SherlockActivity {

	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtVerifyPassword;
	
	private TextWatcher watcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable editor) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			invalidateOptionsMenu();
		}
		
	};
	
	private boolean isInputValid(){
		if(edtEmail.length()==0){
			return false;
		}
		
		if(edtPassword.length()==0 || edtVerifyPassword.length()==0){
			return false;
		}
		
		if(!edtPassword.getText().toString().equals(edtVerifyPassword.getText().toString())){
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_register);
	    
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bg_red));
	    
	    edtEmail = (EditText)findViewById(R.id.activity_register_email);
	    edtPassword = (EditText)findViewById(R.id.activity_register_password);
	    edtVerifyPassword = (EditText)findViewById(R.id.activity_register_verify_password);
	    
	    edtEmail.addTextChangedListener(watcher);
	    edtPassword.addTextChangedListener(watcher);
	    edtVerifyPassword.addTextChangedListener(watcher);
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.register, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(isInputValid()){
			menu.getItem(0).setEnabled(true);
		}else{
			menu.getItem(0).setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case android.R.id.home:
			finish();
			return true;
			
		case R.id.register:
			new RegisterTask(this).execute(edtEmail.getText().toString(), edtPassword.getText().toString());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class RegisterTask extends DialogAsyncTask<String, Void, Response>{

		public RegisterTask(Activity context) {
			super(context);
		}

		@Override
		protected Response doInBackground(String... args) {
		
			Account account = new Account();
			account.setEmail(args[0]);
			account.setPassword(args[1]);
			
			Request request = Request.Builder.setRequest(API.Account.ADD_ACCOUNT).setPayload(account).build();
			return request.sendRequest();
		}

		@Override
		protected void onPostExecute(Response result) {
			super.onPostExecute(result);
			if(result.isSucceed()){
				// Extract secured password from response
				Account account = result.getPayloadAsAccount();
				Util.Connection.saveAuthData(getApplicationContext(), account.getEmail(), account.getPassword());
				Toast.makeText(getApplicationContext(), R.string.account_has_been_set, Toast.LENGTH_SHORT).show();
				String action = getIntent().getAction();
				if(action==null){
					// if this activity has called with explicit intent,
					// finish this activity and navigate to main
					finish();
					startActivity(new Intent(RegisterActivity.this, Main.class));
				}else{
					// if this activity has called with implicit intent,
					// finish just finish this activity.
					finish();
				}
			}else{
				switch(result.getErrorCode()){
				case Codes.Error.Account.DUPLICATE_EMAIL:
					Toast.makeText(getApplicationContext(), R.string.user_e_mail_duplicates, Toast.LENGTH_SHORT).show();
					break;
				case Codes.Error.Account.NOT_VALID_EMAIL:
					Toast.makeText(getApplicationContext(), R.string.e_mail_is_not_valid, Toast.LENGTH_SHORT).show();
					break;
					
				default:
					Toast.makeText(getApplicationContext(), R.string.error_occurred_please_try_again, Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
		
		
		
	}
	

}
