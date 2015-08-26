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
 * ����(Activity)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//���� 
		
		Globals.addActivity(this);
	}
	
	/** ��ȡ������� **/
	protected SharedPreferenceUtil getSP() {
		if(sharedPreferenceUtil == null)
			sharedPreferenceUtil = new SharedPreferenceUtil(this);
		return sharedPreferenceUtil;
	}
	
	/** 
	 * @param isDismiss:�Ƿ��ȡ��
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
		
//		// ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ
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
		
//		//ͳ��ҳ��
//		if(page != null && page.trim().length() != 0){
//			MobclickAgent.onPageStart(getClass().getCanonicalName() + "_" + page); 
//		}else {
//			MobclickAgent.onPageStart(getClass().getCanonicalName()); 
//		}
//		
//		MobclickAgent.onResume(this);          //ͳ��ʱ��
		
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
