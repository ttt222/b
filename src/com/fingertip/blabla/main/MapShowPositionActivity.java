package com.fingertip.blabla.main;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.fingertip.blabla.Cmd;
import com.fingertip.blabla.Globals;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.Tools;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.OverlayEntityList;
import com.fingertip.blabla.entity.OverlayEntityList.OverlayEntity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class MapShowPositionActivity extends BaseActivity{
	private static final String TAG = "MapShowPositionActivity";
	
	public static final int RESULT_POS = 2000;
	public static final String RETURN_VALUE = "return_value";
	
	private MapView mMapView;
	private BaiduMap baiduMap;
	
	private LocationClient mLocationClient;
	private MyLocationListenner myLocationListenner = new MyLocationListenner();
	
	private OverlayEntity overlayEntity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_mapshowpoistion);
		
		findViews();
		setupViews();
		
		initData();
	}//end onCreate

	private void findViews() {
		
	}

	private void setupViews() {
		findViewById(R.id.tv_more).setVisibility(View.INVISIBLE);
		TextView tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("活动位置");
		
		findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		
	}//end setupViews
	
	private void initData(){
		overlayEntity = (OverlayEntity)getIntent().getSerializableExtra(BaseActivity.EXTRA_PARAM);
		if(overlayEntity == null){
			Toast.makeText(MapShowPositionActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		initMap();
		
		if(overlayEntity.title == null || overlayEntity.title.length() == 0){
			showProgressDialog(false);
			getPosData();
			return;
		}
		
		setOverlayData();
		
	}//end initData
	
	private void initMap(){
		mMapView = (MapView)findViewById(R.id.bmapView);
		baiduMap = mMapView.getMap();
		
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		baiduMap.setMyLocationEnabled(true);
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myLocationListenner);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		mMapView.showZoomControls(false);
	}//end initMap
	
	private void setOverlayData(){
		LatLng point = new LatLng(overlayEntity.lat, overlayEntity.lng);
		ViewMapOverlay viewMapOverlay = new ViewMapOverlay(getApplicationContext());
		
		viewMapOverlay.setOverlayEntity(overlayEntity);
		viewMapOverlay.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MapShowPositionActivity.this, OverlayBigActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
				startActivity(intent);
				
			}
		});
		InfoWindow mInfoWindow = new InfoWindow(viewMapOverlay, point, 0);
		baiduMap.showInfoWindow(mInfoWindow);
		
		
//		LatLng point = new LatLng(overlayEntity.lat, overlayEntity.lng);
//		BitmapDescriptor bitmapDescriptor = null;
//		Context context = MapShowPositionActivity.this;
//		View view_markerImage = LayoutInflater.from(context).inflate(R.layout.view_marker_img, null);
//		ImageView iv_markerImg = (ImageView)view_markerImage.findViewById(R.id.image);
//		if(overlayEntity.type == OverlayType.PERFORM){
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_1);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_1));
//		}else if(overlayEntity.type == OverlayType.SOCIALITY){
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_2);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_2));
//		}else if(overlayEntity.type == OverlayType.SPECIAL){
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_3);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_3));
//		}else if(overlayEntity.type == OverlayType.SPORTS){
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_4);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_4));
//		}else if(overlayEntity.type == OverlayType.STUDY){
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_5);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_5));
//		}else {
//			view_markerImage.setBackgroundResource(R.drawable.bg_icon_6);
//			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_6));
//		}
//		
//		
//		bitmapDescriptor = BitmapDescriptorFactory.fromView(view_markerImage);
//		
//		OverlayOptions options = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false);
//		Marker marker = (Marker)(baiduMap.addOverlay(options));
//		
//		
//		overlayEntity.isIcon = true;
		
	}//end setOverlayData
	
	@Override
	protected void onResume(){
		mMapView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy(){
		
		mLocationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		
		super.onDestroy();
	}//end onDestroy
	
	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		private boolean isFirstLoc = true;
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null){
				return;
			}
			
			@SuppressWarnings("unused")
			float zoom = baiduMap.getMapStatus().zoom;
			
			if (isFirstLoc) {
				if(BDLocation.TypeCacheLocation == location.getLocType() || BDLocation.TypeGpsLocation == location.getLocType()|| BDLocation.TypeNetWorkLocation == location.getLocType() || BDLocation.TypeOffLineLocation == location.getLocType()){
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					setMarker(ll);
				}else {
					if(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
						isFirstLoc = false;
						LatLng ll = new LatLng(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
						setMarker(ll);
					}else {
//						Toast.makeText(MapShowPositionActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			
		}//end onReceiveLocation
		public void onReceivePoi(BDLocation poiLocation) { }
	}//end MyLocationListenner class
	
	private void setMarker(LatLng point){
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point ,16);
		baiduMap.animateMapStatus(u);
	}//end setMarker

	
	/** 获取地图数据 **/
	private void getPosData(){
		HttpUtils http_getpos = Tools.getHttpUtils();
		
		JSONObject jsonObject = new JSONObject();
		try { jsonObject.put("fc", "get_action_byactionid"); } catch (Exception e) { }
		try { jsonObject.put("userid", getSP().getStringValue(SharedPreferenceUtil.LAST_UID)); } catch (Exception e) { }
		try { jsonObject.put("loginid", getSP().getStringValue(SharedPreferenceUtil.LAST_LOGIN_ID)); } catch (Exception e) { }
		try { jsonObject.put("actionid", overlayEntity.actionid); } catch (Exception e) { }
		
		
		RequestParams params = new RequestParams();  
	    params.addQueryStringParameter("command", Tools.encodeString(jsonObject.toString()));  
	    
	    Log.e(TAG, "onLoad before:" + jsonObject.toString());
		
		http_getpos.send(HttpRequest.HttpMethod.POST,
		   Globals.URL + Cmd.ACTION_BYPOSID,
		   params,
		   new RequestCallBack<String>(){
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	
		        }

		        @Override
		        public void onSuccess(ResponseInfo<String> responseInfo) {
		        	dimissProgressDialog();
		        	Log.e(TAG, "onLoad finish:" + Tools.decodeString(responseInfo.result));
		            JSONObject jsonObject = null;
		            try {
						jsonObject = new JSONObject(Tools.decodeString(responseInfo.result));
						overlayEntity = OverlayEntityList.parseJSON(jsonObject.getJSONObject("actioninfor"));
			            setOverlayData();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MapShowPositionActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
						return;
					}
		            
		        }//end onSuccess

		        @Override
		        public void onStart() {
		        	Log.e(TAG, ",,,,,,,,,,onStart");
		        }

		        @Override
		        public void onFailure(HttpException error, String msg) {
		        	dimissProgressDialog();
		        	Log.e(TAG, "............onFailure:" + msg);
		        }
		});
		
	}//end getPosData
	
}
