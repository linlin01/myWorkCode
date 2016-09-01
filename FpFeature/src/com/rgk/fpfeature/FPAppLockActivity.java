package com.rgk.fpfeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rgk.fpfeature.bean.LockApp;
import com.rgk.fpfeature.model.LockAppModelImpl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class FPAppLockActivity extends BaseActivity implements ILockAppView {
	private static final String TAG = "RgkFp.FPAppLockActivity";

	ListView mListView;
	TextView topTitle;

	FPAppLockPresenter presenter;
	LockAppModelImpl model;

	List<LockApp> unlockItems = new ArrayList<>();
	List<LockApp> lockItems = new ArrayList<>();
	private LockApp titleItemLock;
	private LockApp titleItemUnLock;
	AppLockAdapter mAdapter;
	PackageManager mPackageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerprint_app_lock_activity);

		model = new LockAppModelImpl(this);
		presenter = new FPAppLockPresenter(this, model);

		presenter.getLockApps(FPAppLockService.lockedApps);

		mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(Intent.ACTION_MAIN);
		queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = mPackageManager.queryIntentActivities(
				queryIntent, PackageManager.MATCH_ALL);
		Collections.sort(infos, new ResolveInfo.DisplayNameComparator(
				mPackageManager));
		lockItems.clear();
		unlockItems.clear();
		for (ResolveInfo info : infos) {
			String activityName = info.activityInfo.name;
			String packageName = info.activityInfo.packageName;
			String appLabel = (String) info.loadLabel(mPackageManager);
			Drawable icon = info.loadIcon(mPackageManager);

			boolean isLocked = false;
			ComponentName componentName = new ComponentName(packageName,
					activityName);

			for (LockApp app : FPAppLockService.lockedApps) {
				if (componentName.equals(app.componentName)) {
					isLocked = true;
					break;
				} else if (LockAppModelImpl.CLASS_NAME_MMS.equals(componentName
						.getClassName())) {
					if ("com.android.mms.ui.ConversationList"
							.equals(app.componentName.getClassName())) {
						isLocked = true;
						break;
					}
				}
			}

			LockApp item = new LockApp(appLabel, componentName, icon, isLocked);
			if (!isLocked) {
				unlockItems.add(item);
			} else {
				lockItems.add(item);
			}
		}

		Log.d(TAG, "unlockItems: " + unlockItems.size());
		Log.d(TAG, "lockItems: " + lockItems.size());

		initListTitles(unlockItems.size(), lockItems.size());

		mListView = (ListView) findViewById(R.id.app_lock_list);
		mAdapter = new AppLockAdapter(unlockItems, lockItems,
				getLayoutInflater());
		mListView.setAdapter(mAdapter);
		
		topTitle = (TextView) findViewById(R.id.top_title);
		if (lockItems.isEmpty()) {
			topTitle.setText(getString(R.string.title_unlock_app,
					unlockItems.size()));
		} else {
			topTitle.setText(getString(R.string.title_lock_app,
					lockItems.size()));
		}
		
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Log.d(TAG, "firstVisibleItem:"+firstVisibleItem);
				if (!lockItems.isEmpty()) {
					if(firstVisibleItem > lockItems.size()) {
						topTitle.setText(getString(R.string.title_unlock_app,
								unlockItems.size()));
					} else {
						topTitle.setText(getString(R.string.title_lock_app,
								lockItems.size()));
					}
				}
			}
		});
	}

	private void initListTitles(int unlockAppCount, int lockAppCount) {
		titleItemLock = new LockApp(null, 0);
		titleItemLock.icon = null;
		titleItemLock.label = getString(R.string.title_lock_app, lockAppCount);
		titleItemLock.locked = false;

		titleItemUnLock = new LockApp(null, 0);
		titleItemUnLock.icon = null;
		titleItemUnLock.label = getString(R.string.title_unlock_app,
				unlockAppCount);
		titleItemUnLock.locked = false;
	}

	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			int position = (int) v.getTag();
			Log.d(TAG, "onClick:" + position);
			LockApp item;
			if (position > 0 && position < lockItems.size() + 1) {
				item = lockItems.get(position - 1);
			} else {
				item = unlockItems.get(position
						- (lockItems.isEmpty() ? 0 : (lockItems.size() + 1))
						- 1);
			}
			
			Switch btn = (Switch) v;
			btn.setChecked(!item.locked);
			
			presenter.lock(item, FPAppLockService.lockedApps);
			
		}
	};

	class AppLockAdapter extends BaseAdapter {

		private List<LockApp> unlockItems;
		private List<LockApp> lockItems;
		private LayoutInflater mInflater;

		public AppLockAdapter(List<LockApp> unlockItems,
				List<LockApp> lockItems, LayoutInflater mInflater) {
			this.unlockItems = unlockItems;
			this.lockItems = lockItems;
			this.mInflater = mInflater;
		}

		@Override
		public int getCount() {
			return (lockItems.isEmpty() ? 0 : (lockItems.size() + 1))
					+ unlockItems.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0 && !lockItems.isEmpty()) {
				return titleItemLock;
			} else if (position > 0 && position < lockItems.size() + 1) {
				return lockItems.get(position - 1);
			} else if (!lockItems.isEmpty() && position == lockItems.size() + 1
					|| lockItems.isEmpty() && position == 0) {
				return titleItemUnLock;
			} else {
				return unlockItems.get(position
						- (lockItems.isEmpty() ? 0 : (lockItems.size() + 1))
						- 1);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "getView:" + position);
			ViewHolder mHolder;
			LockApp item = null;
			if (position == 0
					|| (!lockItems.isEmpty() && position == lockItems.size() + 1)) {
				if (lockItems.size() != 0 && position == 0) {
					Log.d(TAG, "Locked");
					item = titleItemLock;
				} else {
					Log.d(TAG, "Unlock");
					item = titleItemUnLock;
				}
			} else {
				if (position > 0 && position < lockItems.size() + 1) {
					item = lockItems.get(position - 1);
				} else {
					item = unlockItems
							.get(position
									- (lockItems.isEmpty() ? 0 : (lockItems
											.size() + 1)) - 1);
				}
			}

			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(
						R.layout.fingerprint_app_lock_item, null);
				mHolder = new ViewHolder();
				mHolder.image = (ImageView) convertView
						.findViewById(R.id.image);
				mHolder.label = (TextView) convertView.findViewById(R.id.text);
				mHolder.lock = (Switch) convertView.findViewById(R.id.lock);
				mHolder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			if (item.icon == null) {
				mHolder.image.setVisibility(View.GONE);
				mHolder.lock.setVisibility(View.GONE);
				mHolder.label.setVisibility(View.GONE);
				mHolder.title.setVisibility(View.VISIBLE);

				mHolder.title.setText(item.label);
			} else {
				mHolder.image.setVisibility(View.VISIBLE);
				mHolder.lock.setVisibility(View.VISIBLE);
				mHolder.label.setVisibility(View.VISIBLE);
				mHolder.title.setVisibility(View.GONE);

				mHolder.image.setImageDrawable(item.icon);
				mHolder.label.setText(item.label);
				if (item.locked) {
					mHolder.lock.setChecked(true);
				} else {
					mHolder.lock.setChecked(false);
				}
				mHolder.lock.setTag(position);
				mHolder.lock.setOnClickListener(clickListener);
			}

			return convertView;
		}

		// Google I/O
		class ViewHolder {
			public ImageView image;
			public TextView label;
			public Switch lock;
			public TextView title;
		}
	}

	@Override
	public void lock(LockApp app) {
		if (app.locked) {
			if (FPAppLockService.lockedApps.size() == 1) {
				Intent intent = new Intent(FPAppLockActivity.this,
						FPAppLockService.class);
				intent.putExtra(FPAppLockService.APP_LOCK_CMD,
						FPAppLockService.CMD_START_LISTION);
				startService(intent);
			}
		} else {
			if (FPAppLockService.lockedApps.size() == 0) {
				Intent intent = new Intent(FPAppLockActivity.this,
						FPAppLockService.class);
				intent.putExtra(FPAppLockService.APP_LOCK_CMD,
						FPAppLockService.CMD_STOP_LISTION);
				startService(intent);
				stopService(intent);
			}
		}
	}
	
	@Override
	protected void onHomeSelected() {
		super.onHomeSelected();
		finish();
	}
}
