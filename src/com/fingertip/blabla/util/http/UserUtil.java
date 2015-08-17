package com.fingertip.blabla.util.http;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.entity.MessageEntity;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.entity.WatchEntity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UserUtil {

	/**
	 * 获取用户资料
	 * @param user_id
	 * @param callback
	 */
	public static void getUserInfo(String user_id, EntityCallback<UserEntity> callback) {
		getUserInfo(user_id, callback, true);
	}
	
	/**
	 * 获取用户资料
	 * @param user_id
	 * @param callback
	 * @param full
	 */
	public static void getUserInfo(String user_id, final EntityCallback<UserEntity> callback, boolean full) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_user_infor", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "inforuid":1257053, "infor":"mini"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_USER_INFO);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.INFORUID, user_id);
			if (full)
				data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_PLUS);
			else
				data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_MINI);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_USER_INFO, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				UserEntity user = null;
				try {
					JSONObject json = new JSONObject(result);
					Log.e("getUserInfo", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					if (error == null) 
						if (json.has(PARAM_KEYS.INFOR) && json.get(PARAM_KEYS.INFOR) != null 
							&& !"null".equals(json.get(PARAM_KEYS.INFOR).toString())) 
							user = UserEntity.parseJson(json.getJSONObject(PARAM_KEYS.INFOR));
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取用户资料失败:" + e.getMessage();
				}
				if (user != null)
					callback.succeed(user);
				else 
					error = "未找到该用户";
				if (error != null)
					callback.fail(error);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 添加、删除关注，屏蔽
	 * 
	 * @param user_id	用户id
	 * @param action	PARAM_VALUES.LINK_WATCH, LINK_HAOYOU, LINK_HEI, LINK_SHANCHU
	 * @param callback
	 */
	public static void editWatch(String user_id, String action, final DefaultCallback callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_EDIT_WATCH);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.FRIENDUID, user_id);
			data.put(PARAM_KEYS.LINK, action);
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
					Log.e("editWatch", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "操作失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 发送私聊消息
	 * @param to_user_id
	 * @param msg
	 * @param callback
	 */
	public static void sendMsg(String to_user_id, String msg, final DefaultCallback callback) {
//		{"fc":"send_msg", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//			 "touserid":"18979528420", "says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI2SXVONkl5cjU1cUU1YVNwNXJhdjVwaXY1b2lSNTVxRTU0aXgifQ=="}
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"friend_link", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "frienduid":"1644980", "link":"shanchu"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_SEND_MSG);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.TOUSERID, to_user_id);
			JSONObject says = new JSONObject();
			says.put(PARAM_KEYS.KIND, PARAM_VALUES.SAYS_TEXT);
			says.put(PARAM_KEYS.CONTENT, Base64.encodeToString(msg.getBytes(), Base64.DEFAULT));
			data.put(PARAM_KEYS.SAYS, Base64.encodeToString(says.toString().getBytes(), Base64.DEFAULT));
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.SEND_MSG, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				try {
					json = new JSONObject(result);
					Log.e("sendMsg", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "发送失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 获取用户关注列表
	 * @param callback
	 */
	public static void getUserWatchList(final EntityListCallback<WatchEntity> callback) {
		final UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_friend_list", "userid":1257053, "loginid":"t4etskerghskdryhgsdfklhs", "link":"guanzhu", "infor":"mini"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_WATCH);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.LINK, PARAM_VALUES.LINK_WATCH);
			data.put(PARAM_KEYS.INFOR, PARAM_VALUES.INFOR_STAND);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_WATCH, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<WatchEntity> list = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = WatchEntity.parseList(json);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取关注列表失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else {
					callback.succeed(list);
					session.setWatcher_list(list);
				}
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 加载用户关注列表到内存
	 */
	public static void loadWatchList() {
		UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			final Set<String> watch_set = session.getWatcher_list();
			getUserWatchList(new EntityListCallback<WatchEntity>() {
				@Override
				public void succeed(List<WatchEntity> list) {
					if (!Validator.isEmptyList(list)) {
						for (WatchEntity watchEntity : list)
							watch_set.add(watchEntity.user.id);
					}
					Log.e("loadWatchList", "succeed");
				}
				
				@Override
				public void fail(String error) {
					Log.e("loadWatchList", error);
				}
			});
		}
	}
	
	/**
	 * 获取用户关注的活动列表
	 * @param page
	 * @param callback
	 */
//	public static void getUserFavorEvents(int page, final EntityListCallback<EventEntity> callback) {
	public static void getUserFavorEvents(final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"action_fav_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_FAVOR_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
//			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_FAVOR_EVENT, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取活动列表失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(list);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	public static void loadFavorList() {
		UserSession session = UserSession.getInstance();
		if (session.isLogin()) {
			Set<String> favor_set = session.getFavor_event_list();
			loadFavorList0(favor_set);
//			loadFavorList0(1, favor_set);
		}
	}

	/**
	 * 循环加载全部关注的活动
	 * @param page
	 * @param favor_set
	 */
//	private static void loadFavorList0(final int page, final Set<String> favor_set) {
//		getUserFavorEvents(page, new EntityListCallback<EventEntity>() {
	private static void loadFavorList0(final Set<String> favor_set) {
		getUserFavorEvents(new EntityListCallback<EventEntity>() {
			@Override
			public void succeed(List<EventEntity> list) {
				favor_set.clear();
				if (!Validator.isEmptyList(list)) {
					for (EventEntity eventEntity : list)
						favor_set.add(eventEntity.id);
//					loadFavorList0((page + 1), favor_set);
				}
				Log.e("loadFavorList0", "succeed");
			}
			
			@Override
			public void fail(String error) {
				Log.e("loadFavorList0", error);
			}
		});
	}
	
	/**
	 * 获取用户的消息
	 * @param callback
	 */
	public static void loadUserMsg(final EntityListCallback<MessageEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_msg_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "lastread":"-"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_MSG);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.LASTREAD, "-1");
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_MSG, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<MessageEntity> list = new ArrayList<MessageEntity>();
				try {
					json = new JSONObject(result);
					Log.e("getMsg", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = MessageEntity.parseList(json);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取消息列表失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(list);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 获取自己发布的活动
	 * @param callback
	 */
	public static void getMyEvents(int page, final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"action_ofmy", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_MY_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_MY_EVENT, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = new ArrayList<EventEntity>();
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else 
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取活动列表失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(list);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
	
	/**
	 * 获取用户发布的活动
	 * @param user_id
	 * @param callback
	 */
	public static void getUserEvents(String user_id, final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"action_list_byuser", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "byuser":13641411876}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_USER_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.BYUSER, user_id);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_USER_EVENT, params, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = new ArrayList<EventEntity>();
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					e.printStackTrace();
					error = "获取活动列表失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(list);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}

	private static final int BLACK = 0xff000000;
	
	public static Bitmap createQRCode(String user_id, int widthAndHeight) throws WriterException {  
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        BitMatrix matrix = new MultiFormatWriter().encode(URL.USER_BARCODE_BASE + user_id, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        int[] pixels = new int[width * height];  
  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y)) {  
                    pixels[y * width + x] = BLACK;  
                }  
            }  
        }  
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;  
    }
	
	public static String parseBarcode(String result) {
		if (result.startsWith(URL.USER_BARCODE_BASE)) {
			result = result.substring(URL.USER_BARCODE_BASE.length()).trim();
			if (Validator.isMobilePhone(result))
				return result;
		}
		return null;
	}
}
