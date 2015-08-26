package com.fingertip.blabla.util.http;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.CommentEntity;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.entity.OverlayType;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.util.http.ServerConstants.URL;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class EventUtil {

	public static String KINDOF = OverlayType.ALL.getType();
	
	public enum Type {
		nearest, newest, hotest
	}
	
	/**
	 * 查询活动
	 * 
	 * @param type
	 * @param location
	 * @param page
	 * @param callback
	 */
	public static void searchEvents(Type type, String poslong, String poslat, int page, final EntityListCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		String url = null, fc = null;
		switch (type) {
		case nearest :
			url = URL.GET_NEAREST_EVENT;
			fc = PARAM_VALUES.FC_GET_NEAREST_EVENT;
			break;
		case newest :
			url = URL.GET_NEWEST_EVENT;
			fc = PARAM_VALUES.FC_GET_NEWEST_EVENT;
			break;
		case hotest :
			url = URL.GET_HOTEST_EVENT;
			fc = PARAM_VALUES.FC_GET_HOTEST_EVENT;
			break;
		}
		JSONObject data = new JSONObject();
//		{"fc":"get_action_bypos", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", 
//		 "poslong":113.3124, "poslat":23.1428, "kindof":"优惠/特价"}
		try {
			data.put(PARAM_KEYS.FC, fc);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
//			test "poslat":"22.553019","poslong":"113.952339"
//			data.put(PARAM_KEYS.POSLONG, "113.952339");
//			data.put(PARAM_KEYS.POSLAT, "22.553019");
//			data.put(PARAM_KEYS.KINDOF, "社交/聚会");
			data.put(PARAM_KEYS.POSLONG, poslong);
			data.put(PARAM_KEYS.POSLAT, poslat);
			data.put(PARAM_KEYS.KINDOF, KINDOF);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<EventEntity> list = null;
				try {
					json = new JSONObject(result);
//					Log.e("searchEvents", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = EventEntity.parseList(json);
				} catch (JSONException e) {
					error = "查询失败:" + e.getMessage();
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
	 * 获取活动详情
	 * @param id
	 * @param callback
	 */
	public static void getEventInfo(final String id, final EntityCallback<EventEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_action_byactionid", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs",  "actionid":"571f34g-AD1820C-4g"}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_INFO);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, id);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_EVENT_INFO, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				EventEntity event = null;
				try {
					json = new JSONObject(result);
//					Log.e("getEventInfo", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						event = EventEntity.parseJson(json.getJSONObject(PARAM_KEYS.ACTIONINFOR));
				} catch (Exception e) {
					error = "查询失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(event);
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}

	public static void getEventComments(final String id, int page, final EntityListCallback<CommentEntity> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
//		{"fc":"get_action_reply_list", "userid":18979528420, "loginid":"t4etskerghskdryhgsdfklhs", "actionid":"56gi31s-36BC016-4g", "pageno":1}
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_GET_EVENT_COMMENTS);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
			data.put(PARAM_KEYS.ACTIONID, id);
			data.put(PARAM_KEYS.PAGENO, page);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.GET_EVENT_COMMENTS, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				List<CommentEntity> list = null;
				try {
					json = new JSONObject(result);
//					Log.e("getEventComments", json.toString());
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						list = CommentEntity.parseList(json);
				} catch (Exception e) {
					error = "查询失败:" + e.getMessage();
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
	
	public static void publishEvent(String title, String content, String type, String address, String timeto, String latitude, String longitude, 
			List<UploadImgEntity> entitys, final EntityCallback<String> callback) {
		UserSession session = UserSession.getInstance();
		JSONObject data = new JSONObject();
		try {
			data.put(PARAM_KEYS.FC, PARAM_VALUES.FC_PUBLISH_EVENT);
			data.put(PARAM_KEYS.USERID, session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id());
			data.put(PARAM_KEYS.TITLEOF, title);
			data.put(PARAM_KEYS.CONTENT, content);
			data.put(PARAM_KEYS.KINDOF, type);
			data.put(PARAM_KEYS.TIMETO, timeto);
			data.put(PARAM_KEYS.ADDRESS, address);
			data.put(PARAM_KEYS.POSLAT, latitude);
			data.put(PARAM_KEYS.POSLONG, longitude);
			JSONArray pics = new JSONArray();
			for (UploadImgEntity entity : entitys) {
				JSONObject json = new JSONObject();
				json.put(PARAM_KEYS.S, entity.small_url);
				json.put(PARAM_KEYS.B, entity.big_url);
				pics.put(json);
			}
			data.put(PARAM_KEYS.PICOF, pics);
		} catch (JSONException e) {
		}
		RequestParams params = new RequestParams();
		params.addBodyParameter(PARAM_KEYS.COMMAND, Base64.encodeToString(data.toString().getBytes(), Base64.DEFAULT));
		HttpUtils http = Tools.getHttpUtils();
		http.send(HttpRequest.HttpMethod.POST, URL.PUBLISH_EVENT, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = new String(Base64.decode(responseInfo.result, Base64.DEFAULT));
				String error = null;
				JSONObject json = null;
				String event_id = null;
				try {
					json = new JSONObject(result);
					if (PARAM_VALUES.RESULT_FAIL.equals(json.getString(PARAM_KEYS.RESULT_STATUS)))
						error = json.getString(PARAM_KEYS.RESULT_ERROR);
					else
						event_id = json.getString(PARAM_KEYS.ACTIONID);
				} catch (Exception e) {
					error = "发布失败:" + e.getMessage();
				}
				if (error != null)
					callback.fail(error);
				else
					callback.succeed(event_id);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				callback.fail(ServerConstants.NET_ERROR_TIP);
			}
		});
	}
}
