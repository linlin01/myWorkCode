package com.rgk.fpfeature.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.rgk.fpfeature.FingerprintProvider;
import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.DirectionApp;
import com.rgk.fpfeature.bean.MainSwitchBtnItem;

public class ModelImpl implements IDirectionModel, IMainModel {

	private static final String URI_DIRECTION_APP = "content://"
			+ FingerprintProvider.AUTHORITIES + "/"
			+ FingerprintProvider.TABLE_DIRECTION_APP;

	private static final String URI_SETTINGS = "content://"
			+ FingerprintProvider.AUTHORITIES + "/"
			+ FingerprintProvider.TABLE_SETTINGS;

	private static final String TAG = "RgkFp.DirectonModelImpl";

	private Context mContext;

	List<DirectionApp> directionApps;
	List<MainSwitchBtnItem> switchBtnItems;

	public ModelImpl(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void saveDirectionApp(final int direction,
			BaseAppItem chooseedApp) {
		int location = 0;
		switch(direction) {
		case DirectionApp.DIRECTION_LEFT:
			location = DirectionApp.LOCATION_LEFT;
			break;
		case DirectionApp.DIRECTION_UP:
			location = DirectionApp.LOCATION_UP;
			break;
		case DirectionApp.DIRECTION_RIGHT:
			location = DirectionApp.LOCATION_RIGHT;
			break;
		case DirectionApp.DIRECTION_DOWN:
			location = DirectionApp.LOCATION_DOWN;
			break;
		}
		
		String className = chooseedApp.componentName.getClassName();
		String packageName = chooseedApp.componentName.getPackageName();

		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_DIRECTION_APP);
		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.DIRECTION_APP_DIRECTION, direction);
		values.put(FingerprintProvider.DIRECTION_APP_CLASS, className);
		values.put(FingerprintProvider.DIRECTION_APP_PACKAGE, packageName);

		if (directionApps.get(location).getName() == null) {
			contentResolver.insert(uri, values);
		} else {
			contentResolver.update(uri, values,
					FingerprintProvider.DIRECTION_APP_DIRECTION + "=?",
					new String[] { String.valueOf(direction) });
		}
		
		directionApps.get(location).componentName = chooseedApp.componentName;
		directionApps.get(location).icon = chooseedApp.icon;
		directionApps.get(location).label = chooseedApp.label;
	}

	@Override
	public List<DirectionApp> getDirectionApps() {
		if (directionApps == null) {
			directionApps = new ArrayList<>();

			DirectionApp leftApp = new DirectionApp(null,
					DirectionApp.DIRECTION_LEFT);
			DirectionApp upApp = new DirectionApp(null,
					DirectionApp.DIRECTION_UP);
			DirectionApp rightApp = new DirectionApp(null,
					DirectionApp.DIRECTION_RIGHT);
			DirectionApp downApp = new DirectionApp(null,
					DirectionApp.DIRECTION_DOWN);

			directionApps.add(DirectionApp.LOCATION_LEFT, leftApp);
			directionApps.add(DirectionApp.LOCATION_UP, upApp);
			directionApps.add(DirectionApp.LOCATION_RIGHT, rightApp);
			directionApps.add(DirectionApp.LOCATION_DOWN, downApp);

			loadDb();
		}

		return directionApps;
	}

	private void loadDb() {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_DIRECTION_APP);
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor != null) {
			int count = cursor.getCount();
			Log.d(TAG, "count=" + count);
			ComponentName name;
			while (cursor.moveToNext()) {
				int cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.DIRECTION_APP_DIRECTION);
				int direction = cursor.getInt(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.DIRECTION_APP_CLASS);
				String className = cursor.getString(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.DIRECTION_APP_PACKAGE);
				String packageName = cursor.getString(cursorIndex);
				name = new ComponentName(packageName, className);

				switch (direction) {
				case DirectionApp.DIRECTION_LEFT:
					directionApps.get(DirectionApp.LOCATION_LEFT).setName(name);
					break;
				case DirectionApp.DIRECTION_UP:
					directionApps.get(DirectionApp.LOCATION_UP).setName(name);
					break;
				case DirectionApp.DIRECTION_RIGHT:
					directionApps.get(DirectionApp.LOCATION_RIGHT)
							.setName(name);
					break;
				case DirectionApp.DIRECTION_DOWN:
					directionApps.get(DirectionApp.LOCATION_DOWN).setName(name);
					break;
				default:
					break;
				}
			}
			cursor.close();
		}
	}

	@Override
	public void deleteDirectionApp(int direction) {
		int location = 0;
		switch(direction) {
		case DirectionApp.DIRECTION_LEFT:
			location = DirectionApp.LOCATION_LEFT;
			break;
		case DirectionApp.DIRECTION_UP:
			location = DirectionApp.LOCATION_UP;
			break;
		case DirectionApp.DIRECTION_RIGHT:
			location = DirectionApp.LOCATION_RIGHT;
			break;
		case DirectionApp.DIRECTION_DOWN:
			location = DirectionApp.LOCATION_DOWN;
			break;
		}
		
		directionApps.get(location).componentName = null;
		directionApps.get(location).icon = null;
		directionApps.get(location).label = null;
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_DIRECTION_APP);
		contentResolver.delete(uri, FingerprintProvider.DIRECTION_APP_DIRECTION
				+ "=?", new String[] { String.valueOf(direction) });
	}

	@Override
	public void setAnswerCallEnabled(boolean enable) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_SETTINGS);
		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.SETTINGS_NAME, MainSwitchBtnItem.ID_ANSWER_CALL);
		values.put(FingerprintProvider.SETTINGS_VALUE, enable?1:0);

		contentResolver.update(uri, values,
				FingerprintProvider.SETTINGS_NAME + "=?",
				new String[] { String.valueOf(MainSwitchBtnItem.ID_ANSWER_CALL) });
		
		switchBtnItems.get(MainSwitchBtnItem.LOCATION_ANSWER_CALL).setEnable(enable);
	}

	@Override
	public void setCaptureEnabled(boolean enable) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_SETTINGS);
		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.SETTINGS_NAME, MainSwitchBtnItem.ID_CAPTURE);
		values.put(FingerprintProvider.SETTINGS_VALUE, enable?1:0);

		contentResolver.update(uri, values,
				FingerprintProvider.SETTINGS_NAME + "=?",
				new String[] { String.valueOf(MainSwitchBtnItem.ID_CAPTURE) });
		
		switchBtnItems.get(MainSwitchBtnItem.LOCATION_CAPTURE).setEnable(enable);
	}

	@Override
	public void setDismissAlarmEnabled(boolean enable) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_SETTINGS);
		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.SETTINGS_NAME, MainSwitchBtnItem.ID_DISMISS_ALARM);
		values.put(FingerprintProvider.SETTINGS_VALUE, enable?1:0);

		contentResolver.update(uri, values,
				FingerprintProvider.SETTINGS_NAME + "=?",
				new String[] { String.valueOf(MainSwitchBtnItem.ID_DISMISS_ALARM) });
		
		switchBtnItems.get(MainSwitchBtnItem.LOCATION_DISMISS_ALARM).setEnable(enable);
	}

	@Override
	public List<MainSwitchBtnItem> getSwitchBtnItems() {
		if (switchBtnItems == null) {
			switchBtnItems = new ArrayList<>();

			MainSwitchBtnItem answerCallitem = new MainSwitchBtnItem();
			answerCallitem.setId(MainSwitchBtnItem.ID_ANSWER_CALL);
			answerCallitem.setEnable(false);
			switchBtnItems.add(MainSwitchBtnItem.LOCATION_ANSWER_CALL,
					answerCallitem);

			MainSwitchBtnItem captureItem = new MainSwitchBtnItem();
			captureItem.setId(MainSwitchBtnItem.ID_CAPTURE);
			captureItem.setEnable(false);
			switchBtnItems.add(MainSwitchBtnItem.LOCATION_CAPTURE, captureItem);

			MainSwitchBtnItem dismissAlarmitem = new MainSwitchBtnItem();
			dismissAlarmitem.setId(MainSwitchBtnItem.ID_DISMISS_ALARM);
			dismissAlarmitem.setEnable(false);
			switchBtnItems.add(MainSwitchBtnItem.LOCATION_DISMISS_ALARM,
					dismissAlarmitem);

			loadSwitchBtnItems();
		}
		return switchBtnItems;
	}

	private void loadSwitchBtnItems() {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI_SETTINGS);
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor != null) {
			int count = cursor.getCount();
			Log.d(TAG, "count=" + count);
			while (cursor.moveToNext()) {
				int cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.SETTINGS_NAME);
				int name = cursor.getInt(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.SETTINGS_VALUE);
				int value = cursor.getInt(cursorIndex);

				switch (name) {
				case MainSwitchBtnItem.ID_ANSWER_CALL:
					switchBtnItems.get(MainSwitchBtnItem.LOCATION_ANSWER_CALL)
							.setEnable(value == 1);
					break;
				case MainSwitchBtnItem.ID_CAPTURE:
					switchBtnItems.get(MainSwitchBtnItem.LOCATION_CAPTURE)
							.setEnable(value == 1);
					break;
				case MainSwitchBtnItem.ID_DISMISS_ALARM:
					switchBtnItems
							.get(MainSwitchBtnItem.LOCATION_DISMISS_ALARM)
							.setEnable(value == 1);
					break;
				}
			}
			cursor.close();
		}
	}

}
