package com.funtrigger.ahmydroid;

import java.util.List;

import com.funtrigger.tools.MyTime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Debug;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;

/**
 * 手機掉落時的後臺Service，目的是開啟Gsensor來偵測使用者手機掉落，
 * 當掉落時，會開啟Fallen.java檔播放所有相關事件。如︰音效、震動、動畫…等
 * @author simon
 */
public class DropService extends Service implements SensorEventListener{

	private String tag="tag";
	
	NotificationManager mNotificationManager;
	Notification notification;
    /**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;
    /**
     * 該變數被用在告知系統，該類別啟動時，手機不能休眠，
     * 但是因為和我後來的使用方向不符，就沒用到了
     */
    WakeLock wakeLock;
	/**
	 * 電源管理變數，該變數被拿來偵測螢幕是否有亮
	 */
    PowerManager pm;
	/**
	 * 控制音樂播放的變數
	 */
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 用來調整音量Stream大小的啟始變數
	 */
	static AudioManager am;
	/**
	 * 取得時間的實體變數
	 */
	MyTime gettime;
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(tag,"into DropService.onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag,"into DropService.onCreate");
		
		Debug.startMethodTracing("methodtrace");
		
		
		acquireWakeLock();
		
		//以下的程式不能寫在onStart(),因為被TaskManager清掉後，會自動onCreate()
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		notification = new Notification(R.drawable.icon,getString(R.string.startfallprotect), System.currentTimeMillis());
		
		PendingIntent pIntent = PendingIntent.getActivity(this,0,new Intent(this, Ahmydroid.class),PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,getString(R.string.app_name),getString(R.string.startingfallprotect),pIntent);

		notification.defaults=Notification.DEFAULT_SOUND;
		long[] vibrate = {0,100,200,300};
		notification.vibrate = vibrate;
		mNotificationManager.notify(0,notification);
		startForeground(0,notification);
		
		sensormanager=(SensorManager) this.getSystemService(SENSOR_SERVICE);
		List<Sensor> list=sensormanager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		sensormanager.registerListener(DropService.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);
	
		
		
		super.onCreate();
	}


	@Override
	public void onDestroy() {
		Log.i(tag,"into DropService.onDestroy");
		Debug.stopMethodTracing();
		if(sensormanager!=null){
			sensormanager.unregisterListener(this);
		}		
		mNotificationManager.cancelAll();
		if(wakeLock.isHeld()){
			wakeLock.release();
			Log.i(tag, "wakeLock release");
			wakeLock=null;
		}
		super.onDestroy();
	}

	/**
	 * 這段Method最主要拿來當Service打開時，螢幕會一直恆亮
	 * 
	 */
	private void acquireWakeLock() {
        if (wakeLock == null) {
               Log.i(tag,"Acquiring wake lock");
               Log.i(tag, "canonical: "+this.getClass().getCanonicalName());
               pm = (PowerManager) getSystemService(POWER_SERVICE);
               wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                       PowerManager.ACQUIRE_CAUSES_WAKEUP |
                       PowerManager.ON_AFTER_RELEASE, this.getClass().getCanonicalName());
               wakeLock.acquire();
           }
       
   }
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		gettime=new MyTime();
		Log.i(tag, gettime.getHHMMSS()+" DropService.onSensorChanged listening");
		
		float[] buf=event.values;
		
		float x=buf[0];
		float y=buf[1];
		float z=buf[2];
		int a=(int) Math.abs(x);
		int b=(int) Math.abs(y);
		int c=(int) Math.abs(z);
		if(a>15||b>15||c>15){
			
			
			pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			Log.i(tag, "power screen on? "+pm.isScreenOn());
			//針測到螢幕是關的，才需要先播放一聲音效告知使用者
			if(pm.isScreenOn()==false){
				//先叫一聲，表示Gsensor有針測到掉下去，免得使用者以為沒有產生事件
				if(mpplaying==false){
					Log.i(tag, "into mp player");			
					am=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, Fallen.setVolumn, 0);
					mp=MediaPlayer.create(this, this.getResources().getIdentifier("dizzy", "raw", this.getPackageName()));
					
					mp.start();
					mpplaying=true;
				
				mp.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer arg0) {
//						Log.i(tag, "into onCompletion");
						if(mp!=null){
							mp.release();					
						}		
						mpplaying=false;
					}
					
				});
				mp.setOnErrorListener(new OnErrorListener(){

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						if(mp!=null){
							mp.release();
						}
						return false;
					}
					
				});
				}
			}
			
			Log.i(tag, ">15");		
			
			Intent intent = new Intent();
			intent.setClass(this, Fallen.class);
			//如果沒有在新的Task開啟程式，會Error
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}

}
