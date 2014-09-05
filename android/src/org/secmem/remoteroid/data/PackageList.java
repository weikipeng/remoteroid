package org.secmem.remoteroid.data;

import java.util.ArrayList;

import android.content.pm.PackageInfo;

public class PackageList {
	
	private PackageInfo mPackageList ;
	
	private boolean isSearch = false;
	
	public PackageList(PackageInfo mPackageList) {
		this.mPackageList = mPackageList;
	}
	
	
	
	public boolean isSearch() {
		return isSearch;
	}
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}
	public PackageInfo getmPackageList() {
		return mPackageList;
	}
	public void setmPackageList(PackageInfo mPackageList) {
		this.mPackageList = mPackageList;
	}

}
