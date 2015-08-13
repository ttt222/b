package com.fingertip.blabla.setting;

import android.os.Bundle;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;

public class AboutActivity extends BaseNavActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		findViews();
		setupViews();
	}
}
