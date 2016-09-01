package com.rgk.fpfeature;

import java.util.List;


import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.MultiClickItem;
import com.rgk.fpfeature.model.IMultiClickModel;

public class FPMultiClickPresenter {
	IMultiClickView view;
	IMultiClickModel model;
	private int settingCount;
	
	public void setSettingCount(int count) {
		settingCount = count;
	}
	
	public FPMultiClickPresenter(IMultiClickView view, IMultiClickModel model) {
		this.view = view;
		this.model = model;
	}
	
	public List<MultiClickItem> getMultiClickItems() {
		return model.getMultiClickItems();
	}
	
	public void saveMultiClickItem(BaseAppItem item) {
		model.saveMultiClickItem(settingCount, item);
		view.setMultiClickItem(settingCount);
	}
	
	public void deleteMultiClickItem() {
		model.deleteMultiClickItem(settingCount);
		view.rmMultiClickItem(settingCount);
	}
}
