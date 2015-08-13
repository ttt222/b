package com.fingertip.blabla.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fingertip.blabla.entity.WatchEntity;

public class UserSession {
	
	private String id, login_id, nick_name, head_url, sex, place, mark;
	private boolean login, load_info;
	private Set<String> watcher_list, favor_event_list;

	private static UserSession session;
	
	
	private UserSession() {
	}
	
	public static UserSession getInstance() {
		if (session == null)
			session = new UserSession();
		return session;
	}

	public static void logout() {
		if (session != null) {
			session.getFavor_event_list().clear();
			session.getWatcher_list().clear();
			session = null;
		}
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public String getHead_url() {
		return head_url;
	}

	public void setHead_url(String head_url) {
		this.head_url = head_url;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public boolean isLoad_info() {
		return load_info;
	}

	public void setLoad_info(boolean load_info) {
		this.load_info = load_info;
	}

	public Set<String> getWatcher_list() {
		if (watcher_list == null)
			watcher_list = new HashSet<String>();
		return watcher_list;
	}

	public void setWatcher_list(List<WatchEntity> list) {
		getWatcher_list();
		watcher_list.clear();
		if (!Validator.isEmptyList(list)) {
			for (WatchEntity watchEntity : list) {
				watcher_list.add(watchEntity.user.id);
			}
		}
	}

	public Set<String> getFavor_event_list() {
		if (favor_event_list == null)
			favor_event_list = new HashSet<String>();
		return favor_event_list;
	}

	public void setFavor_event_list(Set<String> favor_event_list) {
		this.favor_event_list = favor_event_list;
	}
}
