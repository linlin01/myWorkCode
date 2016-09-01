package com.rgk.fpfeature;

import java.util.ArrayList;
import java.util.List;

import com.rgk.fpfeature.bean.LockApp;
import com.rgk.fpfeature.model.ILockAppModel;
import com.rgk.fpfeature.model.LockAppModelImpl;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class FPAppLockService extends Service {
	private static final String TAG = "RgkFp.FPAppLockService";

	public final static String APP_LOCK_CMD = "app_lock_cmd";
	public final static int CMD_START_LISTION = 1001;
	public final static int CMD_STOP_LISTION = 1002;

	private static final String RECOGNIZE_CLASS = "com.rgk.fpfeature.FPLockChooseActivity";

	private final static int MSG_CHECK = 1;

	ILockAppModel model;
	private IFpService mService = null;

	FingerprintManager mFingerprintManager;

	private HandlerThread mThread; 
	
	private boolean stopListen = false;
	public static List<LockApp> lockedApps = new ArrayList<>();

	ComponentName topActivity;

	Handler mHandler;
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = IFpService.Stub.asInterface(service);
			try {
				mService.getInfo();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}; 
	
	IFpService.Stub stub = new IFpService.Stub() {
		
		@Override
		public void getInfo() throws RemoteException {
			Log.d(TAG, "stub - getInfo");
		}
	}; 

	public void onCreate() {
		Log.d(TAG, "onCreate");
//		Intent intent = new Intent(FPAppLockService.this, FpAppLockDaemon.class);
//		bindService(intent, conn, Context.BIND_IMPORTANT);
		mThread = new HandlerThread("com.rgk.fp.al");
		mThread.start();
		
		mHandler = new Handler(mThread.getLooper()) {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case MSG_CHECK:
					Listen();
					break;

				default:
					break;
				}
			};
		};
	};
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		mThread.quitSafely();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return stub;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			int cmd = intent.getIntExtra(APP_LOCK_CMD, -1);
			switch (cmd) {
			case CMD_START_LISTION:
				Log.d(TAG, "START_LISTION");
				stopListen = false;
				if (model == null) {
					model = new LockAppModelImpl(this);
				}

				model.getLockApps(lockedApps);

				if (mFingerprintManager == null) {
					mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
				}

				if (lockedApps.isEmpty()) {
					stopSelf();
				} else {
					mHandler.sendEmptyMessage(MSG_CHECK);
				}
				break;
			case CMD_STOP_LISTION:
				Log.d(TAG, "CMD_STOP_LISTION");
				stopListen = true;
				break;
			default:
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void Listen() {
		if (stopListen) {
			return;
		}
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		if (mFingerprintManager.hasEnrolledFingerprints()) {

			List<RunningTaskInfo> runningTaskInfos = am.getRunningTasks(1);
			RunningTaskInfo runningTaskInfo = runningTaskInfos.get(0);
			ComponentName componentName = runningTaskInfo.topActivity;
			Log.d(TAG, "topActivity=" + componentName);
			Log.d(TAG, "lockedApp number:" + lockedApps.size());
			if (!RECOGNIZE_CLASS.equals(componentName.getClass())) {
				for (LockApp app : lockedApps) {
					if (app.componentName.equals(componentName)) {
						Log.d(TAG, "Time = " + app.startTime);
						boolean startNearly = true;
						long currentTime = System.currentTimeMillis();
						if (currentTime - app.startTime > 1 * 60 * 1000) {
							startNearly = false;
						}
						app.startTime = currentTime;
						if (!app.componentName.equals(topActivity)
								&& (!startNearly || app.locked)) {
							app.locked = true;
							Intent intent = new Intent();
							intent.setClassName("com.rgk.fpfeature",
									FPLockChooseActivity.class.getName());
							intent.putExtra("package",
									app.componentName.getPackageName());
							intent.putExtra("class",
									app.componentName.getClassName());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

							startActivity(intent);

						}
						break;
					}
				}
			}
			topActivity = componentName;
		} else {
			Log.d(TAG, "No Fingerprint add");
		}

		mHandler.sendEmptyMessageDelayed(MSG_CHECK, 500);
	}

}
