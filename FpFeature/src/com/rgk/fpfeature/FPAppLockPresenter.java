package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.LockApp;
import com.rgk.fpfeature.model.ILockAppModel;

public class FPAppLockPresenter {
	ILockAppView view;
	ILockAppModel model;

	public FPAppLockPresenter(ILockAppView view, ILockAppModel model) {
		this.view = view;
		this.model = model;
	}

	void getLockApps(List<LockApp> apps) {
		model.getLockApps(apps);
	}
	
	public void lock(LockApp app, List<LockApp> lockedApps) {
		model.lock(app, lockedApps);
		view.lock(app);
	}
}
