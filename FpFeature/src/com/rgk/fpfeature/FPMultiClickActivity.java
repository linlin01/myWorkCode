package com.rgk.fpfeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.MultiClickItem;
import com.rgk.fpfeature.model.IMultiClickModel;
import com.rgk.fpfeature.model.MultiClickModelImpl;
import com.rgk.fpfeature.view.ScrollControlLayout;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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

public class FPMultiClickActivity extends BaseActivity implements IMultiClickView, ScrollControlLayout.OnScrollToScreenListener {
	private static final String DIALOG_FRAGMENT_TAG = "Dialog";
	
	enum State {
		IDLE, SETTING
	}
	
	ScrollControlLayout mContent;
	ListView mList;
	GridView mGrid;
	
	FPDeleteAppDialogFragment mDeleteAppDialogFragment;
	
	FPListAdapter mListAdapter;
	FPGridAdapter mGridAdapter;
	
	IMultiClickModel model;
	FPMultiClickPresenter presenter;
	
	List<MultiClickItem> multiClickItems;
	List<BaseAppItem> apps;
	
	State mState = State.IDLE;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fp_multi_click_activity);
		model = new MultiClickModelImpl(this);
		presenter = new FPMultiClickPresenter(this, model);
		multiClickItems = presenter.getMultiClickItems();
		apps = new ArrayList<>();
		
		initApps(apps, multiClickItems);
		
		mContent = (ScrollControlLayout) findViewById(R.id.content);
		mContent.setOnScrollToScreen(this);
		mList = (ListView) findViewById(R.id.list);
		mListAdapter = new FPListAdapter(multiClickItems, this);
		mList.setAdapter(mListAdapter);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				int clickCount = (int) id;
				
				presenter.setSettingCount(clickCount);
				
				switch (clickCount) {
				case MultiClickItem.CLICK_COUNT_DOUBLE:
					setTitle(R.string.set_double_title);
					break;
				case MultiClickItem.CLICK_COUNT_TRIPLE:
					setTitle(R.string.set_triple_title);			
					break;
				case MultiClickItem.CLICK_COUNT_FOURFOLD:
					setTitle(R.string.set_fourfold_title);
					break;
				}
				mContent.snapToScreen(1);
				mContent.setTouchMove(true);
				mState = State.SETTING;
			}
		
		});

		mDeleteAppDialogFragment =  new FPDeleteAppDialogFragment();
		mDeleteAppDialogFragment.setOnDeleteListener(new FPDeleteAppDialogFragment.OnDeleteListener() {
			
			@Override
			public void onClick() {
				presenter.deleteMultiClickItem();
			}
		});
		
		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				presenter.setSettingCount((int)id);
				mDeleteAppDialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
				return true;
			}
		});
		
		mGrid = (GridView) findViewById(R.id.grid);
		mGridAdapter = new FPGridAdapter(apps, this);
		mGrid.setAdapter(mGridAdapter);
		mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				presenter.saveMultiClickItem(apps.get(position));
				
				setTitle(R.string.feature_multi_click);
				mContent.snapToScreen(0);
				mContent.setTouchMove(true);
				mState = State.IDLE;
			}
		
		});
	}

	private void initApps(List<BaseAppItem> apps,
			List<MultiClickItem> multiClickItems) {
		PackageManager mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(Intent.ACTION_MAIN);
		queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> infos = mPackageManager.queryIntentActivities(
				queryIntent, PackageManager.MATCH_ALL);
		Collections.sort(infos, new ResolveInfo.DisplayNameComparator(
				mPackageManager));
		BaseAppItem app;
		for (ResolveInfo info : infos) {
			String activityName = info.activityInfo.name;
			String packageName = info.activityInfo.packageName;
			String appLabel = (String) info.loadLabel(mPackageManager);
			Drawable icon = info.loadIcon(mPackageManager);

			ComponentName componentName = new ComponentName(packageName,
					activityName);
			if (multiClickItems != null) {
				for (MultiClickItem item : multiClickItems) {
					if (componentName.equals(item.componentName)) {
						item.icon = icon;
						item.label = appLabel;
					}
				}
			}
			
			app = new BaseAppItem();
			app.componentName = componentName;
			app.icon = icon;
			app.label = appLabel;
			apps.add(app);
		}
	}
	
	class FPListAdapter extends BaseAdapter{
		List<MultiClickItem> items;
		Context mContext;
		LayoutInflater inflater;
		
		public FPListAdapter(List<MultiClickItem> items, Context context) {
			this.items = items;
			this.mContext = context;
			inflater = LayoutInflater.from(mContext);
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
			return items.get(position).clickCount;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;

			if (convertView == null || convertView.getTag() == null) {
				convertView = inflater.inflate(
						R.layout.fp_list_item_1, null);
				mHolder = new ViewHolder();
				mHolder.guide = (ImageView) convertView.findViewById(R.id.guide);
				mHolder.name = (TextView) convertView.findViewById(R.id.name);
				mHolder.label = (TextView) convertView.findViewById(R.id.label);
				mHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			
			MultiClickItem item = multiClickItems.get(position);
			
			switch (item.clickCount) {
			case MultiClickItem.CLICK_COUNT_DOUBLE:
				mHolder.guide.setImageResource(R.drawable.guide_double_click);
				mHolder.name.setText(R.string.double_click);
				break;
			case MultiClickItem.CLICK_COUNT_TRIPLE:
				mHolder.guide.setImageResource(R.drawable.guide_triple_click);
				mHolder.name.setText(R.string.triple_click);
				break;
			case MultiClickItem.CLICK_COUNT_FOURFOLD:
				mHolder.guide.setImageResource(R.drawable.guide_fourfold_click);
				mHolder.name.setText(R.string.fourfold_click);
				break;
			default:
				break;
			}
			
			if (item.label == null || item.label.isEmpty()) {
				mHolder.label.setText(R.string.not_set);
			} else {
				mHolder.label.setText(item.label);
			}
			if (item.icon == null) {
				mHolder.icon.setImageResource(R.drawable.fingerprint_arrow);
			} else {
				mHolder.icon.setImageDrawable(item.icon);
			}
		
			
			return convertView;
		}
		
		// Google I/O
		class ViewHolder {
			public ImageView guide;
			public TextView name;
			public ImageView icon;
			public TextView label;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mState == State.SETTING) {
			mContent.snapToScreen(0);
			setTitle(R.string.feature_multi_click);
			mContent.setTouchMove(false);
			mState = State.IDLE;
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
	class FPGridAdapter extends BaseAdapter {
		List<BaseAppItem> items;
		Context mContext;
		LayoutInflater inflater;
		
		public FPGridAdapter(List<BaseAppItem> items, Context context) {
			this.items = items;
			this.mContext = context;
			this.inflater = LayoutInflater.from(mContext);
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
				convertView = inflater.inflate(
						R.layout.fp_app_item_1, null);
				mHolder = new ViewHolder();
				mHolder.label = (TextView) convertView.findViewById(R.id.text);
				mHolder.icon = (ImageView) convertView.findViewById(R.id.image);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			
			BaseAppItem item = items.get(position);
			mHolder.icon.setImageDrawable(item.icon);
			mHolder.label.setText(item.label);
			
			return convertView;
		}
		
		// Google I/O
		class ViewHolder {
			public ImageView icon;
			public TextView label;
		}
	}

	@Override
	public void setMultiClickItem(int id) {
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void rmMultiClickItem(int id) {
		mListAdapter.notifyDataSetChanged();
	}

	@Override
	public void doAction(int whichScreen) {
		if (whichScreen == 0) {
			setTitle(R.string.feature_multi_click);
			mContent.setTouchMove(false);
			mState = State.IDLE;
		}
	};
	
	@Override
	protected void onHomeSelected() {
		super.onHomeSelected();
		if (mState == State.IDLE) {
			finish();
		} else {
			mContent.snapToScreen(0);
			setTitle(R.string.feature_multi_click);
			mContent.setTouchMove(false);
			mState = State.IDLE;
		}	
	}
}
