package com.rgk.fpfeature;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class FingerprintProviderPublic extends ContentProvider {
	private static final String TAG = "RgkFp.FingerprintProviderPublic";
	public static final String AUTHORITIES = "com.rgk.fingerprintpublic";
	
	private DBHelper dbHelper;
	private static UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITIES, FingerprintProvider.TABLE_SETTINGS, FingerprintProvider.SETTINGS);
		uriMatcher.addURI(AUTHORITIES, FingerprintProvider.TABLE_SETTINGS + "/#", FingerprintProvider.SETTINGS_ITEM);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case FingerprintProvider.SETTINGS:
			return AUTHORITIES + "/" + FingerprintProvider.TABLE_SETTINGS;
		case FingerprintProvider.SETTINGS_ITEM:
			return AUTHORITIES + "/" + FingerprintProvider.TABLE_SETTINGS;
		default:
			throw new IllegalArgumentException("Unknow Uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.d(TAG, "Provider > onCreate");
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d(TAG, "query");
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		switch (uriMatcher.match(uri)) {
		case FingerprintProvider.SETTINGS:
			Log.d(TAG, "SETTINGS");
			return database.query(FingerprintProvider.TABLE_SETTINGS, projection, selection,
					selectionArgs, null, null, sortOrder);

		case FingerprintProvider.SETTINGS_ITEM: {
			Log.d(TAG, "SETTINGS_ITEM");
			long id = ContentUris.parseId(uri);
			String where = "_id=" + id;
			if (selection != null && !"".equals(selection)) {
				where = selection + " and " + where;
			}

			return database.query(FingerprintProvider.TABLE_SETTINGS, projection, where,
					selectionArgs, null, null, sortOrder);
		}
		default:
			throw new IllegalArgumentException("Unknow Uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
