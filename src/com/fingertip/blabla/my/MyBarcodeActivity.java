package com.fingertip.blabla.my;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseNavActivity;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.util.ImageCache;
import com.fingertip.blabla.util.http.UserUtil;
import com.google.zxing.WriterException;

public class MyBarcodeActivity extends BaseNavActivity {
	
	private ImageView my_head_img, my_barcode_img;
	private TextView my_name_txt, my_place_txt;
	
	private UserSession session;
	private ImageCache imageCache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_barcode);
		findViews();
		setupViews();
	}
	
	protected void findViews() {
		super.findViews();
		my_name_txt = (TextView) findViewById(R.id.my_name_txt);
		my_place_txt = (TextView) findViewById(R.id.my_place_txt);
		my_head_img = (ImageView) findViewById(R.id.my_head_img);
		my_barcode_img = (ImageView) findViewById(R.id.my_barcode_img);
		
		session = UserSession.getInstance();
		imageCache = ImageCache.getInstance();
	}
	
	protected void setupViews() {
		super.setupViews();
		nav_title_txt.setText(R.string.my_watch_myinfo);
		my_name_txt.setText(session.getNick_name());
		my_place_txt.setText(session.getPlace());
		try {
			my_barcode_img.setImageBitmap(UserUtil.createQRCode(session.getId(), 500));
		} catch (WriterException e) {
			e.printStackTrace();
			toastShort("���ɶ�ά��ʧ��\n" + e.getMessage());
		}
		my_head_img.setImageBitmap(BitmapFactory.decodeFile(imageCache.getUserImgPath(session.getId())));
	}

}
