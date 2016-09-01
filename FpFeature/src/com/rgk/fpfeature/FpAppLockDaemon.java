package com.rgk.fpfeature;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class FpAppLockDaemon extends Service {

	private static final String TAG = "RgkFp.FpAppLockDaemon";
	
	private IFpService mService = null;
	
	
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
		super.onCreate();
		Intent intent = new Intent(FpAppLockDaemon.this, FPAppLockService.class);
		bindService(intent, conn, Context.BIND_IMPORTANT);
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return stub;
	}

}
