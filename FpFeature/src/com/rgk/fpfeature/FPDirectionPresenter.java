package com.rgk.fpfeature;

import java.util.List;

import android.content.ComponentName;

import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.DirectionApp;
import com.rgk.fpfeature.model.IDirectionModel;

public class FPDirectionPresenter {
	IDirectionView view;
	IDirectionModel model;

	int settingDirection;

	public FPDirectionPresenter(IDirectionView view, IDirectionModel model) {
		this.view = view;
		this.model = model;
	}

	public List<DirectionApp> getDirectionApps() {
		return model.getDirectionApps();
	}

	public void addDirectionApp(BaseAppItem choosedApp) {
		model.saveDirectionApp(settingDirection, choosedApp);
		view.addDirectionApp(settingDirection);
	}

	public void setSettingDirection(int direction) {
		settingDirection = direction;
	}
	
	public void rmDirectionApp() {
		model.deleteDirectionApp(settingDirection);
		view.rmDirectionApp(settingDirection);
	}
}
