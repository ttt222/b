package com.fingertip.blabla.search;

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
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.lidroid.xutils.BitmapUtils;

public class AdapterSearch extends BaseAdapter implements OnItemClickListener {
	private Context context;
	
	private List<EventEntity> arrayList = new ArrayList<EventEntity>();
	
	private BitmapUtils bitmapUtils;
	private SharedPreferenceUtil sp;
	
	public AdapterSearch(Context context){
		this.context = context;
		sp = new SharedPreferenceUtil(context);
		bitmapUtils = new BitmapUtils(context);
	}
	
	public void addAllData(List<EventEntity> list){
		arrayList.clear();
		if(list != null)
			arrayList.addAll(list);
		notifyDataSetChanged();
	}

	public void appendAllData(List<EventEntity> list){
		if(!Validator.isEmptyList(list)) {
			arrayList.addAll(list);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.view_listitem_search, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.iv_head = (ImageView)convertView.findViewById(R.id.iv_head);
			viewHolder.iv_type = (ImageView)convertView.findViewById(R.id.iv_type);
			viewHolder.iv_topic = (ImageView)convertView.findViewById(R.id.image);
			viewHolder.hidden_img = (ImageView)convertView.findViewById(R.id.hidden_img);
			viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
			viewHolder.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
			viewHolder.tv_type = (TextView)convertView.findViewById(R.id.tv_type);
			viewHolder.tv_popularity = (TextView)convertView.findViewById(R.id.tv_popularity);
			viewHolder.tv_recommend = (TextView)convertView.findViewById(R.id.tv_recommend);
			viewHolder.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		final EventEntity event = (EventEntity)getItem(position);
		viewHolder.tv_name.setText(event.sender.nick_name);
	viewHolder.iv_head.setTag(event.sender.id);
		try {
			ImageCache.loadUserHeadImg(event.sender.head_img_url, event.sender.id, sp, bitmapUtils, viewHolder.iv_head, viewHolder.hidden_img);				
		} catch (Exception e) {
		}
		try {
			if (!Validator.isEmptyList(event.pics_small)) 
				ImageCache.loadUrlImg(event.pics_small.get(0), viewHolder.iv_topic, bitmapUtils);
			else
				viewHolder.iv_topic.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		viewHolder.tv_title.setText(event.title);
		viewHolder.iv_type.setImageDrawable(context.getResources().getDrawable(event.getKindImgInt()));
		viewHolder.tv_type.setText(event.kindof);
		//viewHolder.tv_popularity.setText("" + event.likedcount);
		viewHolder.tv_popularity.setText(""+event.viewcount);
		viewHolder.tv_recommend.setText("" + event.replycount);
		viewHolder.iv_head.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Tools.openUser(context, v.getTag().toString());
			}
		});
		viewHolder.tv_time.setText(Tools.getTimeStr(event.send_time));
		return convertView;
	}

	
	class ViewHolder {
		ImageView iv_head;
		ImageView iv_type;
		ImageView iv_topic;
		ImageView hidden_img;
		TextView tv_name;
		TextView tv_title;
		TextView tv_type;
		TextView tv_popularity;
		TextView tv_recommend;
		TextView tv_time;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Tools.openEvent(context, ((EventEntity)(parent.getAdapter().getItem(position))).id);
	}
}
