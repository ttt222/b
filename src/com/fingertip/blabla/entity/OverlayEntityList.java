package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.entity.CommentEntityList.CommentEntity;
import com.fingertip.blabla.entity.ImgEntityList.ImgEntity;

public class OverlayEntityList{
	
	public static ArrayList<OverlayEntity> parseJSONArray(JSONArray jsonArray){
		ArrayList<OverlayEntity> arrayList = new ArrayList<OverlayEntity>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try { arrayList.add(parseJSON(jsonArray.getJSONObject(i))); } catch (Exception e) { }
		}
		return arrayList;
	}
	
	public static OverlayEntity parseJSON(JSONObject jsonObject){
		OverlayEntity overlayEntity = new OverlayEntity();
		
		try { overlayEntity.actionid = jsonObject.getString("actionid"); } catch (Exception e) { }
		try { overlayEntity.lng = jsonObject.getDouble("poslong"); } catch (Exception e) { }
		try { overlayEntity.lat = jsonObject.getDouble("poslat"); } catch (Exception e) { }
		try { 
			overlayEntity.type = OverlayType.getOverlayType(jsonObject.getString("kindof")); 
			if(overlayEntity.type == null){
				overlayEntity.type = OverlayType.ALL;
			}
		} catch (Exception e) {
			e.printStackTrace();
			overlayEntity.type = OverlayType.ALL;
		}
		try { overlayEntity.expiretime = jsonObject.getString("timeto"); } catch (Exception e) { }
		try { overlayEntity.appraiseCount = jsonObject.getInt("likedcount"); } catch (Exception e) { }
		try { overlayEntity.replyCount = jsonObject.getInt("replycount"); } catch (Exception e) { }
		try { overlayEntity.viewCount = jsonObject.getInt("viewcount"); } catch (Exception e) { }
		try { overlayEntity.title = jsonObject.getString("titleof"); } catch (Exception e) { }
		try { overlayEntity.detail = jsonObject.getString("content"); } catch (Exception e) { }
		try { overlayEntity.addr = jsonObject.getString("address"); } catch (Exception e) { }
		try { 
			overlayEntity.ptime = Tools.getTimeStr(Tools.StrToDate(jsonObject.getString("publictime"), null).getTime()); 
		} catch (Exception e) { }
		try { overlayEntity.status = jsonObject.getString("statusof"); } catch (Exception e) { }
		/** 发布人信息 **/
		try { overlayEntity.userEntity = UserEntity.parseJson(jsonObject.getJSONObject("userinfor")); } catch (Exception e) { }
		/** 图片 **/
		try { overlayEntity.arrayList_img.addAll(ImgEntityList.parseJSONArray(jsonObject.getJSONArray("picof"))); } catch (Exception e) { }
		
		
		return overlayEntity;
	}//end parseJSON
	
	public static class OverlayEntity implements Serializable{
		/**  **/
		private static final long serialVersionUID = 1L;
		
		/** 内容id **/
		public String actionid;
		public double lat;
		public double lng;
		public OverlayType type;
		/** 过期时间 **/
		public String expiretime;
		/** 标题 **/
		public String title;
		/** 详情（内容） **/
		public String detail;
		/** 点赞数 **/
		public int appraiseCount;
		/** 回复数 **/
		public int replyCount;
		/**  **/
		public int viewCount;
		/** 具体地址 **/
		public String addr;
		/** 发布时间**/
		public String ptime;
		/** 发布状态 **/
		public String status;
		
		public UserEntity userEntity;
		
		/** 当前显示的是否为图标 **/
		public boolean isIcon = true;
		
		/** 图片列表 **/
		private ArrayList<ImgEntity> arrayList_img = new ArrayList<ImgEntity>();
		/** 评论列表 **/
		private ArrayList<CommentEntity> arrayList_comment = new ArrayList<CommentEntity>();
		
		public ArrayList<ImgEntity> getImgList(){
			return arrayList_img;
		}
		
		public ArrayList<CommentEntity> getCommentList(){
			return arrayList_comment;
		}
		
	}//end OverlayEntity
}
