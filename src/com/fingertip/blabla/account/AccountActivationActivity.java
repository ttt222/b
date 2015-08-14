package com.fingertip.blabla.account;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.my.MyIndexActivity;
import com.fingertip.blabla.util.LocationUtil;
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

/**
 * ’À∫≈º§ªÓ
 * @author Administrator
 *
 */
public class AccountActivationActivity extends BaseNavActivity implements View.OnClickListener {
	
	public static final String EXTRA_PARAMS = "extra_params";
	private EditText et_pwd;
	private EditText et_inviteNum;
	private TextView tv_noReceive;
	private TextView tv_submit;
	
	private String account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accountactivation);
		findViews();
		setupViews();
		initData();
	}

	protected void findViews() {
		super.findViews();
		et_pwd = (EditText)findViewById(R.id.et_pwd);
		et_inviteNum = (EditText)findViewById(R.id.et_invitenum);
		tv_noReceive = (TextView)findViewById(R.id.tv_noreceive);
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		
	}

	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.account_reg));
		tv_noReceive.setOnClickListener(this);
		tv_submit.setOnClickListener(this);
	}

	private void initData() {
		account = getIntent().getStringExtra(EXTRA_PARAMS);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_noreceive:
//			Intent intent = new Intent();
//			intent.setClass(LoginActivity.this, RegisterActivity.class);
//			startActivity(intent);
			break;
		case R.id.tv_submit:
			active();
			break;
		}
	}
	
	private void active() {
		String password = et_pwd.getText().toString();
		@SuppressWarnings("unused")
		String invite_num = et_inviteNum.getText().toString();
		if (!Validator.isPassword(password))
			toastShort(getResources().getString(R.string.enterRightPwd));
		else {
			Location location = LocationUtil.getLocation(this);

			JSONObject data = new JSONObject();
//			{"fc":"user_login","userid":1257053, "pass":"123456", "poslong":"113.3261", "poslat":"23.1330"}
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_LOGIN);
				data.put(PARAM_KEYS.USERID, account);
				data.put(PARAM_KEYS.PASSWORD, password);
				data.put(PARAM_KEYS.POSLAT, location == null ? "" : location.getLatitude());
				data.put(PARAM_KEYS.POSLONG, location == null ? "" : location.getLongitude());
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, URL.LOGIN, params, new RequestCallBack<String>() {
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
					else
						loginSucceed();
				}
				
				@Override
				public void onFailure(HttpException error, String msg) {
					toastShort(ServerConstants.NET_ERROR_TIP);
				}
			});
		}
	}
	
	private void loginSucceed() {
		Intent intent = new Intent();
		intent.setClass(AccountActivationActivity.this, MyIndexActivity.class);
		startActivity(intent);
		finish();
	}
}
