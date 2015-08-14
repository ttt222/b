package com.fingertip.blabla.my;

import java.io.Serializable;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.barcode.ScanBarcodeActivity;
import com.fingertip.blabla.barcode.ScanBarcodeActivity.BarcodeValidator;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityCallback;
import com.fingertip.blabla.util.http.UserUtil;
import com.google.zxing.Result;

public class AddWatchActivity extends BaseNavActivity implements View.OnClickListener {
	
	private EditText search_edit;
	private LinearLayout my_watch_myinfo, my_watch_scan, my_watch_contacts;
	
	private BarcodeValidater barcodeValidater;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_watch);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		search_edit = (EditText) findViewById(R.id.my_watch_search_edt);
		my_watch_myinfo = (LinearLayout) findViewById(R.id.my_watch_myinfo);
		my_watch_scan = (LinearLayout) findViewById(R.id.my_watch_scan);
		my_watch_contacts = (LinearLayout) findViewById(R.id.my_watch_contacts);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_watch_add);
		
		search_edit.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
					searchUser();
				return false;
			}
		});
		my_watch_myinfo.setOnClickListener(this);
		my_watch_scan.setOnClickListener(this);
		my_watch_contacts.setOnClickListener(this);
		
		barcodeValidater = new BarcodeValidater();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.my_watch_myinfo:
			intent = new Intent();
			intent.setClass(AddWatchActivity.this, MyBarcodeActivity.class);
			break;
		case R.id.my_watch_scan:
			Intent in = new Intent();
			in.setClass(AddWatchActivity.this, ScanBarcodeActivity.class);
			in.putExtra(ScanBarcodeActivity.KEY_VALIDATOR, barcodeValidater);
			startActivityForResult(in, R.id.decode);
			break;
		case R.id.my_watch_contacts:
			intent = new Intent();
			intent.setClass(AddWatchActivity.this, MyContactsActivity.class);
			break;
		}
		if (intent != null)
			startActivity(intent);
	}
	
	private void searchUser() {
		String search_key = search_edit.getText().toString().trim();
		if (Validator.isEmptyString(search_key)) {
			toastShort("请输入手机号码或昵称");
		} else {
			showProgressDialog(false);
			UserUtil.getUserInfo(search_key, new EntityCallback<UserEntity>() {
				@Override
				public void succeed(UserEntity user) {
					Tools.openUser(AddWatchActivity.this, user);
					dimissProgressDialog();
				}
				
				@Override
				public void fail(String error) {
					toastShort(error);
					dimissProgressDialog();
				}
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.decode:
				Tools.openUser(this, UserUtil.parseBarcode(data.getExtras().getString(Globals.COMMON_RESULT)));
				break;
			}
		}
	}
}

class BarcodeValidater implements BarcodeValidator, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean canProcess(Result result) {
		return UserUtil.parseBarcode(result.getText()) != null;
	}
	
}
