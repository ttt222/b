package com.fingertip.blabla.my;

import java.util.Timer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.setting.SettingActivity;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityCallback;
import com.fingertip.blabla.util.http.UserUtil;
import com.lidroid.xutils.BitmapUtils;

public class MyIndexActivity extends BaseActivity implements View.OnClickListener {
	
	private ImageView my_head_img;
	private ImageView hidden_img;
	private ImageView msg_img;
	private TextView my_nick_txt;
	private TextView my_place_txt;
	
	private LinearLayout my_pub_event;
	private LinearLayout my_events;
	private LinearLayout my_msg_center;
	private LinearLayout my_watch_area;
	private LinearLayout my_watch_list;
	private LinearLayout my_setting;
	private LinearLayout my_info;
	
	private BitmapUtils bitmapUtils;
	private UserSession session;
	private ImageCache imageCache;
	private SharedPreferenceUtil sp;
	
	private Timer timer;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			msg_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_msg_red));
		};
	};
	
	@SuppressLint("RtlHardcoded")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_index);
		this.getWindow().setGravity(Gravity.LEFT);
		findViews();
		setupViews();
		loadUserInfo();
	}
	
	private void findViews() {
		my_head_img = (ImageView) findViewById(R.id.my_head_img);
		hidden_img = (ImageView) findViewById(R.id.hidden_img);
		msg_img = (ImageView) findViewById(R.id.msg_img);
		my_nick_txt = (TextView) findViewById(R.id.my_nick_txt);
		my_place_txt = (TextView) findViewById(R.id.my_place_txt);
		
		my_info = (LinearLayout) findViewById(R.id.my_info);
		my_pub_event = (LinearLayout) findViewById(R.id.my_pub_event);
		my_events = (LinearLayout) findViewById(R.id.my_events);
		my_msg_center = (LinearLayout) findViewById(R.id.my_msg_center);
		my_watch_area = (LinearLayout) findViewById(R.id.my_watch_area);
		my_watch_list = (LinearLayout) findViewById(R.id.my_watch_list);
		my_setting = (LinearLayout) findViewById(R.id.my_setting);
	}
	
	private void setupViews() {
		bitmapUtils = new BitmapUtils(this);
		sp = new SharedPreferenceUtil(this);
		session = UserSession.getInstance();
		imageCache = ImageCache.getInstance();
		
		my_info.setOnClickListener(this);
		my_pub_event.setOnClickListener(this);
		my_events.setOnClickListener(this);
		my_msg_center.setOnClickListener(this);
		my_watch_area.setOnClickListener(this);
		my_watch_list.setOnClickListener(this);
		my_setting.setOnClickListener(this);
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
	public void finish() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		if (v.getId() == R.id.my_setting) {
			intent = new Intent();
			intent.setClass(MyIndexActivity.this, SettingActivity.class);
		} else if (Tools.checkLogin(this)) {
			switch (v.getId()) {
			case R.id.my_info:
				intent = new Intent();
				intent.setClass(MyIndexActivity.this, MyInfoActivity.class);
				break;
			case R.id.my_msg_center:
				intent = new Intent();
				intent.setClass(MyIndexActivity.this, MessageCenterActivity.class);
				break;
			case R.id.my_events:
				intent = new Intent();
				intent.setClass(MyIndexActivity.this, MyEventActivity.class);
				break;
			case R.id.my_watch_list:
				intent = new Intent();
				intent.setClass(MyIndexActivity.this, MyWatchListActivity.class);
				break;
			case R.id.my_pub_event:
				Tools.pubEvent(this);
				break;
			}
		}
		if (intent != null)
			startActivity(intent);
	}
	
	private void loadUserInfo() {
		if (session.isLogin() && !Validator.isEmptyString(session.getId())) {
			UserUtil.getUserInfo(session.getId(), new EntityCallback<UserEntity>() {
				@Override
				public void succeed(UserEntity user) {
					setUserInfo(user);
				}
				
				@Override
				public void fail(String error) {
					toastShort(error + "\n«Î…‘∫Û÷ÿ ‘");
				}
			});
		} else 
			my_nick_txt.setText(getString(R.string.not_login_tip));
	}
	
	private void setUserInfo(UserEntity user) {
		String head_img_url = user.head_img_url;
		String nick_name = user.nick_name;
		String place = user.place;
		if (!Validator.isEmptyString(head_img_url)) {
			session.setHead_url(user.head_img_url);
			imageCache.loadUserHeadImg(head_img_url, session.getId(), sp, bitmapUtils, my_head_img, hidden_img);
		}
		if (!Validator.isEmptyString(nick_name)) {
			session.setNick_name(nick_name);
			my_nick_txt.setText(nick_name);
		}
		if (!Validator.isEmptyString(place)) {
			session.setPlace(place);
			my_place_txt.setText(place);
		}
		session.setMark(user.mark);
		session.setPlace(user.place);
		session.setSex(user.sex);
		session.setLoad_info(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (session.isLogin()) {
			if (session.isLoad_info())
				resetUserInfo();
			else
				loadUserInfo();
			if (sp.getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false))
				msg_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_msg_red));
			else
				msg_img.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_msg1));
		}
	}
	
	private void resetUserInfo() {
		my_nick_txt.setText(session.getNick_name());
		my_place_txt.setText(session.getPlace());
		String head_img_url = session.getHead_url();
		if (!imageCache.setUserHeadImg(session.getId(), my_head_img) && !Validator.isEmptyString(head_img_url))
			imageCache.loadUserHeadImg(head_img_url, session.getId(), sp, bitmapUtils, my_head_img, hidden_img);
	}
}
