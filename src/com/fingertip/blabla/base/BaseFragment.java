package com.fingertip.blabla.base;

import android.support.v4.app.Fragment;

import com.fingertip.blabla.common.ProgressLoading;
import com.umeng.analytics.MobclickAgent;

/**
 * 界面(Fragment)基类，目前只设置竖屏，设置页面名称（友盟统计）
 * @author Administrator
 * 
 */
public class BaseFragment extends Fragment {
	
	protected boolean _count = false;
	protected String _page_name = null;
	
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
	}
	
	protected void dimissProgressDialog() {
		if(progressLoading != null)
			progressLoading.dismiss();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (_count)
			MobclickAgent.onPageEnd(_page_name);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (_count)
			MobclickAgent.onPageStart(_page_name);
	}
	
	protected void setPageName(String page_name) {
		this._count = true;
		this._page_name = page_name;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dimissProgressDialog();
	}
}
