package com.fingertip.blabla.info;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PublishPicAdapter extends BaseAdapter implements OnClickListener, OnItemClickListener {

	private PublishInfoActivity activity;
	private ArrayList<String> pics = new ArrayList<String>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private int max_size = PublishInfoActivity.MAX_PIC_SIZE;
	private static String ADD_STR = "add";
	
	public PublishPicAdapter(PublishInfoActivity context, ArrayList<String> pics) {
		this.activity = context;
		this.pics = pics;
		this.pics.add(ADD_STR);
		this.options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_pic_empty)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	}
	
	public void addPics(List<String> pics) {
		if (!Validator.isEmptyList(pics)) {
			this.pics.remove(this.pics.size() - 1);
			this.pics.addAll(pics);
			if (this.pics.size() < max_size)
				this.pics.add(ADD_STR);
			notifyDataSetChanged();
		}
	}
	
	public void deletePic(int position) {
		int size = pics.size();
		String last_path = pics.get(size - 1);
		pics.remove(position);
		if (size == max_size && !ADD_STR.equals(last_path))
			pics.add(ADD_STR);
		notifyDataSetChanged();
	}
	
	public ArrayList<String> getPics() {
		ArrayList<String> preview_pics = new ArrayList<String>();
		for (int i = 0; i < pics.size(); i++) {
			if (i != pics.size() -1 || !ADD_STR.equals(pics.get(i)))
				preview_pics.add(pics.get(i));
		}
		return preview_pics;
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
		String path = getItem(position);
		if (position == pics.size() - 1 && ADD_STR.equals(path)) {
			imageLoader.displayImage("drawable://" + R.drawable.icon_add_big, viewHolder.img, options);
			viewHolder.check_img.setVisibility(View.GONE);
		} else {
			imageLoader.displayImage("file://" + path, viewHolder.img, options);
			imageLoader.displayImage("drawable://" + R.drawable.icon_delete, viewHolder.check_img, options);
			viewHolder.check_img.setVisibility(View.VISIBLE);
			viewHolder.check_img.setTag(position);
			viewHolder.check_img.setOnClickListener(this);
		}
		return convertView;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pic_select_img) {
			try {
				int position = Integer.parseInt(v.getTag().toString());
				deletePic(position);
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String path = getItem(position);
		//Ìí¼ÓÍ¼Æ¬
		if (position == pics.size() - 1 && ADD_STR.equals(path))
			activity.addImg();
		//Ô¤ÀÀ
		else if (pics.size() > 1)
			Tools.previewPics(activity, getPics(), position);
	}
	
	class ViewHolder {
		ImageView img;
		ImageView check_img;
	}
}
