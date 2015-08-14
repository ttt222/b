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
 * 图片选择
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

		// 添加按钮监听
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
		
	}//end onCreate
	

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
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
			localPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName() + File.separator + "临时图片.png";
		}
		Log.i(TAG, "Path:" + localPath);
		FileUtil.createNewFile(localPath);
		
		imageUri = data.getData();
		if(imageUri == null){
			
			Bundle bundle = data.getExtras();    
			if (bundle != null) {
				Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
				// spath :生成图片取个名字和路径包含类型
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
		// 选择完或者拍完照后会在这里处理，然后我们继续使用setResult返回Intent以便可以传递数据和调用
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
				// 拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
				// 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CUTPICTURE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case R.id.btn_pick_photo:
			try {
				// 选择照片的时候也一样，我们用Action为Intent.ACTION_GET_CONTENT，
				// 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
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
	 * 图片剪切
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
