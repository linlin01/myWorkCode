package com.rgk.fpfeature;

import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import com.rgk.fpfeature.bean.LockApp;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.FingerprintManager.AuthenticationResult;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FPRecognizeActivity extends Activity {
	private static final String TAG = "RgkFp.FPRecognizeActivity";
	
    private static final int MSG_SCAN = 1;
    
	private static final String SECRET_MESSAGE = "Very secret message";
	
	/** Alias for our key in the Android Key Store */
	private static final String KEY_NAME = "my_key";

	private static final String KEY_STORE = "AndroidKeyStore";
	
	KeyguardManager mKeyguardManager;
	FingerprintManager mFingerprintManager;
	//KeyStore mKeyStore;
	//KeyGenerator mKeyGenerator;
	//Cipher mCipher;
	//private FingerprintManager.CryptoObject mCryptoObject;
	MyCallBack myCallBack;
	
	TextView textPrompt;
	
	ImageView scan;
	ImageView icon;
	
	ComponentName lockedApp;
    
    int start = 540;
	int end = -60;
	int delta = 10;
	int position = start;
    
    Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_SCAN:
				position -= delta;
				if (position < end || position > start) {
					delta = -delta;
				}
				scan.scrollTo(0, position);
				sendEmptyMessageDelayed(MSG_SCAN, 15);
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerprint_recognize_activity);
	
		textPrompt = (TextView) findViewById(R.id.text_prompt);
		scan = (ImageView) findViewById(R.id.scan);
        scan.scrollTo(0, start);
		icon = (ImageView) findViewById(R.id.icon);
		initIcon();
		
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
		/*try {
			mKeyStore = KeyStore.getInstance(KEY_STORE);
			mKeyGenerator = KeyGenerator.getInstance(
					KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
			mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}*/
		
		if (!mKeyguardManager.isKeyguardSecure()) {
			// Show a message that the user hasn't set up a fingerprint or lock
			// screen.
			Toast.makeText(
					this,
					"Secure lock screen hasn't set up.\n"
							+ "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		// noinspection ResourceType
		if (!mFingerprintManager.hasEnrolledFingerprints()) {
			// This happens when no fingerprints are registered.
			Toast.makeText(
					this,
					"Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
					Toast.LENGTH_LONG).show();
			return;
		}
		
		
		//createKey();
		
		// Set up the crypto object for later. The object will be
		// authenticated by use
		// of the fingerprint.
		//if (initCipher()) {
			// Show the fingerprint dialog. The user has the option to
			// use the fingerprint with
			// crypto, or you can fall back to using a server-side
			// verified password.
			//mCryptoObject = new  FingerprintManager.CryptoObject(
			//		mCipher);
			myCallBack = new MyCallBack(mFingerprintManager, textPrompt, FPRecognizeActivity.this);
		//} else {
			// This happens if the lock screen has been disabled or or a
			// fingerprint got
			// enrolled. Thus show the dialog to authenticate with their
			// password first
			// and ask the user if they want to authenticate with
			// fingerprints in the
			// future
			
		//}
	}

	private void initIcon() {
		Intent intent = getIntent();
		String packageName = intent.getStringExtra("package");
		String className = intent.getStringExtra("class");
		
		if (null != packageName && !packageName.isEmpty()
				&& null != className && !className.isEmpty()) {
			PackageManager mPackageManager = getPackageManager();
			Intent queryIntent = new Intent(Intent.ACTION_MAIN);
			queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			lockedApp = new ComponentName(packageName, className);
			queryIntent.setComponent(lockedApp);
			List<ResolveInfo> infos = mPackageManager.queryIntentActivities(
					queryIntent, PackageManager.MATCH_ALL);
			Collections.sort(infos, new ResolveInfo.DisplayNameComparator(
					mPackageManager));
			Log.i(TAG, "num:"+infos.size());
			if (infos.size() > 0) {
				String appLabel = (String) infos.get(0).loadLabel(mPackageManager);
				Log.i(TAG, "appLabel:"+appLabel);
				Drawable drawable = infos.get(0).loadIcon(mPackageManager);
				if (drawable != null)
					icon.setImageDrawable(drawable);
			}
		}
		
	}
	
	/**
	 * Creates a symmetric key in the Android Key Store which can only be used
	 * after the user has authenticated with fingerprint.
	 */
	/*
	public void createKey() {
		// The enrolling flow for fingerprint. This is where you ask the user to
		// set up fingerprint
		// for your flow. Use of keys is necessary if you need to know if the
		// set of
		// enrolled fingerprints has changed.

		try {
			mKeyStore.load(null);
			mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
					KeyProperties.PURPOSE_ENCRYPT
							| KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(
							KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
			mKeyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	*/
	
	/**
	 * Initialize the {@link Cipher} instance with the created key in the
	 * {@link #createKey()} method.
	 * 
	 * @return {@code true} if initialization is successful, {@code false} if
	 *         the lock screen has been disabled or reset after the key was
	 *         generated, or if a fingerprint got enrolled after the key was
	 *         generated.
	 */
	/*
	private boolean initCipher() {
		try {
			mKeyStore.load(null);
			SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
			mCipher.init(Cipher.ENCRYPT_MODE, key);
			return true;
		} catch (KeyPermanentlyInvalidatedException e) {
			return false;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		} catch (CertificateException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		} catch (UnrecoverableKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		} catch (KeyStoreException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		}
	}
	*/
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MSG_SCAN);
		if (myCallBack != null) myCallBack.startListening(null/*mCryptoObject*/);
	}
	
	@Override
	protected void onPause() {
		mHandler.removeMessages(MSG_SCAN);
		if (myCallBack != null) myCallBack.stopListening();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		scan.setBackground(null);
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//eat back key
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	/**
	 * Tries to encrypt some data with the generated key in {@link #createKey}
	 * which is only works if the user has just authenticated via fingerprint.
	 */
	/*
	private void tryEncrypt() {
		try {
			byte[] encrypted = mCipher.doFinal(SECRET_MESSAGE.getBytes());
			//showConfirmation(encrypted);
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			Toast.makeText(
					this,
					"Failed to encrypt the data with the generated key. "
							+ "Retry the purchase", Toast.LENGTH_LONG).show();
			Log.e("guocl",
					"Failed to encrypt the data with the generated key."
							+ e.getMessage());
		}
	}*/
	
	class MyCallBack extends FingerprintManager.AuthenticationCallback {
		boolean mSelfCancelled;
		private FingerprintManager fingerPrintManager;
		private CancellationSignal mCancellationSignal;
		TextView prompt;
		
		Activity mActivity;
		
		public MyCallBack(FingerprintManager mFingerPrintManager, TextView prompt, Activity mActivity) {
			this.fingerPrintManager = mFingerPrintManager;
			this.prompt = prompt;
			this.mActivity = mActivity;
		}
		
		public boolean isFingerprintAuthAvailable() {
	        return fingerPrintManager.isHardwareDetected()
	                && fingerPrintManager.hasEnrolledFingerprints();
	    }
		
		public void startListening(FingerprintManager.CryptoObject cryptoObject) {
	        if (!isFingerprintAuthAvailable()) {
	            return;
	        }
	        mCancellationSignal = new CancellationSignal();
	        mSelfCancelled = false;
	        fingerPrintManager
	                .authenticate(cryptoObject, mCancellationSignal, 0 /* flags */, this, null);
	    }
		
		public void stopListening() {
	        if (mCancellationSignal != null) {
	            mSelfCancelled = true;
	            mCancellationSignal.cancel();
	            mCancellationSignal = null;
	        }
	    }
		
		@Override
		public void onAuthenticationError(int errorCode, CharSequence errString) {
			super.onAuthenticationError(errorCode, errString);
			Log.d(TAG, "onAuthenticationError");
			prompt.setText("onAuthenticationError: " + errString);
		}
		
		@Override
		public void onAuthenticationFailed() {
			super.onAuthenticationFailed();
			Log.d(TAG, "onAuthenticationFailed");
			prompt.setText("onAuthenticationFailed");
		}
		
		@Override
		public void onAuthenticationSucceeded(AuthenticationResult result) {
			super.onAuthenticationSucceeded(result);
			Log.d(TAG, "onAuthenticationSucceeded");
			prompt.setText("onAuthenticationSucceeded");
			if (lockedApp != null) {
				for (LockApp app : FPAppLockService.lockedApps) {
					if(app.componentName.equals(lockedApp)) {
						app.locked = false;
						break;
					}
				}
			}
			mActivity.finish();
		}
		
		@Override
		public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
			super.onAuthenticationHelp(helpCode, helpString);
			prompt.setText("onAuthenticationHelp: " + helpString);
		}
	}
}
