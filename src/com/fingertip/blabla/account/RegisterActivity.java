package com.fingertip.blabla.account;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
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

public class RegisterActivity extends BaseNavActivity implements View.OnClickListener {

	public static String RESET_PASSWORD = "reset_password";
	
	private EditText et_account;
	private TextView tv_next;
	private boolean reset_pwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initData();
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		et_account = (EditText)findViewById(R.id.et_phonenum);
		tv_next = (TextView)findViewById(R.id.btn_next);
	}
	
	protected void setupViews() {
		super.setupViews();
		if (reset_pwd)
			nav_title_txt.setText(getString(R.string.account_reset_pwd));
		else
			nav_title_txt.setText(getString(R.string.account_reg));
		tv_next.setOnClickListener(this);
		if (reset_pwd)
			tv_next.setText(getString(R.string.account_reset_pwd));
		et_account.addTextChangedListener(new TextWatcher() {		
			private String string_before = "";
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(et_account.getText().toString().length() > 11){
					et_account.setText(string_before);
					et_account.setSelection(start);
					toastShort(getResources().getString(R.string.enterElevenPhone));
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				string_before = et_account.getText().toString();
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private void initData() {
		reset_pwd = getIntent().getBooleanExtra(RESET_PASSWORD, false);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_next:
			if (reset_pwd)
				resetPassword();
			else
				sendMsg();
			break;
		}
	}
	
	private void sendMsg() {
		final String account = et_account.getText().toString();
		if (!Validator.isMobilePhone(account))
			toastShort(getResources().getString(R.string.enterElevenPhone));
		else {
			JSONObject data = new JSONObject();
//			{"fc":"user_reg_sendmsg", "phone":"18979528420"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_REG_SENDMSG);
				data.put(PARAM_KEYS.PHONE, account);
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.REG_SENDMSG, params, new RequestCallBack<String>() {
				@Override
				public void onStart() {
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
					String error = null;
					try {
						JSONObject json = new JSONObject(result);
						if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
							error = json.getString(PARAM_KEYS.RESULT_ERROR);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (error != null)
						toastShort(error);
					else {
						Intent intent = new Intent();
						intent.putExtra(AccountActivationActivity.EXTRA_PARAMS, account);
						intent.setClass(RegisterActivity.this, AccountActivationActivity.class);
						startActivity(intent);
					}
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					toastShort(ServerConstants.NET_ERROR_TIP);
				}
			});
		}
	}

	private void resetPassword() {
		final String account = et_account.getText().toString();
		if (!Validator.isMobilePhone(account))
			toastShort(getResources().getString(R.string.enterElevenPhone));
		else {
			JSONObject data = new JSONObject();
//			{"fc":"user_pass_reset", "userid":"18979528420"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_RESET_PASSWORD);
				data.put(PARAM_KEYS.USERID, account);
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.RESET_PASSWORD, params, new RequestCallBack<String>() {
				@Override
				public void onStart() {
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
					String error = null;
					try {
						JSONObject json = new JSONObject(result);
						if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
							error = json.getString(PARAM_KEYS.RESULT_ERROR);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (error != null)
						toastShort(error);
					else {
						toastShort(getString(R.string.account_reset_pwd_succeed));
						Intent intent = new Intent();
						intent.setClass(RegisterActivity.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					toastShort(ServerConstants.NET_ERROR_TIP);
				}
			});
		}
	}
}
