package com.fingertip.blabla.main;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.fingertip.blabla.CommonData;
import com.fingertip.blabla.R;
import com.fingertip.blabla.common.wheelview.WheelView;
import com.fingertip.blabla.common.wheelview.listener.ArrayWheelAdapter;
import com.fingertip.blabla.common.wheelview.listener.OnWheelChangedListener;
import com.fingertip.blabla.common.wheelview.listener.OnWheelScrollListener;

public class DialogDate extends Dialog{
	
	private WheelView wheel_year;
	private WheelView wheel_month;
	private WheelView wheel_day;
	private WheelView wheel_hours;
	private WheelView wheel_minute;
	
	private ArrayWheelAdapter<String> adapter_year;
	private ArrayWheelAdapter<String> adapter_month;
	private ArrayWheelAdapter<String> adapter_day;
	private ArrayWheelAdapter<String> adapter_hour;
	private ArrayWheelAdapter<String> adapter_minute;
	
	public DialogDate(Context context) {
		super(context, R.style.MyDialogStyleBottom);
		setContentView(R.layout.dialog_date);
		
		setupViews();
	}

	private void setupViews() {
		setTimeViews();
		
		findViewById(R.id.tv_config).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
	}
	
	private void setTimeViews(){
		
		// 初始化一个滑轮视图对象
		wheel_year = (WheelView) findViewById(R.id.fast_year);
		// 设置滑轮标记
		wheel_year.setTag("year");
		// 设置滑轮数据集
		adapter_year = new ArrayWheelAdapter<String>(CommonData.YEAR_STRING);
		wheel_year.setAdapter(adapter_year);
		// 设置滑轮当前所在值
		wheel_year.setCurrentItem(wheel_year.getCurrentVal("15"));
		// 设置滑轮是否可以循环滑动
		wheel_year.setCyclic(true);
		// 设置滑轮标题
		wheel_year.setLabel("年");
		// 添加滑轮变化监听事件
		wheel_year.addChangingListener(wheelChangeListener);
		// 添加滑轮滚动变化监听事件
		wheel_year.addScrollingListener(wheelScrolledListener, null);
		
		wheel_month = (WheelView) findViewById(R.id.fast_month);
		wheel_month.setTag("month");
		adapter_month = new ArrayWheelAdapter<String>(CommonData.MONTH_STRING);
		wheel_month.setAdapter(adapter_month);
		wheel_month.setCurrentItem(wheel_month.getCurrentVal("02"));
		wheel_month.setCyclic(true);
		wheel_month.setLabel("月");
		wheel_month.addChangingListener(wheelChangeListener);
		wheel_month.addScrollingListener(wheelScrolledListener, null);
		wheel_day = (WheelView)findViewById(R.id.fast_day);
		adapter_day = new ArrayWheelAdapter<String>(CommonData.DAY_STRING);
		wheel_day.setAdapter(adapter_day);
		wheel_day.setCurrentItem(3);
		wheel_day.setLabel("日");
		wheel_day.setTag("day");
		wheel_day.setCyclic(true);
		wheel_day.addChangingListener(wheelChangeListener);
		wheel_day.addScrollingListener(wheelScrolledListener, null);
		wheel_hours = (WheelView)findViewById(R.id.fast_hours);
		adapter_hour = new ArrayWheelAdapter<String>(CommonData.HOUR_STRING);
		wheel_hours.setAdapter(adapter_hour);
		wheel_hours.setCyclic(true);
		wheel_hours.setTag("hours");
		wheel_minute = (WheelView)findViewById(R.id.fast_mintue);
		adapter_minute = new ArrayWheelAdapter<String>(CommonData.MINTUE_STRING);
		wheel_minute.setAdapter(adapter_minute);
		wheel_minute.setCyclic(true);
		wheel_minute.setTag("minute");
		wheel_minute.setLabel("分");
		wheel_hours.setLabel("时");
		wheel_hours.setCurrentItem(1);
		wheel_minute.setCurrentItem(2);
		wheel_minute.addScrollingListener(wheelScrolledListener, null);
		wheel_minute.addChangingListener(wheelChangeListener);
		wheel_minute.addScrollingListener(wheelScrolledListener, null);
		wheel_hours.addChangingListener(wheelChangeListener);
		wheel_hours.addScrollingListener(wheelScrolledListener, null);
		wheel_hours.setleftCheck(true);
		wheel_minute.setleftCheck(true);
		wheel_day.setleftCheck(true);
		wheel_year.setleftCheck(true);
	}//end setTimeViews

	public String getTimeString(){
		return "20" + adapter_year.getItem(wheel_year.getCurrentItem())
					+ "-" + adapter_month.getItem(wheel_month.getCurrentItem()) 
					+ "-" + adapter_day.getItem(wheel_day.getCurrentItem())
					+ " " + adapter_hour.getItem(wheel_hours.getCurrentItem())
					+ ":" + adapter_minute.getItem(wheel_minute.getCurrentItem())
					+ ":" + "00";
	}//end getTimeString
	
	
	private OnWheelScrollListener wheelScrolledListener = new OnWheelScrollListener() {
		public void onScrollingStarted(WheelView wheel) {
//			wheelScrolled = true;
		}
		@Override
		public void onScrollingFinished(WheelView wheel) {
//			String tag = wheel.getTag().toString();
//			wheelScrolled = false;
		}
	};

	// Wheel changed listener
	private OnWheelChangedListener wheelChangeListener = new OnWheelChangedListener() {
		@Override
		public void onLayChanged(WheelView wheel, int oldValue, int newValue, LinearLayout layout) {
		}
	};
}
