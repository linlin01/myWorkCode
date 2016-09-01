package com.rgk.fpfeature;

import java.util.ArrayList;
import java.util.List;

import com.rgk.fpfeature.bean.MainFeatureItem;
import com.rgk.fpfeature.bean.MainSwitchBtnItem;
import com.rgk.fpfeature.model.IMainModel;
import com.rgk.fpfeature.model.ModelImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class FpMain extends BaseActivity implements IMainView {
	private static final String TAG = "RgkFp.FpMain";
	
	View testVeiw;
	
	View callView;
	View cameraView;
	View clockView;
	SwitchButtonView callButton;
	SwitchButtonView cameraButton;
	SwitchButtonView clockButton;

	GridView mGridView;

	FPMainPresenter presenter;
	IMainModel model;

	List<MainFeatureItem> featureItems;
	FeatureAdapter mFeatureAdapter;
	List<MainSwitchBtnItem> switchBtnItems;
	
	FingerprintManager fpm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
		setContentView(R.layout.fp_main_activity);

//		startService(new Intent(FpMain.this, ATestServcie.class));
		
		model = new ModelImpl(this);
		presenter = new FPMainPresenter(this, model);

		fpm = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
		
		testVeiw = findViewById(R.id.test_id);
		testVeiw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(FpMain.this,
//						FPLockChooseActivity.class);
//				startActivity(intent);
			}
		});
		
		initSwitchButtons();
		initFeatureItems();
	}

	private void initFeatureItems() {
		mGridView = (GridView) findViewById(R.id.main_menu);

		featureItems = new ArrayList<>();
		MainFeatureItem mulitclickItem = new MainFeatureItem();
		mulitclickItem.setId(MainFeatureItem.ID_MULTIC_CLICK);
		mulitclickItem.setName(getString(R.string.feature_multi_click));
		mulitclickItem.setImgRes(R.drawable.ic_main_mulit_click);
		featureItems.add(mulitclickItem);

		MainFeatureItem freezeappItem = new MainFeatureItem();
		freezeappItem.setId(MainFeatureItem.ID_FREEZE_APP);
		freezeappItem.setName(getString(R.string.feature_freeze_app));
		freezeappItem.setImgRes(R.drawable.ic_main_freeze_app);
		featureItems.add(freezeappItem);

		MainFeatureItem applockItem = new MainFeatureItem();
		applockItem.setId(MainFeatureItem.ID_APP_LOCK);
		applockItem.setName(getString(R.string.feature_app_lock));
		applockItem.setImgRes(R.drawable.ic_main_lock_app);
		featureItems.add(applockItem);

		MainFeatureItem encrytionItem = new MainFeatureItem();
		encrytionItem.setId(MainFeatureItem.ID_ENCRYPTION);
		encrytionItem.setName(getString(R.string.feature_encryption));
		encrytionItem.setImgRes(R.drawable.ic_main_encryption);
		featureItems.add(encrytionItem);

		MainFeatureItem directionappItem = new MainFeatureItem();
		directionappItem.setId(MainFeatureItem.ID_DIRECTION_APP);
		directionappItem.setName(getString(R.string.feature_direction_app));
		directionappItem.setImgRes(R.drawable.ic_main_direction_app);
		featureItems.add(directionappItem);
		
		MainFeatureItem fpManagerItem = new MainFeatureItem();
		fpManagerItem.setId(MainFeatureItem.ID_FP_MANAGER);
		fpManagerItem.setName(getString(R.string.feature_fp_manager));
		fpManagerItem.setImgRes(R.drawable.ic_main_fp_manager);
		featureItems.add(fpManagerItem);

		mFeatureAdapter = new FeatureAdapter(featureItems, this);
		mGridView.setAdapter(mFeatureAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch ((int) id) {
				case MainFeatureItem.ID_MULTIC_CLICK:
					intent = new Intent(FpMain.this, FPMultiClickActivity.class);
					startActivity(intent);
					break;
				case MainFeatureItem.ID_FREEZE_APP:
					/*
					 * intent = new Intent();
					 * intent.setClassName("com.rgk.freeze",
					 * "com.rgk.freeze.MainActivity"); startActivity(intent)
					 */
					if (fpm.hasEnrolledFingerprints()) {
						intent = new Intent(FpMain.this, FPLockChooseActivity.class);
						intent.putExtra(FPLockChooseActivity.EXTRA_KEY_ITEM_ID,
								MainFeatureItem.ID_FREEZE_APP);
					} else {
						intent = new Intent();
						intent.setClassName("com.android.settings",
								"com.android.settings.fingerprint.FingerprintEnrollIntroduction");
					}
					startActivity(intent);
					break;
				case MainFeatureItem.ID_APP_LOCK:
					/*
					 * intent = new Intent(FpMain.this,
					 * FPAppLockActivity.class); startActivity(intent);
					 */
					if (fpm.hasEnrolledFingerprints()) {
						intent = new Intent(FpMain.this,
								FPLockChooseActivity.class);
						intent.putExtra(FPLockChooseActivity.EXTRA_KEY_ITEM_ID,
								MainFeatureItem.ID_APP_LOCK);
					} else {
						intent = new Intent();
						intent.setClassName("com.android.settings",
								"com.android.settings.fingerprint.FingerprintEnrollIntroduction");
					}
					startActivity(intent);
					break;
				case MainFeatureItem.ID_ENCRYPTION:
					intent = new Intent(
							"com.mediatek.dataprotection.ACTION_START_MAIN");
					startActivity(intent);
					break;
				case MainFeatureItem.ID_DIRECTION_APP:
					intent = new Intent(FpMain.this, FpDirectionActivity.class);
					startActivity(intent);
					break;
				case MainFeatureItem.ID_FP_MANAGER:
					intent = new Intent();
					final String clazz;
					if (fpm.hasEnrolledFingerprints()) {
						clazz = "com.android.settings.fingerprint.FingerprintSettings";
					} else {
						clazz = "com.android.settings.fingerprint.FingerprintEnrollIntroduction";
					}
					intent.setClassName("com.android.settings", clazz);
					startActivity(intent);
					break;
				}
			}
		});
	}

	private void initSwitchButtons() {
		switchBtnItems = presenter.getSwitchBtnItems();

		callView = findViewById(R.id.call);
		callButton = new SwitchButtonView();
		initSwitchButton(callView, callButton);
		if (isAnswerCallEnabled()) {
			callButton.icon.setImageResource(R.drawable.ic_call_enable);
			callButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			callButton.icon.setImageResource(R.drawable.ic_call_disable);
			callButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
		callButton.name.setText(R.string.answer_call);
		callButton.icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.setAnswerCallEnabled(!switchBtnItems.get(
						MainSwitchBtnItem.LOCATION_ANSWER_CALL).isEnable());
			}
		});

		cameraView = findViewById(R.id.camera);
		cameraButton = new SwitchButtonView();
		initSwitchButton(cameraView, cameraButton);
		if (isCaptureEnabled()) {
			cameraButton.icon.setImageResource(R.drawable.ic_capture_enable);
			cameraButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			cameraButton.icon.setImageResource(R.drawable.ic_capture_disable);
			cameraButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
		cameraButton.name.setText(R.string.capture);
		cameraButton.icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.setCaptureEnabled(!switchBtnItems.get(
						MainSwitchBtnItem.LOCATION_CAPTURE).isEnable());
			}
		});

		clockView = findViewById(R.id.clock);
		clockButton = new SwitchButtonView();
		initSwitchButton(clockView, clockButton);
		clockButton.name.setText(R.string.off_alarm_clock);
		if (isDismissAlarmEnabled()) {
			clockButton.icon
					.setImageResource(R.drawable.ic_dismiss_alarm_enable);
			clockButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			clockButton.icon
					.setImageResource(R.drawable.ic_dismiss_alarm_disable);
			clockButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
		clockButton.icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presenter.setDismissAlarmEnabled(!switchBtnItems.get(
						MainSwitchBtnItem.LOCATION_DISMISS_ALARM).isEnable());
			}
		});
	}

	private boolean isDismissAlarmEnabled() {
		return switchBtnItems.get(MainSwitchBtnItem.LOCATION_DISMISS_ALARM)
				.isEnable();
	}

	private boolean isCaptureEnabled() {
		return switchBtnItems.get(MainSwitchBtnItem.LOCATION_CAPTURE)
				.isEnable();
	}

	private boolean isAnswerCallEnabled() {
		return switchBtnItems.get(MainSwitchBtnItem.LOCATION_ANSWER_CALL)
				.isEnable();
	}

	private void initSwitchButton(View view, SwitchButtonView button) {
		button.icon = (ImageView) view.findViewById(R.id.icon);
		button.name = (TextView) view.findViewById(R.id.name);
	}

	class SwitchButtonView {
		ImageView icon;
		TextView name;
	}

	class FeatureAdapter extends BaseAdapter {

		List<MainFeatureItem> items;
		Context mContext;
		LayoutInflater mInflater;

		public FeatureAdapter(List<MainFeatureItem> items, Context mContext) {
			this.items = items;
			this.mContext = mContext;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return items.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;

			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.fp_main_feature_item,
						null);
				mHolder = new ViewHolder();
				mHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				mHolder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			MainFeatureItem item = items.get(position);

			mHolder.icon.setImageResource(item.getImgRes());
			mHolder.name.setText(item.getName());
			return convertView;
		}

		class ViewHolder {
			TextView name;
			ImageView icon;
		}

	}

	@Override
	public void setAnswerCallEnabled(boolean enable) {
		Log.d(TAG, "setAnswerCallEnabled:" + enable);
		if (enable) {
			callButton.icon.setImageResource(R.drawable.ic_call_enable);
			callButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			callButton.icon.setImageResource(R.drawable.ic_call_disable);
			callButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
	}

	@Override
	public void setCaptureEnabled(boolean enable) {
		Log.d(TAG, "setCaptureEnabled:" + enable);
		if (enable) {
			cameraButton.icon.setImageResource(R.drawable.ic_capture_enable);
			cameraButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			cameraButton.icon.setImageResource(R.drawable.ic_capture_disable);
			cameraButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
	}

	@Override
	public void setDismissAlarmEnabled(boolean enable) {
		Log.d(TAG, "setDismissAlarmEnabled:" + enable);
		if (enable) {
			clockButton.icon
					.setImageResource(R.drawable.ic_dismiss_alarm_enable);
			clockButton.icon
					.setBackgroundResource(R.drawable.switch_btn_enable_bg);
		} else {
			clockButton.icon
					.setImageResource(R.drawable.ic_dismiss_alarm_disable);
			clockButton.icon
					.setBackgroundResource(R.drawable.switch_btn_disable_bg);
		}
	}

	@Override
	protected void onHomeSelected() {
		super.onHomeSelected();
		finish();
	}
}
