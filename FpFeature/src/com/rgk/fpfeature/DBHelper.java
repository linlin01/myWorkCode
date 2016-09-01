package com.rgk.fpfeature;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rgk.fpfeature.bean.MainSwitchBtnItem;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "RgkFp.DBHelper";
	
	private static final String DATABASE_NAME = "fingerprint.db";
	private static final int DATABASE_VERSION = 5;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "DB Create");
		db.execSQL("CREATE TABLE " + FingerprintProvider.TABLE_MULTI_CLICK + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FingerprintProvider.MULIT_CLICK_COUNT + " INTEGER," + FingerprintProvider.MULIT_CLICK_PACKAGE
				+ " TEXT," + FingerprintProvider.MULIT_CLICK_CLASS + " TEXT);");
		
		db.execSQL("CREATE TABLE " + FingerprintProvider.TABLE_APP_LOCK + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FingerprintProvider.APP_LOCK_IS_LOCKED + " INTEGER," + FingerprintProvider.APP_LOCK_PACKAGE
				+ " TEXT," + FingerprintProvider.APP_LOCK_CLASS + " TEXT);");
		
		db.execSQL("CREATE TABLE " + FingerprintProvider.TABLE_DIRECTION_APP + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ FingerprintProvider.DIRECTION_APP_DIRECTION + " INTEGER,"
				+ FingerprintProvider.DIRECTION_APP_PACKAGE + " TEXT," + FingerprintProvider.DIRECTION_APP_CLASS
				+ " TEXT);");
		
		db.execSQL("CREATE TABLE " + FingerprintProvider.TABLE_SETTINGS + "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT," + FingerprintProvider.SETTINGS_NAME
				+ " INTEGER," + FingerprintProvider.SETTINGS_VALUE + " INTEGER);");
		db.execSQL("INSERT INTO " + FingerprintProvider.TABLE_SETTINGS + " VALUES (0, "
				+ MainSwitchBtnItem.ID_ANSWER_CALL + ", 0);");
		db.execSQL("INSERT INTO " + FingerprintProvider.TABLE_SETTINGS + " VALUES (1, "
				+ MainSwitchBtnItem.ID_CAPTURE + ", 0);");
		db.execSQL("INSERT INTO " + FingerprintProvider.TABLE_SETTINGS + " VALUES (2, "
				+ MainSwitchBtnItem.ID_DISMISS_ALARM + ", 0);");
		
		db.execSQL("CREATE TABLE " + FingerprintProvider.TABLE_FREEZE_APPS + "("
				+ FingerprintProvider.FREEZE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + FingerprintProvider.FREEZE_PACKAGE_NAME
				+ " TEXT," + FingerprintProvider.FREEZE_UID + " INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + FingerprintProvider.TABLE_MULTI_CLICK);
		db.execSQL("DROP TABLE IF EXISTS " + FingerprintProvider.TABLE_APP_LOCK);
		db.execSQL("DROP TABLE IF EXISTS " + FingerprintProvider.TABLE_DIRECTION_APP);
		db.execSQL("DROP TABLE IF EXISTS " + FingerprintProvider.TABLE_SETTINGS);
		db.execSQL("DROP TABLE IF EXISTS " + FingerprintProvider.TABLE_FREEZE_APPS);
		onCreate(db);
	}

}
