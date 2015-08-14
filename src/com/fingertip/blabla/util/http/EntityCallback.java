package com.fingertip.blabla.util.http;

public interface EntityCallback<E> {

	public void succeed(E entity);
	
	public void fail(String error);
}
