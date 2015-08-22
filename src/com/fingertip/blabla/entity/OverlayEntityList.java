package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fingertip.blabla.entity.ImgEntityList.ImgEntity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.ServerConstants.URL;

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
			overlayEntity.ptime = Tools.getTimeStr(Tools.strToDate(jsonObject.getString("publictime"), null).getTime()); 
		} catch (Exception e) { }
		try { overlayEntity.status = jsonObject.getString("statusof"); } catch (Exception e) { }
		/** 发布人信息 **/
		try { overlayEntity.userEntity = UserEntity.parseJson(jsonObject.getJSONObject("userinfor")); } catch (Exception e) { }
		/** 图片 **/
		try { overlayEntity.arrayList_img.addAll(ImgEntityList.parseJSONArray(jsonObject.getJSONArray("picof"))); } catch (Exception e) { }
		
		
		return overlayEntity;
	}//end parseJSON
	
	public static ArrayList<OverlayEntity> fromEventList(List<EventEntity> list) {
		ArrayList<OverlayEntity> arrayList = new ArrayList<OverlayEntity>();
		if (!Validator.isEmptyList(list)) {
			for (int i = 0; i < list.size(); i++) {
				arrayList.add(fromEvent(list.get(i)));
			}
		}
		return arrayList;
	}
	
	public static OverlayEntity fromEvent(EventEntity event) {
		OverlayEntity overlayEntity = new OverlayEntity();
		
		overlayEntity.actionid = event.id;
		overlayEntity.lng = event.poslong;
		overlayEntity.lat = event.poslat;
		overlayEntity.type = OverlayType.getOverlayType(event.kindof);
		overlayEntity.expiretime = event.getTimeToStr();
		overlayEntity.appraiseCount = event.likedcount;
		overlayEntity.replyCount = event.replycount;
		overlayEntity.viewCount = event.viewcount;
		overlayEntity.title = event.title;
		overlayEntity.detail = event.content;
		overlayEntity.addr = event.address;
		overlayEntity.ptime = Tools.getTimeStr(event.send_time);
		overlayEntity.status = event.statusof;
		overlayEntity.userEntity = event.sender;
		
		List<String> pics_small = event.pics_small, pics_big = event.pics_big;
		if (!Validator.isEmptyList(pics_small) && !Validator.isEmptyList(pics_big)) {
			for (int i = 0; i < pics_small.size(); i++) {
				ImgEntity img = new ImgEntity();
				img.small = pics_small.get(i);
				img.big = pics_big.get(i);
				overlayEntity.arrayList_img.add(img);
			}
		}
		return overlayEntity;
	}
	
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
		
		public String getShareContent() {
			StringBuilder buffer = new StringBuilder();
			buffer.append("图丁用户（").append(userEntity.nick_name)
				.append("）邀请您参加活动。").append(addr);
			return buffer.toString();
		}
		
		public String getShareUrl() {
			return URL.SHARE_EVENT_URL + actionid;
		}
		
	}//end OverlayEntity
}
