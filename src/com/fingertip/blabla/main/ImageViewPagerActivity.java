package com.fingertip.blabla.main;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.DotPageDirector;
import com.fingertip.blabla.entity.ImgEntityList.ImgEntity;
import com.lidroid.xutils.BitmapUtils;

/**
 * 图片列表（横向显示，如游戏详情）
 * @author Administrator
 *
 */
public class ImageViewPagerActivity extends BaseActivity {
	
	public static final String INDEX_SHOW = "index_show";
	
	private ViewPager viewPager;
	private DotPageDirector dotPageDirector;
	private ViewPagerAdapter adapterImageViews;

//	private ArrayList<String> imgList;
	private ArrayList<ImgEntity> imgList;
	private ArrayList<View> list_imageViews = new ArrayList<View>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageviewpager);
		
		findViews();
		setupViews();
		initData();
	}

	private void findViews() {
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		dotPageDirector = (DotPageDirector)findViewById(R.id.dot_page_director);
		dotPageDirector.setDotRes(R.drawable.dot_white, R.drawable.dot_gray);
		dotPageDirector.setDotPadding(2, 12);
		
	}

	private void setupViews() {
		
		adapterImageViews = new ViewPagerAdapter(list_imageViews);
		viewPager.setAdapter(adapterImageViews);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {			
			@Override
			public void onPageSelected(int current) {
				dotPageDirector.setCurrent(current);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			@Override
			public void onPageScrollStateChanged(int arg0) { }
		});
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("所有图片");
	}//end setupViews

	@SuppressWarnings("unchecked")
	private void initData() {
		imgList = (ArrayList<ImgEntity>)getIntent().getSerializableExtra(EXTRA_PARAM);
		if(imgList == null){
			Toast.makeText(ImageViewPagerActivity.this, "无查询图片", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ImageView imageView;
		BitmapUtils bitmapUtils = new BitmapUtils(ImageViewPagerActivity.this);
		
		for (int i = 0; i < imgList.size(); i++) {
			imageView = new ImageView(ImageViewPagerActivity.this);
			list_imageViews.add(imageView);
			imageView.setAdjustViewBounds(true);
//			imageView.setImageResource(imgList.get(i));
			bitmapUtils.display(imageView, imgList.get(i).big);
		}
		adapterImageViews.notifyDataSetChanged();
		dotPageDirector.setCount(adapterImageViews.getCount(), 0);
		int index = getIntent().getIntExtra(INDEX_SHOW, 0);
		viewPager.setCurrentItem(index);
		dotPageDirector.setCurrent(index);
		
	}//end initData

}
