package com.fingertip.blabla.util;

import java.io.ByteArrayOutputStream;
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
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.UploadImgEntity;
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
	
	private static String IMG_UPLOAD_SMALL = "small.png";
	private static String IMG_UPLOAD_BIG = "big.png";
	
	public static String getUserImgPath(String user_id) {
		return getUserImgPath(user_id, true, false);
	}

	public static String getUserImgPath(String user_id, boolean small) {
		return getUserImgPath(user_id, small, false);
	}

	public static String getUserImgPath(String user_id, boolean small, boolean tmp) {
		String img_path = IMG_PATH + user_id;
		if (tmp)
			img_path += IMG_TMP_FORMAT;
		return img_path += (small ? IMG_FORMAT : IMG_HD_FORMAT);
	}
	
	public static boolean saveUserImg(Bitmap img, String user_id, boolean small, boolean tmp) {
		if (checkImgDir())
			return FileUtil.saveImage(img, getUserImgPath(user_id, small, tmp));
		return false;
	}
	
	public static boolean saveTmpImg(String user_id, boolean small) {
		File tmp_img = new File(getUserImgPath(user_id, small, true));
		if (!tmp_img.exists())
			return false;
		else {
			File img = new File(getUserImgPath(user_id, small));
			return tmp_img.renameTo(img);
		}
	}
	
	private static boolean checkImgDir() {
		File dir = new File(IMG_PATH);
		if (!dir.exists())
			return dir.mkdirs();
		return true;
	}
	
	/**
	 * 加载本地用户头像
	 * 
	 * @param user_id
	 * @param head_img
	 * @return
	 */
	public static boolean setUserHeadImg(String user_id, ImageView head_img) {
		String img_path = getUserImgPath(user_id);
		if (new File(img_path).exists()) {
			head_img.setImageBitmap(BitmapFactory.decodeFile(img_path));
			return true;
		}
		return false;
	}
	
	/**
	 * 加载用户头像，优先从缓存加载
	 * 
	 * @param down_url
	 * @param user_id
	 * @param sp
	 * @param bitmapUtils
	 * @param image
	 * @param hidden_image
	 */
	public static void loadUserHeadImg(final String down_url, final String user_id, final SharedPreferenceUtil sp,
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
				public void onLoading(View container, String uri,
						BitmapDisplayConfig config, long total, long current) {
					super.onLoading(container, uri, config, total, current);
				}
				
				@Override
				public void onPreLoad(View container, String uri,
						BitmapDisplayConfig config) {
					super.onPreLoad(container, uri, config);
				}
				
				@Override
				public void onLoadStarted(View container, String uri,
						BitmapDisplayConfig config) {
					super.onLoadStarted(container, uri, config);
				}
				
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

	/**
	 * 加载网络图片，不缓存
	 * 
	 * @param url
	 * @param image
	 * @param bitmapUtils
	 */
	public static void loadUrlImg(final String url, final ImageView image, final BitmapUtils bitmapUtils) {
		loadUrlImg(url, image, bitmapUtils, false);
	}

	/**
	 * 加载网络图片
	 * 
	 * @param url
	 * @param image
	 * @param bitmapUtils
	 * @param cache	是否缓存到本地
	 */
	public static void loadUrlImg(final String url, final ImageView image, final BitmapUtils bitmapUtils, final boolean cache) {
		if (Validator.isEmptyString(url))
			return;
		boolean has_cache = false;
		final String file_name = IMG_PATH + Base64.encodeToString(url.getBytes(), Base64.NO_WRAP);
		if (cache) {
			File cache_fie = new File(file_name);
			if (cache_fie.exists()) 
				has_cache = true;
		}
		if (has_cache)
			image.setImageBitmap(BitmapFactory.decodeFile(file_name));
		else
			bitmapUtils.display(image, url, new BitmapLoadCallBack<View>() {
				@Override
				public void onLoadCompleted(View container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
					image.setImageBitmap(bitmap);
					if (cache)
						FileUtil.saveImage(bitmap, file_name);
				}
				
				@Override
				public void onLoadFailed(View container, String uri, Drawable drawable) {
					Log.e("ImageCache", "下载图片失败");
				}
			});
	}
	
	public static boolean checkImgSize(String path, int kb) {
		Bitmap image = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray().length / 1024 <= kb;
	}
	
	public static UploadImgEntity compressImageForUpload(String path) {
		File file = new File(path);
		long kb = file.length() / 1024;
		boolean save_big = false;
		UploadImgEntity entity = new UploadImgEntity();
		if (kb <= ServerConstants.SMALL_PIC_KB) {
			entity.small_file = file;
			entity.big_file = file;
			return entity;
		}
		if (kb <= ServerConstants.BIG_PIC_KB) {
			entity.big_file = file;
			save_big = true;
		}
		if (!save_big)
			entity.big_file = new File(compressImageForUpload(path, true, kb));
		entity.small_file = new File(compressImageForUpload(path, false, kb));
		return entity;
	}
	
	private static String compressImageForUpload(String image_path, boolean big, long kb) {
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        int size = (int)kb / (big ? ServerConstants.BIG_PIC_KB : ServerConstants.SMALL_PIC_KB);
        int i = 1;
        while (i < size)
        	i = i<<1;
        options.inSampleSize = i;
        Bitmap image = BitmapFactory.decodeFile(image_path, options);
		String path = getUploadImgPath(big);
		FileUtil.saveImage(image, path);
		if (big)
			Log.e("saveImage  big", (new File(path).length() / 1024) + " " + path);
		else
			Log.e("saveImage  small", (new File(path).length() / 1024) + " " + path);
		return path;
	}

//	private static String compressImageForUpload(Bitmap image, boolean big, long kb) {
//		int option = (int)(100 *  (big ? ServerConstants.BIG_PIC_KB : ServerConstants.SMALL_PIC_KB) / kb);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		image.compress(Bitmap.CompressFormat.JPEG, option, baos);
//		String path = getUploadImgPath(big);
//		FileUtil.saveImage(BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()), null, null), path);
//		if (big)
//			Log.e("saveImage  big", (baos.toByteArray().length / 1024) + " " + path);
//		else
//			Log.e("saveImage  small", (baos.toByteArray().length / 1024) + " " + path);
//		return path;
//	}
	
	private static String getUploadImgPath(boolean big) {
		File dir = new File(IMG_PATH + Globals.UPLOAD_CACH);
		if (!dir.exists())
			dir.mkdirs();
		return IMG_PATH + Globals.UPLOAD_CACH + File.separator + (big ? IMG_UPLOAD_BIG : IMG_UPLOAD_SMALL);
	}
}
