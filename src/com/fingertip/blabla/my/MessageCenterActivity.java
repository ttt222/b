package com.fingertip.blabla.my;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.MessageEntity;
import com.fingertip.blabla.my.adapter.AdapterMessage;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.UserUtil;

public class MessageCenterActivity extends BaseNavActivity implements View.OnClickListener {
	
	private ImageView nav_delete_btn, nav_right_btn;
	private TextView tv_cancel;
	private Button btn_delete;
	private LinearLayout msg_empty;
	private ListView listView;
	private AdapterMessage adapter;
	
	private UserSession session;
	private SharedPreferenceUtil sp;
	
	private View view_loading;
	private boolean delete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_center);
		findViews();
		setupViews();
		
		showLoadingView(true);
		loadData();
	}
	
	protected void findViews() {
		super.findViews();
		
		view_loading = findViewById(R.id.layout_loading);
		GifView gifView = (GifView)findViewById(R.id.gifView);
		gifView.setGifImage(R.drawable.loading2);
		
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		nav_delete_btn = (ImageView) findViewById(R.id.nav_right_btn2);
		nav_right_btn = (ImageView) findViewById(R.id.nav_right_btn);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		
		msg_empty = (LinearLayout) findViewById(R.id.msg_empty);
		listView = (ListView) findViewById(R.id.msg_listView);
		adapter = new AdapterMessage(this, msg_empty);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
	}
	
	private void showLoadingView(boolean flag){
		if(flag){
			view_loading.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			view_loading.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
		}
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.msg_center);
		tv_cancel.setOnClickListener(this);
		nav_right_btn.setVisibility(View.GONE);
		nav_delete_btn.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_event_delete));
		nav_delete_btn.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		session = UserSession.getInstance();
		sp = new SharedPreferenceUtil(this);
	}

	private void loadData() {
		//更新消息中心状态
		sp.setBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false);
		//先加载本地消息
		new AsyncTask<String, Integer, List<MessageEntity>>() {

			@Override
			protected List<MessageEntity> doInBackground(String... params) {
				return Tools.getMessages(MessageCenterActivity.this, session.getId(), 10, 1);
			}
			
			protected void onPostExecute(List<MessageEntity> result) {
				if (!Validator.isEmptyList(result))
					adapter.appendList(result);
			};

		}.execute();
		//从服务器读取消息
		UserUtil.loadUserMsg(new EntityListCallback<MessageEntity>() {
			
			@Override
			public void succeed(List<MessageEntity> list) {
				showLoadingView(false);
				adapter.insertList(list);
				if (!Validator.isEmptyList(list)) {
					for (MessageEntity msg : list)
						msg.receiver_id = session.getId();
					Tools.saveMessages(MessageCenterActivity.this, list);
				}
			}
			
			@Override
			public void fail(String error) {
				showLoadingView(false);
				toastShort(error);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (delete)
					cancelDelete();
				else
					finish();
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nav_right_btn2:
			beginDelete();
			break;
		case R.id.tv_cancel:
			cancelDelete();
			break;
		case R.id.btn_delete:
			doDelete();
			break;
		}
	}

	private void beginDelete() {
		if (adapter.size() > 0) {
			nav_delete_btn.setVisibility(View.GONE);
			tv_cancel.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			adapter.begainDelete();
			delete = true;
		} else
			toastShort("无数据可删除");
	}
	
	private void cancelDelete() {
		nav_delete_btn.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		adapter.endDelete();
		delete = false;
	}
	
	private void doDelete() {
		if (adapter.selectedSize() > 0)
			adapter.doDelete();
		else
			toastShort("请至少选择一项");
	}
	
	public void finishDelete() {
		nav_delete_btn.setVisibility(View.VISIBLE);
		tv_cancel.setVisibility(View.GONE);
		btn_delete.setVisibility(View.GONE);
		adapter.endDelete();
		delete = false;
	}
	
	public void setSelectedCount(int count) {
		if (count > 0) {
			btn_delete.setText("(" + count + ") 删除");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.blue_msg));
		} else {
			btn_delete.setText("删除");
			btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		}
	}
}
