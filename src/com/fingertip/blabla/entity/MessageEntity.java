package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.ServerConstants.PARAM_KEYS;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 消息
 * @author Administrator
 *
 */
public class MessageEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private static SimpleDateFormat SERVER_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	
//	{"chatid":"56u9223-599CF648CC56B4EA","userid":"18979528420",
//	"says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI1b2lSNVkrcTZJTzk1WkcxNVpHMTVMcUciLCAianVtcHBhcmFtIjoiNTZnaTMxcy0zNkJDMDE2LTRnIn0=",
//	"msgtype":"评论","sendtime":"2015-06-30 09:22:06"}
//	msgtype为消息类型，有"对话", "评论", "回复", "邀请", "关注"
//	sys内容
//	{"kind":"text","content":"5oiR5Y+q6IO95ZG15ZG15LqG", "quote":"5ZCJ5bGL5Ye656ef5LiA5a6k5LiA5Y6F", "jumpparam":"actionid:56gi31s-36BC016-4g"}
	
	public String id, receiver_id;
	public UserEntity sender;
	public String type;
	public long send_time;
	public SaysEntity says;
	public MessageDbEntity msg_db;
	
	public static List<MessageEntity> parseList(JSONObject json) throws JSONException {
		List<MessageEntity> list = new ArrayList<MessageEntity>();
		try {
			JSONArray array = json.getJSONArray(PARAM_KEYS.LIST);
			for (int i = 0; i < array.length(); i++) {
				try {
					list.addAll(parseUserList(array.getJSONObject(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private static List<MessageEntity> parseUserList(JSONObject json) throws JSONException {
		UserSession session = UserSession.getInstance();
		List<MessageEntity> list = new ArrayList<MessageEntity>();
		UserEntity user = UserEntity.parseJson(json);
		JSONArray msgs = json.getJSONArray(PARAM_KEYS.MSGLIST);
//		{"aboutme":"vvv不vv:x_*呵呵古古怪怪刚刚好还好还好刚刚好","userid":"13641411876","sex":"f","head":"http:\/\/x.jpg","nick":"嘎嘎嘎",
//		"msglist":[{"userid":"13641411876","says":"==", "chatid":"57ai47n-67133922063A2A2B","sendtime":"2015-07-10 18:47:46","msgtype":"评论"}],"address":"安徽省安庆市"}
		
//		{"chatid":"56u9223-599CF648CC56B4EA","userid":"18979528420",
//		"says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI1b2lSNVkrcTZJTzk1WkcxNVpHMTVMcUciLCAianVtcHBhcmFtIjoiNTZnaTMxcy0zNkJDMDE2LTRnIn0=",
//		"msgtype":"评论","sendtime":"2015-06-30 09:22:06"}
		for (int i = 0; i < msgs.length(); i++) {
			JSONObject j = msgs.getJSONObject(i);
			MessageEntity msg = new MessageEntity();
			msg.id = j.getString(PARAM_KEYS.CHATID);
			msg.type = j.getString(PARAM_KEYS.MSGTYPE);
			try {
				msg.send_time = SERVER_SDF.parse(j.getString(PARAM_KEYS.SENDTIME)).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			msg.sender = user;
			String says = new String(Base64.decode(j.getString(PARAM_KEYS.SAYS), Base64.DEFAULT));
			msg.says = SaysEntity.parseJson(new JSONObject(says));
			
			MessageDbEntity msg_db = new MessageDbEntity();
			msg_db.receiver_id = session.getId();
			msg_db.id = msg.id;
			msg_db.type = msg.type;
			msg_db.send_time = msg.send_time;
			msg_db.sender_id = user.id;
			msg_db.sender_name = user.nick_name;
			msg_db.sender_head = user.head_img_url;
			msg_db.says = j.getString(PARAM_KEYS.SAYS);
			msg.msg_db = msg_db;
			list.add(msg);
		}
		return list;
	}
	
	public static List<MessageEntity> parseDbEntity(List<MessageDbEntity> list) {
		if (list != null && !list.isEmpty()) {
			List<MessageEntity> result = new ArrayList<MessageEntity>();
			for (MessageDbEntity mde : list) {
				MessageEntity msg = new MessageEntity();
				msg.id = mde.id;
				msg.type = mde.type;
				msg.send_time = mde.send_time;
				UserEntity sender = new UserEntity();
				sender.id = mde.sender_id;
				sender.nick_name = mde.sender_name;
				sender.head_img_url = mde.sender_head;
				msg.sender = sender;
				String says = new String(Base64.decode(mde.says, Base64.DEFAULT));
				try {
					msg.says = SaysEntity.parseJson(new JSONObject(says));
				} catch (JSONException e) {
					continue;
				}
				result.add(msg);
			}
			return result;
		}
		return null;
	}
	

	public static class SaysEntity implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
//	kind值为text表示本消息为纯文本
//	content为编码后的消息内容（本例中为文本内容）
//	quote为当前回复的活动的标题（可选）
//	jumpparam为跳转参数，冒号前表示跳转类型，冒号后表示参数ID（可选）
//	{"kind":"text","content":"5oiR5Y+q6IO95ZG15ZG15LqG", "quote":"5ZCJ5bGL5Ye656ef5LiA5a6k5LiA5Y6F", "jumpparam":"actionid:56gi31s-36BC016-4g"}
		public String kind;
		public String content;
		public String quote;
		public String jumpparam;
		public String event_id;
		
		public static SaysEntity parseJson(JSONObject json) throws JSONException {
			SaysEntity says = new SaysEntity();
			says.kind = json.getString(PARAM_KEYS.KIND);
			says.content = new String(Base64.decode(json.getString(PARAM_KEYS.CONTENT), Base64.DEFAULT));
			String quote = json.has(PARAM_KEYS.QUOTE) ? json.getString(PARAM_KEYS.QUOTE) : null;
			if (!Validator.isEmptyString(quote))
				says.quote = new String(Base64.decode(quote, Base64.DEFAULT));
			String jumpparam = json.has(PARAM_KEYS.JUMPPARAM) ? json.getString(PARAM_KEYS.JUMPPARAM) : null;
			if (!Validator.isEmptyString(jumpparam)) {
				says.jumpparam = json.getString(PARAM_KEYS.JUMPPARAM);
				if (says.jumpparam.startsWith(PARAM_KEYS.ACTIONID) && says.jumpparam.contains(":"))
					says.event_id = says.jumpparam.split(":")[1];
			}
			return says;
		}
		
	}
	
	@Table(name = "message")
	public static class MessageDbEntity implements Serializable {
		
		private static final long serialVersionUID = 1L;

		@Column(column = "msg_id")
	    public String id;
		
		@Column(column = "receiver_id")
		public String receiver_id;

		@Column(column = "says")
		public String says;
		
		@Column(column = "type")
		public String type;
		
		@Column(column = "send_time")
		public long send_time;
		
		@Column(column = "sender_id")
		public String sender_id;
		
		@Column(column = "sender_name")
		public String sender_name;
		
		@Column(column = "sender_head")
		public String sender_head;

	}
}
