package com.fingertip.blabla.base;

import android.support.v4.app.Fragment;

import com.fingertip.blabla.common.ProgressLoading;

/**
 * ����(Fragment)���࣬Ŀǰֻ��������������ҳ�����ƣ�����ͳ�ƣ�
 * @author Administrator
 * 
 */
public class BaseFragment extends Fragment {
	
	private ProgressLoading progressLoading;
	
	/** 
	 * @param isDismiss:�Ƿ��ȡ��
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
//		// ��֤ onPageEnd ��onPause ֮ǰ����,��Ϊ onPause �лᱣ����Ϣ
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
//		//ͳ��ҳ��
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
