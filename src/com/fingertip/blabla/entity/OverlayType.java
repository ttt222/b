package com.fingertip.blabla.entity;


/**
 * 分类
 * @author Administrator
 *
 */
public enum OverlayType{
//	ALL,//全部
//	ACTIVITY,//活动
//	FRIEND,//交友
//	BUY,//买卖
//	EVENT,//事件
//	TO,//叶酸
//	RECOMMEND,//推荐
//	
//	special,//优惠/特价
//	perform,//娱乐/表演
//	sociality,//社交/聚会
//	sports,//运动/户外
//	study;//学习/交流

	
	ALL("全部"),//
	SPECIAL("优惠/特价"),//
	PERFORM("娱乐/表演"),//
	SOCIALITY("社交/聚会"),//
	SPORTS("运动/户外"),//
	STUDY("学习/沙龙"),
	OTHER("其它");//
	
	private String type;
	public String getType(){
		return type;
	}
	
	private  OverlayType(String t) {
		this.type = t;
	}
	
	public static OverlayType getOverlayType(String t){
		for (OverlayType overlayType : values()) {
			if(overlayType.getType().equals(t)){
				return overlayType;
			}
		}
		return null;
	}//
}
