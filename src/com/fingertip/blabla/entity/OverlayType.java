package com.fingertip.blabla.entity;


/**
 * ����
 * @author Administrator
 *
 */
public enum OverlayType{
//	ALL,//ȫ��
//	ACTIVITY,//�
//	FRIEND,//����
//	BUY,//����
//	EVENT,//�¼�
//	TO,//Ҷ��
//	RECOMMEND,//�Ƽ�
//	
//	special,//�Ż�/�ؼ�
//	perform,//����/����
//	sociality,//�罻/�ۻ�
//	sports,//�˶�/����
//	study;//ѧϰ/����

	
	ALL("ȫ��"),//
	SPECIAL("�Ż�/�ؼ�"),//
	PERFORM("����/����"),//
	SOCIALITY("�罻/�ۻ�"),//
	SPORTS("�˶�/����"),//
	STUDY("ѧϰ/ɳ��"),
	OTHER("����");//
	
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
