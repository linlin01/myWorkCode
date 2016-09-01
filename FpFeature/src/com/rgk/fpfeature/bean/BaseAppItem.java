package com.rgk.fpfeature.bean;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class BaseAppItem {
	public ComponentName componentName;
	public String label;
	public Drawable icon;
	
	public BaseAppItem() {}
	
	public BaseAppItem(ComponentName componentName, String label, Drawable icon) {
		this.componentName = componentName;
		this.label = label;
		this.icon = icon;
	}

	public ComponentName getComponentName() {
		return componentName;
	}

	public void setComponentName(ComponentName componentName) {
		this.componentName = componentName;
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
