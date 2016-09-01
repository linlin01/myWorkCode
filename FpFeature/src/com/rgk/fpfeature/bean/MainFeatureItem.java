package com.rgk.fpfeature.bean;


public class MainFeatureItem {
	public static final int ID_INVALID = -1;
	public static final int ID_MULTIC_CLICK = 1;
	public static final int ID_FREEZE_APP = 2;
	public static final int ID_APP_LOCK = 3;
	public static final int ID_ENCRYPTION = 4;
	public static final int ID_DIRECTION_APP = 5;
	public static final int ID_FP_MANAGER = 6;
	
	int id = ID_INVALID;
	String name;
	int imgRes;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getImgRes() {
		return imgRes;
	}
	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}
	
	
}
