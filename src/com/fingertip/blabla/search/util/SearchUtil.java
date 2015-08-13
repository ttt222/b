package com.fingertip.blabla.search.util;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.fingertip.blabla.common.ServerConstants;
import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;
import com.fingertip.blabla.common.ServerConstants.PARAM_VALUES;
import com.fingertip.blabla.common.ServerConstants.URL;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.entity.OverlayType;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class SearchUtil {

	public static String KINDOF = OverlayType.ALL.getType();
	
	public enum Type {
		nearest, newest, hotest
	}
	
	public static void searchEvents(Type type, Location location, int page, final EventListCallback callback) {
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
			data.put(PARAM_KEYS.POSLONG, "113.952339");
			data.put(PARAM_KEYS.POSLAT, "22.553019");
//			data.put(PARAM_KEYS.KINDOF, "社交/聚会");
//			data.put(PARAM_KEYS.POSLONG, location == null ? "" : location.getLongitude());
//			data.put(PARAM_KEYS.POSLAT, location == null ? "" : location.getLatitude());
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
					Log.e("searchEvents", json.toString());
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
	
	public static interface EventListCallback {
		public void succeed(List<EventEntity> list);

		public void fail(String error);
	}
}
