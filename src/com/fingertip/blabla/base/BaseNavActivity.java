package com.fingertip.blabla.base;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.blabla.R;

public class BaseNavActivity extends BaseActivity {

	protected TextView nav_title_txt;
	protected ImageView back_btn;
	
	protected void findViews() {
		back_btn = (ImageView) findViewById(R.id.nav_back_btn);
		nav_title_txt = (TextView)findViewById(R.id.nav_title_txt);
	}

	protected void setupViews() {
		back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				finish();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
