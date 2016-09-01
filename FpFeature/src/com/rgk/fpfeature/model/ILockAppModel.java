package com.rgk.fpfeature.model;

import java.util.List;

import com.rgk.fpfeature.bean.LockApp;

public interface ILockAppModel {
	void lock(LockApp app, List<LockApp> lockedApps);
	void getLockApps(List<LockApp> apps);
}
