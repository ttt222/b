package com.fingertip.blabla.my;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.ImageCache;
import com.fingertip.blabla.common.SelectPicPopupWindow;
import com.fingertip.blabla.common.ServerConstants;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.common.ServerConstants.URL;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.my.widget.SetMarkActivity;
import com.fingertip.blabla.my.widget.SetNickActivity;
import com.fingertip.blabla.my.widget.SetPasswordActivity;
import com.fingertip.blabla.my.widget.SetSexActivity;
import com.fingertip.blabla.my.widget.zoom.SetZoomActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MyInfoActivity extends BaseNavActivity implements View.OnClickListener {
	
	private ImageView my_head_img, my_sex_img, hidden_img;
	private LinearLayout my_head, my_nick, my_sex, my_mark, my_place, my_reset_password;
	private TextView my_nick_txt, my_sex_txt, my_mark_txt, my_place_txt;
	
	private UserSession session;
	private ImageCache imageCache;
	private BitmapUtils bitmapUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		my_head_img = (ImageView) findViewById(R.id.my_head_img);
		my_sex_img = (ImageView) findViewById(R.id.my_sex_img);
		hidden_img = (ImageView) findViewById(R.id.hidden_img);
		
		my_head = (LinearLayout) findViewById(R.id.my_head);
		my_nick = (LinearLayout) findViewById(R.id.my_nick);
		my_sex = (LinearLayout) findViewById(R.id.my_sex);
		my_mark = (LinearLayout) findViewById(R.id.my_mark);
		my_place = (LinearLayout) findViewById(R.id.my_place);
		my_reset_password = (LinearLayout) findViewById(R.id.my_reset_password);
		
		my_nick_txt = (TextView) findViewById(R.id.my_nick_txt);
		my_sex_txt = (TextView) findViewById(R.id.my_sex_txt);
		my_mark_txt = (TextView) findViewById(R.id.my_mark_txt);
		my_place_txt = (TextView) findViewById(R.id.my_place_txt);
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(getString(R.string.user_info));
		my_head.setOnClickListener(this);
		my_nick.setOnClickListener(this);
		my_sex.setOnClickListener(this);
		my_mark.setOnClickListener(this);
		my_place.setOnClickListener(this);
		my_reset_password.setOnClickListener(this);
		
		session = UserSession.getInstance();
		imageCache = ImageCache.getInstance();
		bitmapUtils = new BitmapUtils(this);
		initData();
	}
	
	private void initData() {
		my_place_txt.setText(session.getPlace());
		my_mark_txt.setText(session.getMark());
		my_nick_txt.setText(session.getNick_name());
		
		String sex = session.getSex();
		if (ServerConstants.SEX_MALE.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_MALE_S);
			my_sex_img.setImageResource(R.drawable.icon_male);
		} else if (ServerConstants.SEX_FEMALE.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_FEMALE_S);
			my_sex_img.setImageResource(R.drawable.icon_female);
		} else if (ServerConstants.SEX_UNKNOW.equals(sex)) {
			my_sex_txt.setText(ServerConstants.SEX_UNKNOW_S);
			my_sex_img.setImageResource(R.drawable.icon_male);
		}
		
		String small_head = imageCache.getUserImgPath(session.getId());
		File head_file = new File(small_head);
		if (head_file.exists())
			my_head_img.setImageBitmap(BitmapFactory.decodeFile(small_head));
		else
			imageCache.loadUserHeadImg(session.getHead_url(), session.getId(), getSP(), bitmapUtils, my_head_img, hidden_img);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_head:
			setHead();
			break;
		case R.id.my_nick:
			setNick();
			break;
		case R.id.my_sex:
			setSex();
			break;
		case R.id.my_mark:
			setMark();
			break;
		case R.id.my_place:
			setPlace();
			break;
		case R.id.my_reset_password:
			setPassword();
			break;
		}
	}
	
	private void setSex() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SetSexActivity.class);
		startActivityForResult(intent, R.id.my_info_set_sex);
	}

	private void setNick() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SetNickActivity.class);
		intent.putExtra(PARAM_KEYS.USER_NICK_NAME, my_nick_txt.getText().toString());
		startActivityForResult(intent, R.id.my_info_set_nick);
	}

	private void setMark() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SetMarkActivity.class);
		intent.putExtra(PARAM_KEYS.USER_MARK, my_mark_txt.getText().toString());
		startActivityForResult(intent, R.id.my_info_set_mark);
	}
	
	private void setHead() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SelectPicPopupWindow.class);
		startActivityForResult(intent, R.id.my_info_set_head);
	}
	
	private void setPlace() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SetZoomActivity.class);
		startActivityForResult(intent, R.id.my_info_set_place);
	}
	
	private void setPassword() {
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, SetPasswordActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case R.id.my_info_set_sex:
				modifyUserInfo(PARAM_KEYS.USER_SEX, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_nick:
				modifyUserInfo(PARAM_KEYS.USER_NICK_NAME, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_mark:
				modifyUserInfo(PARAM_KEYS.USER_MARK, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			case R.id.my_info_set_head:
				uploadHeadImg(data);
				break;
			case R.id.my_info_set_place:
				modifyUserInfo(PARAM_KEYS.USER_PLACE, data.getExtras().getString(Globals.COMMON_RESULT));
				break;
			}
		}
	}
	
	private void uploadHeadImg(Intent intent) {
		Uri img_uri = intent.getData();   
		Bitmap image = null;
        //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取   
        if (img_uri != null) {
            try {   
                //这个方法是根据Uri获取Bitmap图片的静态方法   
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), img_uri);   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        } else {   
            Bundle extras = intent.getExtras();   
            if (extras != null) {   
                //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片   
                image = extras.getParcelable("data");   
            }   
        }
        if (image != null) {
        	showProgressDialog(false);
        	String user_id = session.getId();
        	final Bitmap head_img = image;
    		//大图
    		final String big_head = imageCache.getUserImgPath(user_id, false, true);
    		imageCache.saveUserImg(head_img, user_id, false, true);
    		//小图
    		final String small_head = imageCache.getUserImgPath(user_id, true, true);
    		//压缩
    		imageCache.saveUserImg(compressImage(head_img), user_id, true, true);
//    		"fc":"upload_file", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",
//    		 "filefor":"头像"
//    		sfile 缩略图, sfull 原图
    		JSONObject data = new JSONObject();
    		try {
    			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_UPLOAD_FILE);
    			data.put(PARAM_KEYS.UPLOAD_FILEFOR, PARAM_VALUES.UPLOAD_HEAD);
    			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
    			data.put(PARAM_KEYS.USERID, session.getId());
    		} catch (JSONException e) {
    		}
    		RequestParams params = new RequestParams();
    		params.addQueryStringParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
    		params.addBodyParameter(PARAM_KEYS.UPLOAD_SFULL, new File(big_head));
    		params.addBodyParameter(PARAM_KEYS.UPLOAD_SFILE, new File(small_head));

    		HttpUtils http = Tools.getHttpUtils();
    		http.send(HttpRequest.HttpMethod.POST, ServerConstants.URL.UPLOAD_IMG, params,
    		    new RequestCallBack<String>() {

    		        @Override
    		        public void onSuccess(ResponseInfo<String> responseInfo) {
    		        	String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
    					String error = null;
    					String file_url = null;
    					String full_url = null;
    					try {
    						JSONObject json = new JSONObject(result);
    						file_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFILE);
    						full_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFULL);
//    						上传图片返回结果无状态码
//    						if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
//    							error = json.getString(PARAM_KEYS.RESULT_ERROR);
    					} catch (JSONException e) {
    						e.printStackTrace();
    						error = e.getMessage();
    					}
    					if (file_url == null || full_url == null) {
    						toastShort("上传图片失败");
    						dimissProgressDialog();
    					} else if (error != null) {
    						toastShort(error);
    						dimissProgressDialog();
    					} else {
    						modifyUserInfo(new String[]{PARAM_KEYS.USER_HEAD, PARAM_KEYS.USER_HEAD_BIG}, 
    								new String[]{file_url, full_url});
    					}
    		        }

    		        @Override
    		        public void onFailure(HttpException error, String msg) {
    		        	toastLong(ServerConstants.NET_ERROR_TIP);
    		        	dimissProgressDialog();
    		        }
    		});
        }
	}

	private void modifyUserInfo(final String key, final String value) {
		showProgressDialog(false);
		modifyUserInfo(new String[]{key}, new String[]{value});
	}
	
	private void modifyUserInfo(final String[] keys, final String[] values) {
		JSONObject data = new JSONObject();
//		{"fc":"user_infor_edit", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "head":"http://xxxxxxxx/xxx.jpg", "headbig":"http://xxxxxx/xxx.jpg",
//			 "nick":"Jim", "address":"广州市体育东路1号", "aboutme":"我就是我"}
		try {
			//必须参数
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_USER_INFO);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			//修改值
			for (int i = 0; i < keys.length; i++) 
				data.put(keys[i], values[i]);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.EDIT_USER_INFO, params, new RequestCallBack<String>() {
			
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
					error = "获取个人资料失败," + e.getMessage();
				}
				if (error != null)
					toastLong(error);
				else
					editCallBack(keys, values);
				dimissProgressDialog();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				toastLong(ServerConstants.NET_ERROR_TIP);
				dimissProgressDialog();
			}
		});
	}
	
	private void editCallBack(String[] keys, String[] values) {
		String user_id = session.getId();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String value = values[i];
			//头像
			if (PARAM_KEYS.USER_HEAD.equals(key)) {
				imageCache.saveTmpImg(user_id, true);
				getSP().setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE, value);
				my_head_img.setImageBitmap(BitmapFactory.decodeFile(imageCache.getUserImgPath(user_id)));
			} else if (PARAM_KEYS.USER_HEAD_BIG.equals(key)) {
				imageCache.saveTmpImg(user_id, false);
				getSP().setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE_FULL, value);
			//昵称
			} else if (PARAM_KEYS.USER_NICK_NAME.equals(key)) {
				my_nick_txt.setText(value);
				session.setNick_name(value);
			} else if (PARAM_KEYS.USER_PLACE.equals(key)) {
				my_place_txt.setText(value);
				session.setPlace(value);
			} else if (PARAM_KEYS.USER_SEX.equals(key)) {
				if (ServerConstants.SEX_MALE.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_MALE_S);
					my_sex_img.setImageResource(R.drawable.icon_male);
				} else if (ServerConstants.SEX_FEMALE.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_FEMALE_S);
					my_sex_img.setImageResource(R.drawable.icon_female);
				} else if (ServerConstants.SEX_UNKNOW.equals(value)) {
					my_sex_txt.setText(ServerConstants.SEX_UNKNOW_S);
					my_sex_img.setImageResource(R.drawable.icon_male);
				}
				session.setSex(value);
			} else if (PARAM_KEYS.USER_MARK.equals(key)) {
				my_mark_txt.setText(value);
				session.setMark(value);
			}
		}
	}
	
	private Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length / 1024 > 50) {	//循环判断如果压缩后图片是否大于100kb,大于继续压缩		
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			options /= 2;//每次都减半
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
		return bitmap;
	}
}
