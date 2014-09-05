package org.secmem.remoteroid.adapter;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.database.CategoryDatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<String> categoryList;
	private int layout;
	private CategoryDatabase database;
	
	public CategoryAdapter(Context context, ArrayList<String> categoryList, int layout) {
		this.context = context;
		this.categoryList = categoryList;
		this.layout = layout;
		this.database = new CategoryDatabase(context);
		 
	}

	@Override
	public int getCount() {
		return categoryList.size();
	}

	@Override
	public String getItem(int position) {
		return categoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final int pos = position;
		
		if(convertView==null){
			convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layout, null);
		}
		
		final TextView text = (TextView)convertView.findViewById(R.id.list_category_tv);
		Button removeBtn = (Button)convertView.findViewById(R.id.list_category_btn_remove);
		
		text.setText(categoryList.get(position));
		if(pos<3)
			removeBtn.setVisibility(View.GONE);
		else
			removeBtn.setVisibility(View.VISIBLE);
		
//		text.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				if(pos>2){					// 사용자가 추가한 확장명일 때
//				}
//				else{							// 사진, 동영상, 음악 일 때
//					
//				}
//			}
//		});
//		
		removeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				database.open();
				database.removeIndex(text.getText().toString());
				categoryList.remove(pos);
				database.close();
				notifyDataSetChanged();
				
			}
		});
		
		return convertView;
	}

	public ArrayList<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList<String> categoryList) {
		this.categoryList = categoryList;
	}

}
