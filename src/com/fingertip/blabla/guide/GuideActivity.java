package com.fingertip.blabla.guide;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.main.SplashActivity;
import com.fingertip.blabla.main.ViewPagerAdapter;

public class GuideActivity extends BaseActivity implements OnPageChangeListener, OnTouchListener {

	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	private TextView skip_txt;

	// ����ͼƬ��Դ
	private static final int[] pics = { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3, R.drawable.guide_4 };

	// �ײ�С��ͼƬ
	private ImageView[] dots;

	// ��¼��ǰѡ��λ��
	private int currentIndex;
	private float last_x;
	private boolean jumping = false;
	
	private SharedPreferenceUtil sp;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		sp = new SharedPreferenceUtil(this);
		boolean guide = sp.getBooleanValue(SharedPreferenceUtil.NEED_GUIDE, true);
		if (!guide)
			gotoMain(false);
		else {
			views = new ArrayList<View>();
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// ��ʼ������ͼƬ�б�
			for (int i = 0; i < pics.length; i++) {
				ImageView iv = new ImageView(this);
				iv.setLayoutParams(mParams);
				iv.setImageResource(pics[i]);
				iv.setScaleType(ScaleType.FIT_XY);
				views.add(iv);
			}
			skip_txt = (TextView) findViewById(R.id.skip_txt);
			skip_txt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gotoMain(true);
				}
			});
			vp = (ViewPager) findViewById(R.id.viewpager);
			// ��ʼ��Adapter
			vpAdapter = new ViewPagerAdapter(views);
			vp.setAdapter(vpAdapter);
			// �󶨻ص�
			vp.setOnPageChangeListener(this);
			vp.setOnTouchListener(this);
			// ��ʼ���ײ�С��
			initDots();
		}
	}

	private void initDots() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.dot_line);
		dots = new ImageView[pics.length];

		// ѭ��ȡ��С��ͼƬ
		for (int i = 0; i < pics.length; i++) {
			dots[i] = (ImageView) ll.getChildAt(i);
			dots[i].setTag(i);// ����λ��tag������ȡ���뵱ǰλ�ö�Ӧ
		}
		currentIndex = 0;
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));// ����ѡ��״̬
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
	 * ��ֻ��ǰ����С���ѡ��
	 */
	private void setCurDot(int positon) {
		if (positon < 0 || positon >= pics.length || currentIndex == positon)
			return;
		dots[positon].setImageDrawable(getResources().getDrawable(R.drawable.dot_gray));
		dots[currentIndex].setImageDrawable(getResources().getDrawable(R.drawable.dot_white));
		currentIndex = positon;
	}
	
	// ������״̬�ı�ʱ����,arg0 ==1��ʾ���ڻ�����arg0==2��ʾ��������ˣ�arg0==0��ʾʲô��û������ҳ�濪ʼ������ʱ������״̬�ı仯˳��Ϊ��1��2��0����
	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	//��ҳ���ڻ�����ʱ�����ô˷������ڻ�����ֹ֮ͣǰ���˷�����һֱ�õ����á��������������ĺ���ֱ�Ϊ��arg0 :��ǰҳ�棬������������ҳ�档arg1:��ǰҳ��ƫ�Ƶİٷֱȡ�arg2:��ǰҳ��ƫ�Ƶ�����λ�á�
	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
	}

	//�˷�����ҳ����ת���õ����ã�arg0���㵱ǰѡ�е�ҳ���position
	@Override
	public void onPageSelected(int position) {
		// ���õײ�С��ѡ��״̬
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
}
