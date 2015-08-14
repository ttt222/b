package com.fingertip.blabla.my.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.util.http.ServerConstants;

public class SetSexActivity extends BaseActivity implements View.OnClickListener {
	
	private TextView sex_male, sex_female;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_sex);
		findViews();
		setupViews();
	}
	
	private void findViews() {
		sex_male = (TextView) findViewById(R.id.sex_male);
		sex_female = (TextView) findViewById(R.id.sex_female);
	}
	
	private void setupViews() {
		sex_male.setOnClickListener(this);
		sex_female.setOnClickListener(this);
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


	@Override
	public void onClick(View v) {
		int id = v.getId();
		String result = null;
		switch (id) {
		case R.id.sex_male:
			result = sex_male.getText().toString();
			break;
		case R.id.sex_female:
			result = sex_female.getText().toString();
			break;
		}
		if (result != null) {
			if (ServerConstants.SEX_MALE_S.equals(result))
				result = ServerConstants.SEX_MALE;
			else if (ServerConstants.SEX_FEMALE_S.equals(result))
				result = ServerConstants.SEX_FEMALE;
			else if (ServerConstants.SEX_UNKNOW_S.equals(result))
				result = ServerConstants.SEX_UNKNOW;
			Intent intent = new Intent();
			intent.putExtra(Globals.COMMON_RESULT, result);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}
