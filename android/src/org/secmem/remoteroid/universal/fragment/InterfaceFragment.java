package org.secmem.remoteroid.universal.fragment;

import android.support.v4.app.Fragment;

public class InterfaceFragment<T> extends Fragment {
	protected T listener;
	
	public InterfaceFragment(){
		
	}
	
	public InterfaceFragment<T> setListener(T listener){
		this.listener = listener;
		return this;
	}
	
	public T getListener(){
		return this.listener;
	}
}
