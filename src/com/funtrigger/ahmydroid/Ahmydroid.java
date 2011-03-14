package com.funtrigger.ahmydroid;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.funtrigger.tools.InternetInspector;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MyDispatcher;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;
import com.funtrigger.tools.MyTime;
import com.funtrigger.tools.SwitchService;
import com.funtrigger.tools.WriteToTXT;
import com.funtrigger.tuition.DropUI;
import com.funtrigger.tuition.Welcome;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.PowerManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
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
	private String softVersion="v2.0.0.0";
	/**
	 * [怎麼玩]和[離開]的Button變數
	 */
	private Button button_how,button_exit;
    /**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;
    private final static String tag="tag";
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
	public static ImageButton img_btn;
	/**
	 * 機器人暈眩的動畫變數，它會被imgfall變數啟動
	 */
	static private AnimationDrawable aniimg;
	/**
	 * 該變數用在onCreateDialog的switch case裡，
	 * 主要目的用來告知使用者怎麼玩這隻Apk的訊息視窗
	 */
	private static final int HOW_TO_USE_1 = 1;
	private static final int HOW_TO_USE_2 = 2;
	private static final int HOW_TO_USE_3 = 3;
	private static final int HOW_TO_USE_4 = 4;
	private static final int HOW_TO_USE_5 = 5;
//	static Context context;
	static AnimationDrawable ani_elect;
	static Handler handler;
	
    public static void actionShowMain(Context context) {
        Intent i = new Intent(context, Ahmydroid.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "Ahmydroid.onCreate");
        super.onCreate(savedInstanceState);
        
        
 
        setContentView(R.layout.ahmyphone);
        

	
			 button_how=(Button) findViewById(R.id.button_how);
		        button_exit=(Button) findViewById(R.id.button_exit);
		        img_btn=(ImageButton) findViewById(R.id.img_btn);
		        imgfall=(ImageView) findViewById(R.id.fall);
		        img_btn.setBackgroundResource(R.drawable.nostart);
		        
//		        this.context=this;
		        
		        //主畫面不用將離開按鈕隱藏
		        button_exit.setVisibility(View.VISIBLE);
		        
		        //開啟使用教學
		        button_how.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
//						button_how.setVisibility(View.INVISIBLE);
//						showDialog(HOW_TO_USE_1);
						Ahmydroid.this.finish();
						startActivity(new Intent(Ahmydroid.this, Welcome.class));
					}
		        	
		        });

		        button_exit.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						finish();	
						
						/*ConnectivityManager cm=(ConnectivityManager) Ahmydroid.this.getSystemService(Ahmydroid.this.CONNECTIVITY_SERVICE);
						Log.i(tag, "return value: "+String.valueOf(cm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE , ConnectivityManager.CONNECTIVITY_ACTION)));
						cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
						NetworkInfo c=cm.getActiveNetworkInfo();
						if(c!=null){
							Log.i(tag, "Type: "+c.getTypeName());
						}*/
//						WriteToTXT.writeLogToTXT(MyLocation.getLocation(Ahmydroid.this));
						
//						if(checkServiceExist(".LocationUpdateService")==false){
//							SwitchService.startService(Ahmydroid.this, LocationUpdateService.class);
//							Toast.makeText(Ahmydroid.this, "start LocationUpdateService", Toast.LENGTH_SHORT).show();
//						}else{
//							SwitchService.stopService(Ahmydroid.this, LocationUpdateService.class);
//							Toast.makeText(Ahmydroid.this, "stop LocationUpdateService", Toast.LENGTH_SHORT).show();
//							
//							LocationUpdateService lus= new LocationUpdateService();
//							lus.resetLocation();			
//							Toast.makeText(Ahmydroid.this, "resetLocation", Toast.LENGTH_SHORT).show();
//						}
//						try {
//							MyDispatcher.facebook.logout(Ahmydroid.this);
//						} catch (MalformedURLException e) {
//							Log.i(tag, "MalformedURLException: "+e.getMessage());
//						} catch (IOException e) {
//							Log.i(tag, "IOException: "+e.getMessage());
//						}
//						Facebook facebook= new Facebook("171403682887181");
//						facebook.authorize(Ahmydroid.this, new String[]{"publish_stream"}, new BaseDialogListener(){
		//
//							@Override
//							public void onComplete(Bundle values) {
//								Log.i(tag, "success");
//							}
//							
//						});//這行要加，因為有時候Session會過期
						
//						MyDispatcher md= new MyDispatcher();
//						md.facebookDispatcher(Ahmydroid.this, Ahmydroid.this);
						
						
//						Bundle params =new Bundle();
//						params.putString("message", context.getString(R.string.facebook_message_head)+"\n"+
//				    			context.getString(R.string.facebook_message_time).replace("#time", mytime.getHHMMSS())+"\n"+
//				    			context.getString(R.string.facebook_message_location).replace("#location",  LocationUpdateService.getRecordLocation())+"\n"+
//				    			context.getString(R.string.facebook_message_last));
						
						
//						String aa= "test";
//						String bb="message";
//						params.putByteArray("message", aa.getBytes());
//						try {
//							facebook.request("me/feed", params, "POST");
//						} catch (FileNotFoundException e) {
//							Log.i(tag, "FileNotFoundException: "+e.getMessage());
//						} catch (MalformedURLException e) {
//							Log.i(tag, "MalformedURLException: "+e.getMessage());
//						} catch (IOException e) {
//							Log.i(tag, "IOException: "+e.getMessage());
//						}
						
//						ClipboardManager cm=(ClipboardManager) Ahmydroid.this.getSystemService(Context.CLIPBOARD_SERVICE);
//						cm.setText(LocationUpdateService.getRecordLocation(Ahmydroid.this.getApplicationContext()));
//						Toast.makeText(Ahmydroid.this, LocationUpdateService.getRecordLocation(Ahmydroid.this.getApplicationContext()), Toast.LENGTH_SHORT).show();
					}
		        	
		        });
		        
		        
		        
		     
//		        button_exit.setOnLongClickListener(new OnLongClickListener(){
		//
//					@Override
//					public boolean onLongClick(View v) {
//						
//						return false;
//					}
//		        	
//		        });
		        
		        img_btn.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {

						
						if(MySystemManager.checkServiceExist(Ahmydroid.this.getApplicationContext(),".FallDetector")==false){
							SwitchService.startService(Ahmydroid.this,FallDetector.class);
							
							if(MySharedPreferences.getPreference(Ahmydroid.this, "location", false)==true){
								SwitchService.startService(Ahmydroid.this,LocationUpdateService.class);
							}
							//用在讓重新開機時，能夠知道要不要啟動防摔小安
							MySharedPreferences.addPreference(Ahmydroid.this, "falldetector_status", "true");
//							Toast.makeText(Ahmydroid.this, getString(R.string.startfallprotect), Toast.LENGTH_SHORT).show();
							MyDialog.newToast(Ahmydroid.this, getString(R.string.startfallprotect), R.drawable.icon);
							MyDialog.newTwoBtnDialog(Ahmydroid.this, R.drawable.icon, getString(R.string.exit)+"?",getString(R.string.exit_or_not) , getString(R.string.ok), new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
								}
								
							}, getString(R.string.cancel), new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog, int which) {	
								}
								
							});
							
//							img_btn.setBackgroundResource(R.drawable.nostart);
							setAndroid_Machine(Ahmydroid.this.getApplicationContext());
						}else if(MySystemManager.checkServiceExist(Ahmydroid.this.getApplicationContext(),".FallDetector")==true){
							SwitchService.stopService(Ahmydroid.this,FallDetector.class);
							SwitchService.stopService(Ahmydroid.this,LocationUpdateService.class);
							//用在讓重新開機時，能夠知道要不要啟動防摔小安
							MySharedPreferences.addPreference(Ahmydroid.this, "falldetector_status", "false");
//							Toast.makeText(Ahmydroid.this, getString(R.string.stopfallprotect), Toast.LENGTH_SHORT).show();
							MyDialog.newToast(Ahmydroid.this, getString(R.string.stopfallprotect), R.drawable.icon);
//							img_btn.setBackgroundResource(R.drawable.start);
							setAndroid_Machine(Ahmydroid.this.getApplicationContext());
						}
						
						
					}
		        	
		        });
		
		        //檢查緩衝記憶體是否過量
				int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
//				String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);

				if(usedMegs>11){
					Log.i(tag, "Memory is: "+usedMegs+"MB, cancel into Welcome.class");
					button_how.setVisibility(View.INVISIBLE);
					MySharedPreferences.addPreference(this, "into_ahmydroid", true);
				}
	
		
    }
    
	@Override
	protected void onResume() {
		Log.i(tag, "Ahmydroid.onResume");
		
		
		
		setAndroid_Machine(Ahmydroid.this.getApplicationContext());
		
		sensormanager=(SensorManager) this.getSystemService(SENSOR_SERVICE);	

		
	    //讓按鈕怎麼玩變成動畫
		Animation how_animation=AnimationUtils.loadAnimation(this, R.anim.scale_animation);
		
		   //檢查緩衝記憶體是否過量
		int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
//		String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
		
		//過量就不要出現使用教學的動畫了
		if(usedMegs<11){
			button_how.startAnimation(how_animation);
		}
		
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		Log.i(tag, "Ahmydroid.onDestroy");
		finish();
		super.onDestroy();
	}
	
	/*@Override
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
			AlertDialog dialog2=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.warning_ui, R.drawable.warning_ui, R.string.how_to_use_2, R.string.previous,R.string.next,
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
			AlertDialog dialog3=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.send_txt, R.drawable.notify_all, R.string.how_to_use_3, R.string.previous,R.string.next,
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
			AlertDialog dialog4=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.unlock_ui, R.drawable.unlock_ui, R.string.how_to_use_4, R.string.previous,R.string.next,
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
			AlertDialog dialog5=MyDialog.tuitionTwoBtnDialog(Ahmydroid.this, R.string.thank, R.drawable.icon, R.string.how_to_use_5, R.string.previous, R.string.try_warning_ui,new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					showDialog(HOW_TO_USE_4);
				}
				
			}, new DialogInterface.OnClickListener(){
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent =new Intent();
					intent.setClass(Ahmydroid.this, TryFallen.class);
					startActivity(intent);
					
				}
				
			});
			
			//下面這段本來要幫使用者多寫一個離開，後來還是算了。
			//強迫使用者進來學如何關程式
//			LayoutInflater factory = LayoutInflater.from(Ahmydroid.this);
//	        final View EntryView = factory.inflate(Ahmydroid.this.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.tuition), null);
//	        ImageView tu_pic=(ImageView) EntryView.findViewById(R.id.tuition_pic);
//	        TextView tu_cnx=(TextView) EntryView.findViewById(R.id.tuition_cnx);
//	        tu_pic.setImageResource(R.drawable.icon);
//	        tu_cnx.setText(R.string.how_to_use_5);
//			AlertDialog dialog5=new AlertDialog.Builder(Ahmydroid.this)
//            .setTitle(getString(R.string.thank))
//            .setView(EntryView)
//		    .setPositiveButton(R.string.previous, new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					showDialog(HOW_TO_USE_4);
//					
//				}
//		    	
//		    })
//		    .setNeutralButton(R.string.exit, new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//		    	
//		    })
//		    .setNegativeButton(R.string.try_warning_ui, new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent intent =new Intent();
//					intent.setClass(Ahmydroid.this, TryFallen.class);
//					startActivity(intent);
//				}
//		    	
//		    })
//		  
//		    .create();
			
			
			
			
			return dialog5;

		}
		return null;
		
		
	}*/
	
	@Override
	protected void onPause() {
		Log.i(tag, "Ahmydroid.onPause");
		sensormanager.unregisterListener(this);
		if(ani_elect!=null){
			ani_elect.stop();
		}
		super.onPause();
	}
	
	/**
	 * 如果Fallen.Service如果有開啟，設定讓小綠人肚子的齒輪發亮，
	 * 如果LocationUpdateService.Service有開啟，變人電波小安
	 * 否則為暗
	 */
	public static void setAndroid_Machine(Context context){
		
		if(MySystemManager.checkServiceExist(context.getApplicationContext(),".FallDetector")==false){
			img_btn.setBackgroundResource(R.drawable.start);
		}else if(MySystemManager.checkServiceExist(context.getApplicationContext(),".FallDetector")==true){
			
				if(MySystemManager.checkServiceExist(context.getApplicationContext(),".LocationUpdateService")==true){
					//當主畫面是Ahmydroid，而且FallDetector有開，才展現電波小安
					if(MySystemManager.checkTaskExist(context.getApplicationContext(), ".Ahmydroid")==true){
						
						handler= new Handler();
						handler.postDelayed(new Runnable(){
	
							@Override
							public void run() {
								img_btn.setBackgroundResource(R.anim.electric_android);
								ani_elect=(AnimationDrawable) img_btn.getBackground();
								ani_elect.start();
							}
							
						},300);
						
					}
				}else if(MySystemManager.checkServiceExist(context,".LocationUpdateService")==false){
					img_btn.setBackgroundResource(R.drawable.nostart);
				}
		}
		
//		if(checkServiceExist(".LocationUpdateService")==true){
//			startElectricAndroid();
//		}else if(checkServiceExist(".LocationUpdateService")==false){
//			if(checkServiceExist(".FallDetector")==false){
//				img_btn.setBackgroundResource(R.drawable.start);
//			}else if(checkServiceExist(".FallDetector")==true){
//				img_btn.setBackgroundResource(R.drawable.nostart);
//			}
//		}
	}
	
	/**
	 * 關閉電器小安，變成啟動中小安
	 */
	public static void stopElectricToStart(){
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				if(ani_elect!=null){
					ani_elect.stop();
				}
				
				img_btn.setBackgroundResource(R.drawable.nostart);
			}
			
		}, 5000);
		
	}
	
	
	
//	/**
//	 * 專門檢查摔落告知的Service是否有開啟,
//	 * 預設為false;
//	 * @return 若有開啟回傳為true,否則為false
//	 */
//	private boolean checkServiceExist(String checkServiceName){
//		boolean return_field=false;
//		ActivityManager activityManager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE); 
//		List<ActivityManager.RunningServiceInfo> service=activityManager.getRunningServices(100);
////		Log.i(tag, "packagename: "+this.getPackageName());
//		
//		for(RunningServiceInfo service_name:service){
////			Log.i(tag, "exist service: "+service_name.service.getShortClassName());
//			if(service_name.service.getShortClassName().equals(checkServiceName)){
//				return_field=true;
//				break;
//			}
//		}
//		
//		Log.i(tag, "service exist: "+String.valueOf(return_field));
//		return return_field;
//		
//	}
	
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
	
	
	/**
	 * 如果使用者開了AGPS，卻沒開3G，
	 * 此時若按了小綠人，LocationUpdateService打不開
	 * 會在呼叫的頁面顯示沒有開啟的畫面
	 */
	public static void tellUserCantGetLocation(Context context){
		if(LocationUpdateService.getMyBestProvider(context.getApplicationContext())!=null){
			
		
			if(!InternetInspector.checkInternet(context).equals("mobile")
					&& LocationUpdateService.getMyBestProvider(context).equals("network")){
	//			if(MySystemManager.checkServiceExist(context, ".LocationUpdateService")==false){
				new AlertDialog.Builder(context)
		        .setTitle(com.funtrigger.ahmydroid.R.string.attention)
			    .setIcon(com.funtrigger.ahmydroid.R.drawable.warning)
			    .setMessage(com.funtrigger.ahmydroid.R.string.cant_get_location_with_3g)
			    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
			    		   
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
									
					}
				})
	
			    
			    	.show();
			}
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
				setAndroid_Machine(Ahmydroid.this.getApplicationContext());
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
		menu.add(0, 0, 0, R.string.set);
		menu.add(0, 1, 1, R.string.about);
		menu.getItem(0).setIcon(R.drawable.setting);
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
//			String[] tt={getString(R.string.author),getString(R.string.report_problem),getString(R.string.help_translate)};
//			ListAdapter arrayadapter= new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_item, tt);
			
			new AlertDialog.Builder(this)
//			.setMessage(getString(R.string.app_name)+" "+ softVersion +"\n"+getString(R.string.author)+" FunTrigger\n\n"+getString(R.string.copyright)+" 2011")
			.setIcon(R.drawable.icon)
			.setTitle(R.string.about)
			.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.select_dialog_item, 
					new String[]{getString(R.string.author),getString(R.string.report_problem),getString(R.string.help_translate)}), 
					new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(which){
					case 0:
						new AlertDialog.Builder(Ahmydroid.this)
						.setTitle(R.string.author)
						.setIcon(R.drawable.icon)
						.setMessage(getString(R.string.app_name)+" "+ softVersion +"\n"+getString(R.string.author)+" FunTrigger\n\n"+getString(R.string.copyright)+" 2011")
						.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
				
							}
						})
						.show();
						break;
					case 1:
						Intent sendIntent = new Intent(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lp43simon@gmail.com"}); 
						sendIntent.putExtra(Intent.EXTRA_TEXT, "請將意見填寫於此");
						sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+softVersion+" 意見回報");
						sendIntent.setType("message/rfc822");
						startActivity(Intent.createChooser(sendIntent, "Title:"));
						break;
					case 2:
						new AlertDialog.Builder(Ahmydroid.this)
						.setTitle(R.string.help_translate)
						.setIcon(R.drawable.icon)
						.setMessage(getString(R.string.help_translate_context))
						.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
				
							}
						})
						.show();
						break;
					}
					
				}

				
			})
//			.setPositiveButton(getString(R.string.report_problem), new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					Intent sendIntent = new Intent(Intent.ACTION_SEND);
//					sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lp43simon@gmail.com"}); 
//					sendIntent.putExtra(Intent.EXTRA_TEXT, "請將意見填寫於此");
//					sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+softVersion+" 意見回報");
//					sendIntent.setType("message/rfc822");
//					startActivity(Intent.createChooser(sendIntent, "Title:"));
//				}
//			})
//			.setNeutralButton(R.string.back, new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//	
//				}
//			})
			.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
	/**
	 * 關閉摔落告知
	 */
	public static void closeDropProtection(Context context){
		SwitchService.stopService(context.getApplicationContext(), FallDetector.class);
		img_btn.setBackgroundResource(R.drawable.start);
	}
	

}