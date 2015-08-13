package com.fingertip.blabla.my.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.common.ImageCache;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.common.ServerConstants.URL;
import com.fingertip.blabla.common.ServerConstants;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.WatchEntity;
import com.fingertip.blabla.my.MyWatchListActivity;
import com.fingertip.blabla.my.widget.Deleteable;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class AdapterMyWatch extends BaseAdapter implements OnItemClickListener, Deleteable {
	
	private MyWatchListActivity activity;
	private View empty_view;
	private ImageView hidden_img;
	private List<WatchEntity> arrayList  = new ArrayList<WatchEntity>();
	
	private BitmapUtils bitmapUtils;
	private ImageCache imageCache;
	private SharedPreferenceUtil sp;
	
	private boolean delete;
	private List<String> delete_ids = new ArrayList<String>();
	
	public AdapterMyWatch(MyWatchListActivity activity, View empty_view, ImageView hidden_img){
		this.activity = activity;
		this.empty_view = empty_view;
		this.hidden_img = hidden_img;
		
		sp = new SharedPreferenceUtil(activity);
		bitmapUtils = new BitmapUtils(activity);
		imageCache = ImageCache.getInstance();
		this.delete = false;
	}

	public void addAllList(List<WatchEntity> list){
		if(list != null){
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void appendList(List<WatchEntity> list){
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_my_watch, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.my_watcher_head = (ImageView)convertView.findViewById(R.id.my_watcher_head);
			viewHoler.my_watcher_name = (TextView)convertView.findViewById(R.id.my_watcher_name);
			viewHoler.my_watcher_mark = (TextView)convertView.findViewById(R.id.my_watcher_mark);
			viewHoler.my_watcher_place = (TextView)convertView.findViewById(R.id.my_watcher_place);
			viewHoler.my_watcher_up_count = (TextView)convertView.findViewById(R.id.my_watcher_up_count);
			viewHoler.v_delete_check = (LinearLayout)convertView.findViewById(R.id.v_delete_check);
			viewHoler.iv_delete_check = (ImageView)convertView.findViewById(R.id.iv_delete_check);
			
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		WatchEntity watch = (WatchEntity)getItem(position);
		try {
			imageCache.loadUserHeadImg(watch.user.head_img_url, watch.user.id, sp, bitmapUtils, viewHoler.my_watcher_head, hidden_img);
		} catch (Exception e) {
		}
		viewHoler.my_watcher_name.setText(watch.user.nick_name);
		viewHoler.my_watcher_mark.setText(watch.user.mark);
		viewHoler.my_watcher_place.setText(watch.user.place);
		viewHoler.my_watcher_up_count.setText(watch.up_count + "");
		viewHoler.id = watch.user.id;
		if (delete) {
			viewHoler.v_delete_check.setVisibility(View.VISIBLE);
			if (viewHoler.checked)
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			else
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
		} else {
			viewHoler.v_delete_check.setVisibility(View.GONE);
			viewHoler.checked = false;
		}
		return convertView;
	}

	class ViewHoler{
		ImageView my_watcher_head;
		TextView my_watcher_name, my_watcher_mark, my_watcher_place, my_watcher_up_count;
		LinearLayout v_delete_check;
		ImageView iv_delete_check;
		
		String id;
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
		if (delete_ids.size() > 0) {
			activity.showProgressDialog(false);
			for (String id : delete_ids) {
				deleteWatch(id);
			}
		}
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
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		ViewHoler viewHoler = (ViewHoler) view.getTag();
		if(delete) {
			if (viewHoler.checked) {
				delete_ids.remove(viewHoler.id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_unchecked));
			} else {
				delete_ids.add(viewHoler.id);
				viewHoler.iv_delete_check.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_checked));
			}
			viewHoler.checked = !viewHoler.checked;
			activity.setSelectedCount(delete_ids.size());
		} else {
			//跳转到活动页
			Tools.openUser(activity, viewHoler.id);
		}
	}
	
	private void deleteWatch(final String user_id) {
		final UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_WATCH);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.FRIENDUID, user_id);
			data.put(PARAM_KEYS.LINK, PARAM_VALUES.LINK_SHANCHU);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.EDIT_WATCH, params, new RequestCallBack<String>() {
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
					error = "删除失败:" + e.getMessage();
				}
				boolean delete = false;
				if (error != null)
					activity.toastShort(error);
				else {
					delete = true;
					session.getWatcher_list().remove(user_id);
				}
				afterDelete(user_id, delete);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				activity.toastShort(ServerConstants.NET_ERROR_TIP);
				afterDelete(user_id, false);
			}
		});
	}
	
	private void afterDelete(String user_id, boolean delete) {
		delete_ids.remove(user_id);
		if (delete) {
			for (Iterator<WatchEntity> it = arrayList.iterator(); it.hasNext();) {
				WatchEntity watch = it.next();
				if (user_id.equals(watch.user.id))
					it.remove();
			}
		}
		if (delete_ids.isEmpty()) {
			activity.finishDelete();
			activity.dimissProgressDialog();
		}
	}
}
