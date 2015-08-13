package com.fingertip.blabla.barcode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.SelectPicPopupWindow;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;

public class ScanBarcodeActivity extends BaseNavActivity implements SurfaceHolder.Callback, View.OnClickListener {
	private static final String TAG = ScanBarcodeActivity.class.getSimpleName();
	public static String KEY_VALIDATOR = "validator";
	
	static int TOP = 152, WIDTH = 240, HEIGHT = 240, MAX_IMG_SIZE = 300 * 1024; 
	
	private CameraManager cameraManager;
	
	private BarcodeValidator validator;
	
//	private ImageView scan_franme;
	private SurfaceView surface;
	
	private ImageView right_btn;
	
	private boolean surfaceCreated;
	
	private ScanBarcodeHandler scanBarcodeHandler;
	private DecodePicHandler decodePicHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanbarcode);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
//		scan_franme = (ImageView) findViewById(R.id.scan_franme);
		surface = (SurfaceView) findViewById(R.id.scan_surface);
		right_btn = (ImageView) findViewById(R.id.nav_right_btn);
	}
	
	protected void setupViews() {
		super.setupViews();
//		back_txt.setText(R.string.nav_back);
		nav_title_txt.setText(R.string.scan_title);
		right_btn.setImageResource(R.drawable.icon_scan);
		right_btn.setOnClickListener(this);
		
		Intent intent = this.getIntent();
		if (intent != null && intent.hasExtra(KEY_VALIDATOR))
			validator = (BarcodeValidator) intent.getSerializableExtra(KEY_VALIDATOR);
	}
	
	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 *
	 * @param rawResult			The contents of the barcode.
	 * @param scaleFactor		amount by which thumbnail was scaled
	 * @param barcode			A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
		boolean fromLiveScan = barcode != null;
		if (fromLiveScan) {
//			drawResultPoints(barcode, scaleFactor, rawResult);
		}
		if (validator != null) {
			if (validator.canProcess(rawResult))
				returnCode(rawResult);
			else {
				toastShort("未能识别的二维码");
				restartPreviewAfterDelay();
			}
		} else {
			Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
			restartPreviewAfterDelay();
		}
	}
	
	/**
	 * decode a picture
	 * 
	 * @param rawResult
	 */
	public void handleDecodePic(Result rawResult) {
		if (rawResult != null) {
			if (validator != null) {
				if (validator.canProcess(rawResult))
					returnCode(rawResult);
				else {
					toastShort("未能识别的二维码");
					restartPreviewAfterDelay();
				}
			} else
				Toast.makeText(this, rawResult.getText(), Toast.LENGTH_LONG).show();
		} else {
			toastShort("未能识别出二维码");
			restartPreviewAfterDelay();
		}
	}
	
	public void returnCode(Result rawResult) {
		Intent intent = new Intent();
		intent.putExtra(Globals.COMMON_RESULT, rawResult.getText());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cameraManager = new CameraManager(getApplication());
	    SurfaceHolder surfaceHolder = surface.getHolder();
		if (surfaceCreated) {
			// The activity was paused but not stopped, so the surface still exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the camera.
			surfaceHolder.addCallback(this);
		}
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			setCamerRect();
			// Creating the handler starts the preview, which can also throw a RuntimeException.
			if (scanBarcodeHandler == null) {
				scanBarcodeHandler = new ScanBarcodeHandler(this, cameraManager);
			}
    		decodePicHandler = new DecodePicHandler(this);
//			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}
	
	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.show();
	}
	
	private void setCamerRect() {
		Point cameraResolution = cameraManager.getConfigManager().getCameraResolution();
		Point screenResolution = cameraManager.getConfigManager().getScreenResolution();
		if (cameraResolution != null || screenResolution != null) {
			Rect rect = new Rect((screenResolution.x - dp2px(WIDTH)) / 2, dp2px(TOP), 
					(screenResolution.x - (screenResolution.x - dp2px(WIDTH)) / 2), dp2px(TOP + HEIGHT));
			cameraManager.setFramingRect(rect);
		}
	}
	
	private int dp2px(int dp) {
	    final float scale = getResources().getDisplayMetrics().density; 
	    return (int) (dp * scale + 0.5f); 
	} 

	
	@Override
	protected void onPause() {
		if (scanBarcodeHandler != null) {
			scanBarcodeHandler.quitSynchronously();
			scanBarcodeHandler = null;
		}
		cameraManager.closeDriver();
		if (!surfaceCreated) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scan_surface);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//			case KeyEvent.KEYCODE_BACK:
//				setResult(RESULT_CANCELED);
//				finish();
//				return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	public ScanBarcodeHandler getHandler() {
		return scanBarcodeHandler;
	}
	
	public CameraManager getCameraManager() {
		return cameraManager;
	}

	public void restartPreviewAfterDelay() {
		if (scanBarcodeHandler != null) 
			scanBarcodeHandler.sendEmptyMessageDelayed(R.id.restart_preview, 0);
	}
		  
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!surfaceCreated) {
			surfaceCreated = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceCreated = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nav_right_btn:
			Intent intent = new Intent();
			intent.setClass(ScanBarcodeActivity.this, SelectPicPopupWindow.class);
			intent.putExtra(SelectPicPopupWindow.KEY_CUT, false);
			startActivityForResult(intent, R.id.decode);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null && requestCode == R.id.decode) {
			Uri img_uri = data.getData();   
			Bitmap image = null;
            //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取   
            if (img_uri != null) {   
                try {   
                    //这个方法是根据Uri获取Bitmap图片的静态方法   
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), img_uri);   
                } catch (Exception e) {   
                    e.printStackTrace();   
                }   
            } else {   
                Bundle extras = data.getExtras();   
                if (extras != null) {   
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片   
                    image = extras.getParcelable("data");   
                }   
            }
            if (image != null) {
            	Message message = Message.obtain(decodePicHandler, R.id.decode, image);
				message.sendToTarget();
            }   
		}
	}
	
	public static interface BarcodeValidator {
		public boolean canProcess(Result result);
	}
}

class DecodePicHandler extends Handler {
	
	private ScanBarcodeActivity activity;
	private MultiFormatReader reader;
	
	public DecodePicHandler(ScanBarcodeActivity activity) {
		super();
		this.activity = activity;
		this.reader = new MultiFormatReader();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case R.id.decode :
			if (msg.obj != null && msg.obj instanceof Bitmap)
			decode((Bitmap)msg.obj);
			break;
		}
	};
	
	private void decode(Bitmap bitmap) {
		BinaryBitmap binaryBitmap = bitmapToBinary(bitmap);
		Result rawResult = null;
		try {
			rawResult = reader.decode(binaryBitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			reader.reset();
		}
		activity.handleDecodePic(rawResult);
	}
	
	/**
	 * 压缩bitmap
	 * 
	 * @param bitmap
	 * @return
	 */
	private Bitmap compressImage(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight();
		int scale = 100;
	    if (size > ScanBarcodeActivity.MAX_IMG_SIZE) {
	    	scale = ScanBarcodeActivity.MAX_IMG_SIZE / size * 100;
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	bitmap.compress(Bitmap.CompressFormat.JPEG, scale, baos);
	    	ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	    	BitmapFactory.Options options = new BitmapFactory.Options(); 
	    	options.inJustDecodeBounds = false;
	    	options.inSampleSize = (int) Math.sqrt(size / ScanBarcodeActivity.MAX_IMG_SIZE);
	        return BitmapFactory.decodeStream(isBm, null, options);
	    } else 
	    	return bitmap;
    }
	
	/**
	 * bitmap转为BinaryBitmap
	 * 
	 * @param bitmap
	 * @return
	 */
	private BinaryBitmap bitmapToBinary(Bitmap bitmap) {
		bitmap = compressImage(bitmap);
	    int lWidth = bitmap.getWidth();
	    int lHeight = bitmap.getHeight();
	    int[] lPixels = new int[lWidth * lHeight];
	    bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
	    return new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(lWidth, lHeight, lPixels)));
	}
}
