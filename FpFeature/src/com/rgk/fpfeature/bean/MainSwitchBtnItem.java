package com.rgk.fpfeature.bean;

public class MainSwitchBtnItem {
	public static final int ID_INVALID = -1;
	public static final int ID_ANSWER_CALL = 1;
	public static final int ID_CAPTURE = 2;
	public static final int ID_DISMISS_ALARM = 3;
	
	
	public static final int LOCATION_ANSWER_CALL = 0;
	public static final int LOCATION_CAPTURE = 1;
	public static final int LOCATION_DISMISS_ALARM = 2;
	
	int id = ID_INVALID;
	int nameRes;
	int imgRes;
	boolean enable;
	
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNameRes() {
		return nameRes;
	}
	public void setNameRes(int nameRes) {
		this.nameRes = nameRes;
	}
	public int getImgRes() {
		return imgRes;
	}
	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}
	
	
}
