package com.funtrigger.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

import com.funtrigger.ahmydroid.SetNotify;

/**
 * 該類別提供各式各樣請求地理座標的函式
 * @author simon
 */
public class MyLocation {
	private static String tag="tag";
	/**
	 * longitude經度,latitude緯度
	 */
	static double longitude;
	static double latitude;
	private static Location location;
	static SharedPreferences sharedata;
	
	/**
	 * 取得最後的地理座標函式
	 * @return 呼叫此函式時，會將經緯度傳回呼叫端,回傳格式為︰緯、經度以配合Google地圖
	 */
	public static String getLocation(Context context){
		LocationManager lm = (LocationManager)context.getSystemService(SetNotify.LOCATION_SERVICE);
		
		sharedata = context.getSharedPreferences("data", context.MODE_PRIVATE);  
        String recProvider = sharedata.getString("provider", "auto");  
		if(recProvider.equals("auto")){
			String bestProvider=lm.getBestProvider(MyLocation.getBestProvider(), true);
			Log.i(tag, "Best provider is: "+bestProvider);
			
			//這斷判斷是針對若取回來的最佳Provider為空，則強制指派Provider是AGPS
			if(bestProvider!=null){
				location = lm.getLastKnownLocation(bestProvider);
				
				Toast.makeText(context, "best provider is: "+bestProvider, Toast.LENGTH_SHORT).show();
			}else{
				//TODO 開啟對話框請使用者先啟動系統定位
			}

		}else if(recProvider.equals("gps")){
	    	location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//	    	Toast.makeText(context, "provider is gps", Toast.LENGTH_SHORT).show();
		}else if(recProvider.equals("network")){
			location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//			Toast.makeText(context, "provider is network", Toast.LENGTH_SHORT).show();
		}
		

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
//    	Toast.makeText(context,latitude+","+longitude , Toast.LENGTH_SHORT).show();
    	Log.i(tag,latitude+","+longitude);//顯示緯度+經度以配合Google Map格式
    	
    	//將緯經度貼到剪貼簿
    	ClipboardManager cbm=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    	cbm.setText(latitude+","+longitude);
    	
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
	
	/**
	 * 讓程式去選取最好的定位方式<br/>
	 */
	private static Criteria getBestProvider(){
		Criteria criteria= new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度 
		criteria.setAltitudeRequired(false);//不要求海拔信息 
		criteria.setBearingRequired(false);//不要求方位信息 
		criteria.setCostAllowed(true);//是否允许付费 
		criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求
		return criteria;
	}
}
