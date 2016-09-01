package com.rgk.fpfeature.model;

import java.util.List;

import com.rgk.fpfeature.bean.FreezeAppInfo;

public interface IFreezeModel {
	List<FreezeAppInfo> getFreezeApps();
	List<FreezeAppInfo> getNormalApps();
	void deleteFreezeApp(int uid, FreezeAppInfo info);
	void addFreezeApp(int uid, FreezeAppInfo info);
	
}
