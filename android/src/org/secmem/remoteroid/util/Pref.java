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

package org.secmem.remoteroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
	
	public static final String MYPREFS = "Remoteroid_Preference";
	
	public class GCM{
		public static final String KEY_GCM_REGISTRATION = "REGISTRATION";
	}
	
	public class Authentication{
		
		public static final String isExecute = "AUTHENTICATION_EXECUTE";
		public static final String IS_LOGIN = "IS_LOGIN";
		public static final String IS_ADD_DEVICE = "IS_ADD_DEVICE";
		
	}
	
	public class Account {
		public static final String EMAIL = "ACCOUNT_EMAIL";
		public static final String SECURITY_PASSWORD = "ACCOUNT_SECURITY_PWD";
		public static final String PASSWORD = "ACCOUNT_PWD";

	}
	
	public class Device {
		public static final String UUID = "DEVICE_UUID";

	}
	
	
	public static void setMyPreferences(String key, String value, Context c) {
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void setMyPreferences(String key, boolean value, Context c) {
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static String getMyPreferences(String key, Context c) {
		String result="";
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		result=mySharedPreferences.getString(key,null);
		return result;
	}
	
	public static boolean getMyBooleanPreferences(String key, Context c) {
		boolean result=false;
		int mode = Activity.MODE_PRIVATE;
		SharedPreferences mySharedPreferences = c.getSharedPreferences(MYPREFS,mode);
		result=mySharedPreferences.getBoolean(key,false);
		return result;
	}
}
	