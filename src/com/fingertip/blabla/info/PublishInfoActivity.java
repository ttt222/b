package com.fingertip.blabla.info;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.entity.OverlayType;
import com.fingertip.blabla.main.DialogDate;
import com.fingertip.blabla.main.MapPositionSelectionActivity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityCallback;
import com.fingertip.blabla.util.http.EventUtil;
import com.fingertip.blabla.util.http.UploadImgEntity;
import com.fingertip.blabla.util.http.UploadUtil;
import com.fingertip.blabla.util.http.UploadUtil.UploadCallback;
import com.fingertip.blabla.widget.SelectPicActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishInfoActivity extends BaseActivity{
	
	private static final int REQUEST_POS = 1000;
	private static final int REQUEST_PIC = 1001;
	public static int MAX_PIC_SIZE = 9;
	
	private GridView img_gridView;
	private PublishPicAdapter pic_adapter;
	
	private TextView tv_submit;
	
	private TextView tv_position;
	private TextView tv_type_hint;
	private TextView tv_img_hint;
	private TextView tv_time_hint;
	
	private TextView tv_special,tv_perform,tv_sociality,tv_sports,tv_study,tv_other;
	
	private EditText et_title;
	private EditText et_content;
	
	private DialogDate dialogDate;
	
	private OverlayType overlayType = null;
	
	private static String hintText = "活动介绍：\n活动地点：\n活动时间：\n报名方式：\n活动费用：\n提示： \n";
	private double latitude = 0, longitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publishinfo);
		
		setupViews();
		initData();
	}

	private void setupViews() {
		img_gridView = (GridView)findViewById(R.id.img_gridView);
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_position = (TextView)findViewById(R.id.tv_position);
		et_title = (EditText)findViewById(R.id.et_title);
		et_content =(EditText)findViewById(R.id.et_content);
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("发布活动");
		findViewById(R.id.tv_more).setVisibility(View.GONE);
		
		findViewById(R.id.iv_back).setOnClickListener(onClickListener);
		tv_submit.setOnClickListener(onClickListener);
		
		
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
		tv_position.setOnClickListener(onClickListener);
		setTypeBackground(OverlayType.SOCIALITY);
		
		pic_adapter = new PublishPicAdapter(this, new ArrayList<String>());
		img_gridView.setAdapter(pic_adapter);
		img_gridView.setOnItemClickListener(pic_adapter);
	}
	
	
	private void initData() {
		et_content.setText("" + hintText);
		initDialogDate();
	}
	
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
	}
	
	public void addImg() {
		Intent intent = new Intent();
		intent.setClass(PublishInfoActivity.this, SelectPicActivity.class);
		intent.putExtra(SelectPicActivity.KEY_MAX_COUNT, MAX_PIC_SIZE - pic_adapter.getCount() + 1);
		startActivityForResult(intent, REQUEST_PIC);
	}
	
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
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null || resultCode != RESULT_OK)
			return;
		if (requestCode == REQUEST_POS) {
			tv_position.setText(data.getStringExtra(MapPositionSelectionActivity.KEY_ADDRESS));
			latitude = data.getDoubleExtra(MapPositionSelectionActivity.KEY_LAT, 0);
			longitude = data.getDoubleExtra(MapPositionSelectionActivity.KEY_LONG, 0);
		} else if (requestCode == REQUEST_PIC) {
			ArrayList<String> pics = data.getStringArrayListExtra(SelectPicActivity.KEY_PICS);
			if (!Validator.isEmptyList(pics))
				pic_adapter.addPics(pics);
		}
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Context context = PublishInfoActivity.this;
			if(v.getId() == R.id.iv_back){
				finish();
			}else if(v.getId() == R.id.tv_submit){
				publicActivity();
			}else if(v.getId() == R.id.tv_position){
				Intent intent = new Intent();
				intent.setClass(PublishInfoActivity.this, MapPositionSelectionActivity.class);
				startActivityForResult(intent, REQUEST_POS);
			}else if(v.getId() == R.id.tv_time_hint){
				dialogDate.show();
			}else if(v.getId() == R.id.tv_img_hint){
				if(img_gridView.getVisibility() == View.VISIBLE){
					img_gridView.setVisibility(View.GONE);
					tv_img_hint.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(R.drawable.icon_arrow_top), null);
				}else {
					img_gridView.setVisibility(View.VISIBLE);
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
			}
		}
	};
	
	/** 发布活动 **/
	private void publicActivity(){
		//标题
		final String title = et_title.getText().toString().trim();
		if (Validator.isEmptyString(title)) {
			toastShort("请输入活动标题");
			return;
		}
		//内容
		final String content = et_content.getText().toString().trim();
		if (Validator.isEmptyString(content)) {
			toastShort("请输入活动内容");
			return;
		}
		//类型
		final String type = overlayType.getType();
		if (Validator.isEmptyString(type)) {
			toastShort("请选择活动类型");
			return;
		}
		//图片
		List<String> pics = pic_adapter.getPics();
		if (Validator.isEmptyList(pics)) {
			toastShort("请选择活动图片");
			return;
		}
		//截止时间
		final String timeto = dialogDate.getTimeString();
		if (Validator.isEmptyString(tv_time_hint.getText().toString().trim())) {
			toastShort("请选择活动截止时间");
			return;
		}
		//坐标
		final String address = tv_position.getText().toString();
		if (Validator.isEmptyString(address) || latitude == 0 || longitude == 0) {
			toastShort("请标记活动位置");
			return;
		}
		//发布活动
		showProgressDialog(false);
		//先上传图片
		final List<UploadImgEntity> entitys = new ArrayList<UploadImgEntity>();
		UploadUtil.uplodaImg(pics, entitys, new UploadCallback() {
			
			@Override
			public void succeed() {
				EventUtil.publishEvent(title, content, type, address, timeto, latitude + "", longitude + "", entitys, new EntityCallback<String>() {
					@Override
					public void succeed(String event_id) {
						dismissProgressDialog();
						Tools.openEvent(PublishInfoActivity.this, event_id);
						finish();
					}

					@Override
					public void fail(String error) {
						dismissProgressDialog();
						toastShort(error);
					}
				});
			}
			
			@Override
			public void fail(int index, String error) {
				Log.e("uploadFile", index + " " + error);
				toastShort("上传图片失败");
				dismissProgressDialog();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
