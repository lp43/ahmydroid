package com.funtrigger.ahmydroid;

import java.util.List;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Fallen extends Activity implements SensorEventListener{
	/**
	 * 機器人圖案的ImageView
	 */
	private ImageView imgfall;
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 機器人暈眩的動畫變數
	 */
	private AnimationDrawable aniimg;
    private final String tag="tag";
    private SensorManager sensormanager;
	private Button button_how;
	private ImageButton img_btn;
	PowerManager pm;
	List<Sensor> list;
	/**
	 * 用來調整音量Stream大小的啟始變數
	 */
	static AudioManager am;
	Vibrator myVibrator;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "into Fallen.onCreate()");
		super.onCreate(savedInstanceState);
		
		
		stopService();//進來時把Service關掉，避免重覆進入本畫面
		
		
		//檢驗是否為螢幕鎖
		KeyguardManager km = (KeyguardManager) this.getSystemService(
                Context.KEYGUARD_SERVICE);
		Log.i(tag, "keylock? "+km.inKeyguardRestrictedInputMode());
		
		 
		if(km.inKeyguardRestrictedInputMode()==true){
			//如果有螢幕鎖時要做的事
			Log.i(tag, "into inKeyguardRestrictedInputMode()==true");
		}else{
			//沒有進入螢幕鎖時，正常啟動程式
			Log.i(tag, "into inKeyguardRestrictedInputMode()==false");
		}
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		Log.i(tag, "power screen on? "+pm.isScreenOn());
	

				final Window win = getWindow();
				 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
			                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
		                   /* | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON*/);

	

		 
		setContentView(R.layout.ahmyphone);
		Log.i(tag, "setContentView finish");
		

		
		button_how=(Button) findViewById(R.id.button_how);
		img_btn=(ImageButton) findViewById(R.id.img_btn);
		imgfall=(ImageView) findViewById(R.id.fall);
		img_btn.setVisibility(View.INVISIBLE);
		imgfall.setVisibility(View.VISIBLE);
		button_how.setVisibility(View.INVISIBLE);
		imgfall.setBackgroundResource(R.anim.falling_animation);
		aniimg=(AnimationDrawable) imgfall.getBackground();			
		
		am=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		sensormanager=(SensorManager) getSystemService(SENSOR_SERVICE);
		list=sensormanager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		
		
		
		new Thread(){
			public void run(){
				while(pm.isScreenOn()==false){
					Log.i(tag, "into isScreenOn==false");
					while(pm.isScreenOn()==true){
						Log.i(tag, "into isScreenOn==true");
						break;
					}
				}
			}
		}.start();
		
		Handler hanler =new Handler();
		hanler.post(new Runnable(){

			@Override
			public void run() {
				boolean sensormanager_register =sensormanager.registerListener(Fallen.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);	
//				Log.i(tag, "sensormanager_register "+sensormanager_register);		
			}});
		
		//將媒體音量調整到最大，好讓使用者聽見哀嚎聲
		am.setStreamVolume(AudioManager.STREAM_MUSIC,15, 0);
	}

	@Override
	protected void onPause() {
		Log.i(tag, "into Fallen.onPause()");
		if(pm.isScreenOn()==true){
			if(mp!=null){
				mp.release();//釋放掉音樂資源
			}
			
			aniimg.stop();//動畫關掉
			myVibrator.cancel();//震動關掉
			startService();//離開時再把Service開回去
			
			if(sensormanager!=null){
				sensormanager.unregisterListener(this);
				Log.i(tag, "sensormanager.unregisterListener");
				
			}
			finish();

			Log.i(tag, "finish onPause()");
		}
		
		super.onPause();
	}
	

	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.i(tag,"Fallen.onSensorChanged()");
		while(aniimg.isRunning()==false){
			aniimg.start();
		}
		
		if(mpplaying==false){
			Log.i(tag, "into mp player");
			mp=MediaPlayer.create(this, this.getResources().getIdentifier("dizzy", "raw", this.getPackageName()));
			mp.start();
			mpplaying=true;

		
		mp.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer arg0) {
//				Log.i(tag, "into onCompletion");
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
		
		//取得震動服務
		myVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		myVibrator.vibrate(1000);//震動1000秒
		
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub	
	}
	
	/**
	 * 啟動摔落告知Service
	 */
	private void startService(){
		Intent intent = new Intent();
		intent.setClass(this,DropService.class);
		this.startService(intent);
	}
	
	/**
	 * 停止摔落告知Service
	 */
	private void stopService(){
		Intent intent = new Intent();
		intent.setClass(this, DropService.class);
		stopService(intent);
	}
}
