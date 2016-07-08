package com.android.launcher3;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.app.WallpaperManager;

public class WallpaperTypeActivity extends Activity implements
		View.OnClickListener {
	static final String TAG = "WallpaperTypeActivity";
	private final String HOLOSPIRAL = "com.android.wallpaper.holospiral.HoloSpiralWallpaper";
	private final String GALAXY4 = "com.android.galaxy4.Galaxy4Wallpaper";
	private final String NEXUS = "com.android.wallpaper.nexus.NexusWallpaper";
	private final String PHASEBEAM = "com.android.phasebeam.PhaseBeamWallpaper";
	private final String MAGICSMOKE = "com.android.magicsmoke.MagicSmoke";
	private final String NOISEFIELD = "com.android.noisefield.NoiseFieldWallpaper";
	private final String MUSICVIS = "com.android.musicvis.vis3.Visualization3";
	private final String FALL = "com.android.wallpaper.fall.FallWallpaper";

	public static final String KEYGUARD_WALLPAPER = "keyguard_wallpaper";

	private final int REQUEST_SET_KEYGUARD_WALLPAPER = 100;
	private final int REQUEST_SET_HOME_WALLPAPER = 200;

	private View mItemLockScreen;
	private View mItemHomeScreen;
	private ImageView mIconLockScreen;
	private ImageView mIconHomeScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initViews();
		getCurrentWallpaper();
		super.onCreate(savedInstanceState);
	}

	private void initViews() {
		setContentView(R.layout.wallpaper_type);
		mItemLockScreen = findViewById(R.id.lock_screen_type);
		mIconLockScreen = (ImageView) findViewById(R.id.lock_screen_icon);
		mItemHomeScreen = findViewById(R.id.home_screen_type);
		mIconHomeScreen = (ImageView) findViewById(R.id.home_screen_icon);
		mItemLockScreen.setOnClickListener(this);
		mItemHomeScreen.setOnClickListener(this);

		ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // android.R.id.home will be triggered in onOptionsItemSelected()
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.choose_wallpaper_type);
        }
	}

	private void getKeyguardWallpaper() {
		WallpaperManager wm = WallpaperManager.getInstance(this);
		Drawable lockscreen = wm.peekDrawableForKeyguard();
		if (lockscreen == null) {
			Resources sysRes = Resources.getSystem();
			int res_id = sysRes.getIdentifier("keyguard_wallpaper", "drawable",
					"android");
			lockscreen = getResources().getDrawable(res_id);
		}
		mIconLockScreen.setBackgroundDrawable(lockscreen);
	}

	private void getHomeWallpaper() {
		WallpaperManager wm = WallpaperManager.getInstance(this);
		//modify by wangjian for keyguard wallpaper start
		if(wm.getWallpaperInfo() != null){
			if(wm.getWallpaperInfo().getServiceName().equals(HOLOSPIRAL)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_holospiral_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(GALAXY4)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_galaxy4_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(NEXUS)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_nexus_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(PHASEBEAM)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_phasebeam_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(MAGICSMOKE)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_magicsmoke_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(MUSICVIS)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_musicvis_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(FALL)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_fall_thumb);
			}else if(wm.getWallpaperInfo().getServiceName().equals(NOISEFIELD)){
				mIconHomeScreen.setBackgroundResource(R.drawable.wallpaper_noisefield_thumb);
			}else{
				Drawable homescreen = wm.getDrawable();
				mIconHomeScreen.setBackgroundDrawable(homescreen);
			}
		}else {
			Drawable homescreen = wm.getDrawable();
			mIconHomeScreen.setBackgroundDrawable(homescreen);
		}
		//modufy by wangjian for keyguard wallpaper start
	}

	private void getCurrentWallpaper() {
		getKeyguardWallpaper();
		getHomeWallpaper();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		Intent intent = null;
		switch (id) {
		case R.id.lock_screen_type:
			intent = new Intent(this, LauncherWallpaperPickerActivity.class);
			intent.putExtra(KEYGUARD_WALLPAPER, true);
			startActivityForResult(intent, REQUEST_SET_KEYGUARD_WALLPAPER);
			break;
		case R.id.home_screen_type:
			intent = new Intent(this, LauncherWallpaperPickerActivity.class);
			intent.putExtra(KEYGUARD_WALLPAPER, false);
			startActivityForResult(intent, REQUEST_SET_HOME_WALLPAPER);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (RESULT_OK == resultCode) {
			if (REQUEST_SET_KEYGUARD_WALLPAPER == requestCode) {
				getKeyguardWallpaper();
			} else if (REQUEST_SET_HOME_WALLPAPER == requestCode) {
				getHomeWallpaper();
			}
		}
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
        case android.R.id.home:
            finish();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
