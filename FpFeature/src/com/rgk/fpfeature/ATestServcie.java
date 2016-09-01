package com.rgk.fpfeature;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class ATestServcie extends Service {
	private static final String TAG = "RgkFp.ATestServcie";
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.d(TAG, "tEsT");
			mHandler.sendEmptyMessageDelayed(1, 2000);
		};
	};
	
	public void onCreate() {
		super.onCreate();
		mHandler.sendEmptyMessageDelayed(1, 2000);
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

}
