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
		/** ��������Ϣ **/
		try { overlayEntity.userEntity = UserEntity.parseJson(jsonObject.getJSONObject("userinfor")); } catch (Exception e) { }
		/** ͼƬ **/
		try { overlayEntity.arrayList_img.addAll(ImgEntityList.parseJSONArray(jsonObject.getJSONArray("picof"))); } catch (Exception e) { }
		
		
		return overlayEntity;
	}//end parseJSON
	
	public static class OverlayEntity implements Serializable{
		/**  **/
		private static final long serialVersionUID = 1L;
		
		/** ����id **/
		public String actionid;
		public double lat;
		public double lng;
		public OverlayType type;
		/** ����ʱ�� **/
		public String expiretime;
		/** ���� **/
		public String title;
		/** ���飨���ݣ� **/
		public String detail;
		/** ������ **/
		public int appraiseCount;
		/** �ظ��� **/
		public int replyCount;
		/**  **/
		public int viewCount;
		/** �����ַ **/
		public String addr;
		/** ����ʱ��**/
		public String ptime;
		/** ����״̬ **/
		public String status;
		
		public UserEntity userEntity;
		
		/** ��ǰ��ʾ���Ƿ�Ϊͼ�� **/
		public boolean isIcon = true;
		
		/** ͼƬ�б� **/
		private ArrayList<ImgEntity> arrayList_img = new ArrayList<ImgEntity>();
		/** �����б� **/
		private ArrayList<CommentEntity> arrayList_comment = new ArrayList<CommentEntity>();
		
		public ArrayList<ImgEntity> getImgList(){
			return arrayList_img;
		}
		
		public ArrayList<CommentEntity> getCommentList(){
			return arrayList_comment;
		}
		
	}//end OverlayEntity
}
