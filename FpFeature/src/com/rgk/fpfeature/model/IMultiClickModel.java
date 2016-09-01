package com.rgk.fpfeature.model;

import java.util.List;

import com.rgk.fpfeature.bean.BaseAppItem;
import com.rgk.fpfeature.bean.MultiClickItem;

public interface IMultiClickModel {
	void saveMultiClickItem(int clickCount, BaseAppItem item);
	void deleteMultiClickItem(int clickCount);
	List<MultiClickItem> getMultiClickItems();
}
