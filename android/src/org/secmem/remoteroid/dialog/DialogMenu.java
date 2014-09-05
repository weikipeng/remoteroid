package org.secmem.remoteroid.dialog;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.adapter.CategoryAdapter;
import org.secmem.remoteroid.database.CategoryDatabase;
import org.secmem.remoteroid.util.HongUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DialogMenu {
	
	private static ArrayList<String> categoryList = new ArrayList<String>();
	private static CategoryDatabase database;
	private static CategoryAdapter adapter;
	
	public static void categoryDialog(final Context context, final DialogListener listener){
		
		
		final LinearLayout linear = (LinearLayout)View.inflate(context, R.layout.dialog_category2, null);
		
		ListView listview = (ListView)linear.findViewById(R.id.dialog_category2_list);
		
		categoryList = getCategory(context);
		adapter = new CategoryAdapter(context, categoryList, R.layout.list_category);
//		listview.setAdapter(adapter);
		
		new AlertDialog.Builder(context)
		.setTitle(context.getString(R.string.dialog_category_title))
		.setIcon(R.drawable.ic_launcher)
		.setView(linear)
		.setAdapter(adapter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String result = adapter.getItem(which);
				listener.onSearchCategory(getType(which), result);
				dialog.cancel();
				
			}
		})
		.setPositiveButton("add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				categoryDialog(context, listener);
				showInputDialog(context);
			}
		})
		.setNegativeButton("close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.show();
	}

	private static void showInputDialog(final Context context) {
		AlertDialog.Builder builder;
		final AlertDialog dialog;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_input_category, null);
		final EditText edt = (EditText)layout.findViewById(R.id.dialog_input_edt_in);
		
		edt.setFilters(new InputFilter[]{HongUtil.filterAlpha});
		
		Button okBtn = (Button)layout.findViewById(R.id.dialog_input_btn_ok);
		Button cancelBtn = (Button)layout.findViewById(R.id.dialog_input_btn_cancel);
		
		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		builder.setTitle("Input your custom category.");
		builder.setIcon(R.drawable.ic_launcher);
		dialog = builder.create();
//		dialog.setTitle(getString(R.string.category_dialog_title));
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(edt.getText().toString().length()==0){
					HongUtil.makeToast(context, context.getString(R.string.dialog_input_toast));
				}
				else if(!isDuplicated(edt.getText().toString())){
					HongUtil.makeToast(context, context.getString(R.string.index_is_already_registered_));
				}
				else{
					addData(edt.getText().toString());
					categoryList = getCategory(context);
					adapter.setCategoryList(categoryList);
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
				
			}

		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
				
			}
		});
		
		dialog.show();
	}
	
private static ArrayList<String> getCategory(Context context) {
		
		database = new CategoryDatabase(context);
		ArrayList<String> category = new ArrayList<String>();
		String []categoryList = context.getResources().getStringArray(R.array.category_dialog_list);
		for(int i = 0 ; i < categoryList.length ; i++){
			category.add(categoryList[i]);
		}
		
		database.open();
		category.addAll(database.getIndex());
		database.close();
		
		return category;
	}
	
	private static boolean isDuplicated(String string) {
		database.open();
		ArrayList<String> list = database.getIndex();
		database.close();
		if(list.size()>0){
			for(int i = 0 ; i<list.size() ; i++){
				if(list.get(i).equals(string))
					return false;
			}
		}
		
		return true;
	}
	
	private static void addData(String str) {
		database.open();
		database.insertIndex(str);
		database.close();
	}
	
	private static String getType(int position){
		String result="";
		if(position<3)
			result = String.valueOf(position);
		else
			result = "3";
		
		return result;
	}
}
