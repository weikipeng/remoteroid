package org.secmem.remoteroid.data;

import java.io.File;
import java.util.ArrayList;

import org.secmem.remoteroid.activity.ExplorerActivity;

import android.os.Environment;
import android.util.Log;

public class CommunicateInfo{
	
	
	public static String getCurrentPath(){
		String result="";
		
		if(ExplorerActivity.dataList==null || ExplorerActivity.dataList.getPath()==null || ExplorerActivity.dataList.getPath().equals("") || ExplorerActivity.adapter.getType()== ExplorerActivity.ADAPTER_TYPE_CATEGORY){
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/Remoteroid/";
		}
		result = ExplorerActivity.dataList.getPath();
		return result;
	}


}
