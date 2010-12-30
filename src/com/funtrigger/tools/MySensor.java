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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.FallDetector;
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
	 * 經測試發現如果是正常摔落的數據，
	 * 前面5組的x值不會有值超過9
	 * 這個集合用來存放反作用力[0,0,0]之前的前5組x數據
	 * 好讓程式能判斷︰是程式要的摔落狀態，
	 * 還是其它行為的摔落狀態
	 */
	ArrayList<String> arraylist;
/*	int x1,x2,x3,x4,x5;*/
	boolean startRecord=false;
	/**
	 * [0,0,0]後的檢查次數記錄，
	 * 系統會依程式片斷所寫的i總值，
	 * 去計算要去量測幾筆數值以判斷是否為「真摔」
	 */
	int i=0;
	/**
	 * 目前所設定的感測值靈敏度
	 */
	int sensitivity;
	/**
	 * 感測器靈敏度<br/>
	 * 0:最低 →需要4組相同重感數據才能啟動警報畫面
	 */
	final int LOW_SENSITIVITY=0;
	/**
	 * 感測器靈敏度<br/>
	 * 1:中等 →需要3組相同重感數據以啟動警報畫面
	 */	
	final int MODERATE_SENSITIVITY=1;
	/**
	 * 感測器靈敏度<br/>
	 * 2:最高 →只要2組相同重感數據就能啟動警報畫面
	 */
	final int HIGH_SENSITIVITY=2;
	
	
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
		arraylist=new ArrayList();
/*		*//**
		 * 存放啟動Fallen.java[0,0,0]的前5組數據
		 *//*
		int x1,x2,x3,x4,x5;*/
		
	}
	
	
	public void stopSensor(){
		if(sensormanager!=null){
			sensormanager.unregisterListener(this);
		}
	}

	private void sendBroadToFallen(Context context){
		Intent intent=new Intent();
		intent.setAction("STARTFALLEN");
		intent.putExtra("filter", "sendBroadcastFrom: STARTFALLEN");
		context.sendBroadcast(intent);
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		gettime=new MyTime();
		Log.i(tag, gettime.getHHMMSS()+" MySensor.onSensorChanged listening");
		
		sensitivity=MySharedPreferences.getPreference(context, "sensitivity", 0);
		
		float[] buf=event.values;
		
		float x=buf[0];
		float y=buf[1];
		float z=buf[2];
		int a=(int) Math.abs(x);
		int b=(int) Math.abs(y);
		int c=(int) Math.abs(z);
		
	    
		if(startRecord==true){
			WriteToTXT.writeLogToTXT("a"+String.valueOf(a)+", b"+String.valueOf(b)+", c"+String.valueOf(c));
			arraylist.add(String.valueOf(a)+String.valueOf(b)+String.valueOf(c));
			
			//如果使用者設成高感度，針測2組數據相同就啟動警報畫面
			//這組數據很容易啟動Fallen畫面
			if(sensitivity==HIGH_SENSITIVITY){
				Log.i(tag, "into sensitivity==HIGH_SENSITIVITY");
				if(arraylist.size()==2&i<6){
					Log.i(tag, "now array0= "+arraylist.get(0));
					Log.i(tag, "now array1= "+arraylist.get(1));
					Log.i(tag, "i="+i);
					
					if(arraylist.get(0).equals(arraylist.get(1))){
						Log.i(tag, "into arraylist0 == arraylist1");
						//如果達成力道則廣播以開啟Fallen.java
						sendBroadToFallen(context);
						
						startRecord=false;
						i=0;
					}else{
						Log.i(tag, "into ! arraylist.size()==2&i<6");
						arraylist.remove(0);
						i++;				
					}
			
				}else if(i>5){
					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					arraylist.clear();
					Log.i(tag, "now i is: "+i);
				}
			}else if(sensitivity==MODERATE_SENSITIVITY){
				Log.i(tag, "into sensitivity==MODERATE_SENSITIVITY");
				//如果感度被設為中感度
				if(arraylist.size()==3&i<8){
					Log.i(tag, "now array0= "+arraylist.get(0));
					Log.i(tag, "now array1= "+arraylist.get(1));
					Log.i(tag, "now array2= "+arraylist.get(2));
					Log.i(tag, "i="+i);
					
					if(arraylist.get(0).equals(arraylist.get(1))&&arraylist.get(1).equals(arraylist.get(2))){
						Log.i(tag, "into arraylist0 == arraylist1 == arraylist2");
						//如果達成力道則廣播以開啟Fallen.java
						sendBroadToFallen(context);
						
						startRecord=false;
						i=0;
					}else{
						Log.i(tag, "into ! arraylist.size()==3&i<8");
						arraylist.remove(0);
						i++;				
					}
			
				}else if(i>7){
					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					arraylist.clear();
					Log.i(tag, "now i is: "+i);
				}
			}else if(sensitivity==LOW_SENSITIVITY){
				Log.i(tag, "into sensitivity==LOW_SENSITIVITY");
				//如果感度被設為低感度，將會很難啟動畫面
				if(arraylist.size()==4&i<10){
					Log.i(tag, "now array0= "+arraylist.get(0));
					Log.i(tag, "now array1= "+arraylist.get(1));
					Log.i(tag, "now array2= "+arraylist.get(2));
					Log.i(tag, "now array3= "+arraylist.get(3));
					Log.i(tag, "i="+i);
					
					if(arraylist.get(0).equals(arraylist.get(1))&&arraylist.get(1).equals(arraylist.get(2))&&arraylist.get(2).equals(arraylist.get(3))){
						Log.i(tag, "into arraylist0 == arraylist1 == arraylist2 == arraylist3");
						//如果達成力道則廣播以開啟Fallen.java
						sendBroadToFallen(context);
						
						startRecord=false;
						i=0;
					}else{
						Log.i(tag, "into ! arraylist.size()==3&i<8");
						arraylist.remove(0);
						i++;				
					}
			
				}else if(i>9){
					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					arraylist.clear();
					Log.i(tag, "now i is: "+i);
				}
			}
			
		}
		
		
		
		
		//如果是3面向都大於15，發送廣播標籤︰STARTFALLEN，目的是開啟Fallen事件
//		if(a>15||b>15||c>15){
	/*	if(Ahmydroid.times==0){
			MyLog.writeLogToTXT(a,b,c);
	
		}else if(Ahmydroid.times==1){
			MyLog.writeLogToTXT2(a,b,c);
			
		}*/
		
		//撞到地板才產生摔落數據
		if(a<2&&b<2&&c<2){
			Log.i(tag, "into a,b,c<2");
			startRecord=true;
				WriteToTXT.writeLogToTXT("====== a"+String.valueOf(a)+", b"+String.valueOf(b)+", c"+String.valueOf(c)+"======");
				
//				if(x1<11&&x2<11&&x3<11&&x4<11&&x5<11){
//					Log.i(tag, "into x1,x2,x3,x4,x5 <11");
//					//如果達成力道則廣播以開啟Fallen.java
//					Intent intent=new Intent();
//					intent.setAction("STARTFALLEN");
//					intent.putExtra("filter", "sendBroadcastFrom: STARTFALLEN");
//					context.sendBroadcast(intent);
//
//				}

				
//			//如果達成力道則廣播以開啟Fallen.java
//			Intent intent=new Intent();
//			intent.setAction("STARTFALLEN");
//			intent.putExtra("filter", "sendBroadcastFrom: STARTFALLEN");
//			context.sendBroadcast(intent);
//			Log.i(tag, "a,b,c<2");
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
