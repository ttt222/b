package com.fingertip.blabla.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ÏûÏ¢
 * @author Administrator
 *
 */
public class ContactEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static int REGISTERED = 1, NOT_REGISTERED = 2, WATCHED = 3;
	
	public String id;
	public String name;
	public List<String> phone_numbers = new ArrayList<String>();
	public int status = NOT_REGISTERED;
}
