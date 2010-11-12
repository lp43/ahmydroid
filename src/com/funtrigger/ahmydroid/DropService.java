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
import android.util.Log;

public class DropService extends Service implements SensorEventListener{

	private String tag="tag";
	
	NotificationManager mNotificationManager;
    private SensorManager sensormanager;
    
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(tag,"into DropService.onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag,"into DropService.onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(tag,"into DropService.onStart");

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Notification notification = new Notification(R.drawable.icon,getString(R.string.startfallprotect), System.currentTimeMillis());
		
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
	
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		Log.i(tag,"into DropService.onDestroy");
		sensormanager.unregisterListener(this);
		mNotificationManager.cancelAll();
		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.i(tag, "service fall test");
		float[] buf=event.values;
		float x=buf[1];
		int a=(int) Math.abs(x);
		
		if(a>15){
			Log.i(tag, "a>15");
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
