package com.funtrigger.ahmydroid;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.funtrigger.tools.MyDispatcher;
import com.funtrigger.tools.MyLocation;
import com.funtrigger.tools.MySharedPreferences;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class InfoDispatcher extends Service {
	boolean message_status,facebook_status;
	private String tag="tag";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "InfoDispatcher.onCreate");
		super.onCreate();
		MyLocation.getLocation(this);
		checkSettingStatus();
		dispatcher();
		stopSelf();
	}
	
	/**
	 * 檢查各種掉落通知的設定值
	 */
	public void checkSettingStatus(){
		message_status=MySharedPreferences.getPreference(this, "message", false);
		Log.i(tag, "message_status:"+ String.valueOf(MySharedPreferences.getPreference(this, "message", false)));
		facebook_status=MySharedPreferences.getPreference(this, "facebook", false);
		Log.i(tag, "facebook_status:"+ String.valueOf(MySharedPreferences.getPreference(this, "facebook", false)));
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
		if(message_status==true){
			Log.i(tag, "message_dispatch");
			MyDispatcher.messageDispatcher(this);
		}
		if(facebook_status==true){
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
