package com.funtrigger.ahmydroid;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class Ahmydroid extends Activity implements SensorEventListener{
	/**
	 * 記錄版本編號
	 */
	private String softVersion="v1.0.0.1";
	private Button button_how,button_exit;
    private SensorManager sensormanager;
    private final String tag="tag";
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 機器人圖案的ImageView
	 */
	private ImageView imgview,imgfall;
	/**
	 * 機器人暈眩的動畫變數
	 */
	private AnimationDrawable aniimg;

  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahmyphone);
        button_how=(Button) findViewById(R.id.button_how);
        button_exit=(Button) findViewById(R.id.button_exit);
        imgview=(ImageView) findViewById(R.id.img);
        imgfall=(ImageView) findViewById(R.id.fall);
        
        button_how.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				button_how.setVisibility(View.INVISIBLE);
				showDialog(0);
				
			}
        	
        });

        button_exit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
        	
        });
        
    }
	@Override
	protected void onResume() {
		Intent intent = new Intent();
		intent.setClass(this,DropService.class);
		this.startService(intent);
		
		sensormanager=(SensorManager) this.getSystemService(SENSOR_SERVICE);	

		imgview.setBackgroundResource(R.drawable.nostart);
		
	    //讓按鈕怎麼玩變成動畫
		Animation how_animation=AnimationUtils.loadAnimation(this, R.anim.scale_animation);
		button_how.startAnimation(how_animation);
		
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		Log.i(tag, "Ahmydroid.onDestroy");
		Intent intent = new Intent();
		intent.setClass(this, DropService.class);
		stopService(intent);
		super.onDestroy();
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case 0:
			AlertDialog dialog0=new AlertDialog.Builder(Ahmydroid.this)
 	    	.setTitle("怎麼玩？")
 			.setMessage("當小An跌倒時，他會希望你扶他一把。")

 			.setPositiveButton("了解了！", new DialogInterface.OnClickListener() {

 				@Override
 				public void onClick(DialogInterface dialog, int which) {
 					dismissDialog(0);
 					
 					button_exit.setVisibility(View.VISIBLE);
 					List<Sensor> list=sensormanager.getSensorList(Sensor.TYPE_ACCELEROMETER);
 					sensormanager.registerListener(Ahmydroid.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);	
 				}
 				})
 			.setNegativeButton("還是不懂", new DialogInterface.OnClickListener() {

 				@Override
 				public void onClick(DialogInterface dialog, int which) {
 					new AlertDialog.Builder(Ahmydroid.this)
 		 	    	.setTitle("提示")
 		 			.setMessage("待會請把手機平放時，\n會發生事件。")

 		 			.setPositiveButton("了解了！", new DialogInterface.OnClickListener() {

 		 				@Override
 		 				public void onClick(DialogInterface dialog, int which) {
 		 					dismissDialog(0);
 		 					button_exit.setVisibility(View.VISIBLE);
 		 					button_how.setVisibility(View.INVISIBLE);
 		 					List<Sensor> list=sensormanager.getSensorList(Sensor.TYPE_ACCELEROMETER);
 		 					sensormanager.registerListener(Ahmydroid.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);	
 		 				}
 		 				})
 		 			.show();
 				}
 				})
 			.setOnKeyListener(new OnKeyListener(){

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if(keyCode==KeyEvent.KEYCODE_SEARCH|keyCode==KeyEvent.KEYCODE_BACK){
							finish();
						}
						
						return true;
					}
     				
     			})
 			.create();
			return dialog0;

		}
		return super.onCreateDialog(id);
	}
	@Override
	protected void onPause() {
		sensormanager.unregisterListener(this);
		super.onPause();
	}
	@Override
	public void onSensorChanged(SensorEvent event) {

		float[] buf=event.values;
		float x=buf[1];
		int a=(int) Math.abs(x);
		Log.i(tag, String.valueOf(a));
		if(a<2){
			Log.i(tag, "mygod!");
			
			imgview.setVisibility(View.INVISIBLE);
			imgfall.setVisibility(View.VISIBLE);
			imgfall.setBackgroundResource(R.anim.falling_animation);
			aniimg=(AnimationDrawable) imgfall.getBackground();
			

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
//						Log.i(tag, "into onCompletion");
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
			
		}else{
			if(aniimg!=null){
				
				aniimg.stop();
				imgview.setBackgroundResource(R.drawable.stand);
				imgfall.setVisibility(View.INVISIBLE);
				imgview.setVisibility(View.VISIBLE);
			}		
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.about);
		menu.getItem(0).setIcon(R.drawable.about);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 0:
			new AlertDialog.Builder(this)
			.setMessage(getString(R.string.app_name)+" "+ softVersion +"\n"+getString(R.string.author)+" FunTrigger\n\n"+getString(R.string.copyright)+" 2010")
			.setIcon(R.drawable.icon)
			.setTitle(R.string.about)
			.setPositiveButton(getString(R.string.report_problem), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lp43simon@gmail.com"}); 
					sendIntent.putExtra(Intent.EXTRA_TEXT, "請將意見填寫於此");
					sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+softVersion+" 意見回報");
					sendIntent.setType("message/rfc822");
					startActivity(Intent.createChooser(sendIntent, "Title:"));
				}
			})
			.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
	
				}
			})
			.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
}