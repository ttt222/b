package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class CommentEntityList {

	public static ArrayList<CommentEntity> parseJSONArray(JSONArray jsonArray){
		ArrayList<CommentEntity> arrayList = new ArrayList<CommentEntity>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try { arrayList.add(parseJSON(jsonArray.getJSONObject(i))); } catch (Exception e) { }
		}
		return arrayList;
	}
	
	public static CommentEntity parseJSON(JSONObject jsonObject){
		CommentEntity commentEntity = new CommentEntity();
		
		try { commentEntity.id = jsonObject.getString("replyid"); } catch (Exception e) { }
		try { commentEntity.commend = jsonObject.getString("content"); } catch (Exception e) { }
		try { commentEntity.time = jsonObject.getString("stime"); } catch (Exception e) { }		
		/** 评论人信息 **/
		try { commentEntity.userEntity = UserEntity.parseJson(jsonObject.getJSONObject("userinfor")); } catch (Exception e) { }
		
		return commentEntity;
	}//end parseJSON
	
	/**
	 * 评论
	 * @author Administrator
	 *
	 */
	public static class CommentEntity implements Serializable{

		/**  **/
		private static final long serialVersionUID = 1L;
		
		/** 评论人信息 **/
		public UserEntity userEntity;
		/** 评论id **/
		public String id;
		/** 评论内容 **/
		public String commend;
		/** 评论时间 **/
		public String time;
		/** 点赞数 **/
		public int appraise;
		
		/** 评论回复列表 **/
		private ArrayList<ResponseEntity> arrayList_response = new ArrayList<ResponseEntity>();	
		public ArrayList<ResponseEntity> getResponseList(){
			return arrayList_response;
		}
	}

}
