package com.fingertip.blabla.guide;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.main.SplashActivity;
import com.fingertip.blabla.main.ViewPagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GuideActivity extends BaseActivity implements OnPageChangeListener, OnTouchListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;

	// 引导图片资源
	private static final int[] pics = { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3, R.drawable.guide_4 };

	// 底部小店图片
	private ImageView[] dots;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	// 记录当前选中位置
	private int currentIndex;
	private float last_x;
	private boolean jumping = false;
	
	private SharedPreferenceUtil sp;

	/** Called when the activity is first created. */
	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		sp = new SharedPreferenceUtil(this);
		boolean guide = sp.getBooleanValue(SharedPreferenceUtil.NEED_GUIDE, true);
		if (!guide)
			gotoMain(false);
		else {
			this.options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_pic_empty)
				.cacheInMemory(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
			views = new ArrayList<View>();
			// 初始化引导图片列表
			for (int i = 0; i < pics.length; i++) {
				View view = LayoutInflater.from(this).inflate(R.layout.view_guide_page, null);
				ImageView img = (ImageView)view.findViewById(R.id.guide_img);
				img.setTag(pics[i]);
				ImageView btn = (ImageView)view.findViewById(R.id.guide_btn);
				if (i == pics.length -1) {
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							gotoMain(true);
						}
					});
				}
				views.add(view);
			}
			vp = (ViewPager) findViewById(R.id.viewpager);
			// 初始化Adapter
			vpAdapter = new ViewPagerAdapter(views) {
				@Override
				public Object instantiateItem(ViewGroup container, int position) {
					View view = views.get(position);
					container.addView(view, 0);
					ImageView img = (ImageView)view.findViewById(R.id.guide_img);
					imageLoader.displayImage("drawable://" + Integer.parseInt(img.getTag().toString()), img, options);
					return view;
				}
			};
			vp.setAdapter(vpAdapter);
			// 绑定回调
			vp.setOnPageChangeListener(this);
			vp.setOnTouchListener(this);
			// 初始化底部小点
			initDots();
		}
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.dot_line);
		dots = new ImageView[pics.length];
		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
		}
		currentIndex = 0;
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));// 设置选中状态
	}
	
	private void gotoMain(boolean set_sp) {
		if (!jumping) {
			jumping = true;
			sp.setBooleanValue(SharedPreferenceUtil.NEED_GUIDE, false);
			Intent intent = new Intent();
			intent.setClass(this, SplashActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 这只当前引导小点的选中
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon >= pics.length || currentIndex == positon)
			return;
		dots[positon].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
		currentIndex = positon;
	}
	
	// 当滑动状态改变时调用,arg0 ==1表示正在滑动，arg0==2表示滑动完毕了，arg0==0表示什么都没做。当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）。
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	//当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：arg0 :当前页面，及你点击滑动的页面。arg1:当前页面偏移的百分比。arg2:当前页面偏移的像素位置。
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
	}

	//此方法是页面跳转完后得到调用，arg0是你当前选中的页面的position
	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			last_x = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if ((last_x - event.getX()) > 100 && (currentIndex == views.size() - 1))
				gotoMain(true);
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.clearMemoryCache();
	}
}
