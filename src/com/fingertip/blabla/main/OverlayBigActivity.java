package com.fingertip.blabla.main;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.ShareDialog;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.CommentEntity;
import com.fingertip.blabla.entity.ImgEntityList.ImgEntity;
import com.fingertip.blabla.entity.OverlayEntityList.OverlayEntity;
import com.fingertip.blabla.entity.OverlayType;
import com.fingertip.blabla.entity.ShareEntity;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.my.UserInfoActivity;
import com.fingertip.blabla.setting.ReportActivity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.UmengConfig.EVENT;
import com.fingertip.blabla.util.UmengConfig.PAGE;
import com.fingertip.blabla.util.http.DefaultCallback;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.EventUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 地图大气泡
 * @author Administrator
 *
 */
public class OverlayBigActivity extends BaseActivity implements View.OnClickListener{
	
	private LinearLayout layout_main;
	private ImageView iv_head;
	private TextView tv_title;
	private TextView tv_name;
	/*  **/
	private TextView tv_time;
	private TextView tv_detail;
	private TextView tv_collection;
	/** 收藏按钮 **/
	private TextView tv_btnCollection;
	/* 评论标题 **/
	private TextView tv_recommendTopic;
	/* 评论 **/
	private TextView tv_recommend;
	private LinearLayout layout_img;
	/*  **/
	private LinearLayout layout_commend;
	//主题图片
	private ImageView iv_topic;
	
	private OverlayEntity overlayEntity;
	
	private BitmapUtils bitmapUtils;
	
	//屏幕左右间隔
	private int screenMargin = 80;
	
	/** 当前评论页数 **/
	private int pageIndex_recommend= 1;
	
	//马上举报
	private TextView tv_accusation;
	
	//收藏和邀请的布局
	private LinearLayout layout_collection, layout_share;
	
	private UserSession session = UserSession.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overlaybig);
		
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels - screenMargin;
		
		findViews();
		setupViews();
		initEntity();
		
		initFavor();
	}//end onCreate
	
	private void findViews(){
		layout_main = (LinearLayout)findViewById(R.id.layout_main);
		
		layout_commend = (LinearLayout)findViewById(R.id.layout_commend);
		layout_img = (LinearLayout)findViewById(R.id.layout_img);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_detail = (TextView)findViewById(R.id.tv_detail);
		tv_collection = (TextView)findViewById(R.id.tv_collection);
		tv_btnCollection = (TextView)findViewById(R.id.btn_collection);
		tv_recommendTopic = (TextView)findViewById(R.id.tv_recommend_topic);
		tv_recommend = (TextView)findViewById(R.id.tv_recommend);
		tv_time = (TextView)findViewById(R.id.tv_time);
		iv_head = (ImageView)findViewById(R.id.iv_head);
		
		iv_topic = (ImageView)findViewById(R.id.iv_topic);
		tv_accusation=(TextView) findViewById(R.id.tv_accusation);
		
		
		layout_collection=(LinearLayout) findViewById(R.id.layout_collection);
		layout_share = (LinearLayout) findViewById(R.id.layout_share);
	}

	private void setupViews() {		
		
		ViewTreeObserver vto = layout_main.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
				int heightPixels = getResources().getDisplayMetrics().heightPixels;
				if(isFirst && layout_main.getHeight() >= (heightPixels - 150)){
					layoutParams.height = heightPixels - 150;
					getWindow().setAttributes(layoutParams);
					isFirst = false;
				}
			}
		});
		
		
		tv_accusation.setOnClickListener(this);
		tv_recommend.setOnClickListener(this);
		tv_btnCollection.setOnClickListener(this);
		findViewById(R.id.btn_share).setOnClickListener(this);
		iv_head.setOnClickListener(this);
		
		layout_collection.setOnClickListener(this);
		layout_share.setOnClickListener(this);
	}//end setupViews
	private boolean isFirst = true;
	
	private void initEntity(){
		overlayEntity = (OverlayEntity)getIntent().getSerializableExtra(BaseActivity.EXTRA_PARAM);
		
		if(overlayEntity == null){
			Toast.makeText(OverlayBigActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		tv_title.setText("" + overlayEntity.title);
		tv_name.setText("" + overlayEntity.userEntity.nick_name);
		tv_detail.setText("" + overlayEntity.detail);
	tv_collection.setText("" + overlayEntity.viewCount);//调用浏览次数
	//	tv_collection.setText("" + overlayEntity.appraiseCount);
		tv_recommendTopic.setText("评论（" + overlayEntity.replyCount + "）");
		tv_time.setText("" + overlayEntity.ptime);
		
		
		
		Context context = OverlayBigActivity.this;
		
//		try {
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), overlayEntity.headRes);
//		iv_head.setImageBitmap(Tools.toRoundCorner(bitmap, bitmap.getWidth() / 2));
//	} catch (Exception e) {}
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.display(iv_head, "" + overlayEntity.userEntity.head_img_url, new BitmapLoadCallBack<ImageView>() {
			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
				try {
					container.setImageBitmap(Tools.toRoundCorner(bitmap, bitmap.getWidth() / 2));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//end onLoadCompleted

			@Override
			public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
				LogUtils.i("head img load fail");
			}
		});
		
		String topImag = overlayEntity.getImgList().get(0).big;
		bitmapUtils.display(iv_topic, "" + topImag, new BitmapLoadCallBack<ImageView>() {
			@Override
			public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
				try {
					container.setImageBitmap(bitmap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}//end onLoadCompleted

			@Override
			public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
				LogUtils.i("head img load fail");
			}
		});
		
		ImageView imageView = null;
		LinearLayout layout_img_horizontal = null;
		LinearLayout.LayoutParams layoutParams_horizontal = null;
		LinearLayout.LayoutParams layoutParams_vertical = null;
		
		
		//图片
		int paddingRight = 17;
		ArrayList<ImgEntity> arrayList_img = overlayEntity.getImgList();
//		int maxHeight = getResources().getDisplayMetrics().heightPixels / 3;
		int maxWidth = (getResources().getDisplayMetrics().widthPixels - screenMargin - paddingRight * 4) / 3;
		
		
		layoutParams_vertical = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams_vertical.topMargin = paddingRight / 2;
		layoutParams_vertical.bottomMargin = paddingRight / 2;

		
		for(int i = 0; i < arrayList_img.size(); i++){
			layoutParams_horizontal = new LinearLayout.LayoutParams(maxWidth, maxWidth);
			
			if(i % 3 == 0){
				layout_img_horizontal = new LinearLayout(context);
				layout_img.addView(layout_img_horizontal, layoutParams_vertical);
				
				layoutParams_horizontal.leftMargin = paddingRight / 2;
			}
			
			
			if(i % 3 == 2){
				layoutParams_horizontal.rightMargin = 0;
			}else {
				layoutParams_horizontal.rightMargin = paddingRight;
			}
			
			
			imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setAdjustViewBounds(true);
//			imageView.setScaleType(ScaleType.CENTER);
			bitmapUtils.display(imageView, arrayList_img.get(i).small);
			imageView.setTag(i);
			imageView.setOnClickListener(imgOnClickListener);
			
			layout_img_horizontal.addView(imageView, layoutParams_horizontal);
			
		}
		
//		if(arrayList_img.size() > 0){
//			layout_img.setOnClickListener(new View.OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent();
//					intent.setClass(OverlayBigActivity.this, ImageViewPagerActivity.class);
//					intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity.getImgList());
//					startActivity(intent);
//				}
//			});
//		}
		
		imageView = null;
		arrayList_img = null;
		context = null;
		
		
		setOverlayType();
	}//end initEntity
	
	private void setCollected(){
		findViewById(R.id.iv_collection_starts).setBackgroundResource(R.drawable.collection_starts_p);
		tv_btnCollection.setOnClickListener(null);
		tv_btnCollection.setText("已收藏");
	}
	
	private void initFavor(){
		if (session.isLogin()) {
			if (session.isLoad_favor()) {
				if(session.getFavor_event_list().contains(overlayEntity.actionid))
					setCollected();
			} else
				session.isLoad_favor();
		}
	}
	
	private View.OnClickListener imgOnClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(OverlayBigActivity.this, ImageViewPagerActivity.class);
			intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity.getImgList());
			intent.putExtra(ImageViewPagerActivity.INDEX_SHOW, (Integer)v.getTag());
			startActivity(intent);
		}
	};
	
	private void setOverlayType(){
		if(overlayEntity.type == OverlayType.SPORTS){
			layout_main.setBackgroundResource(R.drawable.bg_recommend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_recommend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_recommend);
		}else if(overlayEntity.type == OverlayType.SOCIALITY){
			layout_main.setBackgroundResource(R.drawable.bg_friend);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_friend);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_friend);
		}else if(overlayEntity.type == OverlayType.PERFORM){
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		}else if(overlayEntity.type == OverlayType.STUDY){
			layout_main.setBackgroundResource(R.drawable.bg_event);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_event);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_event);
		}else if(overlayEntity.type == OverlayType.SPECIAL){
			layout_main.setBackgroundResource(R.drawable.bg_buy);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_buy);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_buy);
		}else if(overlayEntity.type == OverlayType.OTHER){
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_accusation.setBackgroundResource(R.drawable.bg_btn_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		}else {
			layout_main.setBackgroundResource(R.drawable.bg_activity);
			tv_recommend.setBackgroundResource(R.drawable.bg_btn_activity);
		}
	}//end setOverlayType
	
	
	/** 设置评论 **/
	@SuppressLint("InflateParams")
	private void setComment(){
		
		Context context = OverlayBigActivity.this;
		// 评论
		ArrayList<CommentEntity> commentList = overlayEntity.getCommentList();
		tv_recommendTopic.setText("评论（" + overlayEntity.replyCount + "）");
		
		CommentEntity comment = null;
		View view_line = null;

		View view_commend = null;
		ImageView iv_commendHead;
		TextView tv_commendName;
		TextView tv_commendReply;
		TextView tv_commendContent;

		for (int i = 0; i < commentList.size(); i++) {
			LogUtils.i("i:" + i + ",i*2:" + (i*2) + ",count:" + layout_commend.getChildCount());
			if((i * 2) < layout_commend.getChildCount()){
				view_commend = layout_commend.getChildAt(i * 2);
			}else {
				view_commend = LayoutInflater.from(context).inflate(R.layout.view_commend_listitem, null);
				layout_commend.addView(view_commend);
				
				view_line = new View(OverlayBigActivity.this);
				view_line.setBackgroundColor(getResources().getColor(R.color.gray_d7));
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
				layoutParams.topMargin = 8;
				layout_commend.addView(view_line, layoutParams);
			}
			
			comment = commentList.get(i);
			iv_commendHead = (ImageView) view_commend.findViewById(R.id.iv_head);
			tv_commendName = (TextView) view_commend.findViewById(R.id.tv_name);
			tv_commendReply = (TextView) view_commend.findViewById(R.id.tv_reply);
			tv_commendContent = (TextView) view_commend.findViewById(R.id.tv_content);

			try {
				iv_commendHead.setImageDrawable(getResources().getDrawable(R.drawable.bg_head_default_little));
				bitmapUtils.display(iv_commendHead, "" + comment.userEntity.head_img_url, new BitmapLoadCallBack<ImageView>() {
					@Override
					public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
						try {
							container.setImageBitmap(Tools.toRoundCorner(bitmap, bitmap.getWidth() / 2));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}//end onLoadCompleted

					@Override
					public void onLoadFailed(ImageView container, String uri, Drawable drawable) {
						LogUtils.i("head img load fail");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			tv_commendName.setText(comment.userEntity.nick_name);
			if (comment.reply != null) {
				tv_commendReply.setText(comment.reply);
				tv_commendReply.setVisibility(View.VISIBLE);
			}
			tv_commendContent.setText(comment.comment);
			
			iv_commendHead.setTag(comment.userEntity);
		    iv_commendHead.setOnClickListener(onClickListener);
		    
		    view_commend.setTag(comment);
		    view_commend.setBackgroundResource(R.drawable.selector_bg_gray);
		    view_commend.setOnClickListener(onClickListener_reply);
		    
		}

		commentList = null;
		comment = null;

		view_commend = null;
		iv_commendHead = null;
		tv_commendName = null;
		tv_commendContent = null;
	}//end setRecommend

	@Override
	public void onClick(View view) {
		Intent intent = null;
		switch (view.getId()) {
		case R.id.iv_head://
			intent = new Intent();
			intent.setClass(OverlayBigActivity.this, UserInfoActivity.class);
			intent.putExtra(UserInfoActivity.KEY_USER_ID, overlayEntity.userEntity.id);
			startActivity(intent);
			break;
		case R.id.tv_accusation://举报
			intent = new Intent();
			intent.setClass(OverlayBigActivity.this, ReportActivity.class);
			intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
			startActivity(intent);
			break;
		case R.id.tv_recommend://评论
			if (Tools.checkLogin(this)) {
				intent = new Intent();
				intent.setClass(OverlayBigActivity.this, PublicRecommendActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
				startActivity(intent);
			}
			break;
		case R.id.layout_collection://
			if (Tools.checkLogin(this)) {
				showProgressDialog(false);
				requestCollecion();
			}
			break;
		case R.id.layout_share://
			ShareEntity shareEntity = new ShareEntity();
			shareEntity.shareTitle = overlayEntity.title;
			shareEntity.shareContent = overlayEntity.getShareContent();
			shareEntity.targetUrl = overlayEntity.getShareUrl();
			shareEntity.aid = overlayEntity.actionid;
			
			intent = new Intent();
			intent.setClass(OverlayBigActivity.this, ShareDialog.class);
			intent.putExtra(BaseActivity.EXTRA_PARAM, shareEntity);
			startActivity(intent);
			break;
		default:
			break;
		}
	}//end onClick
	
	/** 头像 **/
	private View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View view) {
			Tools.openUser(OverlayBigActivity.this, ((UserEntity)view.getTag()).id);
		}
	};
	
	/** 评论回复 **/
	private View.OnClickListener onClickListener_reply = new View.OnClickListener() {		
		@Override
		public void onClick(View view) {
			if (Tools.checkLogin(OverlayBigActivity.this)) {
				Intent intent = new Intent();
				intent.setClass(OverlayBigActivity.this, PublicRecommendActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
				intent.putExtra(PublicRecommendActivity.EXTRA_COMMENT, (CommentEntity)view.getTag());
				startActivity(intent);
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		pageIndex_recommend = 1;
		requestRecommendList();
	}
	
	/** 收藏 **/
	private void requestCollecion(){
		EventUtil.favorEvent(overlayEntity.actionid, new DefaultCallback() {
			@Override
			public void succeed() {
				dismissProgressDialog();
				setCollected();
				toastShort("收藏成功");
				session.getFavor_event_list().add(overlayEntity.actionid);
			}
			
			@Override
			public void fail(String error) {
				dismissProgressDialog();
				toastShort(error);
			}
		});
		MobclickAgent.onEvent(this, EVENT.ADD_FAV, overlayEntity.actionid);
	}
	
	/** 评论列表 **/
	private void requestRecommendList(){
		EventUtil.getEventComments(overlayEntity.actionid, pageIndex_recommend, new EntityListCallback<CommentEntity>() {
			@Override
			public void succeed(List<CommentEntity> list) {
				if(pageIndex_recommend == 1)
					overlayEntity.getCommentList().clear();
				overlayEntity.getCommentList().addAll(list);
				overlayEntity.replyCount = overlayEntity.getCommentList().size();
				setComment();
				pageIndex_recommend++;
			}
			
			@Override
			public void fail(String error) {
				toastShort(error);
			}
		});
	}
	
	@Override
	protected void setPageCount() {
		setPageName(PAGE.EVENT_DETAIL);
	}
}
