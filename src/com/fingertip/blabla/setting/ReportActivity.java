package com.fingertip.blabla.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.R;
import com.fingertip.blabla.account.LoginActivity;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.ServerConstants;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.common.ServerConstants.URL;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.Validator;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.OverlayEntityList.OverlayEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class ReportActivity extends BaseNavActivity implements View.OnClickListener {
	
	private RelativeLayout line1, line2, line3, line4;
	private ImageView check1, check2, check3, check4;
	private TextView text1, text2, text3, text4, content_txt;
	private Button commit_btn;
	private boolean checked1 = false, checked2 = false, checked3 = false, checked4 = false;
	
//	private String kindof = "", paramof = ""; 
	
	private OverlayEntity overlayEntity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		content_txt = (TextView) findViewById(R.id.report_content);
		commit_btn = (Button) findViewById(R.id.report_commit);

		text1 = (TextView) findViewById(R.id.text1);
		text2 = (TextView) findViewById(R.id.text2);
		text3 = (TextView) findViewById(R.id.text3);
		text4 = (TextView) findViewById(R.id.text4);
		check1 = (ImageView) findViewById(R.id.check1);
		check2 = (ImageView) findViewById(R.id.check2);
		check3 = (ImageView) findViewById(R.id.check3);
		check4 = (ImageView) findViewById(R.id.check4);
		line1 = (RelativeLayout) findViewById(R.id.line1);
		line2 = (RelativeLayout) findViewById(R.id.line2);
		line3 = (RelativeLayout) findViewById(R.id.line3);
		line4 = (RelativeLayout) findViewById(R.id.line4);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.report));
		commit_btn.setOnClickListener(this);
		line1.setOnClickListener(this);
		line2.setOnClickListener(this);
		line3.setOnClickListener(this);
		line4.setOnClickListener(this);
		
		overlayEntity = (OverlayEntity)getIntent().getSerializableExtra(BaseActivity.EXTRA_PARAM);
		if(overlayEntity == null){
			Toast.makeText(ReportActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
//		if (intent != null) {
//			kindof = intent.hasExtra(PARAM_KEYS.KINDOF) ? intent.getStringExtra(PARAM_KEYS.KINDOF) : "";
//			paramof = intent.hasExtra(PARAM_KEYS.PARAMOF) ? intent.getStringExtra(PARAM_KEYS.PARAMOF) : "";
//		}
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.report_commit:
			if(getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID).trim().length() == 0){
				Toast.makeText(ReportActivity.this, "请登录后操作", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(ReportActivity.this, LoginActivity.class);
				startActivity(intent);
			}else {
				
				commitReport();
			}
			
			break;
		case R.id.line1:
			checked1 = !checked1;
			checkReason(check1, checked1);
			break;
		case R.id.line2:
			checked2 = !checked2;
			checkReason(check2, checked2);
			break;
		case R.id.line3:
			checked3 = !checked3;
			checkReason(check3, checked3);
			break;
		case R.id.line4:
			checked4 = !checked4;
			checkReason(check4, checked4);
			break;
		}
	}
	
	private void checkReason(ImageView img, boolean checked) {
		if (checked)
			img.setImageResource(R.drawable.icon_checked);
		else
			img.setImageResource(R.drawable.icon_unchecked);
	}
	
	private String getReportReason() {
		StringBuilder buffer = new StringBuilder();
		String result = "";
		buffer.append(checked1 ? text1.getText().toString() + "," : "")
			.append(checked2 ? text2.getText().toString() + "," : "")
			.append(checked3 ? text3.getText().toString() + "," : "")
			.append(checked4 ? text4.getText().toString() + "," : "");
		if (buffer.length() > 0)
			result = "【" + buffer.substring(0, buffer.length() - 1) + "】";
		String content = content_txt.getText().toString();
		if (!Validator.isEmptyString(content))
			result += "," + content;
		return result;
	}
	
	private void commitReport() {
		String content = getReportReason();
		if (Validator.isEmptyString(content))
			toastShort("请至少输入一项举报原因");
		else {
			try {
//				{"fc":"msg_report", "userid":"18979528420", "loginid":"t4etskerghskdryhgsdfklhs",
//					 "kindof":"活动", "paramof":"524572457-634743583", "content":"【色情,广告】该用户约炮"}
				JSONObject data = new JSONObject();
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_REPORT);
				data.put(PARAM_KEYS.LOGINID, getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID));
				data.put(PARAM_KEYS.USERID, getSP().getStringValue(SharedPreferenceUtil.LAST_UID));
				data.put(PARAM_KEYS.CONTENT, content);
				data.put(PARAM_KEYS.KINDOF, overlayEntity.type.getType());
				data.put(PARAM_KEYS.PARAMOF, overlayEntity.actionid);
				
				//http://tutuapp.aliapp.com/_system/msg_feedback.php?command=eyJmYyI6Im1zZ19mZWVkYmFjayIs
				RequestParams params = new RequestParams();
				params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
				HttpUtils http = Tools.getHttpUtils();
				showProgressDialog(false);
				http.send(HttpRequest.HttpMethod.POST, URL.REPORT, params,
				    new RequestCallBack<String>() {

				        @Override
				        public void onSuccess(ResponseInfo<String> responseInfo) {
				        	dimissProgressDialog();
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
							if (error != null){
								toastShort(error);
							}else{
								toastShort("感谢您的举报，我们将尽快核实相关信息");
								finish();
							}
				        }

				        @Override
				        public void onFailure(HttpException error, String msg) {
				        	dimissProgressDialog();
				        	toastShort(ServerConstants.NET_ERROR_TIP);
				        }
				});
			} catch (JSONException e) {
			}
		}
	}//end commitReport
}
