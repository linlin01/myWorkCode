package com.rgk.fpfeature;

import java.util.List;

import com.rgk.fpfeature.bean.MainSwitchBtnItem;
import com.rgk.fpfeature.model.IMainModel;

public class FPMainPresenter {
	IMainView view;
	IMainModel model;
	
	public FPMainPresenter(IMainView view, IMainModel model) {
		this.view = view;
		this.model = model;
	}
	
	void setAnswerCallEnabled(boolean enable) {
		model.setAnswerCallEnabled(enable);
		view.setAnswerCallEnabled(enable);
	}
	void setCaptureEnabled(boolean enable) {
		model.setCaptureEnabled(enable);
		view.setCaptureEnabled(enable);
	}
	void setDismissAlarmEnabled(boolean enable) {
		model.setDismissAlarmEnabled(enable);
		view.setDismissAlarmEnabled(enable);
	}
	
	List<MainSwitchBtnItem> getSwitchBtnItems() {
		return model.getSwitchBtnItems();
	}
}
