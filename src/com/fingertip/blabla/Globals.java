package com.fingertip.blabla;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fingertip.blabla.main.MainActivity;

import android.app.Activity;

public class Globals {

	/** 手机目录名称 **/
	public static final String PATH_BASE = "blabla";
	/** 缓存目录 **/
	public static final String PATH_CACH = PATH_BASE + File.separator + "cache";

	public static final String UPLOAD_CACH = "upload";
	
	
	/** 网址根目录 **/
	public static final String URL = "http://tutuapp.aliapp.com";
	
	/** 微信appid **/
	public static final String APPID_WX = "wx2ba593638ec9fab5";
	/** 微信secret **/
	public static final String SECRET_WX = "336126680718253602e839242e67cbc5";
	
	
	
	public static final String COMMON_RESULT = "result";
	
	private static List<Activity> activity_list = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity) {
		activity_list.add(activity);
	}
	
	public static void removeActivity(Activity activity) {
		activity_list.remove(activity);
	}
	
	public static void clearActivityList(boolean all) {
		for (Iterator<Activity> it = activity_list.iterator(); it.hasNext(); ) {
			Activity activity = it.next();
			if (all || activity.getClass() != MainActivity.class) {
				activity.finish();
				it.remove();
			}
		}
	}
	
	public int getActivitySize() {
		return activity_list.size();
	}
}
