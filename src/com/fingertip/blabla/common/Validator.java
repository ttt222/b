package com.fingertip.blabla.common;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.fingertip.blabla.account.LoginActivity;

public class Validator {

	public static boolean isMobilePhone(String number) {
		if (number != null && number.length() == 11 && number.charAt(0) == '1') {
			try {
				Long.parseLong(number);
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static boolean isPassword(String password) {
		if (password != null && password.length() > 3)
			return true;
		return false;
	}
	
	public static boolean isEmptyString(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}
	
	public boolean checkLogin(Context context) {
		UserSession session = UserSession.getInstance();
		if (session.isLogin())
			return true;
		else {
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			intent.putExtra(LoginActivity.KEY_BACK, true);
			context.startActivity(intent);
			return false;
		}
	}
}
