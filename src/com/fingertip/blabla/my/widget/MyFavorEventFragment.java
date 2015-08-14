package com.fingertip.blabla.my.widget;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseFragment;
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.my.MyEventActivity;
import com.fingertip.blabla.my.adapter.AdapterMyFavorEvent;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.UserUtil;

public class MyFavorEventFragment extends BaseFragment implements Deleteable {
	
	private View mView;
	
	private ListView listView;
	private View view_nodata;
	private Button my_event_find_btn;
	private AdapterMyFavorEvent adapter;
	private View layout_loading;
	private GifView gifView;
	private boolean loaded;
	
	private MyEventActivity activity;
	
	public MyFavorEventFragment(MyEventActivity activity) {
		super();
		this.activity = activity;
		this.loaded = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null){
			mView = inflater.inflate(R.layout.fragment_event_favor, container, false);
			findViews();
			setupViews();
			loadData();
		}
		ViewGroup parent = (ViewGroup)mView.getParent();
		if(parent != null){
			parent.removeAllViewsInLayout();
		}
		return mView;
	}
	
	private void findViews() {
		layout_loading = mView.findViewById(R.id.layout_loading);
		gifView = (GifView)mView.findViewById(R.id.gifView);
		listView = (ListView)mView.findViewById(R.id.event_listView);
		view_nodata = mView.findViewById(R.id.my_event_empty);
		my_event_find_btn = (Button)mView.findViewById(R.id.my_event_find_btn);
	}

	private void setupViews() {
		layout_loading.setVisibility(View.VISIBLE);
		gifView.setGifImage(R.drawable.loading2);
		adapter = new AdapterMyFavorEvent(activity, view_nodata);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		my_event_find_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.searchEvent(getActivity());
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (loaded)
			layout_loading.setVisibility(View.GONE);
	}

	private void loadData() {
		UserUtil.getUserFavorEvents(new EntityListCallback<EventEntity>() {
			@Override
			public void succeed(List<EventEntity> list) {
				adapter.addAllList(list);
				afterLoad();
			}
			
			@Override
			public void fail(String error) {
				activity.toastShort(error);
				afterLoad();
			}
		});
	}
	
	private void afterLoad() {
		loaded = true;
		layout_loading.setVisibility(View.GONE);
	}

	@Override
	public void begainDelete() {
		adapter.begainDelete();
	}

	@Override
	public void endDelete() {
		adapter.endDelete();
	}

	@Override
	public void doDelete() {
		adapter.doDelete();
	}

	@Override
	public int size() {
		return adapter.getCount();
	}
	
	@Override
	public int selectedSize() {
		return adapter.selectedSize();
	}
}
