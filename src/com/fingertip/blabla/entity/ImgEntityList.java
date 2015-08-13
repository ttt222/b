package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ͼƬ
 * @author Administrator
 *
 */
public class ImgEntityList{
	
	public static ArrayList<ImgEntity> parseJSONArray(JSONArray jsonArray){
		ArrayList<ImgEntity> arrayList = new ArrayList<ImgEntity>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try { arrayList.add(parseJSON(jsonArray.getJSONObject(i))); } catch (Exception e) { }
		}
		return arrayList;
	}
	
	public static ImgEntity parseJSON(JSONObject jsonObject){
		ImgEntity imgEntity = new ImgEntity();
		
		try { imgEntity.small = jsonObject.getString("s"); } catch (Exception e) { }
		try { imgEntity.big = jsonObject.getString("b"); } catch (Exception e) { }
		
		return imgEntity;
	}
	
	public static class ImgEntity implements Serializable{
		/**  **/
		private static final long serialVersionUID = 1L;

		public String small;
		public String big;
	}
}
