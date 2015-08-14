package com.fingertip.blabla.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class SuggestActivity extends BaseNavActivity implements View.OnClickListener {
	
	private TextView content_txt;
	private TextView phone_txt;
	private Button commit_btn;
	
	private UserSession session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		content_txt = (TextView) findViewById(R.id.suggest_content);
		phone_txt = (TextView) findViewById(R.id.suggest_phone);
		commit_btn = (Button) findViewById(R.id.suggest_commit);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.suggest_title));
		commit_btn.setOnClickListener(this);
		
		session = UserSession.getInstance();
		if (session.isLogin())
			phone_txt.setText(session.getId());
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.suggest_commit:
			commitSuggestion();
			break;
		}
	}
	
	private void commitSuggestion() {
		String content = content_txt.getText().toString();
		String phone = phone_txt.getText().toString();
		if (content == null || "".equals(content.trim()))
			toastShort("请输入反馈意见");
		else if (phone == null || "".equals(phone.trim()))
			toastShort("请输入联系电话");
		else {
			try {
				showProgressDialog(false);
				//{"fc":"msg_feedback", "userid":"18979528420", "loginid":"t4etskerghskdryhgsdfklhs", "feedback":"你们的软件太赞了"}
				JSONObject data = new JSONObject();
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_FEEDBACK);
				data.put(PARAM_KEYS.LOGINID, session.isLogin() ? session.getLogin_id() : "");
				data.put(PARAM_KEYS.USERID, phone);
				data.put(PARAM_KEYS.FEEDBACK, content);
				
				//http://tutuapp.aliapp.com/_system/msg_feedback.php?command=eyJmYyI6Im1zZ19mZWVkYmFjayIs
				RequestParams params = new RequestParams();
				params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
				HttpUtils http = Tools.getHttpUtils();
				http.send(HttpRequest.HttpMethod.POST, URL.FEEDBACK, params,
				    new RequestCallBack<String>() {

				        @Override
				        public void onSuccess(ResponseInfo<String> responseInfo) {
				        	String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
							String error = null;
							JSONObject json = null;
							try {
								json = new JSONObject(result);
								Log.e("feedback", json.toString());
								if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
									error = json.getString(PARAM_KEYS.RESULT_ERROR);
							} catch (JSONException e) {
								e.printStackTrace();
								error = "操作失败:" + e.getMessage();
							}
							dimissProgressDialog();
							if (error != null)
								toastShort(error);
							else {
								toastShort("感谢您的反馈，我们将尽快与您联系");
								finish();
							}
				        }

				        @Override
				        public void onFailure(HttpException error, String msg) {
				        	toastShort(ServerConstants.NET_ERROR_TIP);
				        	dimissProgressDialog();
				        }
				});
			} catch (JSONException e) {
			}
		}
	}
}
