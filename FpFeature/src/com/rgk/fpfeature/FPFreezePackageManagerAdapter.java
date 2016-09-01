package com.rgk.fpfeature;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

public class FPFreezePackageManagerAdapter {
	private Context mContext;
	private PackageManager mPackageManager;
	private static FPFreezePackageManagerAdapter mPackageManagerAdapter;
	
	public static FPFreezePackageManagerAdapter getInstance(Context context) {
		if (mPackageManagerAdapter == null) {
			mPackageManagerAdapter = new FPFreezePackageManagerAdapter(context);
		}
		return mPackageManagerAdapter;
	}
	
	private FPFreezePackageManagerAdapter(Context context) {
		mContext = context;
		mPackageManager = mContext.getPackageManager();
	}

	public ApplicationInfo getApplicationInfo(String packageName,
            int flags) {
		try {
			return mPackageManager.getApplicationInfo(packageName, flags);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Drawable getApplicationIcon(String packageName) {
		try {
			return mPackageManager.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getApplicationEnabledSetting(String packageName) {
		return mPackageManager.getApplicationEnabledSetting(packageName);
	}
	
	public List<ResolveInfo> queryIntentActivities(Intent intent,
            int flags) {
		return mPackageManager.queryIntentActivities(intent, flags);
	}
	
	public List<ApplicationInfo> getInstalledApplications(int flags) {
		return mPackageManager.getInstalledApplications(flags);
	}
	
	/**
	 * For Application( for one app) enable or disable
	 * @param packageName
	 * @param newState
	 * @param flags
	 */
	public void setApplicationEnabledSetting(String packageName,
            int newState, int flags) {
		mPackageManager.setApplicationEnabledSetting(packageName,
	            newState, flags);
	}
	
	public void setApplicationEnable(String packageName) {
		mPackageManager.setApplicationEnabledSetting(packageName,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0);
	}
	
	public void setApplicationDisable(String packageName) {
		mPackageManager.setApplicationEnabledSetting(packageName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
	}
	
	/**
	 * For Activity( for one activity) enable or disable
	 * @param componentName
	 * @param newState
	 * @param flags
	 */
	public void setComponentEnabledSetting(ComponentName componentName,
            int newState, int flags) {
		mPackageManager.setComponentEnabledSetting(componentName,
	            newState, flags);
	}
}
