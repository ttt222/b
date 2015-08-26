package com.fingertip.blabla.util.http;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UploadUtil {

	public static void uplodaImg(List<String> paths, final List<UploadImgEntity> entitys, final UploadCallback callback) {
		uplodaImg0(paths, 0, entitys, callback);
	}
	
	private static void uplodaImg0(final List<String> paths, final int index, final List<UploadImgEntity> entitys, final UploadCallback callback) {
		uplodaImg(paths.get(index), new EntityCallback<UploadImgEntity>() {
			@Override
			public void succeed(UploadImgEntity entity) {
				entitys.add(entity);
				if (index + 1 < paths.size())
					uplodaImg0(paths, index + 1, entitys, callback);
				else
					callback.succeed();
			}
			
			@Override
			public void fail(String error) {
				callback.fail(index, error);
			}
		});
	}
	
	public static void uplodaImg(String path, final EntityCallback<UploadImgEntity> callback) {
		uplodaImg(path, ServerConstants.SMALL_PIC_KB, ServerConstants.BIG_PIC_KB, callback);
	}
	
	public static void uplodaImg(String path, int small_kb, int big_kb, final EntityCallback<UploadImgEntity> callback) {
//		"fc":"upload_file", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "filefor":"Í·Ïñ"
//		sfile ËõÂÔÍ¼, sfull Ô­Í¼
		UploadImgEntity entity = ImageCache.compressImageForUpload(path);
		if (!checkUploadEntity(entity))
			callback.fail("Ñ¹ËõÍ¼Æ¬Ê§°Ü");
		else {
			UserSession session = UserSession.getInstance();
			JSONObject data = new JSONObject();
			try {
				data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_UPLOAD_FILE);
				data.put(PARAM_KEYS.UPLOAD_FILEFOR, PARAM_VALUES.UPLOAD_EVENT);
				data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
				data.put(PARAM_KEYS.USERID, session.getId());
			} catch (JSONException e) {
			}
			RequestParams params = new RequestParams();
			params.addQueryStringParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
			params.addBodyParameter(PARAM_KEYS.UPLOAD_SFULL, entity.big_file);
			params.addBodyParameter(PARAM_KEYS.UPLOAD_SFILE, entity.small_file);

			HttpUtils http = Tools.getHttpUtils();
			http.send(HttpRequest.HttpMethod.POST, ServerConstants.URL.UPLOAD_IMG, params,
			    new RequestCallBack<String>() {

			        @Override
			        public void onSuccess(ResponseInfo<String> responseInfo) {
			        	String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
						String error = null;
						String small_url = null;
						String big_url = null;
						try {
							JSONObject json = new JSONObject(result);
							small_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFILE);
							big_url = json.getString(PARAM_KEYS.UPLOAD_RESULT_URLFULL);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (small_url == null || big_url == null)
							error = "ÉÏ´«Í¼Æ¬Ê§°Ü";
						if (error == null) {
							UploadImgEntity upload = new UploadImgEntity();
							upload.small_url = small_url;
							upload.big_url = big_url;
							callback.succeed(upload);
						} else
							callback.fail(error);
			        }

			        @Override
			        public void onFailure(HttpException error, String msg) {
			        	callback.fail(ServerConstants.NET_ERROR_TIP);
			        }
			});
		}
	}
	
	private static boolean checkUploadEntity(UploadImgEntity entity) {
		return entity.small_file != null && entity.small_file.exists() && entity.big_file != null && entity.big_file.exists();
	}
	
	public interface UploadCallback {
		public void succeed();

		public void fail(int index, String error);
	}
}
