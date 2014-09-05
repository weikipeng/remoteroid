package org.secmem.remoteroid.adapter;

import java.util.ArrayList;

import org.secmem.remoteroid.R;
import org.secmem.remoteroid.util.FilterUtil;
import org.secmem.remoteroid.util.PackageSoundSearcher;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PackageAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<PackageInfo> mAllPackageList = new ArrayList<PackageInfo>();
	
	private ArrayList<PackageInfo> mSearchResultPackageList = new ArrayList<PackageInfo>();
	
	private PackageManager mPkgManager;
	private FilterUtil mFilterUtil;
	
	
	private String strInitial="";
	private String afterStr="";
	
	private boolean isSync = false;

	public PackageAdapter(Context context){
		mContext = context;
		mPkgManager = mContext.getPackageManager();
		mAllPackageList = (ArrayList<PackageInfo>)mPkgManager.getInstalledPackages(0);
		mFilterUtil = new FilterUtil(mContext);
		mFilterUtil.open();
	}
	
	@Override
	protected void finalize() throws Throwable {
		mFilterUtil.close();
		super.finalize();
	}

	@Override
	public int getCount() {
		if(strInitial.equals("") || strInitial==null){
			return mAllPackageList.size();
		}
		else{
			return mSearchResultPackageList.size();
		}
	}

	@Override
	public PackageInfo getItem(int position) {
		if(strInitial.equals("") || strInitial==null){
			return mAllPackageList.get(position);
		}
		else{
			return mSearchResultPackageList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ImageView icon;
		TextView name;
		TextView pname;
		CheckBox checked;
		
		if(convertView==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.row_package_selector, null);
		}
		
		icon = (ImageView)convertView.findViewById(R.id.row_package_selector_icon);
		name = (TextView)convertView.findViewById(R.id.row_package_selector_title);
		pname = (TextView)convertView.findViewById(R.id.row_package_selector_pname);
		checked = (CheckBox)convertView.findViewById(R.id.row_package_selector_checkmark);
		
		final PackageInfo item = getItem(position);
		
		icon.setImageDrawable(item.applicationInfo.loadIcon(mPkgManager));
		name.setText(item.applicationInfo.loadLabel(mPkgManager));
		pname.setText(item.packageName);
		
		checked.setChecked(mFilterUtil.exists(item.packageName));
		checked.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				ListView lv = (ListView)parent;
				lv.getOnItemClickListener().onItemClick(null, null, position, 0);
			}
			
		});
		
		return convertView;
	}
	public String getStrInitial() {
		return strInitial;
	}
	public void setStrInitial(String strInitial) {
		this.strInitial = strInitial;
		
		if(!isSync){
			new AsyncTask<String, Void, Integer>(){
	
				@Override
				protected Integer doInBackground(String... params) {
					searchSoundInitial(params[0]);
					return 0;
				}
				
				@Override
				protected void onPostExecute(Integer result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if(!isSync)
						notifyDataSetChanged();
				}
				
			}.execute(strInitial);
		}
		
	}
	public String getAfterStr() {
		return afterStr;
	}
	public void setAfterStr(String afterStr) {
		this.afterStr = afterStr;
	}
	
	public synchronized void searchSoundInitial(String msg){
		
		ArrayList<PackageInfo> info = new ArrayList<PackageInfo>();
		
		isSync = true;
		setAfterStr(msg);
		for(int i = 0 ; i < mAllPackageList.size() ; i++){
			if(!msg.equals(this.strInitial)){
				searchSoundInitial(this.strInitial);
				return;
			}
			if(PackageSoundSearcher.matchString((mAllPackageList.get(i).applicationInfo.loadLabel(mPkgManager)).toString(), msg)){
				info.add(mAllPackageList.get(i));
			}
			else{
			}
		}
		mSearchResultPackageList = info;
		isSync = false;
	}

}
