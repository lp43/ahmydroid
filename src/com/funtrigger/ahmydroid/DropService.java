package com.funtrigger.ahmydroid;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;

public class DropService extends Service implements SensorEventListener{

	private String tag="tag";
	
	NotificationManager mNotificationManager;
	Notification notification;
    private SensorManager sensormanager;
    WakeLock wakeLock;
    PowerManager pm;
    
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(tag,"into DropService.onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag,"into DropService.onCreate");
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

		if(sensormanager!=null){
			sensormanager.unregisterListener(this);
		}
		
		mNotificationManager.cancelAll();
		if(wakeLock!=null){
			wakeLock.release();
		}
		
			
		super.onDestroy();
	}

	
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
		Log.i(tag, "DropService.onSensorChanged lintening");
		
//		acquireWakeLock();	
		
		float[] buf=event.values;
		
		float x=buf[0];
		float y=buf[1];
		float z=buf[2];
		int a=(int) Math.abs(x);
		int b=(int) Math.abs(y);
		int c=(int) Math.abs(z);
		if(a>15||b>15||c>15){
			Log.i(tag, ">15");
			
		
			
			
			
			Intent intent = new Intent();
			intent.setClass(this, Fallen.class);

			intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
}
