package com.funtrigger.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.List;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.DropService;
import com.funtrigger.ahmydroid.Fallen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;


/**
 * 該類別管理各種類別的Sensor感測器
 * @author simon
 */
public class MySensor implements SensorEventListener{


	/**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;
	/**
	 * 取得時間的實體變數
	 */
	MyTime gettime;
	private String tag="tag";

	private Context context;
	
	
	/**
	 * 啟動Sensor
	 * @param context 傳入取得Sensor的主體
	 * @param sensorType 欲啟動Sensor的類型
	 * @param sensorRate 欲啟動Sensor的感測度
	 */
	public void startSensor(Context getContext,int sensorType, int sensorRate){
		context=getContext;
		sensormanager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list=sensormanager.getSensorList(sensorType);
		
		sensormanager.registerListener(MySensor.this,list.get(0), sensorRate);
		if(Ahmydroid.times==0){
			Ahmydroid.times++;
		}else if(Ahmydroid.times==1){
			Ahmydroid.times--;
		}
	}
	
	
	public void stopSensor(){
		if(sensormanager!=null){
			sensormanager.unregisterListener(this);
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
		
		//如果是3面向都大於15，發送廣播標籤︰STARTFALLEN，目的是開啟Fallen事件
		if(a>15||b>15||c>15){
	/*	if(Ahmydroid.times==0){
			MyLog.writeLogToTXT(a,b,c);
	
		}else if(Ahmydroid.times==1){
			MyLog.writeLogToTXT2(a,b,c);
			
		}*/
		
		//撞到地板才產生摔落數據
//		if(a<2&&b<2&&c<2){
			if(Ahmydroid.times==0){

				WriteToTXT.writeLogToTXT("a"+String.valueOf(a)+", b"+String.valueOf(b)+", c"+String.valueOf(c));
			}else if(Ahmydroid.times==1){

				WriteToTXT.writeLogToTXT("a"+String.valueOf(a)+", b"+String.valueOf(b)+", c"+String.valueOf(c));
			}
			
			Intent intent=new Intent();
			intent.setAction("STARTFALLEN");
			intent.putExtra("filter", "sendBroadcastFrom: STARTFALLEN");
			context.sendBroadcast(intent);
			Log.i(tag, "a,b,c<2");
		}else{
			//否則就是發送廣播標籤︰FALLENSENSORCHANGED，目的是Fallen裡不斷播動畫和音效
			Intent intent2=new Intent();
			intent2.setAction("FALLENSENSORCHANGED");
			intent2.putExtra("filter", "sendBroadcastFrom: FALLENSENSORCHANGED");
			context.sendBroadcast(intent2);
//			Log.i(tag, "sensor move");
		}
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
}
