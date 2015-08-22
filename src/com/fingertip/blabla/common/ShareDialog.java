package com.fingertip.blabla.common;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.entity.ShareEntity;
import com.fingertip.blabla.main.AttentionSelectedActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeConfig;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.exception.SocializeException;
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
//	public static final String EXTRA_TYPE = "extra_type";
//	/** ������� **/
//	public static final String TYPE_SOFTSHARE = "type_softshare";
//	/** ����� **/
//	public static final String TYPE_ACTIVITY = "type_activity";
//	/** ��Ʒ���� **/
//	public static final String TYPE_GOODS = "type_goods";

	private ShareEntity shareEntity;

//	private boolean isExistWechat = false;
//	private boolean isExistSina = false;
//	private boolean isExistQQ = false;

//	@SuppressWarnings("unused")
//	private String type_share;
	
//	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);
	final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);

		findViews();
		setupViews();
		initData();
		
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); 
		layoutParams.gravity = Gravity.BOTTOM;
		layoutParams.width = getResources().getDisplayMetrics().widthPixels;
		
	}//end onCreate

	private void findViews() {
		
	}//end findViews

	private void setupViews() {
		findViewById(R.id.tuding).setOnClickListener(onClickListener);
		findViewById(R.id.wechat_friend).setOnClickListener(onClickListener);
		findViewById(R.id.wechat).setOnClickListener(onClickListener);
		findViewById(R.id.sms).setOnClickListener(onClickListener);
		findViewById(R.id.qq).setOnClickListener(onClickListener);
		findViewById(R.id.btn_cancel).setOnClickListener(onClickListener);
	}// end setupViews

	private void initData() {
		shareEntity = (ShareEntity)getIntent().getSerializableExtra(EXTRA_PARAM);
		
		if(shareEntity == null){
			Toast.makeText(ShareDialog.this, "�������ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		initShare();
		
	}//end initData
	
	private void initShare() {
		addSMS();
		// ���QQ��QZoneƽ̨
        addQQQZonePlatform();
        
		addWXPlatform();
	}
	
	/**
     * ��Ӷ���ƽ̨</br>
     */
    private void addSMS() {
        // ��Ӷ���
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.addToSocialSDK();
    }
	
	/**
     * @�������� : ���΢��ƽ̨����
     * @return
     */
    private void addWXPlatform() {
        // ע�⣺��΢����Ȩ��ʱ�򣬱��봫��appSecret
        // ���΢��ƽ̨
        UMWXHandler wxHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxHandler.addToSocialSDK();

        // ֧��΢������Ȧ
        UMWXHandler wxCircleHandler = new UMWXHandler(ShareDialog.this, Globals.APPID_WX, Globals.SECRET_WX);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }//end addWXPlatform
    
    /**
     * @�������� : ���QQƽ̨֧�� QQ��������ݣ� �����������ͣ� �����������֡�ͼƬ�����֡���Ƶ. ����˵�� : title, summary,
     *       image url�б�����������һ��, targetUrl��������,��ҳ��ַ������"http://"��ͷ . title :
     *       Ҫ������� summary : Ҫ��������ָ��� image url : ͼƬ��ַ [������������������дһ��] targetUrl
     *       : �û�����÷���ʱ��ת����Ŀ���ַ [����] ( ������д��Ĭ������Ϊ������ҳ )
     * @return
     */
    private void addQQQZonePlatform() {
        String appId = "1104698067";
        String appKey = "gKXBi42MOQeQeSG7";
        // ���QQ֧��, ��������QQ�������ݵ�target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(ShareDialog.this, appId, appKey);
        qqSsoHandler.setTargetUrl(Globals.URL);
        qqSsoHandler.addToSocialSDK();

        // ���QZoneƽ̨
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(ShareDialog.this, appId, appKey);
        qZoneSsoHandler.addToSocialSDK();
    }//end addQQQZonePlatform
    
	
	private void jumpToShare(SHARE_MEDIA shareMedia){
		mController.getConfig().closeToast();
		if(SHARE_MEDIA.WEIXIN == shareMedia){
//			if(TYPE_SOFTSHARE.equals(type_share)){
				WeiXinShareContent weiXinShareContent = new WeiXinShareContent();
				weiXinShareContent.setShareContent(shareEntity.shareContent);
				weiXinShareContent.setTargetUrl(shareEntity.targetUrl);
				weiXinShareContent.setTitle(shareEntity.shareTitle);
				mController.setShareMedia(weiXinShareContent);
				
				mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
//			}else{
//				
//				UMImage mUMImgBitmap = new UMImage(ShareDialog.this, shareEntity.imgUrl);				
//				WeiXinShareContent weiXinShareContent = new WeiXinShareContent(mUMImgBitmap);
//				weiXinShareContent.setShareContent(shareEntity.shareContent);
//				weiXinShareContent.setTargetUrl(shareEntity.targetUrl);
//				weiXinShareContent.setTitle(shareEntity.shareTitle);
//				mController.setShareMedia(weiXinShareContent);
//				
//				mController.directShare(ShareDialog.this, SHARE_MEDIA.WEIXIN, snsPostListener);
//			}
		}else if(SHARE_MEDIA.WEIXIN_CIRCLE == shareMedia){
//			if(TYPE_SOFTSHARE.equals(type_share)){
				
				CircleShareContent circleShareContent = new CircleShareContent();
				circleShareContent.setShareContent(shareEntity.shareContent);
				circleShareContent.setTargetUrl(shareEntity.targetUrl);
				circleShareContent.setTitle(shareEntity.shareTitle);
				mController.setShareMedia(circleShareContent);
				
				mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
//			}else{
//				
//				UMImage mUMImgBitmap = new UMImage(ShareDialog.this, shareEntity.imgUrl);				
//				CircleShareContent circleShareContent = new CircleShareContent(mUMImgBitmap);
//				circleShareContent.setShareContent(shareEntity.shareContent);
//				circleShareContent.setTargetUrl(shareEntity.targetUrl);
//				circleShareContent.setTitle(shareEntity.shareTitle);
//				mController.setShareMedia(circleShareContent);
//				mController.directShare(ShareDialog.this, SHARE_MEDIA.WEIXIN_CIRCLE, snsPostListener);
//				
//			}
		}else if(SHARE_MEDIA.QQ == shareMedia){
			QQShareContent qqShareContent = new QQShareContent();
			qqShareContent.setShareContent(shareEntity.shareContent);
			qqShareContent.setTargetUrl(shareEntity.targetUrl);
			qqShareContent.setTitle(shareEntity.shareTitle);
			qqShareContent.setShareImage(new UMImage(this, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)));
			mController.setShareMedia(qqShareContent);
			
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		}else if(SHARE_MEDIA.SMS == shareMedia){
			SmsShareContent smsShareContent = new SmsShareContent();
			smsShareContent.setShareContent(shareEntity.shareContent);
			mController.setShareMedia(smsShareContent);
			
			mController.directShare(ShareDialog.this, shareMedia, snsPostListener);
		}
	}//end jumpToShare


//	private void checkApk() {
//		PackageManager pm = getPackageManager();
//		List<PackageInfo> packs = pm.getInstalledPackages(0);
//
//		for (PackageInfo pi : packs) {
//			String packageName = pi.applicationInfo.packageName;
//			if (packageName.equals(Global.PACKAGE_WECHAT)) {
//				isExistWechat = true;
//			}
//
//			if (packageName.equals(Global.PACKAGE_SINA)) {
//				isExistSina = true;
//			}
//
//			if (packageName.equals(Global.PACKAGE_QQ)) {
//				isExistQQ = true;
//			}
//
//			if (isExistWechat && isExistSina && isExistQQ) {
//				break;
//			}
//		}
//	}//end checkApk
	
//	private void shareToSinaQQText(final SHARE_MEDIA share_media){
//		mController.setShareContent("" + shareEntity.shareContent);
//		mController.directShare(ShareDialog.this, share_media, new SnsPostListener() {					
//			@Override
//			public void onStart() {
//				Toast.makeText(ShareDialog.this, "����ʼ", Toast.LENGTH_SHORT).show();
//				if(SHARE_MEDIA.SINA == share_media){
//					showProgressLoading();
//				}
//			}//end onStart
//			
//			@Override
//			public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
//				dismissProgressLoading();
//				if(eCode == StatusCode.ST_CODE_SUCCESSED){
//					Toast.makeText(ShareDialog.this, "����ɹ�", Toast.LENGTH_SHORT).show();
//				}else {
//					Toast.makeText(ShareDialog.this, "����ʧ��", Toast.LENGTH_SHORT).show();
//				}
//				finish();
//			}//end onComplete
//		});
//	}//end shareToSina
	
	@Override
	protected void onResume() {
		super.onResume();
		
//		checkApk();
		
	}//end onResume
	

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**ʹ��SSO��Ȩ����������´��� */
//	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
//	    if(ssoHandler != null){
//	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
//	    }
	    UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}//end onActivityResult
	
	View.OnClickListener onClickListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.tuding){
				Intent intent = new Intent();
				intent.setClass(ShareDialog.this, AttentionSelectedActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, shareEntity);
				startActivity(intent);
				finish();
			}else if(v.getId() == R.id.wechat){
//				if (isExistWechat) {
					jumpToShare(SHARE_MEDIA.WEIXIN);
//				} else {
//					dialogCommon2.setCancelVisible(false);
//					dialogCommon2.setContentText(String.format(getResources().getString(R.string.noinstallapk), getResources().getString(R.string.wechat)));
//					dialogCommon2.show();
//				}
			}else if(v.getId() == R.id.wechat_friend){
//				if (isExistWechat) {
					jumpToShare(SHARE_MEDIA.WEIXIN_CIRCLE);
//				} else {
//					dialogCommon2.setCancelVisible(false);
//					dialogCommon2.setContentText(String.format(getResources().getString(R.string.noinstallapk), getResources().getString(R.string.wechatfriend)));
//					dialogCommon2.show();
//				}
			}else if(v.getId() == R.id.qq){
//				jumpToShare(SHARE_MEDIA.SINA);
				jumpToShare(SHARE_MEDIA.QQ);
			}else if(v.getId() == R.id.sms){
				jumpToShare(SHARE_MEDIA.SMS);
			}else if(v.getId() == R.id.btn_cancel){
				finish();
			}
		}//endonClick
	};
	
//	UMAuthListener umAuthListener = new UMAuthListener() {
//		@Override
//		public void onStart(SHARE_MEDIA arg0) {
//			Toast.makeText(ShareDialog.this, "��Ȩ��ʼ", Toast.LENGTH_SHORT).show();
//		}
//		
//		@Override
//		public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
//			Toast.makeText(ShareDialog.this, "��Ȩ����", Toast.LENGTH_SHORT).show();
//		}
//		
//		@Override
//		public void onComplete(Bundle value, SHARE_MEDIA platform) {
//			Toast.makeText(ShareDialog.this, "��Ȩ���", Toast.LENGTH_SHORT).show();
//		}
//		
//		@Override
//		public void onCancel(SHARE_MEDIA arg0) {
//			Toast.makeText(ShareDialog.this, "��Ȩȡ��", Toast.LENGTH_SHORT).show();
//		}
//	};

	private SnsPostListener snsPostListener = new SnsPostListener() {					
		@Override
		public void onStart() {
//			Toast.makeText(ShareDialog.this, "����ʼ", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		@Override
		public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
//			if(eCode == StatusCode.ST_CODE_SUCCESSED){
//				Toast.makeText(ShareDialog.this, "����ɹ�", Toast.LENGTH_SHORT).show();
//			}else {
//				Toast.makeText(ShareDialog.this, "����ʧ��", Toast.LENGTH_SHORT).show();
//			}
			finish();
		}//end onComplete
	};//end snsPostListener
	
}
