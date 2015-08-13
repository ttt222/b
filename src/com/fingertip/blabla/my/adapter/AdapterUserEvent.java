package com.fingertip.blabla.my.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.entity.EventEntity;

public class AdapterUserEvent extends BaseAdapter implements OnItemClickListener {
	
	private Context context;
	private View empty_view;
	private List<EventEntity> arrayList  = new ArrayList<EventEntity>();
	
	public AdapterUserEvent(Context context, View empty_view){
		this.context = context;
		this.empty_view = empty_view;
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user_event, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.event_type_img = (ImageView)convertView.findViewById(R.id.event_type_img);
			viewHoler.event_date_txt = (TextView)convertView.findViewById(R.id.event_date_txt);
			viewHoler.event_title_txt = (TextView)convertView.findViewById(R.id.event_title_txt);
			viewHoler.event_status_txt = (TextView)convertView.findViewById(R.id.event_status_txt);
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		EventEntity event = (EventEntity)getItem(position);
		try {
			viewHoler.event_type_img.setImageResource(event.getKindImgInt());
		} catch (Exception e) {
		}
		viewHoler.event_title_txt.setText(event.title);
		viewHoler.event_date_txt.setText(event.getSendTimeStr());
		if (EventEntity.STATUS_IN.equals(event.statusof))
			viewHoler.event_status_txt.setTextColor(context.getResources().getColor(R.color.green_event));
		else if (EventEntity.STATUS_OVER.equals(event.statusof))
			viewHoler.event_status_txt.setTextColor(context.getResources().getColor(R.color.red_event));
		viewHoler.event_status_txt.setText(event.getStatusStr());
		return convertView;
	}

	class ViewHoler{
		ImageView event_type_img;
		TextView event_date_txt;
		TextView event_title_txt;
		TextView event_status_txt;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tools.openEvent(context, arrayList.get(position).id);
	}
}
