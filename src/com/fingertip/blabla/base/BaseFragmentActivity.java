package com.fingertip.blabla.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.common.ProgressLoading;
import com.umeng.analytics.MobclickAgent;

/**
 * 界面(FragmentActivity)基类，目前只设置竖屏，设置页面名称（友盟统计）
 * @author Administrator
 *
 */
public class BaseFragmentActivity extends FragmentActivity {
	
	protected boolean _count = false;
	
	private ProgressLoading progressLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		Globals.addActivity(this);
		setPageCount();
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
	}
	
	public void dimissProgressDialog() {
		if(progressLoading != null)
			progressLoading.dismiss();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (_count)
			MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (_count)
			MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
		Globals.removeActivity(this);
	}
	
	protected void setPageCount() {
	}
	
	public void toastShort(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toastLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}
