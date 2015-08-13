package com.fingertip.blabla.search;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseFragment;
import com.fingertip.blabla.common.LocationUtil;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.Validator;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.search.util.SearchUtil;
import com.fingertip.blabla.search.util.SearchUtil.Type;
import com.fingertip.blabla.search.widget.RefreshableListView;
import com.fingertip.blabla.search.widget.RefreshableListView.RefreshListener;

public class SearchFragment extends BaseFragment implements RefreshListener {
	
	private View mView;
	private RefreshableListView listView;
	private ImageView hidden_img;
	private AdapterSearch adapterSearch;
	
	private Type seach_type;
	private SearchMainActivity search_activity;
	private int current_page;
	
	public SearchFragment(SearchMainActivity search_activity, Type seach_type) {
		super();
		this.seach_type = seach_type;
		this.search_activity = search_activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null){
			mView = inflater.inflate(R.layout.listview_search, container, false);
			findViews();
			setupViews();
			listView.initData();
		}
		ViewGroup parent = (ViewGroup)mView.getParent();
		if(parent != null){
			parent.removeAllViewsInLayout();
		}
		return mView;
	}
	
	private void findViews() {
		listView = (RefreshableListView)mView.findViewById(R.id.listView);
		hidden_img = (ImageView)mView.findViewById(R.id.hidden_img);
	}


	private void setupViews() {
		adapterSearch = new AdapterSearch(mView.getContext(), hidden_img);
		listView.setAdapter(adapterSearch);
		listView.setOnItemClickListener(adapterSearch);
		listView.setRefreshListener(this);
		listView.setPageSize(20);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void refresh() {
		loadData(1, false);
		current_page = 1;
	}
	
	private void loadData(final int page, final boolean append) {
		Location location = LocationUtil.getLocation(search_activity);
		SearchUtil.searchEvents(seach_type, location, page, new SearchUtil.EventListCallback() {
			@Override
			public void succeed(List<EventEntity> list) {
				if (append)
					adapterSearch.appendAllData(list);
				else
					adapterSearch.addAllData(list);
				afterLoad(append, true, Validator.isEmptyList(list) ? 0 : list.size());
			}
			
			@Override
			public void fail(String error) {
				Tools.toastShort(search_activity, error);
				if (append)
					current_page--;
				afterLoad(append, false, 0);
			}
		});
	}
	
	private void afterLoad(boolean append, boolean succeed, int size) {
		listView.setResultSize(!append, succeed, size);
		if (append)
			listView.loadComplete();
		else
			listView.refreshComplete();
	}

	@Override
	public void onRefresh() {
		refresh();
	}

	@Override
	public void onLoadMore() {
		current_page++;
		loadData(current_page, true);
	}
}
