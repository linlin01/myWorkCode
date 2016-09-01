package com.rgk.fpfeature.model;

import java.util.List;

import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.DirectionApp;

public interface IDirectionModel {
	void saveDirectionApp(int direction, BaseAppItem chooseedApp);
	List<DirectionApp> getDirectionApps();
	void deleteDirectionApp(int direction);
}
