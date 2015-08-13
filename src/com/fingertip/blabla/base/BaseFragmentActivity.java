package com.fingertip.blabla.base;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.common.ProgressLoading;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Toast;

/**
 * ����(FragmentActivity)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
 * @author Administrator
 *
 */
public class BaseFragmentActivity extends FragmentActivity {
	
	private ProgressLoading progressLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ����
		Globals.addActivity(this);
	}

	/** 
	 * @param isDismiss:�Ƿ��ȡ��
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
//		// ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ
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
		
//		// ͳ��ҳ��
//		if(isCountPage){
//			if (page != null && page.trim().length() != 0){
//				MobclickAgent.onPageStart(getClass().getCanonicalName() + "_" + page);
//			} else {
//				MobclickAgent.onPageStart(getClass().getCanonicalName());
//			}
//		}
//		MobclickAgent.onResume(this); // ͳ��ʱ��
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
