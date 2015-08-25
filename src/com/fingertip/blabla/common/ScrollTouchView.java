package com.fingertip.blabla.common;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fingertip.blabla.R;

public class ScrollTouchView extends HorizontalScrollView{
	private RadioGroup radioGroup;
	private ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();
	private UpdateNotify updateNotify;
	
	private int displayWidth;
	@SuppressWarnings("unused")
	private int displayHeight;
	/** 每项最大宽度 **/
	private int maxWidth;
	private int itemWidth;
	
	private int lastScrollX = 0;
	private int distanceRight;
	private int distanceLeft;
	
	//选中后的下标指示
	private Drawable drawable_tabBottom = null;
	
	private HashMap<String, Integer> hashMap_position = new HashMap<String, Integer>();
	
	/** 未选中图片 **/
	private SparseArray<Drawable> hashMap_drawableTopUnselection = new SparseArray<Drawable>();
	/** 选中图片 **/
	private SparseArray<Drawable> hashMap_drawableTopSelection = new SparseArray<Drawable>();
	
	private int color_text_unselection = getResources().getColor(R.color.black);
	private int color_text_selection = getResources().getColor(R.color.black);
	
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
//			int position = hashMap_position.get(Integer.toString(radioGroup.getCheckedRadioButtonId()));
//			int scrollDistance = getScrollX() - selfScroll;
//			
//			if(Math.abs(scrollDistance) > itemWidth / 2){
//				if(scrollDistance < 0 && position > 0){
//					radioButtons.get(position - 1).setChecked(true);
//					selfScroll = getScrollX();
//				}else if(scrollDistance > 0 && position < (radioButtons.size() - 1)){
//					radioButtons.get(position + 1).setChecked(true);
//					selfScroll = getScrollX();
//				}
//			}
			
			if(lastScrollX == getScrollX()){
				computeScrollPosition();
			}else{
//				handler.sendEmptyMessageDelayed(0, 150);
				handler.sendEmptyMessageDelayed(0, 50);
				lastScrollX = getScrollX();
			}
		}
	};//end handler
	
	public ScrollTouchView(Context context) {
		super(context);
		init();
	}
	public ScrollTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public ScrollTouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			handler.sendEmptyMessageAtTime(0, 200);
			break;

		default:
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}//end dispatchTouchEvent
	
	private void computeScrollPosition(){
		int scrollX = getScrollX();
		
		if(itemWidth != 0){
			
			int remainder = scrollX % itemWidth;
			
			if(remainder < itemWidth / 2){
				scrollTo((scrollX / itemWidth) * itemWidth, 0);
				
				//scrollTo(itemWidth+(scrollX / itemWidth) * itemWidth, 0);
			}
			else{
				//scrollTo(itemWidth+(scrollX / itemWidth) * itemWidth, 0);
				scrollTo(itemWidth+(scrollX / itemWidth) * itemWidth, 0);
			}
		}
	}//end computeScrollPosition
	
	private void init(){
		this.setHorizontalScrollBarEnabled(false);
		displayWidth = getResources().getDisplayMetrics().widthPixels;
		displayHeight = getResources().getDisplayMetrics().heightPixels;
		maxWidth = displayWidth / 4;
		radioGroup = new RadioGroup(getContext());
		radioGroup.setOrientation(RadioGroup.HORIZONTAL);
		addView(radioGroup);
		
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(updateNotify != null){
					updateNotify.notifyUpdata(hashMap_position.get(Integer.toString(checkedId)));
				}
				int index = hashMap_position.get(Integer.toString(checkedId));
				resetItemColor(index);
				if(index > 2){
					smoothScrollTo((index - 2) * itemWidth, 0);
				}
				else if(index <= 2){
					smoothScrollTo(0, 0);
				}
			}//end onCheckedChanged
		});
		
		
	}//end init
	
	/**
	 * 设置文本颜色
	 * @param selection
	 * @param unSelection
	 */
	public void setTextColor(int selection, int unselection){
		this.color_text_selection = selection;
		this.color_text_unselection = unselection;
	}
	
	public void setTabBottomDrawable(Drawable drawable){
		this.drawable_tabBottom = drawable;
	}
	
	public void setMarginLeftRight(int marginLeft, int marginRight){
		this.distanceLeft = marginLeft;
		this.distanceRight = marginRight;
		maxWidth = (displayWidth - distanceLeft - distanceRight) / 4;
	}//end setMarginLeftRight
	
	public void setUpdateNotify(UpdateNotify updateNotify){
		this.updateNotify = updateNotify;
	}
	
	public void setItemTextColor(){
		
	}
	
	public String getSelectedText(){
		int position = hashMap_position.get(Integer.toString(radioGroup.getCheckedRadioButtonId()));
		return "" + radioButtons.get(position).getText().toString();
	}//end getSelectedText
	
	public void addItem(String name, Drawable drawableUnselection, Drawable drawableSelection){
		RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.MATCH_PARENT);
		RadioButton radioButton = new RadioButton(getContext());
		radioButton.setGravity(Gravity.CENTER);
		radioButton.setPadding(0, 0, 0, 0);
		radioButton.setTextSize(14);
		radioButton.setText("" + name);
		Bitmap bitmap = null;
		radioButton.setButtonDrawable(new BitmapDrawable(getResources(), bitmap));
		radioGroup.addView(radioButton, layoutParams);
		hashMap_position.put(Integer.toString(radioButton.getId()), radioGroup.getChildCount() - 1);
		hashMap_drawableTopUnselection.put(radioGroup.getChildCount() - 1, drawableUnselection);
		hashMap_drawableTopSelection.put(radioGroup.getChildCount() - 1, drawableSelection);
		radioButtons.add(radioButton);
		
		resetItemWidth();
	}//end addItem
	
	public void clearItem(){
		radioGroup.removeAllViews();
		hashMap_position.clear();
		radioButtons.clear();
	}//end clearItem
	
	private void resetItemWidth(){
		int count = radioGroup.getChildCount();
		if(count < 5){
			itemWidth = (displayWidth - distanceLeft - distanceRight) / count;
		}else{
			itemWidth = maxWidth;
		}
		for(int i = 0; i < count; i++){
			RadioGroup.LayoutParams layoutParams = (RadioGroup.LayoutParams)radioGroup.getChildAt(i).getLayoutParams();
			layoutParams.width = itemWidth;
		}
	}//end resetItemWidth
	
	/**
	 * 
	 * @param index:最终返回的位置
	 * @return
	 */
	public int setSelectionPosition(int index){
		if(index <= 0){
			index = 0;
		}else{
			while(index >= radioButtons.size()){
				index--;
				if(index < 0){
					index = 0;
					break;
				}
			}
		}
		Log.i("scrolltouchview","<<<<<<<<<<<<<selection:" + index);
		if(radioButtons.get(index) != null){
			radioButtons.get(index).setChecked(true);
		}
		
		return index;
	}//end setSelectionPosition
	
	private void resetItemColor(int index){
		for(int i = 0; i < radioButtons.size(); i++){
			radioButtons.get(i).setTextColor(color_text_unselection);
			radioButtons.get(i).setCompoundDrawablesWithIntrinsicBounds(null, hashMap_drawableTopUnselection.get(i), null, null);
		}
		radioButtons.get(index).setTextColor(color_text_selection);
		radioButtons.get(index).setCompoundDrawablesWithIntrinsicBounds(null, hashMap_drawableTopSelection.get(index), null, drawable_tabBottom);
	}//end resetItemColor
	
	public static interface UpdateNotify{
		public void notifyUpdata(int index);
	}
	
}
