package com.funtrigger.ahmydroid;

import java.util.Timer;
import java.util.TimerTask;

import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 該Service被啟動後，會運算時間
 * @author simon
 *
 */
public class TimeService extends Service {

	private String tag="tag";
	public static int timeCounter;
	Timer time;
	TimerTask timertask;
	/**
	 * 發射訊息的第1時間
	 */
	private static int startTime;

	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "TimeService.onCreate");
		startTime=Integer.valueOf(MySharedPreferences.getPreference(TimeService.this, "dispatcher_first_time", "15"));
		timeCounter=startTime;
		Log.i(tag, "startTime is: "+timeCounter +" later");
		
		
		//如果Service被創立之初，就發現沒有開啟定時通報
		//就把自己關掉
		//註︰這行不能刪，否則下次會從onStart()開始
		if(startTime==0){
			stopSelf();
		}
		
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(tag, "TimeService.onStart");
		
		if(timertask==null){
			timertask=new MyTimer();	
		}
		if(time==null){
			time=new Timer();
		}
		
			try{
				//如果沒有選擇持續發送的話,loopPeriod為0
				
					//讓程式每1秒做一次減1
					time.schedule(timertask, 1000, 1000);	
			}catch(IllegalArgumentException e){
				Log.i(tag, "IllegalArgumentException: "+e.getMessage());		
				stopSelf();
			}
		
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(tag, "TimeService.onDestroy");
		if(timertask!=null&&time!=null){
			timertask.cancel();
			time.cancel();
		}		
		
		super.onDestroy();
	}

	public static int getTimeCounter() {
		return timeCounter;
	}
	

	/**
	 * 當使用者點選了解鎖密碼時，讓使用者多10秒的密碼緩衝時間
	 * @param addTime 要讓時間計數器增加的秒數
	 */
	public static void setTimeCounter(int addTime) {
		timeCounter=addTime;
	}

	public class MyTimer extends TimerTask{

		@Override
		public void run() {
			Log.i(tag, "into MyTime run()");
			
			//每一次就將TimeCounter減1
			timeCounter--;
			Log.i(tag, "timeCounter remain: "+timeCounter+" s");

			if(timeCounter==0){
				Log.i(tag, "because timeCounter==startTime,");
				Log.i(tag, "start InfoDispatcher.Service");
				SwitchService.startService(TimeService.this, InfoDispatcher.class);
				//發送完就把自己關掉
				TimeService.this.stopSelf();
			}
			
		}
		
	}
}
