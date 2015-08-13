package com.google.zxing.client.android;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.os.Handler;
import android.os.Looper;

import com.fingertip.blabla.barcode.ScanBarcodeActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ResultPointCallback;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public class DecodeThread extends Thread {

	public static final String BARCODE_BITMAP = "barcode_bitmap";
	public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

	private final ScanBarcodeActivity activity;
	private final Map<DecodeHintType, Object> hints;
	private DecodeHandler handler;
	private final CountDownLatch handlerInitLatch;

	public DecodeThread(ScanBarcodeActivity activity, ResultPointCallback resultPointCallback) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
		hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
		// The prefs can't change while the thread is running, so pick them up once here.
		Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
		decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
		decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//		decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
//		decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
	}

	public Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	public MultiFormatReader getMultiFormatReader() {
		return handler.getMultiFormatReader();
	}
	
	@Override
	public void run() {
		Looper.prepare();
		handler = new DecodeHandler(activity, hints);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
