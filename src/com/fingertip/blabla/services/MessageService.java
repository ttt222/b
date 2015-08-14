package com.fingertip.blabla.services;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.MessageEntity;
import com.fingertip.blabla.util.Tools;
import com.fingertip.blabla.util.Validator;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.ServerConstants;
import com.fingertip.blabla.util.http.UserUtil;

public class MessageService extends Service {
	
	private Timer timer = new Timer();
	private SharedPreferenceUtil sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (sp == null)
			sp = new SharedPreferenceUtil(this);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				boolean has_new = sp.getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false);
				if (!has_new && Tools.isNetworkConnected(MessageService.this)) {
					UserUtil.loadUserMsg(new EntityListCallback<MessageEntity>() {
						@Override
						public void succeed(List<MessageEntity> list) {
							if (!Validator.isEmptyList(list)) {
								sp.setBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, true);
								UserSession session = UserSession.getInstance();
								for (MessageEntity msg : list)
									msg.receiver_id = session.getId();
								Tools.saveMessages(MessageService.this, list);
							}
						}
						
						@Override
						public void fail(String error) {
						}
					});
				}
			}
		}, 0, ServerConstants.GET_MESSAGE_GAP * 1000);
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		timer.cancel();
		timer = null;
	}
}
