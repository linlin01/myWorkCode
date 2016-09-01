package com.rgk.fpfeature.bean;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class DirectionApp extends BaseAppItem {
	public static final int DIRECTION_INVALID = -1;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_RIGHT = 3;
	public static final int DIRECTION_DOWN = 4;
	
	public static final int LOCATION_LEFT = 0;
	public static final int LOCATION_UP = 1;
	public static final int LOCATION_RIGHT = 2;
	public static final int LOCATION_DOWN = 3;
	
	private int mDirection = DIRECTION_INVALID;
	
	public DirectionApp(ComponentName name, int mDirection) {
		this.componentName = name;
		this.mDirection = mDirection;
	}

	public ComponentName getName() {
		return componentName;
	}

	public void setName(ComponentName name) {
		this.componentName = name;
	}

	public int getDirection() {
		return mDirection;
	}

	public void setDirection(int mDirection) {
		this.mDirection = mDirection;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	
}
