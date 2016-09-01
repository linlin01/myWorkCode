package com.rgk.fpfeature.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.rgk.fpfeature.FPFreezePackageManagerAdapter;
import com.rgk.fpfeature.FingerprintProvider;
import com.rgk.fpfeature.bean.FreezeAppInfo;

public class FreezeModelImpl implements IFreezeModel {
	private static final String TAG = "RgkFp.FreezeModelImpl";

	private static final String URI = "content://"
			+ FingerprintProvider.AUTHORITIES + "/"
			+ FingerprintProvider.TABLE_FREEZE_APPS;

	private Context mContext;

	private List<FreezeAppInfo> freezeAppInfoList;
	private List<FreezeAppInfo> normalAppInfoList;

	public FreezeModelImpl(Context context) {
		this.mContext = context;
		freezeAppInfoList = new ArrayList<FreezeAppInfo>();
		normalAppInfoList = new ArrayList<FreezeAppInfo>();
	}

	@Override
	public List<FreezeAppInfo> getFreezeApps() {
		FPFreezePackageManagerAdapter mAdapter = FPFreezePackageManagerAdapter
				.getInstance(mContext);
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		freezeAppInfoList.clear();
		FreezeAppInfo app;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.FREEZE_PACKAGE_NAME);
				String packageName = cursor.getString(cursorIndex);
				ApplicationInfo appInfo = mAdapter.getApplicationInfo(
						packageName, 0);
				if (appInfo != null) {
					app = new FreezeAppInfo();
					app.applicationInfo = appInfo;
					app.name = appInfo.loadLabel(mContext.getPackageManager());
					app.icon = appInfo.loadIcon(mContext.getPackageManager());
					app.isFreezed = true;
					freezeAppInfoList.add(app);
				}
			}
			cursor.close();
		}
		return freezeAppInfoList;
	}

	@Override
	public void deleteFreezeApp(int uid, FreezeAppInfo info) {
		FPFreezePackageManagerAdapter mAdapter = FPFreezePackageManagerAdapter
				.getInstance(mContext);
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		contentResolver.delete(uri, FingerprintProvider.FREEZE_UID + "=?",
				new String[] { String.valueOf(uid) });
		
		mAdapter.setApplicationEnable(info.applicationInfo.packageName);
		freezeAppInfoList.remove(info);
		info.isFreezed = false;
		normalAppInfoList.add(info);
	}

	@Override
	public void addFreezeApp(int uid, FreezeAppInfo info) {
		FPFreezePackageManagerAdapter mAdapter = FPFreezePackageManagerAdapter
				.getInstance(mContext);
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.FREEZE_UID, uid);
		values.put(FingerprintProvider.FREEZE_PACKAGE_NAME, info.applicationInfo.packageName);
		contentResolver.insert(uri, values);
		
		mAdapter.setApplicationDisable(info.applicationInfo.packageName);
		
		normalAppInfoList.remove(info);
		info.isFreezed = true;
		freezeAppInfoList.add(info);
	}

	@Override
	public List<FreezeAppInfo> getNormalApps() {
		normalAppInfoList.clear();
		
		FPFreezePackageManagerAdapter pkgAdapter = FPFreezePackageManagerAdapter
				.getInstance(mContext);

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pkgAdapter.queryIntentActivities(
				mainIntent, 0);
		FreezeAppInfo app;
		for (ResolveInfo resolveInfo : resolveInfos) {
			if (!"com.android.settings"
					.equals(resolveInfo.activityInfo.applicationInfo.packageName)) {
				app = new FreezeAppInfo();
				app.applicationInfo = resolveInfo.activityInfo.applicationInfo;
				app.name = resolveInfo.activityInfo.applicationInfo
						.loadLabel(mContext.getPackageManager());
				app.icon = resolveInfo.activityInfo.applicationInfo
						.loadIcon(mContext.getPackageManager());
				app.isFreezed = false;
				normalAppInfoList.add(app);
			}
		}
		return normalAppInfoList;
	}
}
