package com.fingertip.blabla.my;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.my.adapter.AdapterUserEvent;
import com.fingertip.blabla.my.widget.SendMsgActivity;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.DefaultCallback;
import com.fingertip.blabla.util.http.EntityCallback;
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.fingertip.blabla.util.http.UserUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UserInfoActivity extends BaseNavActivity implements View.OnClickListener {
	
	public static String KEY_USER = "user";
	public static String KEY_USER_ID = "user_id";
	
	private ImageView user_info_head_img, user_info_sex_img, hidden_img;
	private TextView user_info_name_txt, user_info_up_count_txt, user_info_place_txt, user_info_mark_txt, event_empty_txt;
	private Button user_info_watch_btn, user_info_chat_btn, user_info_mask_btn;
	private GifView gifView;
	private LinearLayout empty_area;
	private ListView listView;
	private AdapterUserEvent adapter;
	
	private UserEntity user;
	
	private BitmapUtils bitmapUtils;
	private UserSession session;
	private ImageCache imageCache;
	private SharedPreferenceUtil sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_user_info);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		user_info_head_img = (ImageView) findViewById(R.id.user_info_head_img);
		user_info_sex_img = (ImageView) findViewById(R.id.user_info_sex_img);
		hidden_img = (ImageView) findViewById(R.id.hidden_img);
		gifView = (GifView)findViewById(R.id.gifView);
		
		user_info_name_txt = (TextView) findViewById(R.id.user_info_name_txt);
		user_info_up_count_txt = (TextView) findViewById(R.id.user_info_up_count_txt);
		user_info_place_txt = (TextView) findViewById(R.id.user_info_place_txt);
		user_info_mark_txt = (TextView) findViewById(R.id.user_info_mark_txt);
		event_empty_txt = (TextView) findViewById(R.id.user_info_event_empty_txt);
		
		user_info_watch_btn = (Button) findViewById(R.id.user_info_watch_btn);
		user_info_chat_btn = (Button) findViewById(R.id.user_info_chat_btn);
		user_info_mask_btn = (Button) findViewById(R.id.user_info_mask_btn);
		
		empty_area = (LinearLayout) findViewById(R.id.user_info_event_empty);
		listView = (ListView) findViewById(R.id.user_info_event_listview);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_user_info);
		gifView.setGifImage(R.drawable.loading2);
		
		user_info_watch_btn.setOnClickListener(this);
		user_info_chat_btn.setOnClickListener(this);
		user_info_mask_btn.setVisibility(View.GONE);
		user_info_mask_btn.setOnClickListener(this);
		
		bitmapUtils = new BitmapUtils(this);
		session = UserSession.getInstance();
		imageCache = ImageCache.getInstance();
		sp = new SharedPreferenceUtil(this);
		
		adapter = new AdapterUserEvent(this, empty_area);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		loadUser();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//关注
		case R.id.user_info_watch_btn:
			watch();
			break;
		//私聊
		case R.id.user_info_chat_btn:
			showMsgDialog();
			break;
		//屏蔽
		case R.id.user_info_mask_btn:
			mask();
			break;
		}
	}
	
	private void loadUser() {
		Intent intent = this.getIntent();
		if (intent != null && (intent.hasExtra(KEY_USER) || intent.hasExtra(KEY_USER_ID))) {
			user = (UserEntity) intent.getSerializableExtra(KEY_USER);
			if (user == null) {
				String user_id = intent.getStringExtra(KEY_USER_ID);
				if (user_id == null)
					toastLong("未选择用户，请返回重试");
				else {
					UserUtil.getUserInfo(user_id, new EntityCallback<UserEntity>() {
						@Override
						public void succeed(UserEntity user) {
							UserInfoActivity.this.user = user;
							setUserInfo();
						}
						
						@Override
						public void fail(String error) {
							toastShort(error + "\n请返回重试");
						}
					});
				}
			} else
				setUserInfo();
		}
	}
	
	private void setUserInfo() {
//		 user_info_head_img, user_info_sex_img;
//		TextView user_info_name_txt, user_info_up_count_txt, user_info_province_txt, user_info_city_txt, user_info_mark_txt, user_info_event_empty_txt
		
		imageCache.loadUserHeadImg(user.head_img_url, user.id, sp, bitmapUtils, user_info_head_img, hidden_img);
		if (ServerConstants.SEX_FEMALE.equals(user.sex))
			user_info_sex_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_female));
		user_info_name_txt.setText(user.nick_name);
		user_info_up_count_txt.setText(user.up_count + "");
		user_info_place_txt.setText(user.place);
		user_info_mark_txt.setText(user.mark);
		
		//已关注
		if (session.isLogin() && session.getWatcher_list().contains(user.id)) {
			user_info_watch_btn.setBackgroundColor(getResources().getColor(R.color.gray_ad));
			user_info_watch_btn.setText("已关注");
			user_info_watch_btn.setOnClickListener(null);
		}
		
		loadUserEvents();
	}
	
	private void loadUserEvents() {
		JSONObject data = new JSONObject();
//		{"fc":"action_list_byuser", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "byuser":13641411876}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_USER_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.BYUSER, user.id);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_USER_EVENT, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取活动列表失败:" + e.getMessage();
				}
				if (error == null && json != null) {
					try {
						List<EventEntity> list = EventEntity.parseList(json);
						adapter.addAllList(list);
						if (list.isEmpty())
							event_empty_txt.setVisibility(View.VISIBLE);
						else
							empty_area.setVisibility(View.GONE);
					} catch (JSONException e) {
						e.printStackTrace();
						error = "获取用户活动失败:" + e.getMessage();
					}
				}
				if (error != null) {
					event_empty_txt.setVisibility(View.VISIBLE);
					event_empty_txt.setText(error);
				}
				gifView.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				toastShort(ServerConstants.NET_ERROR_TIP);
				gifView.setVisibility(View.GONE);
				event_empty_txt.setVisibility(View.VISIBLE);
				event_empty_txt.setText("获取用户活动失败");
			}
		});
	}
	
	private void watch() {
		if (Tools.checkLogin(this)) {
			showProgressDialog(false);
			UserUtil.editWatch(user.id, PARAM_VALUES.LINK_WATCH, new DefaultCallback() {
				@Override
				public void succeed() {
					toastShort("关注成功");
					session.getWatcher_list().add(user.id);
					user_info_watch_btn.setBackgroundColor(getResources().getColor(R.color.gray_ad));
					user_info_watch_btn.setText("已关注");
					user_info_watch_btn.setOnClickListener(null);
					dimissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort("关注失败\n" + error);
					dimissProgressDialog();
				}
			});
		}
	}
	
	private void showMsgDialog() {
		if (Tools.checkLogin(this)) {
			Intent intent = new Intent();
			intent.setClass(this, SendMsgActivity.class);
			startActivityForResult(intent, R.id.send_msg);
		}
	}

	private void sendMsg(String msg) {
		showProgressDialog(false);
		UserUtil.sendMsg(user.id, msg, new DefaultCallback() {
			@Override
			public void succeed() {
				toastShort("发送成功");
				dimissProgressDialog();
			}
			
			@Override
			public void fail(String error) {
				toastShort("发送失败\n" + error);
				dimissProgressDialog();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.send_msg:
				sendMsg(data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			}
		}
	}
	
	private void mask() {
		if (Tools.checkLogin(this)) {
			showProgressDialog(false);
			UserUtil.editWatch(user.id, PARAM_VALUES.LINK_HEI, new DefaultCallback() {
				@Override
				public void succeed() {
					toastShort("屏蔽成功");
					dimissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort("屏蔽失败\n" + error);
					dimissProgressDialog();
				}
			});
		}
	}
}
