package com.fingertip.blabla.setting;

import java.io.File;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.account.LoginActivity;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.FileUtil;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;

public class SettingActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView cache_size_txt;
	private LinearLayout about;
	private LinearLayout suggest;
	private LinearLayout clear_cache;
	private LinearLayout logout;
	
	private String cache_dir;
	private SharedPreferenceUtil sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		cache_size_txt = (TextView) findViewById(R.id.cache_size_txt);
		about = (LinearLayout) findViewById(R.id.setting_about);
		suggest = (LinearLayout) findViewById(R.id.setting_suggest);
		clear_cache = (LinearLayout) findViewById(R.id.setting_delete_cache);
		logout = (LinearLayout) findViewById(R.id.setting_logout);
		sp = new SharedPreferenceUtil(this);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.setting_title);
		
		about.setOnClickListener(this);
		suggest.setOnClickListener(this);
		clear_cache.setOnClickListener(this);
		logout.setOnClickListener(this);
		
		cache_dir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Globals.PATH_CACH;
		
		getCacheSize();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.setting_about:
			intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			break;
		case R.id.setting_suggest:
			intent = new Intent();
			intent.setClass(this, SuggestActivity.class);
			break;
		case R.id.setting_delete_cache:
			clearCache();
			break;
		case R.id.setting_logout:
			UserSession.logout();
			sp.setStringValue(SharedPreferenceUtil.LAST_LOGIN_ID, "");
			intent = new Intent();
			Globals.clearActivityList(false);
			intent.setClass(this, LoginActivity.class);
			break;
		}
		if (intent != null)
			startActivity(intent);
	}
	
	private void getCacheSize() {
		cache_size_txt.setText(FileUtil.getCacheSize(cache_dir));
	}
	
	private void clearCache() {
		FileUtil.deleteDir(new File(cache_dir));
		getCacheSize();
		toastShort("缓存清理成功");
	}
	
}
