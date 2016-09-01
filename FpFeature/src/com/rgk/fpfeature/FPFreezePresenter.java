package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.FreezeAppInfo;
import com.rgk.fpfeature.model.IFreezeModel;

public class FPFreezePresenter {
	IFreezeView view;
	IFreezeModel model;
	
	public FPFreezePresenter (IFreezeView view, IFreezeModel model) {
		this.view = view;
		this.model = model;
	}
	
	public List<FreezeAppInfo> getFreezeApps() {
		return model.getFreezeApps();
	}
	
	public List<FreezeAppInfo> getNormalApps() {
		return model.getNormalApps();
	}
	
	public void deleteFreezeApp(int uid, FreezeAppInfo mAppInfo) {
		model.deleteFreezeApp(uid, mAppInfo);
		view.deleteFreezeApp(mAppInfo);
	}

	public void addFreezeApp(int uid, FreezeAppInfo mAppInfo) {
		model.addFreezeApp(uid, mAppInfo);
		view.addFreezeApp(mAppInfo);
	}
}
