package com.fingertip.blabla.main;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.Cmd;
import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.ShareEntity;
import com.fingertip.blabla.entity.WatchEntity;
import com.fingertip.blabla.my.util.UserUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

public class AttentionSelectedActivity extends BaseActivity implements View.OnClickListener{
	private ListView listView;
	private AdapterAttentions adapterAttentions;
	
	private View view_nodata;
	private View layout_loading;
	private GifView gifView;
	private TextView tv_more;

	
	private ShareEntity shareEntity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_common);
		
		findViews();
		setupViews();
		
		loadData();
	}

	private void findViews() {
		findViewById(R.id.view_title).setVisibility(View.VISIBLE);
		tv_more = (TextView)findViewById(R.id.tv_more);
		
		listView = (ListView)findViewById(R.id.listView);
		
		view_nodata = findViewById(R.id.tv_nodata);
		layout_loading = findViewById(R.id.layout_loading);
		gifView = (GifView)findViewById(R.id.gifView);
		gifView.setGifImage(R.drawable.loading2);
	}

	private void setupViews() {
		tv_more.setText("发送");
		adapterAttentions = new AdapterAttentions(AttentionSelectedActivity.this);
		listView.setAdapter(adapterAttentions);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				adapterAttentions.putSelectPosition(position);
			}
		});
		
		tv_more.setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("联系人选择");
		
		shareEntity = (ShareEntity)getIntent().getSerializableExtra(BaseActivity.EXTRA_PARAM);
		
		if(shareEntity == null){
			Toast.makeText(AttentionSelectedActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
			finish();
		}
	}//endsetupViews

	private void setNodataVisible(){
		layout_loading.setVisibility(View.GONE);
		if(adapterAttentions.getCount() == 0){
			listView.setVisibility(View.GONE);
			view_nodata.setVisibility(View.VISIBLE);
		}else {
			listView.setVisibility(View.VISIBLE);
			view_nodata.setVisibility(View.GONE);
		}
	}
	
	private void loadData() {
		UserUtil.getUserWatchList(new UserUtil.EntityListCallback<WatchEntity>() {
			@Override
			public void succeed(List<WatchEntity> list) {
				adapterAttentions.addAllList(list);
				setNodataVisible();
			}
			
			@Override
			public void fail(String error) {
				toastShort(error);
				setNodataVisible();
			}
		});
	}//end loadData

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_more:
			requestSendMsg();
			break;

		default:
			break;
		}
	}//end onClick
	
	private int deleteCount = 0;
	
	private void deleteReduce(){
		deleteCount--;
		if(deleteCount <= 0){
			dimissProgressDialog();
			Toast.makeText(AttentionSelectedActivity.this, "发送完成", Toast.LENGTH_SHORT).show();
			finish();
		}
	}//end deleteReduce
	
	/** 消息发送 **/
	private void requestSendMsg(){
		HashMap<Integer, Boolean> hashMap_selected = adapterAttentions.getSelected();
		if(hashMap_selected.isEmpty()){
			Toast.makeText(AttentionSelectedActivity.this, "发送对象不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		Set<Integer> keys = hashMap_selected.keySet();
		showProgressDialog(false);
		deleteCount = keys.size();
		
		WatchEntity watchEntity = null;
		for (Integer position : keys) {
			watchEntity = (WatchEntity)adapterAttentions.getItem(position);
			try {
				requestSendMsg(watchEntity.user.id);
			} catch (Exception e) {
				Toast.makeText(AttentionSelectedActivity.this, "" + watchEntity.user.nick_name + "发送失败", Toast.LENGTH_SHORT).show();
				deleteReduce();
			}
			
		}
		
	}//end requestSendMsg
	
	/** 单个消息发送 **/
	private void requestSendMsg(String toid){
		HttpUtils http_getpos = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "send_msg"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try {  jsonObject.put("touserid", toid);  } catch (Exception e) { }
		try { 
			JSONObject jsonObject_says = new JSONObject();
			try {  jsonObject_says.put("kind", "text");  } catch (Exception e) { }
			try {  jsonObject_says.put("content", "" + shareEntity.shareContent);  } catch (Exception e) { }
			try {  jsonObject_says.put("quote", "" + shareEntity.shareTitle);  } catch (Exception e) { }
			try {  jsonObject_says.put("jumpparam", "actionid:" + shareEntity.aid);  } catch (Exception e) { }
			jsonObject.put("says", jsonObject_says); 
		} catch (Exception e) { }
		
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString()));  
	    
	    LogUtils.e("onLoad before:" + jsonObject.toString());
		
		http_getpos.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_SENDMSG,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	deleteReduce();
		        	LogUtils.e("...............onSuccess:" + Tools.decodeString(responseInfo.result));
		            
		        }//end onSuccess

		        @Override
		        public void onStart() {
		        	LogUtils.e(",,,,,,,,,,onStart");
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	deleteReduce();
		        	LogUtils.e("............onFailure:" + msg);
		        }
		});
	}//end requestSendMsg(String toid)
}
