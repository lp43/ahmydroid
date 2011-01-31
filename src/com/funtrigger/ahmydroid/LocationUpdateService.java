package com.funtrigger.ahmydroid;

import com.facebook.android.R;
import com.funtrigger.tools.InternetInspector;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;
import com.funtrigger.tools.MyTime;
import com.funtrigger.tools.SwitchService;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

	static LocationManager lm/*_gps,lm_agps*/;
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

	NotificationManager mNotificationManager;
	Notification notification;
	/**
	 * 最後經緯度更新時間
	 */
	private static String updated_time="";
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "LocationUpdateService.onCreate");
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		
		//privous_location_setting用來儲存使用者本來倒底有沒有想要開小安定位
		//因為程式GPS開關的location的勾選與否，會被程式改來改去
		//需要有另一個空間來存放使用者倒底是不是想開定位
		if(MySharedPreferences.getPreference(this, "previous_location_setting", false)==true){
			Log.i(tag, "into 55");

			if(getMyProvider()==null){
				stopSelf();
				Log.i(tag, "into 59");
			}else if(getMyProvider().equals("network")){
				
				if(InternetInspector.InternetOrNot(this)==false){
					Log.i(tag, "into 63");
					stopSelf();
					tellAhmydroidNoLocation();
					
				}else{
					Log.i(tag, "into 68");
					updateLocation(LocationManager.NETWORK_PROVIDER);
					if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
						Ahmydroid.setAndroid_Machine();
						
					}
					
				}	
				
			}else if(getMyProvider().equals("gps")){
				
				Log.i(tag, "into 78");
				updateLocation(LocationManager.GPS_PROVIDER);
				if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
					Ahmydroid.setAndroid_Machine();
				}		
			}	
			
		}else{
			stopSelf();
			Log.i(tag, "into 87");
			if(MySystemManager.checkTaskExist(this, ".Ahmydroid")==true){
				Ahmydroid.stopElectricToStart();
			}
				
		}
		
		
		
		super.onCreate();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(tag, "into LocationUpdateService.onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		try{
			lm.removeUpdates(this);
			Log.i(tag, "remove locationmanager : success!");
			//底下這行如果加了，主畫面關掉定位小安時，下次就會開不了了
//			MySharedPreferences.addPreference(this, "location", false);
		}catch(NullPointerException e){
			Log.i(tag, "NullPointerException: "+e.getMessage());
		}
		
		Log.i(tag, "LocationUpdateService.onDestroy");
		Toast.makeText(this, getString(R.string.stop_location_update), Toast.LENGTH_SHORT).show();
		
		
		//==============關閉Notify欄視窗
		//TODO 小米測到這裡出問題
		try{
			mNotificationManager.cancelAll();
			 stopForeground(true);
		}catch(NullPointerException e){
			Log.i(tag, "NullPointerException: "+e.getMessage());
		}
			 		
		 
		//============================
	
		
		super.onDestroy();
	}

	/**
	 * 更新地理座標函式
	 */
	private void updateLocation(String provider){
		Toast.makeText(this, getString(R.string.start_location_update), Toast.LENGTH_SHORT).show();
		
		//====================================================
		//以下的程式不能寫在onStart(),因為被TaskManager清掉後，會自動onCreate()
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		notification = new Notification(R.drawable.location_icon,getString(R.string.location_name_ing), System.currentTimeMillis());
		
		PendingIntent pIntent = PendingIntent.getActivity(this,0,new Intent(this, Settings.class),PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,getString(R.string.location_name),getString(R.string.location_name_ing),pIntent);	
		
		if(notification!=null){
			Log.i(tag, "notification!=null");
			startForeground(R.string.location_name,notification);//將Service強制在前景執行
		}
		mNotificationManager.notify(R.string.location_name,notification);
		//============================================================
		
		
		
		lm.requestLocationUpdates(provider, 10000, 5,  this);

		Log.i("tag", "into 121");

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
			if(lm==null)lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
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
	public static String getRecordLocation(Context context){

//			if(latitude==0.0 & longitude==0.0){
//				Log.i(tag, "into latitude==0.0, getLastKnownLocation");
//				latitude=lm.getLastKnownLocation(getMyBestProvider()).getLatitude();
//				longitude=lm.getLastKnownLocation(getMyBestProvider()).getLongitude();
//				
//			}
		
		return latitude==0.0?context.getString(R.string.no_geolocation_data):latitude+","+longitude;
		
	}
	
	/**
	 * 取得經緯度最後更新的時間
	 */
	public static String getLastUpdatedTime(Context context){
		Log.i(tag, "updated_time.equals(): " + String.valueOf(updated_time.equals("")));
		return updated_time.equals("")?context.getString(R.string.no_geolocation_data):updated_time;
		
	}
	 
	/**
	 * 告訴Ahmydroid沒有辦法定位
	 */
	private void tellAhmydroidNoLocation(){
		
		if(MySystemManager.checkTaskExist(LocationUpdateService.this, ".Ahmydroid")==true){
			Ahmydroid.closeDropProtection();
			Ahmydroid.tellUserCantGetLocation();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
//		Toast.makeText(SetNotify.this,"LocationListener onLocationChanged" , Toast.LENGTH_SHORT).show();

		latitude = location.getLatitude();//查詢緯度
		longitude=location.getLongitude();//查詢經度,並存進經度
		updated_time=MyTime.getHHMMSS1();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(tag, "LocationListener onProviderDisabled");
		resetLocation();
		
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderDisabled" , Toast.LENGTH_SHORT).show();
		
			
			stopSelf();
		
			MySharedPreferences.addPreference(LocationUpdateService.this, "location", false);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(tag, "LocationListener onProviderEnabled");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderEnabled" , Toast.LENGTH_SHORT).show();
		if(MySharedPreferences.getPreference(LocationUpdateService.this, "previous_location_setting", false)==true){
			SwitchService.startService(LocationUpdateService.this, LocationUpdateService.class);
			MySharedPreferences.addPreference(LocationUpdateService.this, "location", true);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(tag, "LocationListener onStatusChanged");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onStatusChanged" , Toast.LENGTH_SHORT).show();
		
	}


	
	
}
