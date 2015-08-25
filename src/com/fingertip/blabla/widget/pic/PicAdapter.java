package com.fingertip.blabla.widget.pic;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.widget.SelectPicActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PicAdapter extends BaseAdapter implements OnItemClickListener {

	private SelectPicActivity activity;
	private ArrayList<String> pics = new ArrayList<String>();
	private ArrayList<String> select_pics = new ArrayList<String>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public PicAdapter(SelectPicActivity context, ArrayList<String> pics) {
		this.activity = context;
		this.pics = pics;
		this.options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_pic_empty)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	}
	
	public void setPics(List<String> pics) {
		this.pics.clear();
		this.pics.addAll(pics);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public String getItem(int position) {
		return pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(activity).inflate(R.layout.view_griditem_pic, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.img = (ImageView)convertView.findViewById(R.id.pic_img);
			viewHolder.check_img = (ImageView)convertView.findViewById(R.id.pic_select_img);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final String path = getItem(position);
		imageLoader.displayImage("file://" + path, viewHolder.img, options);
		if (select_pics.contains(path)) {
			viewHolder.img.setColorFilter(activity.getResources().getColor(R.color.selected_img));
			viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_selected));
		} else {
			viewHolder.img.setColorFilter(null);
			viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_unselected));
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		String path = getItem(position);
		boolean selected = false;
		if (select_pics.contains(path)) {
			select_pics.remove(path);
			viewHolder.img.setColorFilter(null);
			viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_unselected));
			selected = true;
			activity.selected(false);
		} else if (activity.canSelect()) {
			select_pics.add(path);
			viewHolder.img.setColorFilter(activity.getResources().getColor(R.color.selected_img));
			viewHolder.check_img.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_pic_selected));
			selected = true;
			activity.selected(true);
		}
		if (selected)
			viewHolder.checked = !viewHolder.checked;
	}
	
	public ArrayList<String> getSelect_pics() {
		return select_pics;
	}

	class ViewHolder {
		ImageView img;
		ImageView check_img;
		boolean checked;
	}
}
