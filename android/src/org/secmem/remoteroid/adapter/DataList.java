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

package org.secmem.remoteroid.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.secmem.remoteroid.data.ExplorerType;
import org.secmem.remoteroid.data.FileList;
import org.secmem.remoteroid.data.FolderList;
import org.secmem.remoteroid.expinterface.OnFileSelectedListener;
import org.secmem.remoteroid.expinterface.OnPathChangedListener;
import org.secmem.remoteroid.util.HongUtil;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DataList extends ListView {
	
	private ArrayList<ExplorerType> expList = new ArrayList<ExplorerType>();
	private ArrayList<FolderList> folderList = new ArrayList<FolderList>();
	private ArrayList<FileList> fileList = new ArrayList<FileList>();
	
	private ArrayAdapter<String> _Adapter = null; 
	
	private String _Path = "";
	
	private OnPathChangedListener onPathChangedListener = null;
	private OnFileSelectedListener onFileSelectedListener = null;
	public static ArrayList<File> fileInfo = new ArrayList<File>();
	public DataList(Context context) {
		super(context);
		
	}
	
	private boolean openPath(String path) {
		folderList.clear();
		fileList.clear();
		
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
        	return false;
        }
        
        for (int i=0; i<files.length; i++) {
        	if (files[i].isDirectory()) {
        		folderList.add(new FolderList(files[i].getName(), ExplorerType.TYPE_FOLDER));
        	} else {
        		FileList f = new FileList(files[i].getName(), ExplorerType.TYPE_FILE);
        		f.setMimeType(getPath()+files[i].getName());
        		fileList.add(new FileList(files[i].getName(), ExplorerType.TYPE_FILE));
        	}
        }
        
        Collections.sort(folderList, HongUtil.nameComparator);
        Collections.sort(fileList, HongUtil.nameComparator);
        
        folderList.add(0, new FolderList("<..>", ExplorerType.TYPE_FOLDER));
        
        return true;
	}
	
	private void setList() {
		expList.clear();
		expList.addAll(folderList);
		expList.addAll(fileList);
        
	}
	

	public void setPath(String value) {
		if (value.length() == 0) {
			value = "";
		} 
		else {
			String lastChar = value.substring(value.length()-1, value.length());
			
			if (lastChar.matches("/") == false) 
				value = value + "/"; 
		}
		
		if (openPath(value)) {
			_Path = value;
			setList();	        
			if (onPathChangedListener != null) onPathChangedListener.onChanged(value);
		}
	}
	
	private String deleteLastFolder(String value) {
		String list[] = value.split("/");

		String result = "";
		
		for (int i=0; i<list.length-1; i++) {
			result = result + list[i] + "/"; 
		}
		
		return result;
	}
	
	public int getPathCount(){
		return _Path.split("/").length;
	}
	
	public String getRealPathName(String newPath) {
		
		if (newPath.equals("<..>")) {
			return deleteLastFolder(_Path);
		} else {
			return _Path + newPath + "/";
		}
	}
	
	public String getBackPathName(){
		return deleteLastFolder(_Path);
	}
	
	
	public String getPath() {
		return _Path;
	}
	
	public void setOnPathChangedListener(OnPathChangedListener value) {
		onPathChangedListener = value;
	}

	public OnPathChangedListener getOnPathChangedListener() {
		return onPathChangedListener;
	}

	public void setOnFileSelected(OnFileSelectedListener value) {
		onFileSelectedListener = value;
	}

	public OnFileSelectedListener getOnFileSelected() {
		return onFileSelectedListener;
	}
	
	public String get_Path() {
		return _Path;
	}

	public void set_Path(String _Path) {
		this._Path = _Path;
	}
	
	public ArrayList<ExplorerType> getExpList() {
		return expList;
	}

	public void setExpList(ArrayList<ExplorerType> expList) {
		this.expList = expList;
	}

	public ArrayList<FolderList> getFolderList() {
		return folderList;
	}

	public void setFolderList(ArrayList<FolderList> folderList) {
		this.folderList = folderList;
	}

	public ArrayList<FileList> getFileList() {
		return fileList;
	}

	public void setFileList(ArrayList<FileList> fileList) {
		this.fileList = fileList;
	}

}
