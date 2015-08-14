package com.fingertip.blabla.my.widget;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseFragment;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.common.gif.GifView;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.my.MyEventActivity;
import com.fingertip.blabla.my.adapter.AdapterMyEvent;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MyEventFragment extends BaseFragment implements Deleteable {
	
	private View mView;
	
	private ListView listView;
	private View view_nodata;
	private Button my_event_pub_btn;
	private AdapterMyEvent adapter;
	private View layout_loading;
	private GifView gifView;
	private boolean loaded;
	
	private UserSession session;
	private MyEventActivity activity;
	
	public MyEventFragment(MyEventActivity activity) {
		super();
		this.activity = activity;
		this.loaded = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(mView == null){
			mView = inflater.inflate(R.layout.fragment_event_publish, container, false);
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
		my_event_pub_btn = (Button)mView.findViewById(R.id.my_event_pub_btn);
	}

	private void setupViews() {
		layout_loading.setVisibility(View.VISIBLE);
		gifView.setGifImage(R.drawable.loading2);
		adapter = new AdapterMyEvent(activity, view_nodata);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(adapter);
		my_event_pub_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Tools.pubEvent(activity);
			}
		});
		session = UserSession.getInstance();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (loaded)
			layout_loading.setVisibility(View.GONE);
	}

	private void loadData() {
		JSONObject data = new JSONObject();
//		{"fc":"action_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.PAGENO, 1);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_EVENT, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取活动列表失败:" + e.getMessage();
				}
				if (error != null)
					activity.toastShort(error);
				else if (json != null) {
					try {
						List<EventEntity> list = EventEntity.parseList(json);
						adapter.addAllList(list);
					} catch (Exception e) {
					}
				}
				afterLoad();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				activity.toastShort(ServerConstants.NET_ERROR_TIP);
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
