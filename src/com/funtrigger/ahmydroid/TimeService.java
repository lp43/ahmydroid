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
	private int timeCounter;
	Timer time;
	TimerTask timertask;
	/**
	 * 發射訊息的第1時間
	 */
	public static int startTime;
	/**
	 * 發射訊息的持續間隔
	 */
	public static int loopPeriod=0;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "TimeService.onCreate");
		startTime=Integer.valueOf(MySharedPreferences.getPreference(TimeService.this, "dispatcher_first_time", "0"));
		Log.i(tag, "startTime is: "+startTime);
		timeCounter=startTime;
		
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
		
		timertask=new MyTimer();
		time=new Timer();
		
			try{
				
				if(loopPeriod==0){
					time.schedule(timertask, startTime);
				}else{
					time.schedule(timertask, startTime, loopPeriod);
				}
						
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

	public int getTimeCounter() {
		return timeCounter;
	}

	public class MyTimer extends TimerTask{

		@Override
		public void run() {
			Log.i(tag, "into MyTime run()");
			
			SwitchService.startService(TimeService.this, InfoDispatcher.class);
			
			timeCounter+=loopPeriod;
			Log.i(tag, "timeCounter: "+timeCounter);
			
		}
		
	}
}