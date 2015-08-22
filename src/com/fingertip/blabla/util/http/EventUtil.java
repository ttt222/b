package com.fingertip.blabla.util.http;

import java.util.List;

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
	 * ��ѯ�
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
//		 "poslong":113.3124, "poslat":23.1428, "kindof":"�Ż�/�ؼ�"}
		try {
			data.put(PARAM_KEYS.FC, fc);
			data.put(PARAM_KEYS.USERID, session.getId() == null ? "" : session.getId());
			data.put(PARAM_KEYS.LOGINID, session.getLogin_id() == null ? "" : session.getLogin_id());
//			test "poslat":"22.553019","poslong":"113.952339"
//			data.put(PARAM_KEYS.POSLONG, "113.952339");
//			data.put(PARAM_KEYS.POSLAT, "22.553019");
//			data.put(PARAM_KEYS.KINDOF, "�罻/�ۻ�");
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
					error = "��ѯʧ��:" + e.getMessage();
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
	 * ��ȡ�����
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
					error = "��ѯʧ��:" + e.getMessage();
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
					error = "��ѯʧ��:" + e.getMessage();
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
}
