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
import com.rgk.fpfeature.bean.LockApp;
import com.rgk.fpfeature.bean.MultiClickItem;

public class MultiClickModelImpl implements IMultiClickModel {
	private static final String TAG = "RgkFp.MultiClickModelImpl";

	private static final String URI = "content://"
			+ FingerprintProvider.AUTHORITIES + "/"
			+ FingerprintProvider.TABLE_MULTI_CLICK;
	

	Context mContext;
	List<MultiClickItem> items;
	
	public MultiClickModelImpl(Context context) {
		mContext = context;
		items = new ArrayList<>();
	}
	
	@Override
	public void saveMultiClickItem(final int clickCount, BaseAppItem item) {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		int location = 0;
		switch (clickCount) {
		case MultiClickItem.CLICK_COUNT_DOUBLE:
			location = MultiClickItem.LOCATION_DOUBLE;
			break;
		case MultiClickItem.CLICK_COUNT_TRIPLE:
			location = MultiClickItem.LOCATION_TRIPLE;
			break;
		case MultiClickItem.CLICK_COUNT_FOURFOLD:
			location = MultiClickItem.LOCATION_FOURFOLD;
			break;
		}
		
		
		String className = item.componentName.getClassName();
		String packageName = item.componentName.getPackageName();

		ContentValues values = new ContentValues();
		values.put(FingerprintProvider.MULIT_CLICK_COUNT, clickCount);
		values.put(FingerprintProvider.MULIT_CLICK_CLASS, className);
		values.put(FingerprintProvider.MULIT_CLICK_PACKAGE, packageName);
		
		if (items.get(location).componentName == null) {
			contentResolver.insert(uri, values);
		} else {
			contentResolver.update(uri, values,
					FingerprintProvider.MULIT_CLICK_COUNT + "=?",
					new String[] { String.valueOf(clickCount) });
		}
		
		items.get(location).componentName = item.componentName;
		items.get(location).icon = item.icon;
		items.get(location).label = item.label;
	}

	@Override
	public void deleteMultiClickItem(int clickCount) {
		int location = 0;
		switch (clickCount) {
		case MultiClickItem.CLICK_COUNT_DOUBLE:
			location = MultiClickItem.LOCATION_DOUBLE;
			break;
		case MultiClickItem.CLICK_COUNT_TRIPLE:
			location = MultiClickItem.LOCATION_TRIPLE;
			break;
		case MultiClickItem.CLICK_COUNT_FOURFOLD:
			location = MultiClickItem.LOCATION_FOURFOLD;
			break;
		}
		
		items.get(location).componentName = null;
		items.get(location).icon = null;
		items.get(location).label = null;
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		contentResolver.delete(uri, FingerprintProvider.MULIT_CLICK_COUNT
				+ "=?", new String[] { String.valueOf(clickCount) });
	}

	@Override
	public List<MultiClickItem> getMultiClickItems() {
		initItems();
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URI);
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		if (cursor != null) {
			int count = cursor.getCount();
			Log.d(TAG, "count=" + count);
			ComponentName name;
			while (cursor.moveToNext()) {
				int cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.MULIT_CLICK_COUNT);
				int multiClickCount = cursor.getInt(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.MULIT_CLICK_CLASS);
				String className = cursor.getString(cursorIndex);
				cursorIndex = cursor
						.getColumnIndex(FingerprintProvider.MULIT_CLICK_PACKAGE);
				String packageName = cursor.getString(cursorIndex);
				name = new ComponentName(packageName, className);
				
				switch (multiClickCount) {
				case MultiClickItem.CLICK_COUNT_DOUBLE:
					items.get(MultiClickItem.LOCATION_DOUBLE).componentName = name;
					break;
				case MultiClickItem.CLICK_COUNT_TRIPLE:
					items.get(MultiClickItem.LOCATION_TRIPLE).componentName = name;			
					break;
				case MultiClickItem.CLICK_COUNT_FOURFOLD:
					items.get(MultiClickItem.LOCATION_FOURFOLD).componentName = name;
					break;
				}
			}
			cursor.close();
		}
		
		return items;
	}

	private void initItems() {
		MultiClickItem doubleClickItem = new MultiClickItem();
		doubleClickItem.clickCount = MultiClickItem.CLICK_COUNT_DOUBLE;
		items.add(MultiClickItem.LOCATION_DOUBLE, doubleClickItem);
		
		MultiClickItem tripleClickItem = new MultiClickItem();
		tripleClickItem.clickCount = MultiClickItem.CLICK_COUNT_TRIPLE;
		items.add(MultiClickItem.LOCATION_TRIPLE, tripleClickItem);
		
		MultiClickItem fourfoldClickItem = new MultiClickItem();
		fourfoldClickItem.clickCount = MultiClickItem.CLICK_COUNT_FOURFOLD;
		items.add(MultiClickItem.LOCATION_FOURFOLD, fourfoldClickItem);
		
	}

}
