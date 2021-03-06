package com.num.helpers;

import android.content.Context;

import com.num.utils.PreferencesUtil;

public class UserDataHelper {
	
	
	Context context;
	
	public UserDataHelper(Context context){
		this.context = context;
	}
	
	public int getDataCap() {
		return PreferencesUtil.getDataInt("datacap", context);
	}
	public void setDataCap(int dataCap) {
		PreferencesUtil.setDataInt("datacap", dataCap, context);
	}
	public int getBillingCycle() {
		return PreferencesUtil.getDataInt("billingcycle", context);
	}
	public void setBillingCycle(int billingCycle) {
		PreferencesUtil.setDataInt("billingcycle", billingCycle, context);
	}
	
	public void setDataEnable(int val) {
		PreferencesUtil.setDataInt("dataenable", val, context);
	}
	
	public int getDataEnable() {
		return PreferencesUtil.getDataInt("dataenable", context);
	}
	
	public boolean isFilled(){		
		return PreferencesUtil.contains("datacap", context) && 
				PreferencesUtil.contains("billingcycle", context) && 
				PreferencesUtil.contains("dataenable", context);
	}

}
