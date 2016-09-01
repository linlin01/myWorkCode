package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.FreezeAppInfo;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

public class FPFreezeAppsLoader extends AsyncTaskLoader<List<FreezeAppInfo>> {
	private static final String TAG = "RgkFp.FPFreezeAppsLoader";
	
	private Context mContext;

	public static final int FLAG_FREEZED_APPS = 1;
	public static final int FLAG_NORMAL_APPS = 2;

	private int mFlag;
	private FPFreezePresenter presenter;

	public FPFreezeAppsLoader(Context context, int flag, FPFreezePresenter presenter) {
		super(context);
		if (flag != FLAG_FREEZED_APPS && flag != FLAG_NORMAL_APPS) {
			throw new IllegalArgumentException("Invalid flag: " + flag);
		}
		mFlag = flag;
		this.presenter = presenter;
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		forceLoad();
	}

	@Override
	public List<FreezeAppInfo> loadInBackground() {
		
		switch (mFlag) {
		case FLAG_FREEZED_APPS:
			Log.d(TAG, "FLAG_FREEZED_APPS");
			synchronized (TAG) {
				return presenter.getFreezeApps();
			}
			
		case FLAG_NORMAL_APPS:
			Log.d(TAG, "FLAG_NORMAL_APPS");
			synchronized (TAG) {
				return presenter.getNormalApps();
			}
		}

		return null;
	}

	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		cancelLoad();
	}

	@Override
	protected void onReset() {
		super.onReset();
		cancelLoad();
	}
}
