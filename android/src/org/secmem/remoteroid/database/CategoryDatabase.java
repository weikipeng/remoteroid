package org.secmem.remoteroid.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryDatabase {
	private DataManager dataManager;
	private SQLiteDatabase db;
	private final Context context;

	private static final String DATABASE_NAME = "package.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String _TABLENAME = "packages";
	
	private static final String INDEX = "ind";
	
	private static final String _CREATE = "create table " + _TABLENAME + " ( "
	+ INDEX + " text not null);";

	private static final String _DROP = "drop table if exists "+ _TABLENAME;

	public CategoryDatabase(Context context) {
		this.context = context;
	}

	public CategoryDatabase open() throws SQLException {
		dataManager = new DataManager(context);
		db = dataManager.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dataManager.close();
	}
	
	public long insertIndex(String index){
		ContentValues values = new ContentValues();
		
		values.put(this.INDEX, index);
		
		return db.insert(_TABLENAME, null, values);
	}
	
	public void removeIndex(String index){
		
		int i = db.delete(_TABLENAME, "ind = '" + index + "'", null);
		
	}
	
	public ArrayList<String> getIndex() {
		Cursor mCursor = db.query(_TABLENAME, null, null, null, null, null,	null);
		ArrayList<String> contactList = new ArrayList<String>();

		if (mCursor != null) {
			mCursor.moveToFirst();
			for (int i = 0; i < mCursor.getCount(); i++) {
				
				String index = mCursor.getString(mCursor.getColumnIndex(INDEX));
				contactList.add(index);
				mCursor.moveToNext();
			}
		}
		return contactList;
	}
	
	public void dropTable() throws SQLException{
		db.execSQL(_DROP);
	}
	
	public void createTable() throws SQLException{
		db.execSQL(_CREATE +" IF NOT EXISTS " + _TABLENAME);
	}
	
	public class DataManager extends SQLiteOpenHelper{

		public DataManager(Context context) {
			super( context, DATABASE_NAME, null, DATABASE_VERSION );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			db.execSQL(_CREATE +" IF NOT EXISTS " + _TABLENAME);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w( "INFO : ", "Upgrading db from version" + oldVersion + " to" +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + _TABLENAME );
		}
	}
 }
