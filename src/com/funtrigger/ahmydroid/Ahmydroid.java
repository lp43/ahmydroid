package com.funtrigger.ahmydroid;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.facebook.android.LoginPage;
import com.facebook.android.Facebook;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
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
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 這隻程式當你的手機掉落時，會發出聲音+震動告知你
 *  @author Simon
 */
public class Ahmydroid extends Activity implements SensorEventListener{
	/**
	 * 記錄當前的版本編號<br/>
	 * 這個編號會被放在[Menu]的[關於]裡
	 */
	private String softVersion="v1.0.0.24";
	/**
	 * [怎麼玩]和[離開]的Button變數
	 */
	private Button button_how,button_exit;
    /**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;
    private final String tag="tag";
	/**
	 * 控制音樂播放的變數
	 */
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 機器人跌倒的ImageView，
	 * 程式中會和aniimg變數做啟動動畫的動作
	 */
	private ImageView imgfall;
	/**
	 * 帶齒輪的小綠人的圖形按鈕元件變數
	 */
	private ImageButton img_btn;
	/**
	 * 機器人暈眩的動畫變數，它會被imgfall變數啟動
	 */
	private AnimationDrawable aniimg;
	/**
	 * 該變數用在onCreateDialog的switch case裡，
	 * 主要目的用來告知使用者怎麼玩這隻Apk的訊息視窗
	 */
	private static final int HOW_TO_USE_1 = 1;
	private static final int HOW_TO_USE_2 = 2;
	private static final int HOW_TO_USE_3 = 3;
	private static final int HOW_TO_USE_4 = 4;
	private static final int HOW_TO_USE_5 = 5;


	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "Ahmydroid.onCreate");
        super.onCreate(savedInstanceState);
        
        //可能使用者選擇背景持續發送訊息，因此再次進入主畫面時，
        //TimeService沒有關閉，導致小綠人開關產生問題
        //故要在畫面未設定前，將TimeService關掉
        if(checkServiceExist(".TimeService")==true){
        	SwitchService.stopService(Ahmydroid.this, TimeService.class);
        }
        
        setContentView(R.layout.ahmyphone);
        button_how=(Button) findViewById(R.id.button_how);
        button_exit=(Button) findViewById(R.id.button_exit);
        img_btn=(ImageButton) findViewById(R.id.img_btn);
        imgfall=(ImageView) findViewById(R.id.fall);
        img_btn.setBackgroundResource(R.drawable.nostart);
        
        //開啟使用教學
        button_how.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				button_how.setVisibility(View.INVISIBLE);
				showDialog(HOW_TO_USE_1);
				
			}
        	
        });

        button_exit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				finish();
			
				
//				Log.i(tag, "dispatcher time is : "+MySharedPreferences.getPreference(Ahmydroid.this, "dispatcher_first_time", "0"));
			
			}
        	
        });
        img_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(checkServiceExist(".FallDetector")==false){
					SwitchService.startService(Ahmydroid.this,FallDetector.class);
					MySharedPreferences.addPreference(Ahmydroid.this, "falldetector_status", "true");
					Toast.makeText(Ahmydroid.this, getString(R.string.startfallprotect), Toast.LENGTH_SHORT).show();
					img_btn.setBackgroundResource(R.drawable.nostart);
				}else if(checkServiceExist(".FallDetector")==true){
					SwitchService.stopService(Ahmydroid.this,FallDetector.class);
					
					MySharedPreferences.addPreference(Ahmydroid.this, "falldetector_status", "false");
					Toast.makeText(Ahmydroid.this, getString(R.string.stopfallprotect), Toast.LENGTH_SHORT).show();
					img_btn.setBackgroundResource(R.drawable.start);
				}
				
			}
        	
        });
    }
    
	@Override
	protected void onResume() {
		Log.i(tag, "Ahmydroid.onResume");
		
		
		
		setAndroid_Machine();
		
		sensormanager=(SensorManager) this.getSystemService(SENSOR_SERVICE);	

		
	    //讓按鈕怎麼玩變成動畫
		Animation how_animation=AnimationUtils.loadAnimation(this, R.anim.scale_animation);
		button_how.startAnimation(how_animation);
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(tag, "Ahmydroid.onDestroy");
		finish();
		super.onDestroy();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case HOW_TO_USE_1:
			AlertDialog dialog1=MyDialog.tuitionOneBtnDialog(Ahmydroid.this, R.string.welcome, R.drawable.how_to_use_1, R.string.how_to_use_1, R.string.next, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					showDialog(HOW_TO_USE_2);
					
				}
				
			});
			return dialog1;
			
		case HOW_TO_USE_2:
			AlertDialog dialog2=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.warning_ui, R.drawable.warning_ui, R.string.how_to_use_2, 
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_1);
							
						}
				
					}, new DialogInterface.OnClickListener(){
		
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_3);
							
						}
						
					});
			return dialog2;
			
		case HOW_TO_USE_3:
			AlertDialog dialog3=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.unlock_ui, R.drawable.unlock_ui, R.string.how_to_use_3, 
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_2);
							
						}
				
					}, new DialogInterface.OnClickListener(){
		
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_4);
							
						}
						
					});
			return dialog3;
			
		case HOW_TO_USE_4:
			AlertDialog dialog4=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.send_txt, R.drawable.notify_all, R.string.how_to_use_4, 
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_3);
							
						}
				
					}, new DialogInterface.OnClickListener(){
		
						@Override
						public void onClick(DialogInterface dialog, int which) {
							showDialog(HOW_TO_USE_5);
							
						}
						
					});
			return dialog4;
		
		case HOW_TO_USE_5:
			AlertDialog dialog5=MyDialog.tuitionOneBtnDialog(Ahmydroid.this, R.string.thank, R.drawable.icon, R.string.how_to_use_5, R.string.exit, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
				
			});
			return dialog5;
		}
		return null;
		
		
	}
	
	@Override
	protected void onPause() {
		Log.i(tag, "Ahmydroid.onPause");
		sensormanager.unregisterListener(this);
		super.onPause();
	}
	
	/**
	 * Service如果有開啟，設定讓小綠人肚子的齒輪發亮，
	 * 否則為暗
	 */
	private void setAndroid_Machine(){
		if(checkServiceExist(".FallDetector")==false){
			img_btn.setBackgroundResource(R.drawable.start);
		}else if(checkServiceExist(".FallDetector")==true){
			img_btn.setBackgroundResource(R.drawable.nostart);
		}
	}
	
	
	/**
	 * 專門檢查摔落告知的Service是否有開啟,
	 * 預設為false;
	 * @return 若有開啟回傳為true,否則為false
	 */
	private boolean checkServiceExist(String checkServiceName){
		boolean return_field=false;
		ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE); 
		List<ActivityManager.RunningServiceInfo> service=activityManager.getRunningServices(100);
//		Log.i(tag, "packagename: "+this.getPackageName());
		
		for(RunningServiceInfo service_name:service){
//			Log.i(tag, "exist service: "+service_name.service.getShortClassName());
			if(service_name.service.getShortClassName().equals(checkServiceName)){
				return_field=true;
				break;
			}
		}
		
		Log.i(tag, "service exist: "+String.valueOf(return_field));
		return return_field;
		
	}
	
	/**
	 * 專播動畫的UI控制Method
	 */
	private void startFallAnimation(){
		img_btn.setVisibility(View.INVISIBLE);
		imgfall.setVisibility(View.VISIBLE);
		imgfall.setBackgroundResource(R.anim.falling_animation);
		aniimg=(AnimationDrawable) imgfall.getBackground();
		
		//當小綠人沒有在暈眩時，給他暈眩
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
//					Log.i(tag, "into onCompletion");
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
	
	@Override//控制Sensor的專屬Method
	public void onSensorChanged(SensorEvent event) {

		float[] buf=event.values;
		float x=buf[1];
		int a=(int) Math.abs(x);
		Log.i(tag, String.valueOf(a));
		//當x角度小於2時，啟動動畫
		if(a<2){
			Log.i(tag, "mygod!");
			
			startFallAnimation();
			
		}else{
			if(aniimg!=null){
				
				aniimg.stop();
				setAndroid_Machine();
				imgfall.setVisibility(View.INVISIBLE);
				img_btn.setVisibility(View.VISIBLE);
			}		
		}
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.set_notify);
		menu.add(0, 1, 1, R.string.about);
		menu.getItem(0).setIcon(R.drawable.set_notify);
		menu.getItem(1).setIcon(R.drawable.about);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case 0:

			Intent intent = new Intent();
			intent.setClass(this, /*SetNotify.class*/Settings.class);
			startActivity(intent);
			break;
			
		case 1:
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