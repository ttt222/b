package com.fingertip.blabla.my.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.common.ImageCache;
import com.fingertip.blabla.common.ServerConstants;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.common.ServerConstants.URL;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.my.MyEventActivity;
import com.fingertip.blabla.my.widget.Deleteable;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class AdapterMyFavorEvent extends BaseAdapter implements OnItemClickListener, OnClickListener, Deleteable {
	
	private MyEventActivity activity;
	private View empty_view;
	private ImageView hidden_img;
	private List<EventEntity> arrayList  = new ArrayList<EventEntity>();
	private BitmapUtils bitmapUtils;
	private ImageCache imageCache;
	private SharedPreferenceUtil sp;
	
	private List<String> delete_ids = new ArrayList<String>();
	private boolean delete;
	
	public AdapterMyFavorEvent(MyEventActivity activity, View empty_view, ImageView hidden_img){
		this.activity = activity;
		this.empty_view = empty_view;
		this.hidden_img = hidden_img;
		sp = new SharedPreferenceUtil(activity);
		bitmapUtils = new BitmapUtils(activity);
		imageCache = ImageCache.getInstance();
		this.delete = false;
	}
	
	public void addAllList(List<EventEntity> list){
		if(list != null){
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void appendList(List<EventEntity> list){
		if(list != null){
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		int count = arrayList.size();
		if (count <= 0)
			empty_view.setVisibility(View.VISIBLE);
		else
			empty_view.setVisibility(View.GONE);
		return count;
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHoler viewHoler;
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_my_favor_event, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.head_img = (ImageView)convertView.findViewById(R.id.event_sender_head);
			viewHoler.sender_name_text = (TextView)convertView.findViewById(R.id.event_sender_name);
			viewHoler.event_title_text = (TextView)convertView.findViewById(R.id.event_title);
			viewHoler.event_type_text = (TextView)convertView.findViewById(R.id.event_type);
			viewHoler.event_send_time_text = (TextView)convertView.findViewById(R.id.event_send_time);
			viewHoler.event_up_count_text = (TextView)convertView.findViewById(R.id.event_up_count);
			viewHoler.event_status_text = (TextView)convertView.findViewById(R.id.event_status);
			
			viewHoler.v_delete_check = (LinearLayout)convertView.findViewById(R.id.v_delete_check);
			viewHoler.iv_delete_check = (ImageView)convertView.findViewById(R.id.iv_delete_check);
//			viewHoler.tv_delete = (TextView)convertView.findViewById(R.id.tv_delete);
//			viewHoler.tv_delete.setOnClickListener(this);
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		
		EventEntity event = (EventEntity)getItem(position);
		try {
			imageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, viewHoler.head_img, hidden_img);
		} catch (Exception e) {
		}
		viewHoler.sender_name_text.setText(event.sender.nick_name);
		viewHoler.event_title_text.setText(event.title);
		viewHoler.event_type_text.setText(event.kindof);
		viewHoler.event_up_count_text.setText(event.likedcount + "");
		if (EventEntity.STATUS_IN.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.green_event));
		else if (EventEntity.STATUS_OVER.equals(event.statusof))
			viewHoler.event_status_text.setTextColor(activity.getResources().getColor(R.color.red_event));
		viewHoler.event_status_text.setText(event.getStatusStr());
		viewHoler.event_send_time_text.setText(Tools.getTimeStr(event.send_time));
		
		if (delete) {
//			viewHoler.tv_delete.setVisibility(View.VISIBLE);
			viewHoler.v_delete_check.setVisibility(View.VISIBLE);
			if (viewHoler.checked)
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			else
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
		} else {
//			viewHoler.tv_delete.setVisibility(View.GONE);
			viewHoler.v_delete_check.setVisibility(View.GONE);
			viewHoler.checked = false;
		}
		viewHoler.event_id = event.id;
//		viewHoler.position = position;
		return convertView;
	}

	class ViewHoler{
		ImageView head_img;
		TextView sender_name_text;
		TextView event_title_text;
		TextView event_type_text;
		TextView event_send_time_text;
		TextView event_up_count_text;
		TextView event_status_text;
		
		//É¾³ý
		LinearLayout v_delete_check;
		ImageView iv_delete_check;
//		TextView tv_delete;
		String event_id;
//		int position;
		boolean checked;
	}

	@Override
	public void begainDelete() {
		delete = true;
		notifyDataSetChanged();
	}

	@Override
	public void endDelete() {
		delete = false;
		delete_ids.clear();
		notifyDataSetChanged();	
	}

	@Override
	public void doDelete() {
		deleteEvents();
	}

	@Override
	public int size() {
		return arrayList.size();
	}
	
	@Override
	public int selectedSize() {
		return delete_ids.size();
	}

	@Override
	public void onClick(View v) {
//		int v_id = v.getId();
//		switch (v_id) {
//		case R.id.tv_delete:
//			ViewHoler viewHoler = (ViewHoler)((View)v.getParent()).getTag();
//			deleteEvent(viewHoler.event_id, viewHoler.position);
//			break;
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHoler viewHoler = (ViewHoler) view.getTag();
		if(delete) {
			if (viewHoler.checked) {
				delete_ids.remove(viewHoler.event_id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
			} else {
				delete_ids.add(viewHoler.event_id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			}
			viewHoler.checked = !viewHoler.checked;
			activity.setSelectedCount(delete_ids.size());
		} else {
			//Ìø×ªµ½»î¶¯Ò³
			Tools.openEvent(activity, viewHoler.event_id);
		}
	}
	
	private void deleteEvents() {
		if (delete_ids.size() > 0) {
			activity.showProgressDialog(false);
			for (String id : delete_ids) {
				deleteEvent(id);
			}
		}
	}
	
	private void deleteEvent(final String event_id) {
		final UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"action_fav_del", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gg55e-D5CB6E8-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_DELETE_MY_FAVOR_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, event_id);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.DELETE_MY_FAVOR_EVENT, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}
			
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
					error = "É¾³ýÊ§°Ü:" + e.getMessage();
				}
				boolean delete = false;
				if (error != null)
					activity.toastShort(error);
				else {
					delete = true;
					session.getFavor_event_list().remove(event_id);
				}
				afterDelete(event_id, delete);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				activity.toastShort(ServerConstants.NET_ERROR_TIP);
				afterDelete(event_id, false);
			}
		});
	}
	
	private void afterDelete(String event_id, boolean delete) {
		delete_ids.remove(event_id);
		if (delete) {
			for (Iterator<EventEntity> it = arrayList.iterator(); it.hasNext();) {
				EventEntity event = it.next();
				if (event_id.equals(event.id))
					it.remove();
			}
		}
		if (delete_ids.isEmpty()) {
			activity.finishDelete();
			activity.dimissProgressDialog();
		}
	}
}
