package com.fingertip.blabla.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference储存公共类
 * @author Administrator
 *
 */
public class SharedPreferenceUtil {
	@SuppressWarnings("unused")
	private Context context;
	/**SharePreferences对象 */
	private SharedPreferences sp ;
	/**修改Preferences对象*/
	private SharedPreferences.Editor editor;
	
	/** 用户id **/
	public static final String LAST_UID = "last_uid";
	/** 登录id(session) **/
	public static final String LAST_LOGIN_ID = "last_login_id";
	/** 引导页 **/
	public static final String NEED_GUIDE = "need_guide";
	
	/** 上一时间段纬度 **/
	public static final String LASTLOCATIONLAT = "lastlocationlat";
	/** 上一时间段经度 **/
	public static final String LASTLOCATIONLONG = "lastlocationlong";
	
	public static final String HEADIMAGE = "headImage";
	public static final String HEADIMAGE_FULL = "headImage_full";

	public static final String HAS_NEW_MESSAGE = "has_new_message";
	
	public SharedPreferenceUtil(Context context){
		this.context = context;
		sp = context.getSharedPreferences("blabla", 0);
		editor = sp.edit();
	}
	
	
	public void setLongValue(String key, long value){
		editor.putLong(key, value).commit();
	}
	
	public long getLongValue(String key){
		return sp.getLong(key, 0);
	}
	
	public void setStringValue(String key, String value){
		editor.putString(key, value).commit();
	}
	public String getStringValue(String key){
		return sp.getString(key, "");
	}
	
	public void setBooleanValue(String key, boolean value){
		editor.putBoolean(key, value).commit();
	}
	public boolean getBooleanValue(String key, boolean flag_default){
		return sp.getBoolean(key, flag_default);
	}
	
	public void setIntValue(String key, int value){
		editor.putInt(key, value).commit();
	}
	public int getIntValue(String key){
		return sp.getInt(key, -1);
	}
	
	public void setFloatValue(String key, float value){
		editor.putFloat(key, value).commit();
	}
	public float getFloatValue(String key){
		return sp.getFloat(key, -1);
	}
	
	public void setStringValue(String user_id, String key, String value){
		editor.putString(user_id + "." + key, value).commit();
	}
	
	public String getStringValue(String user_id, String key){
		return sp.getString(user_id + "." + key, "");
	}
	
}
