package com.rgk.fpfeature;

import com.rgk.fpfeature.bean.LockApp;
import com.rgk.fpfeature.bean.MainFeatureItem;
import com.rgk.fpfeature.utils.Utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.internal.widget.LockPatternUtils;

public class FPLockChooseActivity extends Activity {
	private static final String TAG = "RgkFp.FPLockChooseActivity";
	
	private static final String TAG_FRAGMENT_PASSWORD = "password";
	private static final String TAG_FRAGMENT_PATTERN = "pattern";
	private static final String TAG_FRAGMENT_FP = "fingerprint";
	
	public static final String EXTRA_KEY_ITEM_ID = "main_item_id";
	
	
	KeyguardManager mKeyguardManager;
	FingerprintManager mFingerprintManager;
	FragmentManager mFragmentManager;
	
	LockPatternUtils mLockPatternUtils;
	FPLockPatternFragment mLockPatternFragment;
	FPLockPasswordFragment mPasswordFragment;
	FPLockFpFragment mFpFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fp_lock_choose_activity);
		
//		if (getActionBar() != null) {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//            getActionBar().setHomeButtonEnabled(true);
//        }
		
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
		
		if (!mKeyguardManager.isKeyguardSecure()) {
			// Show a message that the user hasn't set up a fingerprint or lock
			// screen.
			Toast.makeText(
					this,
					"Secure lock screen hasn't set up.\n"
							+ "Go to 'Settings ->  Fingerprint' to set up a fingerprint",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		mLockPatternUtils = new LockPatternUtils(this);
		mLockPatternFragment = new FPLockPatternFragment();
		mPasswordFragment = new FPLockPasswordFragment();
		mFpFragment = new FPLockFpFragment();
		
		mFragmentManager = getFragmentManager();
		
		
		if (mFingerprintManager.hasEnrolledFingerprints()) {
			FragmentTransaction transaction = mFragmentManager.beginTransaction();
			transaction.replace(R.id.container, mFpFragment, TAG_FRAGMENT_FP);
        	transaction.commit();
		} else {
			launchLock();
		}
	}
	
	@Override
	public void onBackPressed() {
		String packageName = getIntent().getStringExtra("package");
		String className = getIntent().getStringExtra("class");
		
		if (null != packageName && !packageName.isEmpty()
				&& null != className && !className.isEmpty()) {
			//Eat it.
		} else {
			super.onBackPressed();
		}
	}
	
	public void launchLock() {
		int effectiveUserId = Utils.getEffectiveUserId(this);
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		//transaction.setCustomAnimations(R.anim.activity_open_enter, R.anim.activity_open_exit);
		switch (mLockPatternUtils.getKeyguardStoredPasswordQuality(effectiveUserId)) {
        case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
        	Log.d(TAG, "LockPattern");
        	transaction.replace(R.id.container, mLockPatternFragment, TAG_FRAGMENT_PATTERN);
        	transaction.commit();
            break;
        case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
        case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
        case DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
        case DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
        case DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
        	transaction.replace(R.id.container, mPasswordFragment, TAG_FRAGMENT_PASSWORD);
        	transaction.commit();
        	Log.d(TAG, "LockPassword");
            break;
		}
	}
	
	public void unlock() {
		String packageName = getIntent().getStringExtra("package");
		String className = getIntent().getStringExtra("class");
		
		if (null != packageName && !packageName.isEmpty()
				&& null != className && !className.isEmpty()) {
			ComponentName lockedApp = new ComponentName(packageName, className);
			for (LockApp app : FPAppLockService.lockedApps) {
				if(app.componentName.equals(lockedApp)) {
					app.locked = false;
					break;
				}
			}
			finish();
		} else {

			Intent intent;
			int id = getIntent().getIntExtra(EXTRA_KEY_ITEM_ID, -1);
			switch (id) {
			case MainFeatureItem.ID_APP_LOCK:
				intent = new Intent(FPLockChooseActivity.this, FPAppLockActivity.class);
				startActivity(intent);
				finish();
				break;
			case MainFeatureItem.ID_FREEZE_APP:
				intent = new Intent(FPLockChooseActivity.this, FPFreezeActivity.class);
	            startActivity(intent);
				finish();
				break;
			default:
				finish();
				break;
			}
		}
		
	}
}
