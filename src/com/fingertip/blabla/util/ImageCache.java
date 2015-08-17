package com.fingertip.blabla.util;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

public class ImageCache {

	private static String IMG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator 
			+ Globals.PATH_CACH + File.separator + "img" + File.separator;
	private static String IMG_FORMAT = ".png";
	private static String IMG_HD_FORMAT = ".hd.png";
	private static String IMG_TMP_FORMAT = ".tmp";
	
	private static ImageCache imageCache;
	
	private ImageCache() {
	}
	
	public static ImageCache getInstance() {
		if (imageCache == null)
			imageCache = new ImageCache();
		return imageCache;
	}
	
	public String getUserImgPath(String user_id) {
		return getUserImgPath(user_id, true, false);
	}

	public String getUserImgPath(String user_id, boolean small) {
		return getUserImgPath(user_id, small, false);
	}

	public String getUserImgPath(String user_id, boolean small, boolean tmp) {
		String img_path = IMG_PATH + user_id;
		if (tmp)
			img_path += IMG_TMP_FORMAT;
		return img_path += (small ? IMG_FORMAT : IMG_HD_FORMAT);
	}
	
	public boolean saveUserImg(Bitmap img, String user_id, boolean small, boolean tmp) {
		if (checkImgDir())
			return FileUtil.saveImage(img, getUserImgPath(user_id, small, tmp));
		return false;
	}
	
	public boolean saveTmpImg(String user_id, boolean small) {
		File tmp_img = new File(getUserImgPath(user_id, small, true));
		if (!tmp_img.exists())
			return false;
		else {
			File img = new File(getUserImgPath(user_id, small));
			return tmp_img.renameTo(img);
		}
	}
	
	private boolean checkImgDir() {
		File dir = new File(IMG_PATH);
		if (!dir.exists())
			return dir.mkdirs();
		return true;
	}
	
	public boolean setUserHeadImg(String user_id, ImageView head_img) {
		String img_path = getUserImgPath(user_id);
		if (new File(img_path).exists()) {
			head_img.setImageBitmap(BitmapFactory.decodeFile(img_path));
			return true;
		}
		return false;
	}
	
	public void loadUserHeadImg(final String down_url, final String user_id, final SharedPreferenceUtil sp,
			final BitmapUtils bitmapUtils, final ImageView image, final ImageView hidden_image) {
		String last_url = sp.getStringValue(user_id, SharedPreferenceUtil.HEADIMAGE);
		boolean download_img = false;
		//本地缓存
		boolean has_cache = false;
		String local_img_path = getUserImgPath(user_id);
		File dir = new File(local_img_path);
    	if (dir.exists())
    		has_cache = true;
		//无缓存
		if (Validator.isEmptyString(last_url)) {
			//有头像，下载
			if (!Validator.isEmptyString(down_url))
				download_img = true;
			else 
				return;
		//有缓存
		} else {
			//有头像
			if (!Validator.isEmptyString(down_url))
				download_img = !last_url.equals(down_url) || !has_cache;
		}
		if (download_img) {
			//bitmapUtils与RoundImageView不兼容，临时解决办法
			bitmapUtils.display(hidden_image, down_url, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
					image.setImageBitmap(bitmap);
					if (saveUserImg(bitmap, user_id, true, false))
						sp.setStringValue(user_id, SharedPreferenceUtil.HEADIMAGE, down_url);
				}
				
				@Override
				public void onLoadFailed(View container, String uri, Drawable drawable) {
					Log.e("ImageCache", "下载头像失败");
				}
			});
		} else if (has_cache)
			image.setImageBitmap(BitmapFactory.decodeFile(local_img_path));
	}

	public void loadUrlImg(final String url, final ImageView image, final BitmapUtils bitmapUtils) {
		final String file_name = IMG_PATH + Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
		File cache_fie = new File(file_name);
		if (cache_fie.exists()) 
			image.setImageBitmap(BitmapFactory.decodeFile(file_name));
		else
			bitmapUtils.display(image, url, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
					image.setImageBitmap(bitmap);
					FileUtil.saveImage(bitmap, file_name);
				}
				
				@Override
				public void onLoadFailed(View container, String uri, Drawable drawable) {
					Log.e("ImageCache", "下载图片失败");
				}
			});
	}
}
