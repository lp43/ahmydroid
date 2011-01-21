package com.funtrigger.ahmydroid;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.funtrigger.tools.MyDispatcher;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class InfoDispatcher extends Service {
//	boolean message_status,facebook_status;
	public static final int Messasge=0;
	public static final int Facebook=1; 
	private static String tag="tag";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "InfoDispatcher.onCreate");
		super.onCreate();

		dispatcher();
		stopSelf();
	}
	
	/**
	 * 檢查各種掉落通知的設定值
	 * @return boolean陣列，第1個位置放message的狀態，
	 * 第2個位置放facebook的狀態
	 */
	public static Boolean[] checkSettingStatus(Context context){
//		message_status=MySharedPreferences.getPreference(this, "message", false);
//		Log.i(tag, "message_status:"+ String.valueOf(MySharedPreferences.getPreference(this, "message", false)));
//		facebook_status=MySharedPreferences.getPreference(this, "facebook", false);
//		Log.i(tag, "facebook_status:"+ String.valueOf(MySharedPreferences.getPreference(this, "facebook", false)));
	
		return new Boolean[]{MySharedPreferences.getPreference(context, "message", false),
				MySharedPreferences.getPreference(context, "facebook", false)};
	}
	
	@Override
	public void onDestroy() {
		Log.i(tag, "InfoDispatcher.onDestroy");
		super.onDestroy();
	}

	/**
	 * 判斷並啟動各種發送訊息
	 */
	public void dispatcher(){
//		if(message_status==true){
		if(checkSettingStatus(this)[Messasge]==true){
			Log.i(tag, "message_dispatch");
			MyDispatcher.messageDispatcher(this);
		}
//		if(facebook_status==true){
		if(checkSettingStatus(this)[Facebook]==true){
			Log.i(tag, "facebook_dispatch");
			/*發送要寄Facebook訊息的廣播
			  註︰本來直接寄出廣播即可，但因為Facebook類別需要Activity參數
			  只好將寄送Facebook的動作放到(Activity)Fallen.class去做*/
			Intent intent = new Intent();
			intent.setAction("FACEBOOKDISPATCHER");
			intent.putExtra("filter", "sendBroadcastFrom: SFACEBOOKDISPATCHER");
			this.sendBroadcast(intent);
		}
	}
	
	
}
