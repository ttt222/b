package com.fingertip.blabla.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;

/**
 * ͼƬѡ����ͷ���ϴ�
 * @author Administrator
 *
 */
public class SelectPicPopupWindow extends BaseActivity implements OnClickListener {
	public static String KEY_CUT = "cut_pic";
	public static final int REQUEST_CUTPICTURE = 2000, REQUEST_HEAD = 3000;

	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private Intent intent;
	private Uri imageUri;
	private boolean cut_pic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piture_alert_dialog);
		intent = getIntent();
		cut_pic = intent.getBooleanExtra(KEY_CUT, true);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		
		// ��Ӱ�ť����
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setVisibility(View.GONE);
	}

	// ʵ��onTouchEvent���������������Ļʱ���ٱ�Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		imageUri = data.getData();
		if(imageUri == null){
			Bundle bundle = data.getExtras();    
			if (bundle == null) {
				toastShort("�޷�ѡ��ͼƬ");
				return;
			}  
		}
		
		if(requestCode == REQUEST_CUTPICTURE && cut_pic){
		 	cropImageUri(imageUri, 400, 400, REQUEST_HEAD);
			return;
		}
		// ѡ������������պ�������ﴦ��Ȼ�����Ǽ���ʹ��setResult����Intent�Ա���Դ������ݺ͵���
		if (data.getExtras() != null)
			intent.putExtras(data.getExtras());
		if (data.getData() != null)
			intent.setData(data.getData());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_pick_photo:
			try {
				// ѡ����Ƭ��ʱ��Ҳһ����������ActionΪIntent.ACTION_GET_CONTENT��
				// ��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(intent, REQUEST_CUTPICTURE);
				
//				
//				intent=new Intent(Intent.ACTION_GET_CONTENT);  
//                intent.addCategory(Intent.CATEGORY_OPENABLE);  
//                intent.setType("image/*");  
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {  
//                    startActivityForResult(intent,SELECT_PIC_KITKAT);  
//                } else {  
//                    startActivityForResult(intent,IMAGE_REQUEST_CODE);  
//                }  
				
				
			} catch (Exception e) {
			}
			break;
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

	/**
	 * ͼƬ����
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
//		intent.putExtra("scale", true);
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, requestCode);
	}

}
