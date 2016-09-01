package com.rgk.fpfeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.DirectionApp;
import com.rgk.fpfeature.model.ModelImpl;
import com.rgk.fpfeature.view.ScrollControlLayout;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FpDirectionActivity extends BaseActivity implements IDirectionView,  ScrollControlLayout.OnScrollToScreenListener{
	private static final String TAG = "RgkFp.DirectionActivity";

	private static final String DIALOG_FRAGMENT_TAG = "Dialog";

	enum State {
		IDLE, SETTING
	}

	GridView mGridView;
	ListView mListView;

	ScrollControlLayout mContent;
	DirectionAppAdapter mGridAdapter;
	DirectionListAdapter mListAdapter;

	FPDirectionPresenter presenter;
	ModelImpl model;

	List<BaseAppItem> mApps;
	List<DirectionApp> mDirectionApps;

	private State mState = State.IDLE;

	FPDeleteAppDialogFragment deleteFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		model = new ModelImpl(this);
		presenter = new FPDirectionPresenter(this, model);
		mApps = new ArrayList<>();
		mDirectionApps = presenter.getDirectionApps();
		initApps(mApps, mDirectionApps);

		setContentView(R.layout.fp_direction_activity);

		mContent = (ScrollControlLayout) findViewById(R.id.content);
		mContent.setOnScrollToScreen(this);
		mGridView = (GridView) mContent.findViewById(R.id.grid);
		mGridAdapter = new DirectionAppAdapter(this, mApps);
		mGridView.setAdapter(mGridAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				presenter.addDirectionApp(mApps.get(position));
				mContent.snapToScreen(0);
				mContent.setTouchMove(false);
				setTitle(R.string.direction_settings);
				mState = State.IDLE;
			}

		});

		mListView = (ListView) mContent.findViewById(R.id.list);
		mListAdapter = new DirectionListAdapter(this, mDirectionApps);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int direction = (int) id;
				switch (direction) {
				case DirectionApp.DIRECTION_LEFT:
					setTitle(R.string.move_left_title);
					break;
				case DirectionApp.DIRECTION_UP:
					setTitle(R.string.move_up_title);			
					break;
				case DirectionApp.DIRECTION_RIGHT:
					setTitle(R.string.move_right_title);
					break;
				case DirectionApp.DIRECTION_DOWN:
					setTitle(R.string.move_down_title);
					break;
				}
				presenter.setSettingDirection(direction);
				mContent.snapToScreen(1);
				mContent.setTouchMove(true);
				mState = State.SETTING;
			}
		});
		mListView
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						presenter.setSettingDirection((int) id);
						deleteFragment.show(getFragmentManager(),
								DIALOG_FRAGMENT_TAG);
						return true;
					}
				});

		deleteFragment = new FPDeleteAppDialogFragment();
		deleteFragment
				.setOnDeleteListener(new FPDeleteAppDialogFragment.OnDeleteListener() {
					@Override
					public void onClick() {
						presenter.rmDirectionApp();
					}

				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mState == State.SETTING) {
			mContent.snapToScreen(0);
			mContent.setTouchMove(false);
			setTitle(R.string.direction_settings);
			mState = State.IDLE;
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private void initApps(List<BaseAppItem> apps,
			List<DirectionApp> directionApps) {
		PackageManager mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(Intent.ACTION_MAIN);
		queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = mPackageManager.queryIntentActivities(
				queryIntent, PackageManager.MATCH_ALL);
		Collections.sort(infos, new ResolveInfo.DisplayNameComparator(
				mPackageManager));
		for (ResolveInfo info : infos) {
			String activityName = info.activityInfo.name;
			String packageName = info.activityInfo.packageName;
			String appLabel = (String) info.loadLabel(mPackageManager);
			Drawable icon = info.loadIcon(mPackageManager);

			ComponentName componentName = new ComponentName(packageName,
					activityName);
			if (directionApps != null) {
				for (DirectionApp app : directionApps) {
					if (componentName.equals(app.getName())) {
						app.setIcon(icon);
						app.setLabel(appLabel);
					}
				}
			}

			BaseAppItem appItem = new BaseAppItem(componentName, appLabel, icon);
			apps.add(appItem);
		}
	}

	class DirectionAppAdapter extends BaseAdapter {

		Context mContext;
		List<BaseAppItem> items;
		LayoutInflater mInflater;

		public DirectionAppAdapter(Context mContext, List<BaseAppItem> items) {
			this.mContext = mContext;
			this.items = items;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;

			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.fp_app_item_1, null);
				mHolder = new ViewHolder();
				mHolder.image = (ImageView) convertView
						.findViewById(R.id.image);
				mHolder.label = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			BaseAppItem item = items.get(position);
			mHolder.image.setImageDrawable(item.getIcon());
			mHolder.label.setText(item.getLabel());

			return convertView;
		}

		// Google I/O
		class ViewHolder {
			public ImageView image;
			public TextView label;
		}

	}

	class DirectionListAdapter extends BaseAdapter {

		Context mContext;
		List<DirectionApp> items;
		LayoutInflater mInflater;

		public DirectionListAdapter(Context mContext, List<DirectionApp> items) {
			this.mContext = mContext;
			this.items = items;
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
			return items.get(position).getDirection();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "DirectionListAdapter - getView:" + position);

			ViewHolder mHolder;

			if (convertView == null || convertView.getTag() == null) {
				convertView = mInflater.inflate(R.layout.fp_list_item_1, null);
				mHolder = new ViewHolder();
				mHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				mHolder.label = (TextView) convertView.findViewById(R.id.label);
				mHolder.name = (TextView) convertView.findViewById(R.id.name);
				mHolder.guide = (ImageView) convertView
						.findViewById(R.id.guide);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			DirectionApp item = items.get(position);
			switch (item.getDirection()) {
			case DirectionApp.DIRECTION_LEFT:
				mHolder.name.setText(R.string.direction_move_left);
				mHolder.guide.setImageResource(R.drawable.ic_fp_direction_left);
				break;
			case DirectionApp.DIRECTION_UP:
				mHolder.name.setText(R.string.direction_move_up);
				mHolder.guide.setImageResource(R.drawable.ic_fp_direction_up);
				break;
			case DirectionApp.DIRECTION_RIGHT:
				mHolder.name.setText(R.string.direction_move_right);
				mHolder.guide
						.setImageResource(R.drawable.ic_fp_direction_right);
				break;
			case DirectionApp.DIRECTION_DOWN:
				mHolder.name.setText(R.string.direction_move_down);
				mHolder.guide.setImageResource(R.drawable.ic_fp_direction_down);
				break;
			}

			if (item.getLabel() != null) {
				mHolder.label.setText(item.getLabel());
			} else {
				mHolder.label.setText(R.string.not_set);
			}

			if (item.getIcon() != null) {
				mHolder.icon.setImageDrawable(item.getIcon());
			} else {
				mHolder.icon.setImageResource(R.drawable.fingerprint_arrow);
			}

			return convertView;
		}

		// Google I/O
		class ViewHolder {
			public ImageView icon;
			public TextView label;
			public TextView name;
			public ImageView guide;
		}

	}

	@Override
	public void addDirectionApp(int direction) {
		// change list view ICON
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void rmDirectionApp(int direction) {
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void doAction(int whichScreen) {
		if (whichScreen == 0) {
			mContent.setTouchMove(false);
			setTitle(R.string.direction_settings);
			mState = State.IDLE;
		}
	}
	
	@Override
	protected void onHomeSelected() {
		super.onHomeSelected();
		if (mState == State.IDLE) {
			finish();
		} else {
			mContent.snapToScreen(0);
			mContent.setTouchMove(false);
			setTitle(R.string.direction_settings);
			mState = State.IDLE;
		}
		
	}
}
