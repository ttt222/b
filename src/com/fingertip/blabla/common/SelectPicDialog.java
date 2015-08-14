package com.fingertip.blabla.common;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.util.FileUtil;

/**
 * ͼƬѡ��
 * @author Administrator
 *
 */
public class SelectPicDialog extends BaseActivity implements OnClickListener {
	private static final String TAG = "SelectPicDialog";
	
	public static final int REQUEST_COMMON = 1000;
	public static final int REQUEST_CUTPICTURE = 2000;
	
	public static final String RETURN_VALUE = "return_value";
	
	private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private Intent intent;
	private Uri imageUri;
	
	private String localPath = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.piture_alert_dialog);
		intent = getIntent();
		localPath = intent.getStringExtra(BaseActivity.EXTRA_PARAM);
		
		
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);

		// ��Ӱ�ť����
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
		
	}//end onCreate
	

	// ʵ��onTouchEvent���������������Ļʱ���ٱ�Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}//end onTouchEvent

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK || data == null) {
			return;
		}
		if(localPath == null){
			localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "��ʱͼƬ.png";
		}
		Log.i(TAG, "Path:" + localPath);
		FileUtil.createNewFile(localPath);
		
		imageUri = data.getData();
		if(imageUri == null){
			
			Bundle bundle = data.getExtras();    
			if (bundle != null) {
				Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
				// spath :����ͼƬȡ�����ֺ�·����������
				FileUtil.saveImage(photo, localPath);
//				imageUri = Uri.parse(localPath);
				imageUri = Uri.fromFile(new File(localPath));
				
			} else {
				Toast.makeText(getApplicationContext(), "err****", Toast.LENGTH_LONG).show();
				return;
			}  
			
		}
		
//		if(requestCode == REQUEST_CUTPICTURE){
//			 cropImageUri(imageUri, 116, 116, REQUEST_COMMON);
//			return;
//		}
		// ѡ������������պ�������ﴦ��Ȼ�����Ǽ���ʹ��setResult����Intent�Ա���Դ������ݺ͵���
		if (data.getExtras() != null)
			intent.putExtras(data.getExtras());
		if (data.getData() != null)
			intent.setData(data.getData());
		intent.putExtra(RETURN_VALUE, localPath);
		setResult(REQUEST_COMMON, intent);
		finish();
	}//end onActivityResult
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			try {
				// ����������ActionΪMediaStore.ACTION_IMAGE_CAPTURE��
				// ��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CUTPICTURE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_pick_photo:
			try {
				// ѡ����Ƭ��ʱ��Ҳһ����������ActionΪIntent.ACTION_GET_CONTENT��
				// ��Щ��ʹ��������Action���ҷ�������Щ�����л�����⣬��������ѡ�����
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, REQUEST_CUTPICTURE);
			} catch (ActivityNotFoundException e) {

			}
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}

	}//end onClick

	/**
	 * ͼƬ����
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
//	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode) {
//		Intent intent = new Intent("com.android.camera.action.CROP");
//		intent.setDataAndType(uri, "image/*");
//		intent.putExtra("crop", "true");
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		intent.putExtra("outputX", outputX);
//		intent.putExtra("outputY", outputY);
////		intent.putExtra("scale", true);
////		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//		intent.putExtra("return-data", true);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
////		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//		intent.putExtra("noFaceDetection", true); // no face detection
//		startActivityForResult(intent, requestCode);
//	}

}
