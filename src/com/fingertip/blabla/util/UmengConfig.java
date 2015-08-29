package com.fingertip.blabla.util;

public class UmengConfig {

	public static String VALUE_KEY = "__ct__";
	
	public static class PAGE {
		//主页
		public static String MAIN = "main_page";
		//消息中心
		public static String MSG_CENTER = "msg_center_page";
		//我的关注
		public static String MY_WATCH = "my_watch_page";
		//用户详情
		public static String USER_DETAIL = "user_detail_page";
		//活动详情
		public static String EVENT_DETAIL = "event_detail_page";
		//搜索活动
		public static String SEARCH_EVENT = "search_event_page";
		//最近
		public static String SEARCH_EVENT_NEAR = "search_event_near_page";
		//最热
		public static String SEARCH_EVENT_HOT = "search_event_hot_page";
		//最新
		public static String SEARCH_EVENT_NEW = "search_event_new_page";
	}
	
	public static class EVENT {
		//收藏活动
		public static String ADD_FAV = "add_fav";
		//关注用户
		public static String ADD_WATCHER = "add_watcher";
		//点击活动
		public static String CLICK_EVENT = "click_event";
		//点击用户
		public static String CLICK_USER = "click_user";
		//消息中心
		public static String MSG_CENTER = "msg_center";
		//发布评论
		public static String PUB_COMMENT = "pub_comment";
		//分享
		public static String SOCIAL_SHARE = "social_share";
	}
	
	public static class EVENT_ID {
		public static int ADD_FAV_ID = 1;
	}
}
