package com.funtrigger.ahmydroid;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;
import com.facebook.android.R;
import com.facebook.android.Util;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 這個類別用來設定當手機掉落時通知使用者的方式<br/>
 * 目前規劃的提示告知方式有︰
 * 1.簡訊告知
 * 2.Facebook告知
 * 
 * @author simon
 */
public class SetNotify extends Activity {
	Button gmail_btn,message_btn,twitter_btn,fb_test;
	LoginButton facebook_btn;
	Facebook facebook;
	protected String tag="tag";
	protected int count;
	private AsyncFacebookRunner mAsyncRunner;
	/**
	 * 呼叫Main Thread的handler
	 */
	private Handler handler;
	/**
	 * handler裡的msg.what的各個變數名稱<br/>
	 * 這個變數拿來做為當偵測到FB登入後，能夠發送訊息的Callback
	 */
	private final int POSTTOFACEBOOK=0;
	/**
	 * longitude經度,latitude緯度
	 */
	double longitude,latitude;
	/**
	 * 使用者設定欲接收簡訊的電話號碼
	 */
	private String send_msg__to_number="0953672003";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_notify);
		gmail_btn=(Button) findViewById(R.id.gmail_btn);
		message_btn=(Button) findViewById(R.id.msg_btn);
		twitter_btn=(Button) findViewById(R.id.twitter_btn);
//		facebook_btn=(Button) findViewById(R.id.facebook_btn);
		facebook_btn = (LoginButton) findViewById(R.id.facebook_btn);
		fb_test=(Button) findViewById(R.id.fb_test);
		facebook=new Facebook("171403682887181");
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		//設定初始為灰色
/*		gmail_btn.getBackground().setAlpha(50);
		message_btn.getBackground().setAlpha(50);
		twitter_btn.getBackground().setAlpha(50);
		facebook_btn.getBackground().setAlpha(50);*/
		
		//叫出主Thread的handler並覆寫，好讓程式可以handle我們要處理的事
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case POSTTOFACEBOOK:
					Bundle params = new Bundle();
	            	Date date=new Date();	

	            	count++;
	            	params.putString("message", "嗨！我是你的手機，我掉了！\n剛剛我看了系統時間是︰"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+
	            			"\n我在["+getLocation()+"]\n快用GoogleMap貼上這個經緯度找我！");
//	            	params.putString("message", "test "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+getLocation());
	            	Log.i(tag, "count: "+count);
	            	mAsyncRunner.request("me/feed", params, "POST", new PostRequestListener());
					
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		
		
		message_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SmsManager smsmanager=SmsManager.getDefault();
				smsmanager.sendTextMessage(send_msg__to_number, null, "test", null, null);
			}
			
		});
		
		
		facebook_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {		
				facebook_btn.init(SetNotify.this, facebook);
			}
			
		});
		fb_test.setOnClickListener(new OnClickListener(){

			

			@Override
			public void onClick(View v) {
				if(facebook!=null){
//					Log.i(tag, "into facebook!=null");
				
					if(facebook.isSessionValid()==true){
						Log.i(tag, "session valid: "+facebook.isSessionValid());
						//當Session存在時，就不用登入了，直接發文
						handler.sendEmptyMessage(POSTTOFACEBOOK);
		            	
					}else if(facebook.isSessionValid()==false){
						Log.i(tag, "session valid: "+facebook.isSessionValid());
						facebook.authorize(SetNotify.this, new String[]{"publish_stream"}, new LoginSuccessListener());//這行要加，因為有時候Session會過期
					}
					
	            	
				}else{
					Log.i(tag, "facebook entity is null");
				}
				
			}
			
		});

	}
	
	/**
	 * 取得最後的地理座標函式
	 * @return 呼叫此函式時，會將經緯度傳回呼叫端,回傳格式為︰緯、經度以配合Google地圖
	 */
	private String getLocation(){
		LocationManager lm = (LocationManager)getSystemService(SetNotify.LOCATION_SERVICE); 
    	Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(location!=null){
    		latitude = location.getLatitude();//查詢緯度
    		longitude = location.getLongitude();//查詢經度
    	}else{
    		Log.i(tag, "location=null, start requestLocationUpdates");
    		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10,  new LocationListener(){

				@Override
				public void onLocationChanged(Location location) {
//					Toast.makeText(SetNotify.this,"LocationListener onLocationChanged" , Toast.LENGTH_SHORT).show();
					latitude = location.getLatitude();//查詢緯度
					longitude=location.getLongitude();//查詢經度,並存進經度
				}

				@Override
				public void onProviderDisabled(String provider) {
					Log.i(tag, "LocationListener onProviderDisabled");
//					Toast.makeText(SetNotify.this,"LocationListener onProviderDisabled" , Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onProviderEnabled(String provider) {
					Log.i(tag, "LocationListener onProviderEnabled");
//					Toast.makeText(SetNotify.this,"LocationListener onProviderEnabled" , Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onStatusChanged(String provider,
						int status, Bundle extras) {
					Log.i(tag, "LocationListener onStatusChanged");
//					Toast.makeText(SetNotify.this,"LocationListener onStatusChanged" , Toast.LENGTH_SHORT).show();
				}
    			
    		});
    	}
    	Toast.makeText(SetNotify.this,latitude+","+longitude , Toast.LENGTH_SHORT).show();
    	Log.i(tag,latitude+","+longitude);//顯示緯度+經度以配合Google Map格式
		return latitude+","+longitude;
	}
	
	
	/**
	 * 改自Example.java範例的SampleRequestListener<br/>
	 * Facebook源碼規範WebView完成PO文後，我們應該要寫一個監聽來做回應
	 * 寫在onComplete()裡的是PO完文後，我們自己想做的事
	 * @author simon
	 *
	 */
    public class PostRequestListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: executed in background thread

                JSONObject json = Util.parseJson(response);
                final String id = json.getString("id");
                Log.i(tag, "post id: "+id);
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(SetNotify.this, getString(R.string.post_success), Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * 改自Example.java範例的SampleDialogListener<br/>
     * 當成功登入後要做的動作可寫在onComplete()裡面
     * 因為我的程式拿來登入+發測試文一次完成，
     * 所以一旦偵測到Login了，就馬上sendMessage給main thead來Poll文
     * @author simon
     *
     */
    public class LoginSuccessListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
        	Log.i(tag, "login success!");
        	Log.i(tag, "access_token: "+values.getString("access_token"));
        	//一旦偵測到Login了，就馬上sendMessage給main thead來Poll文
        	handler.sendEmptyMessage(POSTTOFACEBOOK);
        }
    }
    
}
