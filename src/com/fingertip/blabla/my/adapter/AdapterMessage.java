package com.fingertip.blabla.my.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.MessageEntity;
import com.fingertip.blabla.my.MessageCenterActivity;
import com.fingertip.blabla.my.widget.Deleteable;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class AdapterMessage extends BaseAdapter implements OnItemClickListener, Deleteable {
	
	private MessageCenterActivity activity;
	private View empty_view;
	private List<MessageEntity> arrayList = new ArrayList<MessageEntity>();
	
	private BitmapUtils bitmapUtils;
	private ImageCache imageCache;
	private SharedPreferenceUtil sp;
	
	private boolean delete;
	private List<String> delete_ids = new ArrayList<String>();
	
	public AdapterMessage(MessageCenterActivity activity, View empty_view){
		this.activity = activity;
		this.empty_view = empty_view;
		sp = new SharedPreferenceUtil(activity);
		bitmapUtils = new BitmapUtils(activity);
		imageCache = ImageCache.getInstance();
		this.delete = false;
	}

	public void addAllList(List<MessageEntity> list){
		if(list != null){
			arrayList.clear();
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}
	
	public void appendList(List<MessageEntity> list){
		if(list != null){
			arrayList.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void insertList(List<MessageEntity> list){
		if(list != null){
			arrayList.addAll(0, list);
		}
		notifyDataSetChanged();
	}
	
	public void deleteMsg(int position) {
		arrayList.remove(position);
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
			convertView = LayoutInflater.from(activity).inflate(R.layout.list_item_message, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.head_img = (ImageView)convertView.findViewById(R.id.msg_sender_head);
			viewHoler.hidden_img = (ImageView)convertView.findViewById(R.id.hidden_img);
			viewHoler.sender_name_text = (TextView)convertView.findViewById(R.id.msg_sender_name);
			viewHoler.msg_title_text = (TextView)convertView.findViewById(R.id.msg_title);
			viewHoler.msg_type_text = (TextView)convertView.findViewById(R.id.msg_type);
			viewHoler.msg_content_text = (TextView)convertView.findViewById(R.id.msg_content);
			viewHoler.msg_send_time_text = (TextView)convertView.findViewById(R.id.msg_send_time);
			viewHoler.v_delete_check = (LinearLayout)convertView.findViewById(R.id.v_delete_check);
			viewHoler.iv_delete_check = (ImageView)convertView.findViewById(R.id.iv_delete_check);
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		
		MessageEntity msg = (MessageEntity)getItem(position);
		try {
			imageCache.loadUserHeadImg(msg.sender.head_img_url, msg.sender.id, sp, bitmapUtils, viewHoler.head_img, viewHoler.hidden_img);
		} catch (Exception e) {
		}
		viewHoler.sender_name_text.setText(msg.sender.nick_name);
		if (Validator.isEmptyString(msg.says.quote))
			viewHoler.msg_title_text.setVisibility(View.GONE);
		else
			viewHoler.msg_title_text.setText(msg.says.quote);
		viewHoler.msg_type_text.setText(msg.getTypeStr());
		viewHoler.msg_content_text.setText(msg.says.content);
		viewHoler.id = msg.id;
		viewHoler.msg_send_time_text.setText(Tools.getTimeStr(msg.send_time));
		
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
		ImageView head_img;
		ImageView hidden_img;
		TextView sender_name_text;
		TextView msg_title_text;
		TextView msg_type_text;
		TextView msg_content_text;
		TextView msg_send_time_text;
		LinearLayout v_delete_check;
		ImageView iv_delete_check;
		
		String id;
		boolean checked = false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
			MessageEntity msg = arrayList.get(position);
			if (msg.says.event_id != null)
				Tools.openEvent(activity, msg.says.event_id);
			else
				Tools.openUser(activity, msg.sender.id);
		}
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
			UserSession session = UserSession.getInstance();
			Tools.deleteMessages(activity, session.getId(), delete_ids);
			for (Iterator<MessageEntity> it = arrayList.iterator(); it.hasNext();) {
				MessageEntity msg = it.next();
				if (delete_ids.contains(msg.id))
					it.remove();
			}
			notifyDataSetChanged();
			activity.dimissProgressDialog();
			activity.finishDelete();
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
}
