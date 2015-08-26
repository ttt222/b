package com.fingertip.blabla.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.common.ProgressLoading;
import com.fingertip.blabla.db.SharedPreferenceUtil;

/**
 * 界面(Activity)基类，目前只设置竖屏，设置页面名称（友盟统计）
 * @author Administrator
 *
 */
public class BaseActivity extends Activity{
	public static final String EXTRA_PARAM = "extra_param";
	
	private ProgressLoading progressLoading;
	
	private SharedPreferenceUtil sharedPreferenceUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏 
		
		Globals.addActivity(this);
	}
	
	/** 获取共享参数 **/
	protected SharedPreferenceUtil getSP() {
		if(sharedPreferenceUtil == null)
			sharedPreferenceUtil = new SharedPreferenceUtil(this);
		return sharedPreferenceUtil;
	}
	
	/** 
	 * @param isDismiss:是否可取消
	 *  **/
	public void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(this);
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}//end showProgressDialog
	
	public void dismissProgressDialog() {
		if(progressLoading != null){
			progressLoading.dismiss();
		}
	}//end dimissProgressDialog

	@Override
	protected void onPause() {
		super.onPause();
		
//		// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
//		if(page != null && page.trim().length() != 0){
//			MobclickAgent.onPageEnd(getClass().getCanonicalName() + "_" + page); 
//		}else {
//			MobclickAgent.onPageEnd(getClass().getCanonicalName()); 
//		}
//		
//		MobclickAgent.onPause(this);
	}//end onPause

	@Override
	protected void onResume() {
		super.onResume();
		
//		//统计页面
//		if(page != null && page.trim().length() != 0){
//			MobclickAgent.onPageStart(getClass().getCanonicalName() + "_" + page); 
//		}else {
//			MobclickAgent.onPageStart(getClass().getCanonicalName()); 
//		}
//		
//		MobclickAgent.onResume(this);          //统计时长
		
	}//end onResume
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dismissProgressDialog();
		Globals.removeActivity(this);
	}
	
	public void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
}
