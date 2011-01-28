package com.funtrigger.ahmydroid;

import com.funtrigger.tools.InternetInspector;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * 負責監聽3G是否有開啟
 * @author simon
 *
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

	private String tag="tag";

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i(tag, "previous_location_setting: "+MySharedPreferences.getPreference(context, "previous_location_setting", false));
		
		 if(MySharedPreferences.getPreference(context, "previous_location_setting", false)==true){
			 
		 
		
			if(InternetInspector.InternetOrNot(context)==true
					&&	LocationUpdateService.getMyBestProvider(context)!=null 
					&& LocationUpdateService.getMyBestProvider(context).equals("network")){
	//			 Toast.makeText( context, "Network Type : " + InternetInspector.checkInternet(context), Toast.LENGTH_SHORT ).show();
	
					 SwitchService.startService(context, LocationUpdateService.class);
					 MySharedPreferences.addPreference(context, "location", true);
				 
			}else{
	//			Toast.makeText( context, "closed network", Toast.LENGTH_SHORT ).show();
				if(LocationUpdateService.getMyBestProvider(context)!=null 
					&& LocationUpdateService.getMyBestProvider(context).equals("network")){
					
					
					 
					SwitchService.stopService(context, LocationUpdateService.class);
					MySharedPreferences.addPreference(context, "location", false);
					
					
				}
				
			}

		 }
	 }


}
