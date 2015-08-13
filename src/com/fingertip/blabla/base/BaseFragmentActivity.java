package com.fingertip.blabla.base;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.common.ProgressLoading;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

/**
 * 界面(FragmentActivity)基类，目前只设置竖屏，设置页面名称（友盟统计）
 * @author Administrator
 *
 */
public class BaseFragmentActivity extends FragmentActivity {
	
	private ProgressLoading progressLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		Globals.addActivity(this);
	}

	/** 
	 * @param isDismiss:是否可取消
	 *  **/
	public void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(BaseFragmentActivity.this);
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}//end showProgressDialog
	
	public void dimissProgressDialog() {
		if(progressLoading != null){
			progressLoading.dismiss();
		}
	}//end dimissProgressDialog

	@Override
	protected void onPause() {
		super.onPause();
//		// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
//		if(isCountPage){
//			if (page != null && page.trim().length() != 0){
//				MobclickAgent.onPageEnd(getClass().getCanonicalName() + "_" + page);
//			} else {
//				MobclickAgent.onPageEnd(getClass().getCanonicalName());
//			}
//		}
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
//		// 统计页面
//		if(isCountPage){
//			if (page != null && page.trim().length() != 0){
//				MobclickAgent.onPageStart(getClass().getCanonicalName() + "_" + page);
//			} else {
//				MobclickAgent.onPageStart(getClass().getCanonicalName());
//			}
//		}
//		MobclickAgent.onResume(this); // 统计时长
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
		Globals.removeActivity(this);
	}
	
	public void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
