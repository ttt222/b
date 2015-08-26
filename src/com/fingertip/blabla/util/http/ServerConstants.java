package com.fingertip.blabla.util.http;

public class ServerConstants {

	public static String SEX_MALE = "m", SEX_FEMALE = "f", SEX_UNKNOW = "x",
			SEX_MALE_S = "男", SEX_FEMALE_S = "女", SEX_UNKNOW_S = "未知";
	
	//从服务器获取消息的时间间隔
	public static int GET_MESSAGE_GAP = 30;

	//http请求的超时时间
	public static int HTTP_TIME_OUT = 15 * 1000;
	public static int SMALL_PIC_KB = 200;
	public static int BIG_PIC_KB = 500;
	
	public static String NET_ERROR_TIP = "网络异常，请确认打开网络或稍后再试";
	
	public static class URL {
		public static String BASE_URL = "http://tutuapp.aliapp.com";
		
		public static String SHARE_EVENT_URL = "http://tutuapp.aliapp.com/tzadminnn/index.php?m=home&aid=";
		
		/**
		 * user
		 */
		public static String UPLOAD_IMG = BASE_URL + "/_upload/upload_file_oss.php";
		public static String LOGIN = BASE_URL + "/_user/user_login.php";
		public static String REG_SENDMSG = BASE_URL + "/_user/user_reg_sendmsg.php";
		public static String RESET_PASSWORD = BASE_URL + "/_user/user_pass_reset.php";
		public static String GET_USER_INFO = BASE_URL + "/_user/get_user_infor.php";
		public static String EDIT_USER_INFO = BASE_URL + "/_user/user_infor_edit.php";
		public static String EDIT_USER_PASS = BASE_URL + "/_user/user_pass_edit.php";
		public static String CHECK_PHONE_REG = BASE_URL + "/_user/phonelist_reg_check.php";

		public static String USER_BARCODE_BASE = BASE_URL + "/_user/info/";
		
		/**
		 * event
		 */
		public static String GET_MY_EVENT = BASE_URL + "/_action/action_ofmy.php";
		public static String GET_MY_FAVOR_EVENT = BASE_URL + "/_action/action_fav_ofmy.php";
		public static String DELETE_MY_EVENT = BASE_URL + "/_action/action_delete.php";
		public static String DELETE_MY_FAVOR_EVENT = BASE_URL + "/_action/action_fav_del.php";
		public static String GET_USER_EVENT = BASE_URL + "/_action/action_list_byuser.php";
		public static String GET_NEAREST_EVENT = BASE_URL + "/_action/get_action_bypos.php";
		public static String GET_NEWEST_EVENT = BASE_URL + "/_action/get_action_bynew.php";
		public static String GET_HOTEST_EVENT = BASE_URL + "/_action/get_action_byhot.php";

		public static String GET_EVENT_INFO = BASE_URL + "/_action/get_action_byactionid.php";
		public static String GET_EVENT_COMMENTS = BASE_URL + "/_action/get_action_reply_list.php";

		public static String PUBLISH_EVENT = BASE_URL + "/_action/action_post.php";
		
		/**
		 * msg
		 */
		public static String GET_MY_MSG = BASE_URL + "/_chat/get_msg_ofmy.php";
		public static String SEND_MSG = BASE_URL + "/_chat/send_msg.php";
		
		/**
		 * watch
		 */
		public static String GET_MY_WATCH = BASE_URL + "/_friend/get_friend_list.php";
		public static String EDIT_WATCH = BASE_URL + "/_friend/friend_link.php";

		/**
		 * system
		 */
		public static String FEEDBACK = BASE_URL + "/_system/msg_feedback.php";
		public static String REPORT = BASE_URL + "/_system/msg_report.php";
	}
	
	public static class PARAM_KEYS {
		public static String FC = "fc";
		public static String COMMAND = "command";
		public static String USERID = "userid";
		public static String PASSWORD = "pass";
		public static String POSLONG = "poslong";
		public static String POSLAT = "poslat";
		public static String LOGINID = "loginid";
		public static String PHONE = "phone";
		public static String RESULT_STATUS = "ok";
		public static String RESULT_ERROR = "error";
		public static String INFORUID = "inforuid";
		public static String INFOR = "infor";
		public static String LIST = "list";
		public static String PAGENO = "pageno";
		public static String BYUSER = "byuser";
		public static String PHONELIST = "phonelist";
		public static String CHECKED = "checked";
		public static String FEEDBACK = "feedback";
		public static String PARAMOF = "paramof";
		
		/**
		 * user info
		 */
		public static String USER_NICK_NAME = "nick";
		public static String USER_MARK = "aboutme";
		public static String USER_SEX = "sex";
		public static String USER_HEAD = "head";
		public static String USER_HEAD_BIG = "headbig";
		public static String USER_PLACE = "address";
		public static String USER_OLD_PASS = "oldpass";
		public static String USER_NEW_PASS = "newpass";
		public static String USER_FANS = "fans";
		public static String USER_FANS_G = "g";
		public static String USER_FANS_F = "f";
		
		/**
		 * upload file
		 */
		public static String UPLOAD_FILEFOR = "filefor";
		public static String UPLOAD_SFULL = "sfull";
		public static String UPLOAD_SFILE = "sfile";
		public static String UPLOAD_RESULT_URLFILE = "urlfile";
		public static String UPLOAD_RESULT_URLFULL = "urlfull";
		
		/**
		 * event
		 */
//		"publictime":"2015-07-07 11:03:04","userinfor":
//		{"aboutme":"vvv不","userid":"13641411876","sex":"f","head":"http:\/\.jpg","nick":"嘎嘎嘎","address":"安徽省安庆市"},
//		"poslat":"22.553186","titleof":"饭否风格","userid":"13641411876","picof":"","replycount":"0","likedcount":"0","content":"红河谷",
//		"statusof":"over","actionid":"577b032-603E420-6h","poslong":"113.952568","address":"","timeto":"0000-00-00 00:00:00","kindof":"优惠\/特价"}
		public static String USERINFOR = "userinfor";
		public static String PUBLICTIME = "publictime";
		public static String TITLEOF = "titleof";
		public static String PICOF = "picof";
		public static String REPLYCOUNT = "replycount";
		public static String LIKEDCOUNT = "likedcount";
		public static String VIEWCOUNT = "viewcount";
		public static String CONTENT = "content";
		public static String STATUSOF = "statusof";
		public static String ACTIONID = "actionid";
		public static String ADDRESS = "address";
		public static String TIMETO = "timeto";
		public static String KINDOF = "kindof";
		public static String B = "b";
		public static String S = "s";
		public static String ACTIONINFOR = "actioninfor";
		
		/**
		 * comment
		 */
		public static String REPLYID = "replyid";
		public static String STIME = "stime";
		public static String REPLYLIST = "replylist";
		
		/**
		 * msg
		 */
//		{"chatid":"56u9223-599CF648CC56B4EA","userid":"18979528420",
//		"says":"eyJraW5kIjoidGV4dCIsImNvbnRlbnQiOiI1b2lSNVkrcTZJTzk1WkcxNVpHMTVMcUciLCAianVtcHBhcmFtIjoiNTZnaTMxcy0zNkJDMDE2LTRnIn0=",
//		"msgtype":"评论","sendtime":"2015-06-30 09:22:06"}
//		sys内容
//		{"kind":"text","content":"5oiR5Y+q6IO95ZG15ZG15LqG", "quote":"5ZCJ5bGL5Ye656ef5LiA5a6k5LiA5Y6F", "jumpparam":"actionid:56gi31s-36BC016-4g"}
		public static String LASTREAD = "lastread";
		public static String MSGLIST = "msglist";
		public static String CHATID = "chatid";
		public static String SAYS = "says";
		public static String MSGTYPE = "msgtype";
		public static String SENDTIME = "sendtime";
		public static String KIND = "kind";
		public static String QUOTE = "quote";
		public static String JUMPPARAM = "jumpparam";
		public static String TOUSERID = "touserid";

		/**
		 * watch
		 */
		public static String LINK = "link";
		public static String FRIENDUID = "frienduid";
	}

	public static class PARAM_VALUES {
		/**
		 * user
		 */
		public static String FC_LOGIN = "user_login";
		public static String FC_REG_SENDMSG = "user_reg_sendmsg";
		public static String FC_RESET_PASSWORD = "user_pass_reset";
		public static String FC_GET_USER_INFO = "get_user_infor";
		public static String FC_UPLOAD_FILE = "upload_file";
		public static String FC_EDIT_USER_INFO = "user_infor_edit";
		public static String FC_EDIT_USER_PASS = "user_pass_edit";
		public static String FC_CHECK_PHONE_REG = "phonelist_reg_check";
		
		/**
		 * event
		 */
		public static String FC_GET_MY_EVENT = "action_ofmy";
		public static String FC_GET_MY_FAVOR_EVENT = "action_fav_ofmy";
		public static String FC_DELETE_MY_EVENT = "action_delete";
		public static String FC_DELETE_MY_FAVOR_EVENT = "action_fav_del";
		public static String FC_GET_USER_EVENT = "action_list_byuser";
		public static String FC_GET_NEAREST_EVENT = "get_action_bypos";
		public static String FC_GET_NEWEST_EVENT = "get_action_bynew";
		public static String FC_GET_HOTEST_EVENT = "get_action_byhot";

		public static String FC_GET_EVENT_INFO = "get_action_byactionid";
		public static String FC_GET_EVENT_COMMENTS = "get_action_reply_list";

		public static String FC_PUBLISH_EVENT = "action_post";
		
		/**
		 * msg
		 */
		public static String FC_GET_MY_MSG = "get_msg_ofmy";
		public static String FC_SEND_MSG = "send_msg";
		
		public static String SAYS_TEXT = "text";
		public static String MSG_TYPE_COMMENT = "新的活动评论";
		public static String MSG_TYPE_CHAT = "新的对话";

		/**
		 * watch
		 */
		public static String FC_GET_MY_WATCH = "get_friend_list";
		public static String FC_EDIT_WATCH = "friend_link";

		public static String LINK_WATCH = "guanzhu";
		public static String LINK_HAOYOU = "haoyou";
		public static String LINK_HEI = "hei";
		public static String LINK_SHANCHU = "shanchu";
		
		/**
		 * system
		 */
		public static String FC_FEEDBACK = "msg_feedback";
		public static String FC_REPORT = "msg_report";
		
		public static String RESULT_SUCCEED = "y";
		public static String RESULT_FAIL = "n";
		
		public static String INFOR_MINI = "mini";
		public static String INFOR_STAND = "stand";
		public static String INFOR_PLUS = "plus";
		public static String UPLOAD_HEAD = "头像";
		public static String UPLOAD_EVENT = "活动";
	}
}
