package com.fingertip.blabla.search.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.common.Tools;

/**
 * @author SunnyCoffee
 * @create 2013-10-24
 * @version 1.0
 * @desc 自定义Listview　下拉刷新,上拉加载更多
 */

public class RefreshableListView extends ListView implements OnScrollListener {

	// 区分当前操作是刷新还是加载
	public static final int REFRESH = 0;
	public static final int LOAD = 1;

	// 区分PULL和RELEASE的距离的大小
	private static final int SPACE = 20;

	// 定义header的四种状态和当前状态
	private static final int NONE = 0;
	private static final int PULL = 1;
	private static final int RELEASE = 2;
	private static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	private View header;
	private View footer;
	private TextView tip;
	private TextView lastUpdate;
	private ImageView arrow;
	private ProgressBar refreshing;

	private TextView noData;
	private TextView loadFull;
	private TextView more;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;

	private int firstVisibleItem;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	// 只有在listview第一个item显示的时候（listview滑到了顶部）才进行下拉刷新， 否则此时的下拉只是滑动listview
	private boolean isRecorded;
	private boolean isLoading;// 判断是否正在加载
	private boolean loadEnable = true;// 开启或者关闭加载更多功能
	private boolean isLoadFull;
	private boolean init;
	private int pageSize = 10;

	private RefreshListener refreshListener;

	public RefreshableListView(Context context) {
		super(context);
		initView(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public void setRefreshListener(RefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	// 这里的开启或者关闭加载更多，并不支持动态调整
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
//		this.removeFooterView(footer);
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// 初始化组件
	@SuppressLint("InflateParams")
	private void initView(Context context) {
		// 设置箭头特效
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.view_listview_footer, null);
		noData = (TextView) footer.findViewById(R.id.noData);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		more = (TextView) footer.findViewById(R.id.more);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.view_listview_header, null);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		tip = (TextView) header.findViewById(R.id.tip);
		lastUpdate = (TextView) header.findViewById(R.id.lastUpdate);
		refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

		// 为listview添加头部和尾部，并进行初始化
		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		addHeaderView(header);
		setOnScrollListener(this);
	}

	public void refreshComplete() {
		lastUpdate.setText(this.getContext().getString(R.string.lastUpdateTime, Tools.getTimeStr(System.currentTimeMillis())));
		state = NONE;
		if (!init) {
			init = true;
			addFooterView(footer);
		}
		refreshHeaderViewByState();
			
	}

	// 用于加载更多结束后的回调
	public void loadComplete() {
		isLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	// 根据listview滑动的状态判断是否需要加载更多
	private void ifNeedLoad(AbsListView view, int scrollState) {
		if (!loadEnable)
			return;
		try {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& view.getLastVisiblePosition() == view.getPositionForView(footer) 
					&& !isLoadFull && !isLoading) {
				refreshListener.onLoadMore();
				isLoading = true;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 监听触摸事件，解读手势
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRecorded = true;
				startY = (int) ev.getY();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (state == PULL) {
				state = NONE;
				refreshHeaderViewByState();
			} else if (state == RELEASE) {
				state = REFRESHING;
				refreshHeaderViewByState();
				refreshListener.onRefresh();
			}
			isRecorded = false;
			break;
		case MotionEvent.ACTION_MOVE:
			whenMove(ev);
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	public void initData() {
		init = false;
		state = REFRESHING;
		refreshHeaderViewByState();
		refreshListener.onRefresh();
	}

	// 解读手势，刷新header状态
	private void whenMove(MotionEvent ev) {
		if (!isRecorded) {
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		int topPadding = space - headerContentHeight;
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > headerContentHeight + SPACE) {
				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE) {
				state = PULL;
				refreshHeaderViewByState();
			} else if (space <= 0) {
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	// 调整header的大小。其实调整的只是距离顶部的高度。
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	/**
	 * 这个方法是根据结果的大小来决定footer显示的。
	 * <p>
	 * 这里假定每次请求的条数为10。如果请求到了10条。则认为还有数据。如过结果不足10条，则认为数据已经全部加载，这时footer显示已经全部加载
	 * </p>
	 * 
	 * @param refresh
	 * @param succeed
	 * @param resultSize
	 */
	public void setResultSize(boolean refresh, boolean succeed, int resultSize) {
		if (!succeed) {
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		} else {
			if (resultSize == 0) {
				isLoadFull = true;
				loading.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				loadFull.setVisibility(refresh ? View.GONE : View.VISIBLE);
				noData.setVisibility(refresh ? View.VISIBLE : View.GONE);
			} else if (resultSize < pageSize) {
				isLoadFull = true;
				loadFull.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				noData.setVisibility(View.GONE);
			} else if (resultSize == pageSize) {
				isLoadFull = false;
				loadFull.setVisibility(View.GONE);
				loading.setVisibility(View.VISIBLE);
				more.setVisibility(View.VISIBLE);
				noData.setVisibility(View.GONE);
			}
		}
		setLoadEnable(getAdapter().getCount() >= pageSize);
	}

	// 根据当前状态，调整header
	private void refreshHeaderViewByState() {
		switch (state) {
		case NONE:
			topPadding(-headerContentHeight);
			tip.setText(R.string.pull_to_refresh);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			tip.setVisibility(View.VISIBLE);
			lastUpdate.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			tip.setText(R.string.pull_to_refresh);
			tip.setText(R.string.release_to_refresh);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			refreshing.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			lastUpdate.setVisibility(View.GONE);
			break;
		}
	}

	// 用来计算header大小的。比较隐晦。因为header的初始高度就是0,貌似可以不用。
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) 
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec = lpHeight > 0 ? MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY) :
			MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		child.measure(childWidthSpec, childHeightSpec);
	}

	public interface RefreshListener {
		public void onRefresh();
		public void onLoadMore();
	}

}
