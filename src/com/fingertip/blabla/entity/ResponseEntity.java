package com.fingertip.blabla.entity;

import java.io.Serializable;

/**
 * 评论回复
 * @author Administrator
 *
 */
public class ResponseEntity implements Serializable{

	/**  **/
	private static final long serialVersionUID = 1L;
	
	/** 回复名字/昵称 **/
	public String name;
	/**  **/
//	private int headRes;
	/** 回复内容 **/
	public String content;
	/** 被回复人名字/昵称 **/
	public String parentName;
	
}
