package com.rgk.fpfeature.bean;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class LockApp extends BaseAppItem {
    public boolean locked = false;
	public long startTime;
	
	public LockApp(ComponentName componentName, long startTime) {
		this.componentName = componentName;
		this.startTime = startTime;
	}
	
	public LockApp(String appLabel, ComponentName componentName, Drawable icon, boolean isLocked) {
		this.label = appLabel;
		this.componentName = componentName;
		this.icon = icon;
		this.locked = isLocked;
	}
}
