package org.secmem.remoteroid.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class FilterUtil {
	private static final String DB_NAME = "filter.db";
	private static final int DB_VERSION = 1;
	
	private SQLiteDatabase mDb;
	private DatabaseHelper mHelper;
	private Context mContext;
	
	public FilterUtil(Context context){
		mContext = context;
	}
	
	public void open(){
		mHelper = new DatabaseHelper(mContext, DB_NAME, null, DB_VERSION);
		mDb = mHelper.getWritableDatabase();
	}
	
	public void close(){
		mDb.close();
	}
	
	public boolean exists(String pname){
		if(mDb==null || !mDb.isOpen())
			throw new IllegalStateException();
		Cursor c = mDb.query(FilterDB._TABLENAME, null, "pname='"+pname+"'", null, null, null, null);
		boolean result = false;
		if(c.getCount()!=0)
			result = true;
		c.close();
		return result;
	}
	
	public boolean quickCheckExists(String pname){
		open();
		Cursor c = mDb.query(FilterDB._TABLENAME, null, "pname='"+pname+"'", null, null, null, null);
		boolean result = false;
		if(c.getCount()!=0)
			result = true;
		c.close();
		close();
		return result;
	}
	
	public void addToFilter(String pname){
		ContentValues values = new ContentValues();
		values.put(FilterDB.PNAME, pname);
		open();
		if(!exists(pname))
			mDb.insert(FilterDB._TABLENAME, null, values);
		close();
	}
	
	public void removeFromFilter(String pname){
		open();
		mDb.delete(FilterDB._TABLENAME, FilterDB.PNAME+"='"+pname+"'", null);
		close();
	}
		
}

class FilterDB implements BaseColumns{
	static final String PNAME = "pname";
	static final String _TABLENAME = "filterdb";
	static final String _CREATE = "create table filterdb(_id integer primary key autoincrement, pname text not null);";
}

class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(FilterDB._CREATE);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS filterdb");
		onCreate(db);
	}	
}

