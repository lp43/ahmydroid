package com.funtrigger.ahmydroid;

import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ahmyphone);
		button_how=(Button) findViewById(R.id.button_how);
		imgfall=(ImageView) findViewById(R.id.fall);
		imgfall.setVisibility(View.VISIBLE);
		imgfall.setBackgroundResource(R.anim.falling_animation);
		aniimg=(AnimationDrawable) imgfall.getBackground();
		
		button_how.setVisibility(View.INVISIBLE);
		
		sensormanager=(SensorManager) this.getSystemService(SENSOR_SERVICE);	
		List<Sensor> list=sensormanager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		sensormanager.registerListener(Fallen.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);	
	
	}
	@Override
	protected void onPause() {
		finish();
		mp.release();
		aniimg.stop();
		super.onPause();
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
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
				mp.release();
				mpplaying=false;
			}
			
		});
		mp.setOnErrorListener(new OnErrorListener(){

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.release();
				return false;
			}
			
		});
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
