package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.DirectionApp;
import com.rgk.fpfeature.model.IDirectionModel;
import com.rgk.fpfeature.model.ModelImpl;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FPDirectionService extends Service {
	private static final String TAG = "RgkFp.FPDirectionService";

	public final static String CMD = "cmd";
	public final static int CMD_UP = 1001;
	public final static int CMD_DOWN = 1002;
	public final static int CMD_LEFT = 1003;
	public final static int CMD_RIGHT = 1004;

	IDirectionModel model;
	List<DirectionApp> directionApps;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
			RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
			boolean isHome = am.isInHomeStack(runningTaskInfo.id);
			
			Log.d(TAG, "isHome="+isHome);
			if (isHome) {
				model = new ModelImpl(this);
				directionApps = model.getDirectionApps();
				int cmd = intent.getIntExtra(CMD, -1);
				int direction = DirectionApp.DIRECTION_INVALID;
				
				Intent startIntent = null;
				switch (cmd) {
				case CMD_DOWN:
					Log.d(TAG, "Down");
					direction = DirectionApp.DIRECTION_DOWN;
					if (null != directionApps.get(DirectionApp.LOCATION_DOWN).componentName) {
						startIntent = new Intent();
						startIntent.setComponent(directionApps
								.get(DirectionApp.LOCATION_DOWN).componentName);
					}
					break;
				case CMD_UP:
					Log.d(TAG, "UP");
					direction = DirectionApp.DIRECTION_UP;
					if (null != directionApps.get(DirectionApp.LOCATION_UP).componentName) {
						startIntent = new Intent();
						startIntent.setComponent(directionApps
								.get(DirectionApp.LOCATION_UP).componentName);
					}
					break;
				case CMD_LEFT:
					Log.d(TAG, "LEFT");
					direction = DirectionApp.DIRECTION_LEFT;
					if (null != directionApps.get(DirectionApp.LOCATION_LEFT).componentName) {
						startIntent = new Intent();
						startIntent.setComponent(directionApps
								.get(DirectionApp.LOCATION_LEFT).componentName);
					}
					break;
				case CMD_RIGHT:
					Log.d(TAG, "RIGHT");
					direction = DirectionApp.DIRECTION_RIGHT;
					if (null != directionApps.get(DirectionApp.LOCATION_RIGHT).componentName) {
						startIntent = new Intent();
						startIntent.setComponent(directionApps
								.get(DirectionApp.LOCATION_RIGHT).componentName);
					}
					break;
				}
				if (null != startIntent) {
					startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					try {
						startActivity(startIntent);
					} catch (ActivityNotFoundException e) {
						Log.d(TAG, "ActivityNotFoundException");
						model.deleteDirectionApp(direction);
					}
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
}
