package com.rgk.fpfeature.model;

import java.util.List;

import com.rgk.fpfeature.bean.MainSwitchBtnItem;

public interface IMainModel {
	void setAnswerCallEnabled(boolean enable);
	void setCaptureEnabled(boolean enable);
	void setDismissAlarmEnabled(boolean enable);
	List<MainSwitchBtnItem> getSwitchBtnItems();
}
