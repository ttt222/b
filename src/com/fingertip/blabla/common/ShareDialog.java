package com.fingertip.blabla.common;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.entity.ShareEntity;
import com.fingertip.blabla.main.AttentionSelectedActivity;
import com.fingertip.blabla.util.Tools;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMPlatformData;
import com.umeng.analytics.social.UMPlatformData.UMedia;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class ShareDialog extends BaseActivity {

	private ShareEntity shareEntity;

	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);

		setupViews();
		initData();
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
		layoutParams.gravity = Gravity.BOTTOM;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels;
	}

	private void setupViews() {
		findViewById(R.id.tuding).setOnClickListener(onClickListener);
		findViewById(R.id.wechat_friend).setOnClickListener(onClickListener);
		findViewById(R.id.wechat).setOnClickListener(onClickListener);
		findViewById(R.id.sms).setOnClickListener(onClickListener);
		findViewById(R.id.qq).setOnClickListener(onClickListener);
		findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
	}

	private void initData() {
		shareEntity = (ShareEntity)getIntent().getSerializableExtra(EXTRA_PARAM);
		if(shareEntity == null){
			toastShort("分享内容不能为空");
			finish();
			return;
		}
		initShare();
	}
	
	private void initShare() {
		addSMS();
		// 添加QQ、QZone平台
        addQQQZonePlatform();
		addWXPlatform();
	}
	
	/**
     * 添加短信平台</br>
     */
    private void addSMS() {
        // 添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }
	
	/**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 注意：在微信授权的时候，必须传递appSecret
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
    
    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
        String appId = "1104698067";
        String appKey = "gKXBi42MOQeQeSG7";
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ShareDialog.this, appId, appKey);
        qqSsoHandler.setTargetUrl(Globals.URL);
        qqSsoHandler.addToSocialSDK();
        // 添加QZone平台
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ShareDialog.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }
	
	private void jumpToShare(SHARE_MEDIA shareMedia){
		mController.getConfig().closeToast();
		if (SHARE_MEDIA.WEIXIN == shareMedia) {
			WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
			weiXinShareContent.setShareContent(shareEntity.shareContent);
			weiXinShareContent.setTargetUrl(shareEntity.targetUrl);
			weiXinShareContent.setTitle(shareEntity.shareTitle);
			weiXinShareContent.setShareImage(new UMImage(this, BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_img)));
			mController.setShareMedia(weiXinShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.WEIXIN_CIRCLE == shareMedia) {
			CircleShareContent circleShareContent = new CircleShareContent();
			circleShareContent.setShareContent(shareEntity.shareContent);
			circleShareContent.setTargetUrl(shareEntity.targetUrl);
			circleShareContent.setTitle(shareEntity.shareTitle);
			circleShareContent.setShareImage(new UMImage(this, BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_img)));
			mController.setShareMedia(circleShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.QQ == shareMedia) {
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(shareEntity.shareContent);
			qqShareContent.setTargetUrl(shareEntity.targetUrl);
			qqShareContent.setTitle(shareEntity.shareTitle);
			qqShareContent.setShareImage(new UMImage(this, BitmapFactory.decodeResource(getResources(), R.drawable.icon_share_img)));
			mController.setShareMedia(qqShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		} else if (SHARE_MEDIA.SMS == shareMedia) {
			SmsShareContent smsShareContent = new SmsShareContent();
			smsShareContent.setShareContent(getResources().getString(R.string.share_sms) + "\n" + shareEntity.targetUrl);
			mController.setShareMedia(smsShareContent);
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		}
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		// UMSsoHandler ssoHandler =
		// mController.getConfig().getSsoHandler(requestCode) ;
		// if(ssoHandler != null){
		// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		// }
		UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
		if (ssoHandler != null)
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.tuding) {
				if (Tools.checkLogin(ShareDialog.this)) {
					Intent intent = new Intent();
					intent.setClass(ShareDialog.this, AttentionSelectedActivity.class);
					intent.putExtra(BaseActivity.EXTRA_PARAM, shareEntity);
					startActivity(intent);
				}
				finish();
			} else if (v.getId() == R.id.wechat)
				jumpToShare(SHARE_MEDIA.WEIXIN);
			else if (v.getId() == R.id.wechat_friend)
				jumpToShare(SHARE_MEDIA.WEIXIN_CIRCLE);
			else if (v.getId() == R.id.qq)
				jumpToShare(SHARE_MEDIA.QQ);
			else if (v.getId() == R.id.sms)
				jumpToShare(SHARE_MEDIA.SMS);
			else if (v.getId() == R.id.btn_cancel)
				finish();
		}
	};
	
	private SnsPostListener snsPostListener = new SnsPostListener() {					
		@Override
		public void onStart() {
			finish();
		}
		
		@Override
		public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
			try {
				if (eCode == StatusCode.ST_CODE_SUCCESSED) {
					UserSession session = UserSession.getInstance();
					String user_id = session.getId() == null ? "null" : session.getId();
					UMPlatformData pf = new UMPlatformData(UMedia.valueOf(platform.toString()), user_id);
					MobclickAgent.onSocialEvent(ShareDialog.this, pf);
				}
			} catch (Exception e) {
			}
			finish();
		}
	};
	
}
