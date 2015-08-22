package com.fingertip.blabla.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.fingertip.blabla.R;
import com.fingertip.blabla.base.BaseActivity;
import com.fingertip.blabla.common.ScrollTouchView;
import com.fingertip.blabla.common.ScrollTouchView.UpdateNotify;
import com.fingertip.blabla.common.UserSession;
import com.fingertip.blabla.db.SharedPreferenceUtil;
import com.fingertip.blabla.entity.EventEntity;
import com.fingertip.blabla.entity.OverlayEntityList;
import com.fingertip.blabla.entity.OverlayEntityList.OverlayEntity;
import com.fingertip.blabla.entity.OverlayType;
import com.fingertip.blabla.my.MyIndexActivity;
import com.fingertip.blabla.search.SearchMainActivity;
import com.fingertip.blabla.services.MessageService;
import com.fingertip.blabla.util.http.EntityCallback;
import com.fingertip.blabla.util.http.EntityListCallback;
import com.fingertip.blabla.util.http.EventUtil;
import com.fingertip.blabla.util.http.ServerConstants;

public class MainActivity extends BaseActivity implements UpdateNotify{
	private static final String TAG = "MainActivity";
	
	private MapView mMapView = null;
	private BaiduMap baiduMap;
	private LocationClient mLocationClient;
	private MyLocationListenner myLocationListenner = new MyLocationListenner();
	private ScrollTouchView  view_tab;
	
	/** 地图数据 **/
	private ArrayList<OverlayEntity> arrayList_overlaytEntity = new ArrayList<OverlayEntity>();
	private ArrayList<Marker> arrayList_marker = new ArrayList<Marker>();
	private HashMap<Marker, OverlayEntity> hashMap_overlayMarket = new HashMap<Marker, OverlayEntity>();
	
	private ImageView iv_icon_info;
	private Intent service;
	private Timer timer;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			iv_icon_info.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_info_red));
		};
	};
	
	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(TAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//				text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private SDKReceiver mSDKReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);
        
        setupViews();
        
        initSDKCheck();
        
        initMyData();
        if (UserSession.getInstance().isLogin())
        	startMsgService();
	}
	
	private void startMsgService() {
		service = new Intent();
		service.setClass(this, MessageService.class);
		startService(service);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				//有新消息
				if (getSP().getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false)) {
					Message msg = Message.obtain(handler, 0);
					msg.sendToTarget();
				}
			}
		}, 5000, ServerConstants.GET_MESSAGE_GAP * 1000);
	}

	private void setupViews() {
		iv_icon_info = (ImageView)findViewById(R.id.iv_icon_info);
		
		mMapView = (MapView)findViewById(R.id.bmapView);
		baiduMap = mMapView.getMap();
		
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		baiduMap.setMyLocationEnabled(true);
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(myLocationListenner);
		baiduMap.setOnMapStatusChangeListener(onMapStatusChangeListener);
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		mMapView.showZoomControls(false);
		
		view_tab = (ScrollTouchView)findViewById(R.id.view_tab);
		initTabeData();
		
		findViewById(R.id.iv_icon_info).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MyIndexActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SearchMainActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.iv_position).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
					isFirstLoc = false;
					LatLng ll = new LatLng(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
					baiduMap.animateMapStatus(u);
				}
				isFirstLoc = true;
				mLocationClient.start();
			}//end onClick
		});
		
		findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				MapStatus mapStatus = baiduMap.getMapStatus();
				baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(mapStatus.zoom + 1));
			}
		});
		findViewById(R.id.iv_plus).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				MapStatus mapStatus = baiduMap.getMapStatus();
				baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(mapStatus.zoom - 1));
			}
		});
		
		
	}//setupViews
	
	private void initSDKCheck(){
		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mSDKReceiver = new SDKReceiver();
		registerReceiver(mSDKReceiver, iFilter);
		
		if(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) >= 0){
			LatLng ll = new LatLng(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
			baiduMap.animateMapStatus(u);
		}
		
	}//end initSDKCheck
	
	/** 初始化底部数据 **/
	private void initTabeData(){
		view_tab.setUpdateNotify(MainActivity.this);
		
		Resources resources = getResources();
		Drawable drawableUnSelection = resources.getDrawable(R.drawable.icon_tab_activity_dot);
		
		view_tab.setTextColor(resources.getColor(R.color.maintab_selection), resources.getColor(R.color.gray_58));
		
		view_tab.addItem(OverlayType.ALL.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_6));
		view_tab.addItem(OverlayType.SPORTS.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_5));
		view_tab.addItem(OverlayType.SOCIALITY.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_3));
		view_tab.addItem(OverlayType.PERFORM.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_4));
		view_tab.addItem(OverlayType.STUDY.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_1));
		view_tab.addItem(OverlayType.SPECIAL.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_2));
		view_tab.addItem(OverlayType.OTHER.getType(), drawableUnSelection, resources.getDrawable(R.drawable.icon_classify_12));
		
		view_tab.setSelectionPosition(0);
	}
	
	private void initMyData(){
		MapStatus mapStatus = baiduMap.getMapStatus();
		lastZoom = mapStatus.zoom;
		clearMarkerList();
		baiduMap.setOnMapClickListener(new OnMapClickListener() {			
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				baiduMap.hideInfoWindow();
			}
		});
		
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {			
			@Override
			public boolean onMarkerClick(Marker marker) {
				return clickMarker(marker);
			}
		});
	}
	
	private void clearMarkerList(){
		Marker marker = null;
		for (int i = 0; i < arrayList_marker.size(); i++) {
			marker = arrayList_marker.get(i);
			marker.remove();
		}
		arrayList_marker.clear();
		hashMap_overlayMarket.clear();
	}
	
	@SuppressLint("InflateParams")
	private void setOverlayData(OverlayEntity overlayEntity){
		LatLng point = new LatLng(overlayEntity.lat, overlayEntity.lng);
		Context context = MainActivity.this;
		View view_markerImage = LayoutInflater.from(context).inflate(R.layout.view_marker_img, null);
		ImageView iv_markerImg = (ImageView)view_markerImage.findViewById(R.id.image);
		
		if(overlayEntity.type == OverlayType.SPORTS){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_5);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_5));
		}else if(overlayEntity.type == OverlayType.SOCIALITY){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_3);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_3));
		}else if(overlayEntity.type == OverlayType.PERFORM){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_4);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_4));
		}else if(overlayEntity.type == OverlayType.STUDY){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_1);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_1));
		}else if(overlayEntity.type == OverlayType.SPECIAL){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_2);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_2));
		}else if(overlayEntity.type == OverlayType.OTHER){
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_4);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_4));
		}else {
			view_markerImage.setBackgroundResource(R.drawable.bg_icon_6);
			iv_markerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_classify_6));
		}
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view_markerImage);
		
		OverlayOptions options = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false);
		Marker marker = (Marker)(baiduMap.addOverlay(options));
		if(!arrayList_marker.contains(marker))
			arrayList_marker.add(marker);
		hashMap_overlayMarket.put(marker, overlayEntity);
		overlayEntity.isIcon = true;
	}
	
	private Marker setZoomOverlayData(OverlayEntity overlayEntity){
		LatLng point = new LatLng(overlayEntity.lat, overlayEntity.lng);
		
		ViewMapOverlay viewMapOverlay = new ViewMapOverlay(getApplicationContext());
		viewMapOverlay.setOverlayEntity(overlayEntity);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitmapFromView(viewMapOverlay));;
		
		OverlayOptions options = new MarkerOptions().position(point).icon(bitmapDescriptor).draggable(false);
		Marker marker = (Marker)(baiduMap.addOverlay(options));
		if(!arrayList_marker.contains(marker)){
			arrayList_marker.add(marker);
		}
		
		hashMap_overlayMarket.put(marker, overlayEntity);
		
		overlayEntity.isIcon = false;
		return marker;
	}
	/* 从view 得到图片
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
	}
	
	private void resetOverlay(MapStatus mapStatus){
		clearMarkerList();
//		if(lastZoom >= 17){
//			resetZoomData(mapStatus);
//		}else {
			resetOverlayData(mapStatus);
//		}
	}//end resetOverlay
	
	private void resetOverlayData(MapStatus mapStatus){
		for (int i = 0; i < arrayList_overlaytEntity.size(); i++) {
			setOverlayData(arrayList_overlaytEntity.get(i));
		}
	}
	
//	private void resetZoomData(MapStatus mapStatus){
//		for (int i = 0; i < arrayList_overlaytEntity.size(); i++) {
//			setZoomOverlayData(arrayList_overlaytEntity.get(i));
//		}
//	}//end resetData
	
	private float lastZoom = 16;
	private OnMapStatusChangeListener onMapStatusChangeListener = new OnMapStatusChangeListener() {		
		@Override
		public void onMapStatusChangeStart(MapStatus mapStatus) {
			Log.e("onMapStatusChangeStart", mapStatus.target.latitude + "  " + mapStatus.target.longitude);
		}
		
		@Override
		public void onMapStatusChangeFinish(MapStatus mapStatus) {
			Log.e("onMapStatusChangeFinish", mapStatus.target.latitude + "  " + mapStatus.target.longitude);	
			getPosData();
		}
		
		@Override
		public void onMapStatusChange(MapStatus mapStatus) {
			if(lastZoom == mapStatus.zoom){
				return;
			}
			lastZoom = mapStatus.zoom;
			resetOverlay(mapStatus);
			Log.e("onMapStatusChange", mapStatus.toString());	
		}
	};
	
	private boolean isFirstLoc = true;	
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null){
				return;
			}
			
			@SuppressWarnings("unused")
			float zoom = baiduMap.getMapStatus().zoom;

			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			baiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				if(BDLocation.TypeCacheLocation == location.getLocType() || BDLocation.TypeGpsLocation == location.getLocType()|| BDLocation.TypeNetWorkLocation == location.getLocType() || BDLocation.TypeOffLineLocation == location.getLocType()){
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
					baiduMap.animateMapStatus(u);
					
					getSP().setFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT, (float)location.getLatitude());
					getSP().setFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG, (float)location.getLongitude());
				}else {
					if(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT) > 0){
						isFirstLoc = false;
						LatLng ll = new LatLng(getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT), getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG));
						MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll ,16);
						baiduMap.animateMapStatus(u);
					}else {
						//Toast.makeText(MainActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}//end onReceiveLocation
		public void onReceivePoi(BDLocation poiLocation) { }
	}//end MyLocationListenner class
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null){
			return;
		}
	}//end onActivityResult
	
	@Override
	protected void onDestroy(){
		// 取消监听 SDK 广播
		unregisterReceiver(mSDKReceiver);
		
		mLocationClient.stop();
		baiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		
		view_tab.clearItem();
		
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (service != null)
			stopService(service);
		super.onDestroy();
	}//end onDestroy
	
	@Override
	protected void onResume(){
		mMapView.onResume();
		super.onResume();
		if (getSP().getBooleanValue(SharedPreferenceUtil.HAS_NEW_MESSAGE, false))
			iv_icon_info.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_info_red));
		else
			iv_icon_info.setImageDrawable(getResources().getDrawable(R.drawable.icon_main_info_black));
	}
	
	@Override
	protected void onPause(){
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public void notifyUpdata(int index) {
		EventUtil.KINDOF = OverlayType.ALL.getType().equals(view_tab.getSelectedText()) ? "" : view_tab.getSelectedText();
		showProgressDialog(false);
		getPosData();
	}
	
	
	/** 获取地图数据 **/
	private void getPosData(){
		double latitude = getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLAT);
		double longitude = getSP().getFloatValue(SharedPreferenceUtil.LASTLOCATIONLONG);
		LatLng ll = mMapView.getMap().getMapStatus().bound.getCenter();
		if (ll != null && ll.latitude != 0 && ll.longitude != 0) {
			latitude = ll.latitude;
			longitude = ll.longitude;
		}
		EventUtil.searchEvents(EventUtil.Type.nearest, longitude + "", latitude + "", 1, new EntityListCallback<EventEntity>(){
				@Override
				public void succeed(List<EventEntity> list) {
					dimissProgressDialog();
					arrayList_overlaytEntity.clear();
		            arrayList_overlaytEntity.addAll(OverlayEntityList.fromEventList(list));
		            resetOverlay(null);
				}

				@Override
				public void fail(String error) {
					dimissProgressDialog();
					toastShort(error);
				}
			
		});
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra(EXTRA_PARAM)) {
			String event_id = intent.getStringExtra(EXTRA_PARAM);
			locateEvent(event_id);
		}
	}
	
	private void locateEvent(String event_id) {
		showProgressDialog(false);
		EventUtil.getEventInfo(event_id, new EntityCallback<EventEntity>() {
			
			@Override
			public void succeed(EventEntity entity) {
				dimissProgressDialog();
				Marker marker = getMarker(entity.id);
				if (marker == null)
					marker = setZoomOverlayData(OverlayEntityList.fromEvent(entity));
				clickMarker(marker);
			}
			
			@Override
			public void fail(String error) {
				toastShort(error);
				dimissProgressDialog();
			}
		});
	}
	
	private Marker getMarker(String event_id) {
		for (Marker marker : hashMap_overlayMarket.keySet()) {
			OverlayEntity overlayEntity = hashMap_overlayMarket.get(marker);
			if (overlayEntity.actionid.equals(event_id))
				return marker;
		}
		return null;
	}
	
	private boolean clickMarker(Marker marker) {
		final OverlayEntity overlayEntity = hashMap_overlayMarket.get(marker);
		if(overlayEntity == null){
			Log.e(TAG, "overlayt is null");
			return false;
		}
		if(!overlayEntity.isIcon){
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, OverlayBigActivity.class);
			intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
			startActivity(intent);
			return true;
		}
		ViewMapOverlay viewMapOverlay = new ViewMapOverlay(getApplicationContext());
		viewMapOverlay.setOverlayEntity(overlayEntity);
		viewMapOverlay.setOnClickListener(new View.OnClickListener() {					
			@Override
			public void onClick(View v) {
				baiduMap.hideInfoWindow();
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, OverlayBigActivity.class);
				intent.putExtra(BaseActivity.EXTRA_PARAM, overlayEntity);
				startActivity(intent);
				
			}
		});
		LatLng ll = marker.getPosition();
		InfoWindow mInfoWindow = new InfoWindow(viewMapOverlay, ll, 0);
		baiduMap.showInfoWindow(mInfoWindow);
		MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(ll.latitude, ll.longitude))
				.zoom(baiduMap.getMapStatus().zoom).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        baiduMap.setMapStatus(mMapStatusUpdate);
		return true;
	}
	
	private long exitTime;
	private int quit_idle = 2000;
	
	@Override
	public void onBackPressed() {
		boolean quit = true;
		if (System.currentTimeMillis() - exitTime > quit_idle) {
			toastShort("再按一次退出图丁");
			exitTime = System.currentTimeMillis();
			quit = false;
		}
		if (quit)
			finish();
	}
}
