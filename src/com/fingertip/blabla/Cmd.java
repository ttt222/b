package com.fingertip.blabla;

public class Cmd {
	private Cmd(){}
	
	/** ���ѯ **/
	public static final String ACTION_BYPOS = "/_action/get_action_bypos.php";
	/** ����id��ѯ� **/
	public static final String ACTION_BYPOSID = "/_action/get_action_byactionid.php";
	
	/** ͼƬ�ϴ�(������) **/
	public static final String ACTION_UPLLOADFILE = "/_upload/upload_file_oss.php";
	/** ����� **/
	public static final String ACTION_PUBLISH = "/_action/action_post.php";
	/** ��ղ� **/
	public static final String ACTION_COLLECTION = "/_action/action_fav_add.php";
	
	/** ������б� **/
	public static final String ACTION_RECOMMENDLIST = "/_action/get_action_reply_list.php";
	
	/** ����� **/
	public static final String ACTION_RECOMMEND = "/_action/action_reply.php";
	/** ���ۻظ� **/
	public static final String ACTION_RECOMMENDREPLY = "/_action/action_reply_reply.php";
	
	/** ��Ϣ���� **/
	public static final String ACTION_SENDMSG = "/_chat/send_msg.php";
	
	
	public static final String OK_STRING = "ok";
	
}
