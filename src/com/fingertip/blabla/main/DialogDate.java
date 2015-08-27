package com.fingertip.blabla.main;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.fingertip.blabla.R;
import com.fingertip.blabla.common.wheelview.WheelView;
import com.fingertip.blabla.common.wheelview.listener.ArrayWheelAdapter;
import com.fingertip.blabla.common.wheelview.listener.OnWheelChangedListener;
import com.fingertip.blabla.common.wheelview.listener.OnWheelScrollListener;

public class DialogDate extends Dialog {
	
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
	
	private OnDateSelectdListener onDateSelectdListener;
	
	private Calendar calendar;
	
	public DialogDate(Context context, OnDateSelectdListener onDateSelectdListener) {
		super(context, R.style.MyDialogStyleBottom);
		this.onDateSelectdListener = onDateSelectdListener;
		this.calendar = Calendar.getInstance();
		setContentView(R.layout.dialog_date);
		setupViews();
	}

	private void setupViews() {
		setTimeViews();
		
		findViewById(R.id.tv_config).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				dismiss();
				if (onDateSelectdListener != null)
					onDateSelectdListener.onDateSelectd(getTimeString());
			}
		});
	}
	
	private void setTimeViews(){
		
		// 初始化一个滑轮视图对象
		wheel_year = (WheelView) findViewById(R.id.fast_year);
		// 设置滑轮标记
		wheel_year.setTag("year");
		// 设置滑轮数据集
		adapter_year = new ArrayWheelAdapter<String>(YEAR_STRING);
		wheel_year.setAdapter(adapter_year);
		// 设置滑轮当前所在值
		wheel_year.setCurrentItem(wheel_year.getCurrentVal(getYear()));
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
		adapter_month = new ArrayWheelAdapter<String>(MONTH_STRING);
		wheel_month.setAdapter(adapter_month);
		wheel_month.setCurrentItem(wheel_month.getCurrentVal(getMonth()));
		wheel_month.setCyclic(true);
		wheel_month.setLabel("月");
		wheel_month.addChangingListener(wheelChangeListener);
		wheel_month.addScrollingListener(wheelScrolledListener, null);
		wheel_day = (WheelView)findViewById(R.id.fast_day);
		adapter_day = new ArrayWheelAdapter<String>(DAY_STRING);
		wheel_day.setAdapter(adapter_day);
		wheel_day.setCurrentItem(calendar.get(Calendar.DATE) - 1);
		wheel_day.setLabel("日");
		wheel_day.setTag("day");
		wheel_day.setCyclic(true);
		wheel_day.addChangingListener(wheelChangeListener);
		wheel_day.addScrollingListener(wheelScrolledListener, null);
		wheel_hours = (WheelView)findViewById(R.id.fast_hours);
		adapter_hour = new ArrayWheelAdapter<String>(HOUR_STRING);
		wheel_hours.setAdapter(adapter_hour);
		wheel_hours.setCyclic(true);
		wheel_hours.setTag("hours");
		wheel_minute = (WheelView)findViewById(R.id.fast_mintue);
		adapter_minute = new ArrayWheelAdapter<String>(MINTUE_STRING);
		wheel_minute.setAdapter(adapter_minute);
		wheel_minute.setCyclic(true);
		wheel_minute.setTag("minute");
		wheel_minute.setLabel("分");
		wheel_hours.setLabel("时");
		wheel_hours.setCurrentItem(23);
		wheel_minute.setCurrentItem(59);
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
	}
	
	private String getYear() {
		return (calendar.get(Calendar.YEAR) % 100) + "";
	}

	private String getMonth() {
		int month = calendar.get(Calendar.MONTH) + 1;
		return month < 10 ? "0" + month : "" + month;
	}
	
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
	
	public interface OnDateSelectdListener {
		public void onDateSelectd(String time);
	}
	
	public static final String[] HOUR_STRING = { "00", "01", "02", "03", "04",
			"05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
			"16", "17", "18", "19", "20", "21", "22", "23" };

	public static final String[] MINTUE_STRING = { "00", "01", "02", "03",
			"04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
			"26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36",
			"37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58",
			"59" };
	public static final String[] DAY_STRING = { "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
			"17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
			"28", "29", "30", "31" };
	public static final String[] MONTH_STRING = { "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "10", "11", "12" };

	public static final String[] YEAR_STRING = { "13", "14", "15", "16", "17",
			"18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
			"29", "30", "31", "32", "33", "34", "35" };
}
