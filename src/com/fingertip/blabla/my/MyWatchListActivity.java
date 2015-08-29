package com.fingertip.blabla.my;

import java.util.List;

import android.content.Intent;
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
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.entity.WatchEntity;
import com.fingertip.blabla.my.adapter.AdapterMyWatch;
import com.fingertip.blabla.util.UmengConfig.PAGE;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.UserUtil;

public class MyWatchListActivity extends BaseNavActivity implements View.OnClickListener {
	
	private ImageView add_watch_btn, delete_watch_btn;
	private TextView tv_cancel;
	private ListView listView;
	private Button my_watch_find_btn, btn_delete;
	private AdapterMyWatch adapter;
	private LinearLayout empty_area;
	private View layout_loading;
	private GifView gifView;
	
	private boolean loading;
	private boolean delete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_watch);
		findViews();
		setupViews();
		loadData();
	}
	
	protected void findViews() {
		super.findViews();
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		add_watch_btn = (ImageView) findViewById(R.id.nav_right_btn);
		delete_watch_btn = (ImageView) findViewById(R.id.nav_right_btn2);
		empty_area = (LinearLayout) findViewById(R.id.my_watch_empty);
		my_watch_find_btn = (Button) findViewById(R.id.my_watch_find_btn);
		btn_delete = (Button) findViewById(R.id.btn_delete);
		listView = (ListView) findViewById(R.id.my_watch_listView);
		layout_loading = findViewById(R.id.layout_loading);
		gifView = (GifView)findViewById(R.id.gifView);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_watch_list);
		add_watch_btn.setImageDrawable(getResources().getDrawable(R.drawable.icon_add_watch));
		delete_watch_btn.setImageDrawable(getResources().getDrawable(R.drawable.icon_my_event_delete));
		layout_loading.setVisibility(View.VISIBLE);
		gifView.setGifImage(R.drawable.loading2);
		tv_cancel.setOnClickListener(this);
		add_watch_btn.setOnClickListener(this);
		delete_watch_btn.setOnClickListener(this);
		my_watch_find_btn.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		btn_delete.setBackgroundColor(getResources().getColor(R.color.gray_ad));
		
		adapter = new AdapterMyWatch(this, empty_area);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (!loading) {
			layout_loading.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			empty_area.setVisibility(View.GONE);
			loadData();
		}
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
		case R.id.nav_right_btn:
		case R.id.my_watch_find_btn:
			Intent intent = new Intent();
			intent.setClass(MyWatchListActivity.this, AddWatchActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	private void beginDelete() {
		if (adapter.size() > 0) {
			delete_watch_btn.setVisibility(View.GONE);
			tv_cancel.setVisibility(View.VISIBLE);
			btn_delete.setVisibility(View.VISIBLE);
			adapter.begainDelete();
			delete = true;
		} else
			toastShort("无数据可删除");
	}
	
	private void cancelDelete() {
		delete_watch_btn.setVisibility(View.VISIBLE);
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
		delete_watch_btn.setVisibility(View.VISIBLE);
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
	
	private void loadData() {
		loading = true;
		UserUtil.getUserWatchList(new EntityListCallback<WatchEntity>() {
			
			@Override
			public void succeed(List<WatchEntity> list) {
				adapter.addAllList(list);
				if (Validator.isEmptyList(list))
					listView.setVisibility(View.GONE);
				else
					listView.setVisibility(View.VISIBLE);
				afterLoad();
			}
			
			@Override
			public void fail(String error) {
				toastShort(error);
				afterLoad();
			}
		});
	}
	
	private void afterLoad() {
		loading = false;
		listView.setVisibility(View.VISIBLE);
		layout_loading.setVisibility(View.GONE);
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.MY_WATCH);
	}
}
