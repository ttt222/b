package com.fingertip.blabla.main;

import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.Cmd;
import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.CommentEntity;
import com.fingertip.blabla.entity.OverlayEntityList.OverlayEntity;
import com.fingertip.blabla.util.Tools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class PublicRecommendActivity extends BaseActivity{
	private static final String TAG = "PublicRecommendActivity";
	public static final String EXTRA_COMMENT = "extra_comment";
	
	private EditText et_content;
	
	private OverlayEntity overlayEntity;
	
	/** ��Ϊ���ۻظ����ñ�����Ϊ�� **/
	private CommentEntity commentEntity;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publicrecommend);
		
		setupViews();
		
		initData();
	}//end onCreate

	private void setupViews() {
		findViewById(R.id.tv_more).setVisibility(View.GONE);
		et_content = (EditText)findViewById(R.id.et_content);
		
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		findViewById(R.id.tv_config).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				String string_content = et_content.getText().toString();
				
				if(string_content.length() == 0){
					Toast.makeText(PublicRecommendActivity.this, "��û��д����Ӵ��", Toast.LENGTH_SHORT).show();
					return;
				}else if(string_content.length() > 300){
					Toast.makeText(PublicRecommendActivity.this, "�ѳ���300�֣���������", Toast.LENGTH_SHORT).show();
					return;
				}else{
					showProgressDialog(false);
					if(commentEntity != null){
						requestPublicRecommendReply();
					}else {
						requestPublicRecommend();
					}
					
				}
			}
		});
	}//end setupViews

	private void initData(){
		overlayEntity =(OverlayEntity)getIntent().getSerializableExtra(BaseActivity.EXTRA_PARAM);
		
		if(overlayEntity == null){
			Toast.makeText(PublicRecommendActivity.this, "���ݴ���", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		commentEntity = (CommentEntity)getIntent().getSerializableExtra(EXTRA_COMMENT);
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		if(commentEntity != null){
			tv_title.setText("���ۻظ�");
		}else {
			tv_title.setText("��������");
		}
		
	}//end initData
	
	/** �������� **/
	private void requestPublicRecommend(){
		HttpUtils http_getpos = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "action_reply"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try { jsonObject.put("actionid", "" + overlayEntity.actionid); } catch (Exception e) { }
		try { jsonObject.put("content", et_content.getText().toString()); } catch (Exception e) { }
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString()));  
	    
	    Log.e(TAG, "onLoad before:" + jsonObject.toString());
		
		http_getpos.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_RECOMMEND,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) { }
		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	dismissProgressDialog();
		            Log.e(TAG, "...............onSuccess:" + Tools.decodeString(responseInfo.result));
		            
		            JSONObject jsonObject = null;
		            try {
						jsonObject = new JSONObject(Tools.decodeString(responseInfo.result));
						
						if("y".equals(jsonObject.getString("ok"))){
							Toast.makeText(PublicRecommendActivity.this, "���۳ɹ�", Toast.LENGTH_SHORT).show();
							finish();
						}else {
							Toast.makeText(PublicRecommendActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Toast.makeText(PublicRecommendActivity.this, "���ݴ���", Toast.LENGTH_SHORT).show();
						return;
					}
		        }//end onSuccess
		        @Override
		        public void onStart() { }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	dismissProgressDialog();
		        	Toast.makeText(PublicRecommendActivity.this, "�������", Toast.LENGTH_SHORT).show();
		        	Log.e(TAG, "............onFailure:" + msg);
		        }//end onFailure
		});
	}//end requestPublicRecommend
	
	/** ���ۻظ� **/
	private void requestPublicRecommendReply(){
		HttpUtils http_getpos = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "action_reply_reply"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try { jsonObject.put("actionid", "" + overlayEntity.actionid); } catch (Exception e) { }
		try { jsonObject.put("replyid", "" + commentEntity.id); } catch (Exception e) { }
		try { jsonObject.put("content", et_content.getText().toString()); } catch (Exception e) { }
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString()));  
	    
	    Log.e(TAG, "onLoad before:" + jsonObject.toString());
		
		http_getpos.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_RECOMMENDREPLY,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) { }
		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	dismissProgressDialog();
		            Log.e(TAG, "...............onSuccess:" + Tools.decodeString(responseInfo.result));
		            
		            JSONObject jsonObject = null;
		            try {
						jsonObject = new JSONObject(Tools.decodeString(responseInfo.result));
						
						if("y".equals(jsonObject.getString("ok"))){
							Toast.makeText(PublicRecommendActivity.this, "���ۻظ��ɹ�", Toast.LENGTH_SHORT).show();
							finish();
						}else {
							Toast.makeText(PublicRecommendActivity.this, "���ۻظ�ʧ��", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						Toast.makeText(PublicRecommendActivity.this, "���ݴ���", Toast.LENGTH_SHORT).show();
						return;
					}
		        }//end onSuccess
		        @Override
		        public void onStart() { }
		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	dismissProgressDialog();
		        	Toast.makeText(PublicRecommendActivity.this, "�������", Toast.LENGTH_SHORT).show();
		        }//end onFailure
		});
	}//end requestPublicRecommend
}
