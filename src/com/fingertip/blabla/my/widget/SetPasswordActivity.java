package com.fingertip.blabla.my.widget;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SetPasswordActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView set_password_btn;
	private EditText set_password_old, set_password_new, set_password_new2;
	
	private UserSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_password);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		set_password_btn = (TextView) findViewById(R.id.set_password_btn);
		set_password_old = (EditText) findViewById(R.id.set_password_old);
		set_password_new = (EditText) findViewById(R.id.set_password_new);
		set_password_new2 = (EditText) findViewById(R.id.set_password_new2);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.set_password));
		set_password_btn.setOnClickListener(this);
		session = UserSession.getInstance();
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.set_password_btn:
			setPassword();
			break;
		}
	}
	
	private void setPassword() {
		String old_pwd = set_password_old.getText().toString();
		String new_pwd1 = set_password_new.getText().toString();
		String new_pwd2 = set_password_new2.getText().toString();
		String msg = null;
		if (old_pwd.length() <= 0)
			msg = "请输入旧密码";
		else if (new_pwd1.length() <= 0)
			msg = "请输入新密码";
		else if (new_pwd2.length() <= 0)
			msg = "请输入确认新密码";
		else if (!new_pwd1.equals(new_pwd2))
			msg = "两次输入的新密码不一致";
		if (msg != null)
			toastShort(msg);
		else {
			showProgressDialog(false);
			JSONObject data = new JSONObject();
			try {
				//http://tutuapp.aliapp.com/_user/user_pass_edit.php
				//{"fc":"user_pass_edit", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "oldpass":"123456", "newpass":"abcdefg"}
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_USER_PASS);
				data.put(PARAM_KEYS.USERID, session.getId());
				data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
				data.put(PARAM_KEYS.USER_OLD_PASS, old_pwd);
				data.put(PARAM_KEYS.USER_NEW_PASS, new_pwd1);
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.EDIT_USER_PASS, params, new RequestCallBack<String>() {
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
					String error = null;
					try {
						JSONObject json = new JSONObject(result);
						if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
							error = json.getString(PARAM_KEYS.RESULT_ERROR);
					} catch (JSONException e) {
						error = "修改密码失败。" + e.getMessage();
						e.printStackTrace();
					}
					if (error != null)
						toastShort(error);
					else {
						toastShort("修改成功");
						finish();
					}
					dismissProgressDialog();
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					toastShort(ServerConstants.NET_ERROR_TIP);
					dismissProgressDialog();
				}
			});
		}
	}
}
