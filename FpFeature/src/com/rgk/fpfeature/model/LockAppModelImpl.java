package com.rgk.fpfeature.model;

import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.rgk.fpfeature.FingerprintProvider;
import com.rgk.fpfeature.bean.LockApp;

public class LockAppModelImpl implements ILockAppModel {
	private static final String TAG = "RgkFp.LockAppModelImpl";

	private static final String URI_LOCK_APP = "content://"
			+ FingerprintProvider.AUTHORITIES + "/"
			+ FingerprintProvider.TABLE_APP_LOCK;

	public static final String CLASS_NAME_MMS = "com.android.mms.ui.BootActivity";

	Context mContext;

	public LockAppModelImpl(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void getLockApps(List<LockApp> apps) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_LOCK_APP);
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor != null) {
			int count = cursor.getCount();
			Log.d(TAG, "count=" + count);
			LockApp app;
			ComponentName name;
			apps.clear();
			while (cursor.moveToNext()) {
				int cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.APP_LOCK_IS_LOCKED);
				int isLock = cursor.getInt(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.APP_LOCK_CLASS);
				String className = cursor.getString(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.APP_LOCK_PACKAGE);
				String packageName = cursor.getString(cursorIndex);
				name = new ComponentName(packageName, className);
				app = new LockApp(name, 0);
				apps.add(app);
			}
			cursor.close();
		}
	}

	@Override
	public void lock(LockApp app, List<LockApp> lockedApps) {
		String packageName = app.componentName.getPackageName();
		String className = app.componentName.getClassName();
		if (CLASS_NAME_MMS.equals(className)) {
			className = "com.android.mms.ui.ConversationList";
		}
		ComponentName componentName = new ComponentName(packageName, className);

		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_LOCK_APP);

		if (app.locked) {
			app.locked = false;
			
			for (LockApp lockApp : lockedApps) {
				if (lockApp.componentName.equals(componentName)) {
					lockedApps.remove(lockApp);
					break;
				}
			}

			contentResolver.delete(uri, FingerprintProvider.APP_LOCK_PACKAGE
					+ "=? AND " + FingerprintProvider.APP_LOCK_CLASS + "=?",
					new String[] { componentName.getPackageName(),
							componentName.getClassName() });
		} else {
			app.locked = true;
			LockApp lockApp = new LockApp(componentName, 0);
			lockedApps.add(lockApp);
			ContentValues values = new ContentValues();
			values.put(FingerprintProvider.APP_LOCK_PACKAGE,
					componentName.getPackageName());
			values.put(FingerprintProvider.APP_LOCK_CLASS,
					componentName.getClassName());
			contentResolver.insert(uri, values);
			
		}
	}

}
