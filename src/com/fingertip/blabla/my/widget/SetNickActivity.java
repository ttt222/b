package com.fingertip.blabla.my.widget;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;

public class SetNickActivity extends BaseActivity implements View.OnClickListener {
	
	private TextView set_text_title, set_text_save_btn;
	private EditText set_text_content;
	
	private String nick;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		nick = extras.getString(PARAM_KEYS.USER_NICK_NAME);
		setContentView(R.layout.activity_set_text);
		findViews();
		setupViews();
	}
	
	private void findViews() {
		set_text_title = (TextView) findViewById(R.id.set_text_title);
		set_text_save_btn = (TextView) findViewById(R.id.set_text_save_btn);
		set_text_content = (EditText) findViewById(R.id.set_text_content);
	}
	
	private void setupViews() {
		set_text_title.setText(getString(R.string.set_nick));
		if (nick != null) {
			set_text_content.setText(nick);
			set_text_content.setSelection(nick.length());
		}
		set_text_save_btn.setOnClickListener(this);
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
		case R.id.set_text_save_btn:
			result = set_text_content.getText().toString().trim();
			if (result.length() <= 0)
				Toast.makeText(this, "昵称不能为空，请重新输入", Toast.LENGTH_SHORT).show();
			else {
				//保存昵称到服务器
				if (nick != null && !result.equals(nick)) {
					Intent intent = new Intent();
					intent.putExtra(Globals.COMMON_RESULT, result);
					setResult(RESULT_OK, intent);
				}
				finish();
			}
			break;
		}
	}
}
