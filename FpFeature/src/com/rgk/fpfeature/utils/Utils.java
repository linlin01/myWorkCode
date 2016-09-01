package com.rgk.fpfeature.utils;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

public class Utils {
	private static final String TAG = "RgkFp.Utils";
	
	public static int getEffectiveUserId(Context context) {
		UserManager um = UserManager.get(context);
		if (um != null) {
            return um.getCredentialOwnerProfile(UserHandle.myUserId());
        } else {
            Log.e(TAG, "Unable to acquire UserManager");
            return UserHandle.myUserId();
        }
	}
}
