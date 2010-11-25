package com.funtrigger.tools;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.funtrigger.ahmydroid.SetNotify;

public class MyLocation {
	private static String tag="tag";
	/**
	 * longitude經度,latitude緯度
	 */
	static double longitude;
	static double latitude;
	private static Location location;
	
	
	/**
	 * 取得最後的地理座標函式
	 * @return 呼叫此函式時，會將經緯度傳回呼叫端,回傳格式為︰緯、經度以配合Google地圖
	 */
	public static String getLocation(Context context){
		LocationManager lm = (LocationManager)context.getSystemService(SetNotify.LOCATION_SERVICE);
		
		location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		
		
//    	Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(location!=null){
    		Log.i(tag, "location distance: "+String.valueOf(location.distanceTo(location)));
    		latitude = location.getLatitude();//查詢緯度
    		longitude = location.getLongitude();//查詢經度
    	}else{
    		Log.i(tag, "location=null, start requestLocationUpdates");
//    		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10,  new LocationListener(){
    		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10,  new LocationListener(){
				

				@Override
				public void onLocationChanged(Location location) {
//					Toast.makeText(SetNotify.this,"LocationListener onLocationChanged" , Toast.LENGTH_SHORT).show();
					latitude = location.getLatitude();//查詢緯度
					longitude=location.getLongitude();//查詢經度,並存進經度
				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.i(tag, "LocationListener onProviderDisabled");
//					Toast.makeText(SetNotify.this,"LocationListener onProviderDisabled" , Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onProviderEnabled(String provider) {
					Log.i(tag, "LocationListener onProviderEnabled");
//					Toast.makeText(SetNotify.this,"LocationListener onProviderEnabled" , Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onStatusChanged(String provider,
						int status, Bundle extras) {
					Log.i(tag, "LocationListener onStatusChanged");
//					Toast.makeText(SetNotify.this,"LocationListener onStatusChanged" , Toast.LENGTH_SHORT).show();
				}
    			
    		});
    	}
    	Toast.makeText(context,latitude+","+longitude , Toast.LENGTH_SHORT).show();
    	Log.i(tag,latitude+","+longitude);//顯示緯度+經度以配合Google Map格式
		return latitude+","+longitude;
	}
	
	/**
	 * 清除Location資料
	 */
	public static void resetLocation(){
		if(location!=null){
			location.reset();
		}
	}
}
