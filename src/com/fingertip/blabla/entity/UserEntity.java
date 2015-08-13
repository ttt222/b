package com.fingertip.blabla.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.fingertip.blabla.common.ServerConstants.PARAM_KEYS;

/**
 * 用户实体
 * @author Administrator
 *
 */
public class UserEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String id;
	public String nick_name;
	public String head_img_url;
	public String place;
	public String sex;
	public String mark;
	public int up_count;
	
//	{"userid": "1257053","nick": "Jim","sex": "m","head": "","address": "广州市体育东路1号","aboutme": "我就是我","headbig": "http://x/v922l-6313D63.jpg",
//    "fans": {"f": "3","h": "0","g": "3","x": "0"}}
	public static UserEntity parseJson(JSONObject json) throws JSONException {
		UserEntity user = new UserEntity();
		user.id = json.getString(PARAM_KEYS.USERID);
		user.nick_name = json.getString(PARAM_KEYS.USER_NICK_NAME);
//		user.head_img_url = new String(Base64.decode(json.getString(PARAM_KEYS.USER_HEAD), Base64.DEFAULT));
		user.head_img_url = json.getString(PARAM_KEYS.USER_HEAD);
		try {user.place = json.getString(PARAM_KEYS.USER_PLACE);} catch (Exception e) { }	
		user.sex = json.getString(PARAM_KEYS.USER_SEX);
		try { user.mark = json.getString(PARAM_KEYS.USER_MARK);} catch (Exception e) { }	
		try { user.up_count = json.getJSONObject(PARAM_KEYS.USER_FANS).getInt(PARAM_KEYS.USER_FANS_G);} catch (Exception e) { }	
		return user;
	}
	
}
