package com.fingertip.blabla;

public class Cmd {
	private Cmd(){}
	
	/** 活动查询 **/
	public static final String ACTION_BYPOS = "/_action/get_action_bypos.php";
	/** 根据id查询活动 **/
	public static final String ACTION_BYPOSID = "/_action/get_action_byactionid.php";
	
	/** 图片上传(阿里云) **/
	public static final String ACTION_UPLLOADFILE = "/_upload/upload_file_oss.php";
	/** 发布活动 **/
	public static final String ACTION_PUBLISH = "/_action/action_post.php";
	/** 活动收藏 **/
	public static final String ACTION_COLLECTION = "/_action/action_fav_add.php";
	
	/** 活动评论列表 **/
	public static final String ACTION_RECOMMENDLIST = "/_action/get_action_reply_list.php";
	
	/** 活动评论 **/
	public static final String ACTION_RECOMMEND = "/_action/action_reply.php";
	/** 评论回复 **/
	public static final String ACTION_RECOMMENDREPLY = "/_action/action_reply_reply.php";
	
	/** 消息发送 **/
	public static final String ACTION_SENDMSG = "/_chat/send_msg.php";
	
	
	public static final String OK_STRING = "ok";
	
}
