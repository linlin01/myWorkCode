/**
 * Add by chenlong.guo for New Feature
 */
package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.MultiClickItem;
import com.rgk.fpfeature.model.IMultiClickModel;
import com.rgk.fpfeature.model.MultiClickModelImpl;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;

import android.util.Log;

public class FPMultiClickService extends Service {
	private static final String TAG = "RgkFp.FPMultiClickService";

	List<MultiClickItem> multiClickItems;
	IMultiClickModel model;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int count = -1;
		Intent i = null;
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
		RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
		boolean isHome = am.isInHomeStack(runningTaskInfo.id);
		
		Log.d(TAG, "isHome="+isHome);
		if (isHome) {
			getQuickStartSettings();
	
			if (intent != null) {
				count = intent.getIntExtra("clickCount", -1);
				switch (count) {
				case MultiClickItem.CLICK_COUNT_DOUBLE:
					i = new Intent();
					if (multiClickItems.get(MultiClickItem.LOCATION_DOUBLE).componentName != null) {
						i.setComponent(multiClickItems
								.get(MultiClickItem.LOCATION_DOUBLE).componentName);
					}
					break;
				case MultiClickItem.CLICK_COUNT_TRIPLE:
					i = new Intent();
					if (multiClickItems.get(MultiClickItem.LOCATION_TRIPLE).componentName != null) {
						i.setComponent(multiClickItems
								.get(MultiClickItem.LOCATION_TRIPLE).componentName);
					}
					break;
				case MultiClickItem.CLICK_COUNT_FOURFOLD:
					i = new Intent();
					if (multiClickItems.get(MultiClickItem.LOCATION_FOURFOLD).componentName != null) {
						i.setComponent(multiClickItems
								.get(MultiClickItem.LOCATION_FOURFOLD).componentName);
					}
					break;
				default:
					break;
				}
				if (i != null) {
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					try {
						startActivity(i);
					} catch (ActivityNotFoundException e) {
						Log.d(TAG, "ActivityNotFoundException");
						model.deleteMultiClickItem(count);
					}
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void getQuickStartSettings() {
		Log.d(TAG, "getQuickStartSettings");
		model = new MultiClickModelImpl(this);
		multiClickItems = model.getMultiClickItems();
	}
}
