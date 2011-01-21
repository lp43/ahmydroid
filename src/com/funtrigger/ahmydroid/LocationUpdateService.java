package com.funtrigger.ahmydroid;

import com.funtrigger.tools.InternetInspector;
import com.funtrigger.tools.MySystemManager;
import com.funtrigger.tools.SwitchService;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 該類別被拿來專門在背景更新geographic location
 * @author simon
 *
 */
public class LocationUpdateService extends Service implements LocationListener{

	static LocationManager lm;
	static ClipboardManager cbm;
	private static String tag="tag";
	/**
	 * longitude經度
	 */
	static double longitude=0.0;
	/**
	 * latitude緯度
	 */
	static double latitude=0.0;

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "LocationUpdateService.onCreate");
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		if(InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Messasge]==true
				|InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Facebook]==true){
			if(getMyProvider()==null){
				stopSelf();
				tellAhmydroidNoLocation();
			}else if(getMyProvider().equals("GPS")){
				updateLocation(LocationManager.GPS_PROVIDER);
				if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
					Ahmydroid.setAndroid_Machine();
				}
				
			}else if(getMyProvider().equals("network")){
				if(!InternetInspector.checkInternet(LocationUpdateService.this).equals("mobile")){
					stopSelf();
					tellAhmydroidNoLocation();
					
					
				}else{
					updateLocation(LocationManager.NETWORK_PROVIDER);
					if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
						Ahmydroid.setAndroid_Machine();
					}
					
				}
			}
			
		}else{
			stopSelf();
		}
		
		
//		if(getMyProvider()==null){
//			stopSelf();
//			tellAhmydroidNoLocation();
//			//如果是開GPS，然後也有開啟發送簡訊或Facebook，才需要開啟updateLocation
//		}else if(getMyProvider().equals("GPS")){
//			if(InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Messasge]==true
//					|InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Facebook]==true){
//				updateLocation(LocationManager.GPS_PROVIDER);
//			}else{
//				stopSelf();
//			}
//			
//		}else if(getMyProvider().equals("network")){
//			if(!InternetInspector.checkInternet(LocationUpdateService.this).equals("mobile")){
//				stopSelf();
//				tellAhmydroidNoLocation();
//			}else{
//				if(InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Messasge]==true
//						|InfoDispatcher.checkSettingStatus(this)[InfoDispatcher.Facebook]==true){
//					updateLocation(LocationManager.NETWORK_PROVIDER);
//				}else{
//					stopSelf();
//				}
//				
//			}
//	
//		}else{
//			stopSelf();
//		}
		
		
		super.onCreate();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(tag, "into LocationUpdateService.onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		lm.removeUpdates(this);
		Log.i(tag, "LocationUpdateService.onDestroy");
		super.onDestroy();
	}

	/**
	 * 更新地理座標函式
	 */
	private void updateLocation(String provider){
		
			lm.requestLocationUpdates(provider, 10000, 5,  this);
		

//    	Toast.makeText(context,"Location: "+latitude+","+longitude , Toast.LENGTH_SHORT).show();
//    	Log.i(tag,latitude+","+longitude);//顯示緯度+經度以配合Google Map格式
	
	}
	/**
	 * 清除Location資料
	 */
	public void resetLocation(){
			
			latitude=0.0;
			longitude=0.0;
	}
	
	/**
	 * 讓程式去選取最好的定位方式標準<br/>
	 */
	private static Criteria getBestCriteria(){
		Criteria criteria= new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度 
		criteria.setAltitudeRequired(false);//不要求海拔信息 
		criteria.setBearingRequired(false);//不要求方位信息 
		criteria.setCostAllowed(true);//是否允许付费 
		criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

		return criteria;
	}
	
	/**
	 * 該函式是提供給對外的程式，取得依據Criteria所得到的最好的Provider
	 * @return String : Best Provider
	 */
	public static String getMyBestProvider(Context context){
		String return_value=null;
		
		try{
			lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			Log.i(tag, "Best Provider: "+lm.getBestProvider(getBestCriteria(), true));
			
			return_value=lm.getBestProvider(getBestCriteria(), true);
		}catch(IllegalArgumentException e){
			Log.i(tag, "LocationUpdateService119: IllegalArgumentException: "+e.getMessage());
		}catch(NullPointerException e){
			Log.i(tag, "LocationUpdateService122: NullPointerException: "+e.getMessage());	
		}
		
//		Toast.makeText(LocationUpdateService.this, "bestProvider is: "+lm.getBestProvider(getBestCriteria(), true), Toast.LENGTH_SHORT).show();
		return return_value;
		
	}
	
	
	
	/**
	 * 取得依據Criteria所得到的最好的Provider
	 * @return String : Best Provider
	 */
	private String getMyProvider(){
		String return_value=null;
		
		try{
			
			Log.i(tag, "Best Provider: "+lm.getBestProvider(getBestCriteria(), true));
			
			return_value=lm.getBestProvider(getBestCriteria(), true);
		}catch(IllegalArgumentException e){
			Log.i(tag, "LocationUpdateService119: IllegalArgumentException: "+e.getMessage());
			
		}catch(NullPointerException e){
			Log.i(tag, "LocationUpdateService122: NullPointerException: "+e.getMessage());	
		}
		
//		Toast.makeText(LocationUpdateService.this, "bestProvider is: "+lm.getBestProvider(getBestCriteria(), true), Toast.LENGTH_SHORT).show();
		return return_value;
		
	}
	
	
	/**
	 * API文檔說︰如果要馬上取得經緯度，用getLastKnownLocation()
	 * @return 回傳String︰latitue,longitude
	 */
	public static String getRecordLocation(){

//			if(latitude==0.0 & longitude==0.0){
//				Log.i(tag, "into latitude==0.0, getLastKnownLocation");
//				latitude=lm.getLastKnownLocation(getMyBestProvider()).getLatitude();
//				longitude=lm.getLastKnownLocation(getMyBestProvider()).getLongitude();
//				
//			}
			
		
		//將緯經度貼到剪貼簿
//    	cbm=(ClipboardManager) LocationUpdateService.this.getSystemService(Context.CLIPBOARD_SERVICE);
//    	cbm.setText(latitude+","+longitude);
    	
		return latitude+","+longitude;
		
	}

	/**
	 * 告訴Ahmydroid沒有辦法定位
	 */
	private void tellAhmydroidNoLocation(){
		
		if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
			Ahmydroid.closeFallen();
			Ahmydroid.tellUserCantGetLocation();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
//		Toast.makeText(SetNotify.this,"LocationListener onLocationChanged" , Toast.LENGTH_SHORT).show();

		latitude = location.getLatitude();//查詢緯度
		longitude=location.getLongitude();//查詢經度,並存進經度

		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(tag, "LocationListener onProviderDisabled");
		resetLocation();
		
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderDisabled" , Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(tag, "LocationListener onProviderEnabled");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderEnabled" , Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(tag, "LocationListener onStatusChanged");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onStatusChanged" , Toast.LENGTH_SHORT).show();
		
	}


	
	
}
