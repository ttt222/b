package com.fingertip.blabla.my.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.entity.ContactEntity;

public class AdapterContact extends BaseAdapter {
	
	private Context context;
	private View empty_view;
	private List<ContactEntity> arrayList  = new ArrayList<ContactEntity>();
	
	public AdapterContact(Context context, View empty_view){
		this.context = context;
		this.empty_view = empty_view;
	}

	public void addAllList(List<ContactEntity> list) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_my_contact, parent, false);
			viewHoler = new ViewHoler();
			viewHoler.contact_name_txt = (TextView)convertView.findViewById(R.id.contact_name_txt);
			viewHoler.contact_add_sm = (TextView)convertView.findViewById(R.id.contact_add_sm);
			viewHoler.contact_add_watch = (LinearLayout)convertView.findViewById(R.id.contact_add_watch);
			convertView.setTag(viewHoler);
		}else {
			viewHoler = (ViewHoler)convertView.getTag();
		}
		ContactEntity contact = (ContactEntity)getItem(position);
		viewHoler.contact_name_txt.setText(contact.name);
		if (contact.status == ContactEntity.REGISTERED) {
			viewHoler.contact_add_sm.setVisibility(View.GONE);
			viewHoler.contact_add_watch.setVisibility(View.VISIBLE);
		} else if (contact.status == ContactEntity.WATCHED) {
			viewHoler.contact_add_watch.setVisibility(View.GONE);
			viewHoler.contact_add_sm.setText(context.getString(R.string.my_contact_watched));
			viewHoler.contact_add_sm.setVisibility(View.VISIBLE);
		} else if (contact.status == ContactEntity.NOT_REGISTERED) {
			viewHoler.contact_add_watch.setVisibility(View.GONE);
			viewHoler.contact_add_sm.setText(context.getString(R.string.my_contact_sm));
			viewHoler.contact_add_sm.setVisibility(View.VISIBLE);
		} 
		return convertView;
	}

	class ViewHoler{
		TextView contact_name_txt;
		LinearLayout contact_add_watch;
		TextView contact_add_sm;
	}
}
