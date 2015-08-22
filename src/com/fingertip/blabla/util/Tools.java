package com.fingertip.blabla.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.account.LoginActivity;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.entity.MessageEntity;
import com.fingertip.blabla.entity.MessageEntity.MessageDbEntity;
import com.fingertip.blabla.entity.UserEntity;
import com.fingertip.blabla.info.PublishInfoActivity;
import com.fingertip.blabla.main.MainActivity;
import com.fingertip.blabla.my.UserInfoActivity;
import com.fingertip.blabla.util.http.ServerConstants;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class Tools {
	/**
	 * 
	 * 图片圆角
	 * @param bitmap:需要转化成圆角的图片
	 * @param pixels:圆角的度数，数值越大，圆角越大
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}//end toRoundCorner
	
	
	/** 字符串64位解码 **/
	public static String decodeString(String encode){
		return new String(Base64.decode(encode.getBytes(), Base64.DEFAULT));
	}
	
	/** 字符串64位编码 **/
	public static String encodeString(String encode_string){
		return new String(Base64.encodeToString(encode_string.getBytes(), Base64.DEFAULT));
	}
	
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
	
	/**
	 * 字符串格式
	 * 
	 * @param str
	 * @return 
	 */
	public static String strToFormat(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
		String returnString = "";
		Date date = null;
		try {
			date = format.parse(str);
			returnString = format.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}//end StrToDate
	
	/**
	 * 字符串格式
	 * 
	 * @param str
	 * @return 
	 */
	public static String strToFormatDay(String str) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
		SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日", Locale.getDefault());
		String returnString = "";
		Date date = null;
		try {
			date = format.parse(str);
			returnString = format2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnString;
	}//end StrToDate
	
	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @param formatString
	 * @return date
	 */
	public static Date strToDate(String str) {
		return strToDate(str, null);
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @param formatString
	 * @return date
	 */
	public static Date strToDate(String str, String formatString) {
		if(formatString == null){
			formatString = "yyyy-MM-dd HH:mm:ss";
		}
		SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
		Date date = null;
		try {
			date = format.parse(str);
		} catch (Exception e) {
			date = new Date();
//			e.printStackTrace();
		}
		return date;
	}
	
	
	public static void toastShort(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	public static void toastLeng(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static boolean checkLogin(Context context) {
		UserSession session = UserSession.getInstance();
		if (!session.isLogin()) {
			Intent intent = new Intent();
			intent.setClass(context, LoginActivity.class);
			context.startActivity(intent);
			return false;
		}
		return true;
	}
	
	/**
	 * 发布活动
	 * @param context
	 */
	public static void pubEvent(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, PublishInfoActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 找活动，跳转到首页
	 * @param context
	 */
	public static void searchEvent(Context context) {
		Globals.clearActivityList(false);
	}

	/**
	 * 跳转到活动详情界面
	 * @param context
	 * @param event_id
	 */
	public static void openEvent(Context context, String event_id) {
//		OverlayEntity overlayEntity = new OverlayEntity();
//		overlayEntity.actionid = event_id;
//		Intent intent = new Intent();
//		intent.setClass(context, MapShowPositionActivity.class);
//		intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
//		context.startActivity(intent);
		Intent intent = new Intent();
		intent.setClass(context, MainActivity.class);
		intent.putExtra(BaseActivity.EXTRA_PARAM, event_id);
		context.startActivity(intent);
	}
	
	/**
	 * 跳转到用户详情界面
	 * @param context
	 * @param id
	 */
	public static void openUser(Context context, String id) {
		Intent intent = new Intent();
		intent.setClass(context, UserInfoActivity.class);
		intent.putExtra(UserInfoActivity.KEY_USER_ID, id);
		context.startActivity(intent);
	}

	/**
	 * 跳转到用户详情界面
	 * @param context
	 * @param user
	 */
	public static void openUser(Context context, UserEntity user) {
		Intent intent = new Intent();
		intent.setClass(context, UserInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(UserInfoActivity.KEY_USER, user);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	public static List<MessageEntity> getMessages(Context context, String receiver_id, int count, int page) {
		List<MessageDbEntity> list = null;
		DbUtils db = DbUtils.create(context);
		try {
			list = db.findAll(Selector.from(MessageDbEntity.class).where("receiver_id" ,"=", receiver_id).orderBy("send_time", true)
					.limit(count).offset(count * (page - 1)));
		} catch (DbException e) {
			Log.e("getMessages", e.getMessage(), e);
		}
		return MessageEntity.parseDbEntity(list);
	}
	
	public static void saveMessages(Context context, List<MessageEntity> list) {
		if (list != null && !list.isEmpty()) {
			List<MessageDbEntity> db_list = new ArrayList<MessageDbEntity>();
			for (MessageEntity msg : list)
				db_list.add(msg.msg_db);
			DbUtils db = DbUtils.create(context);
			try {
				db.saveAll(db_list);
			} catch (DbException e) {
				Log.e("saveMessages", e.getMessage(), e);
			}
		}
	}
	
	/**
	 * 删除数据库中的消息
	 * @param context
	 * @param receiver_id
	 * @param msg_ids
	 */
	public static void deleteMessages(Context context, String receiver_id, List<String> msg_ids) {
		if (!Validator.isEmptyString(receiver_id) && !Validator.isEmptyList(msg_ids)) {
			DbUtils db = DbUtils.create(context);
			StringBuilder buffer = new StringBuilder("delete from message where sender_id = '")
			.append(receiver_id).append("' and msg_id in(");
			StringBuilder ids = new StringBuilder();
			for (String msg_id : msg_ids) {
				ids.append("'").append(msg_id).append("',");
			}
			buffer.append(ids.subSequence(0, ids.length() - 1)).append(")");
			try {
				db.execNonQuery(buffer.toString());
			} catch (DbException e) {
				Log.e("deleteMessages", e.getMessage(), e);
			}
		}
	}
	
	public static String getTimeStr(long send_time) {
		long now = System.currentTimeMillis();
		long time = (now - send_time) / 1000 / 60;
		//一分钟以内
		if (time <= 0)
			return "刚刚";
		//一小时内
		long tmp = time / 60;
		if (tmp < 1)
			return time + "分钟前";
		//24小时内
		time = tmp;
		tmp /= 24;
		Calendar now_c = Calendar.getInstance();
		now_c.setTimeInMillis(now);
		Calendar send_c = Calendar.getInstance();
		send_c.setTimeInMillis(send_time);
		int M = send_c.get(Calendar.MONTH) + 1;
		int D = send_c.get(Calendar.DAY_OF_MONTH);
		if (tmp < 1) {
			if (D == now_c.get(Calendar.DAY_OF_MONTH))
				return time + "小时前";
			else
				return "昨天";
		}
		if (tmp < 3) {
			int days = getDays(now_c, send_c);
			if (days == 1)
				return "昨天";
			if (days == 2)
				return "前天";
		}
//		//一个月内
//		time = tmp;
//		tmp /= 30;
//		if (tmp < 0)
//			return time + "天前";
//		//一年内
//		time = tmp;
//		tmp /= 12;
//		if (tmp < 0)
//			return time + "月前";
		return M + "月" + D + "日"; 
	}
	
	private static int getDays(Calendar now, Calendar before) {
		int y1 = now.get(Calendar.YEAR);
		int y2 = before.get(Calendar.YEAR);
		if (y1 == y2)
			return now.get(Calendar.DAY_OF_YEAR) - before.get(Calendar.DAY_OF_YEAR);
		else {
			int d1 = now.get(Calendar.DAY_OF_YEAR);
			while (y1 > y2) {
				y1--;
				now.set(Calendar.YEAR, y1 - 1);
				d1 += now.getMaximum(Calendar.DAY_OF_YEAR);
			}
			return d1 - before.get(Calendar.DAY_OF_YEAR);
		}
	}
	
	public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
	
	public static HttpUtils getHttpUtils() {
		return new HttpUtils(ServerConstants.HTTP_TIME_OUT);
	}
}
