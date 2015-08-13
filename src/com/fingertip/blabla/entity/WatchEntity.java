package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;

/**
 * 消息
 * @author Administrator
 *
 */
public class WatchEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String id;
	public int up_count;
	
	public UserEntity user;
	
//	{"list":[{"aboutme":"我就是我","userid":"1644980","sex":"m","head":"","nick":"jim","address":"广州市体育东路1号"}],"ok":"y","fc":"get_friend_list"}
	public static List<WatchEntity> parseList(JSONObject json) throws JSONException {
		List<WatchEntity> list = new ArrayList<WatchEntity>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				WatchEntity watch = new WatchEntity();
				try {
					watch.user = UserEntity.parseJson(array.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				list.add(watch);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
