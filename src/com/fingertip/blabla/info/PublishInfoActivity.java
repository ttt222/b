package com.fingertip.blabla.info;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.Cmd;
import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.SelectPicDialog;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.OverlayType;
import com.fingertip.blabla.main.DialogDate;
import com.fingertip.blabla.main.MapPositionSelectionActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.util.LogUtils;

public class PublishInfoActivity extends BaseActivity{
	private static final String TAG = "PublishInfoActivity";
	
	private static final int REQUEST_POS = 1000;
	private static final int REQUEST_PIC = 1001;
	
	private LinearLayout layout_img;
	@SuppressWarnings("unused")
	private TextView tv_submit;
	
	@SuppressWarnings("unused")
	private View view_title;
	private TextView tv_position;
	
	private TextView tv_type_hint;
	private TextView tv_img_hint;
	private TextView tv_time_hint;
	private ImageView iv_img_add;
	
	private TextView tv_special,tv_perform,tv_sociality,tv_sports,tv_study,tv_other;
	
	private EditText et_title;
	private EditText et_content;
	
	private DialogDate dialogDate;
	
	private ArrayList<String> arrayList_pic = new ArrayList<String>();
	private JSONArray jsonArray_pic = new JSONArray();
	
	private OverlayType overlayType = null;
	
	private static String hintText = "活动介绍：\n活动地点：\n活动时间：\n报名方式：\n活动费用：\n提示： \n";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publishinfo);
		
		setupViews();
		initData();
	}

	private void setupViews() {
		layout_img = (LinearLayout)findViewById(R.id.layout_img);
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		view_title = findViewById(R.id.view_title);
		tv_position = (TextView)findViewById(R.id.tv_positon);
		
		et_title = (EditText)findViewById(R.id.et_title);
		et_content =(EditText)findViewById(R.id.et_content);
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("发布活动");
		findViewById(R.id.tv_more).setVisibility(View.GONE);
		
		findViewById(R.id.iv_back).setOnClickListener(onClickListener);
		findViewById(R.id.tv_submit).setOnClickListener(onClickListener);
		
		
		tv_type_hint = (TextView)findViewById(R.id.tv_type_hint);
		tv_img_hint = (TextView)findViewById(R.id.tv_img_hint);
		tv_time_hint = (TextView)findViewById(R.id.tv_time_hint);
		tv_type_hint.setOnClickListener(onClickListener);
		tv_img_hint.setOnClickListener(onClickListener);
		tv_time_hint.setOnClickListener(onClickListener);
		
		tv_special = (TextView)findViewById(R.id.tv_special);
		tv_perform = (TextView)findViewById(R.id.tv_perform);
		tv_sociality = (TextView)findViewById(R.id.tv_sociality);
		tv_sports = (TextView)findViewById(R.id.tv_sports);
		tv_study = (TextView)findViewById(R.id.tv_study);
		tv_other = (TextView)findViewById(R.id.tv_other);
		tv_special.setOnClickListener(onClickListener);
		tv_perform.setOnClickListener(onClickListener);
		tv_sociality.setOnClickListener(onClickListener);
		tv_sports.setOnClickListener(onClickListener);
		tv_study.setOnClickListener(onClickListener);
		tv_other.setOnClickListener(onClickListener);
		setTypeBackground(OverlayType.SOCIALITY);
		
		findViewById(R.id.layout_position).setOnClickListener(onClickListener);
		
	}//end setupViews
	
	
	private void initData() {
		et_content.setText("" + hintText);
		
		Context context = PublishInfoActivity.this;
		int displayWidth = context.getResources().getDisplayMetrics().widthPixels;
		int rightMargin = 16;
		
		iv_img_add = new ImageView(context);
		iv_img_add.setBackgroundResource(R.drawable.selector_bg_gray);
		iv_img_add.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_add_big));
		iv_img_add.setScaleType(ScaleType.FIT_XY);
		iv_img_add.setAdjustViewBounds(true);
		
		LinearLayout view_img = new LinearLayout(context);
		params_horizontal = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//		params_vertical = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params_horizontal.width = (displayWidth - rightMargin) / 3;
//		imgParams.weight = 1;
		params_horizontal.rightMargin = rightMargin;
		params_horizontal.topMargin = rightMargin;
		params_horizontal.bottomMargin = rightMargin;
		view_img.addView(iv_img_add, params_horizontal);
		layout_img.addView(view_img);
		
		iv_img_add.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
//				addImgView();
				
				String localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Globals.PATH_CACH + File.separator + arrayList_pic.size() + ".png";
				
				Intent intent = new Intent();
				intent.setClass(PublishInfoActivity.this, SelectPicDialog.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, localPath);
				startActivityForResult(intent, REQUEST_PIC);
			}//end onClick
		});
		
		
		initDialogDate();
	}//end initData
	
	private void initDialogDate(){
		dialogDate = new DialogDate(PublishInfoActivity.this);
		
		Window window = dialogDate.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lParams = window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		lParams.width = dm.widthPixels;
		window.setAttributes(lParams);
		
		dialogDate.setCancelable(false);
		dialogDate.setOnDismissListener(new OnDismissListener() {		
			@Override
			public void onDismiss(DialogInterface arg0) {
				tv_time_hint.setText("信息截止时间  (" + dialogDate.getTimeString() + ")");
			}
		});
	}//end initDialogDate
	
	private LinearLayout.LayoutParams params_horizontal = null;
//	private LinearLayout.LayoutParams params_vertical = null;
	
	/** 每个图片子布局中最多3个控件 **/
	private void addImgView(Bitmap bitmap){
		LinearLayout view_img = (LinearLayout)layout_img.getChildAt(layout_img.getChildCount() - 1);
		Context context = PublishInfoActivity.this;
		view_img.removeView(iv_img_add);
		
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.FIT_CENTER);
		imageView.setAdjustViewBounds(true);
		imageView.setImageBitmap(bitmap);
//		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.abc_ic_search));
		view_img.addView(imageView, params_horizontal);
		
		if(view_img.getChildCount() ==3){
			view_img = new LinearLayout(context);
			layout_img.addView(view_img);
		}
		view_img.addView(iv_img_add, params_horizontal);
		
	}//end addImgView
	
	private void setTypeBackground(OverlayType type){
		this.overlayType = type;
		
		int color = getResources().getColor(R.color.transparent);
		int res_drawable_gray = R.drawable.bg_gray_click;
		tv_special.setBackgroundColor(color);
		tv_perform.setBackgroundColor(color);
		tv_sociality.setBackgroundColor(color);
		tv_sports.setBackgroundColor(color);
		tv_study.setBackgroundColor(color);
		tv_other.setBackgroundColor(color);
		
		switch (overlayType) {
		case SPECIAL:
			tv_special.setBackgroundResource(res_drawable_gray);
			break;
		case PERFORM:
			tv_perform.setBackgroundResource(res_drawable_gray);
			break;
		case SOCIALITY:
			tv_sociality.setBackgroundResource(res_drawable_gray);
			break;
		case SPORTS:
			tv_sports.setBackgroundResource(res_drawable_gray);
			break;
		case STUDY:
			tv_study.setBackgroundResource(res_drawable_gray);
			break;
		case OTHER:
			tv_other.setBackgroundResource(res_drawable_gray);
			break;
		default:
			break;
		}
	}//end setTypeBackground
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null){
			return;
		}
		if(requestCode == REQUEST_POS){
//			getPicture(data);
			tv_position.setText("" + data.getStringExtra(MapPositionSelectionActivity.RETURN_VALUE));
		}else if(requestCode == REQUEST_PIC){
			String filePath = data.getStringExtra(SelectPicDialog.RETURN_VALUE);
			if(filePath == null) {
				Toast.makeText(PublishInfoActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
				return;
			}
			showProgressDialog(false);
			uploadFile(null, filePath);
		}
	}//end onActivityResult
	
	View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Context context = PublishInfoActivity.this;
			if(v.getId() == R.id.iv_back){
				finish();
			}else if(v.getId() == R.id.tv_submit){
				showProgressDialog(false);
				publicActivity();
			}else if(v.getId() == R.id.layout_position){
				Intent intent = new Intent();
				intent.setClass(PublishInfoActivity.this, MapPositionSelectionActivity.class);
				startActivityForResult(intent, REQUEST_POS);
			}else if(v.getId() == R.id.tv_time_hint){
//				if(findViewById(R.id.layout_time).getVisibility() == View.VISIBLE){
//					findViewById(R.id.layout_time).setVisibility(View.GONE);
//					tv_time_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
//					tv_time_hint.setText("信息截止时间(" + getTimeString() + ")");
//				}else {
//					findViewById(R.id.layout_time).setVisibility(View.VISIBLE);
//					tv_time_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);
//					tv_time_hint.setText("信息截止时间");
//				}
				
				dialogDate.show();
			}else if(v.getId() == R.id.tv_img_hint){
				if(findViewById(R.id.layout_img).getVisibility() == View.VISIBLE){
					findViewById(R.id.layout_img).setVisibility(View.GONE);
					tv_img_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
				}else {
					findViewById(R.id.layout_img).setVisibility(View.VISIBLE);
					tv_img_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);
				}
			}else if(v.getId() == R.id.tv_type_hint){
				if(findViewById(R.id.layout_type).getVisibility() == View.VISIBLE){
					findViewById(R.id.layout_type).setVisibility(View.GONE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
					tv_type_hint.setText("活动类型(" + overlayType.getType() + ")");
				}else {
					findViewById(R.id.layout_type).setVisibility(View.VISIBLE);
					tv_type_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_down), null);					
					tv_type_hint.setText("活动类型");
				}
			}else if(v.getId() == R.id.tv_special){
				setTypeBackground(OverlayType.SPECIAL);
			}else if(v.getId() == R.id.tv_perform){
				setTypeBackground(OverlayType.PERFORM);
			}else if(v.getId() == R.id.tv_sociality){
				setTypeBackground(OverlayType.SOCIALITY);
			}else if(v.getId() == R.id.tv_sports){
				setTypeBackground(OverlayType.SPORTS);
			}else if(v.getId() == R.id.tv_study){
				setTypeBackground(OverlayType.STUDY);
			}else if(v.getId() == R.id.tv_other){
				setTypeBackground(OverlayType.OTHER);
			}else {
				Toast.makeText(PublishInfoActivity.this, "功能未提供", Toast.LENGTH_SHORT).show();
			}
		}
	};//end onClickListener 
	
	/**
	 * 图片上传
	 * @param smallPath:缩略图路径
	 * @param filePath:原图路径
	 */
	private void uploadFile(final String smallPath, final String filePath){
		if(filePath == null){
			Toast.makeText(PublishInfoActivity.this, "上传图片不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		File small = null;
		File file = new File(filePath); 
		if(smallPath ==null){
			small = file;
		}
		
		HttpUtils http_upload = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "upload_file"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try { jsonObject.put("filefor", "活动"); } catch (Exception e) { }
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString()));  
	    params.addBodyParameter(PARAM_KEYS.UPLOAD_SFILE, small);
	    params.addBodyParameter(PARAM_KEYS.UPLOAD_SFULL, file);
		
	    http_upload.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_UPLLOADFILE,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	Log.e(TAG, "nnnnnnnnnn onLoading");
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	dimissProgressDialog();
//		            Log.e(TAG, ".......upload........onSuccess:" + Tools.decodeString(responseInfo.result));
		            
		            try {
						JSONObject jsonObject = new JSONObject(Tools.decodeString(responseInfo.result));
						JSONObject jsonObject_new = new JSONObject();
						jsonObject_new.put("s", jsonObject.getString("urlfile"));
						jsonObject_new.put("b", jsonObject.getString("urlfull"));
						
						jsonArray_pic.put(jsonObject_new);
						
						LogUtils.i(jsonObject_new.toString() + "," + jsonArray_pic.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
		            
		            arrayList_pic.add(filePath);
					Bitmap bitmap = null;
					bitmap = BitmapFactory.decodeFile(filePath); 
					addImgView(bitmap);
					
		        }

		        @Override
		        public void onStart() {
		        	Log.e(TAG, ",,,,,,,,,,onStart");
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e(TAG, "............onFailure:" + msg);
		        	dimissProgressDialog();
		        }
		});
	}//end uploadFile
	
	
	
	/** 发布活动 **/
	private void publicActivity(){
		HttpUtils http_upload = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "action_post"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try { jsonObject.put("poslong", getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG)); } catch (Exception e) { }
		try { jsonObject.put("poslat", getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT)); } catch (Exception e) { }
		try { jsonObject.put("kindof", overlayType.getType()); } catch (Exception e) { }
		try { jsonObject.put("titleof", et_title.getText().toString()); } catch (Exception e) { }
		try { jsonObject.put("content", et_content.getText().toString()); } catch (Exception e) { }
		try { 
			jsonObject.put("timeto", dialogDate.getTimeString());

		} catch (Exception e) { }
		try { jsonObject.put("address", tv_position.getText().toString()); } catch (Exception e) { }
		try { jsonObject.put("picof",jsonArray_pic); } catch (Exception e) { }
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString())); 
	    
	    LogUtils.e(jsonObject.toString());
		
	    http_upload.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_PUBLISH,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	Log.e(TAG, "nnnnnnnnnn onLoading");
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		            Log.e(TAG, ".......upload........onSuccess:" + Tools.decodeString(responseInfo.result));
		            Toast.makeText(PublishInfoActivity.this, "内容发布成功", Toast.LENGTH_SHORT).show();
		            dimissProgressDialog();
		            
		            try {
		            	JSONObject jsonObject = new JSONObject(Tools.decodeString(responseInfo.result));
		            	Tools.openEvent(PublishInfoActivity.this, jsonObject.getString("actionid"));
					} catch (Exception e) {
						// TODO: handle exception
					}
		            
		            
		            finish();
		        }//end onSuccess

		        @Override
		        public void onStart() {
		        	Log.e(TAG, ",,,,,,,,,,onStart");
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	Log.e(TAG, "............onFailure:" + msg);
		        	Toast.makeText(PublishInfoActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
		        	dimissProgressDialog();
		        }
		});
	}//end publicActivity
	
	
}
