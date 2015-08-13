package com.fingertip.blabla.base;

import android.support.v4.app.Fragment;

import com.fingertip.blabla.common.ProgressLoading;

/**
 * 界面(Fragment)基类，目前只设置竖屏，设置页面名称（友盟统计）
 * @author Administrator
 * 
 */
public class BaseFragment extends Fragment {
	
	private ProgressLoading progressLoading;
	
	/** 
	 * @param isDismiss:是否可取消
	 *  **/
	protected void showProgressDialog(boolean isDismiss) {
		if(progressLoading == null){
			progressLoading = new ProgressLoading(getActivity());
			progressLoading.setCancelable(isDismiss);
		}
		progressLoading.show();
	}//end showProgressDialog
	
	protected void dimissProgressDialog() {
		if(progressLoading != null){
			progressLoading.dismiss();
		}
	}//end dimissProgressDialog

	@Override
	public void onPause() {
		super.onPause();
//		// 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
//		if(isPageCount){
//			if (page != null && page.trim().length() != 0){
//				MobclickAgent.onPageEnd(getClass().getCanonicalName() + "_" + page);
//			} else {
//				MobclickAgent.onPageEnd(getClass().getCanonicalName());
//			}
//		}
//		MobclickAgent.onPause(getActivity().getApplicationContext());
	}//end onPause

	@Override
	public void onResume() {
		super.onResume();
//		//统计页面
//		if(isPageCount){
//			if (page != null && page.trim().length() != 0){
//				MobclickAgent.onPageStart(getClass().getCanonicalName() + "_" + page);
//			} else {
//				MobclickAgent.onPageStart(getClass().getCanonicalName());
//			}
//		}
//		MobclickAgent.onResume(getActivity().getApplicationContext());
	}//end onResume

	@Override
	public void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
	}
}
