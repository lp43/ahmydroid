package com.funtrigger.ahmydroid;

import java.util.Calendar;
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
import com.funtrigger.tools.MyLocation;
import com.funtrigger.tools.MyTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
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


	SharedPreferences sp;
	Editor sharedata;
	
	
	Button message_btn,facebook_btn,twitter_btn;
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
	 * 使用者設定欲接收簡訊的電話號碼
	 */
	private String send_msg__to_number="0953672003";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_notify);
		
		message_btn=(Button) findViewById(R.id.msg_btn);
		facebook_btn = (Button) findViewById(R.id.fb_btn);
		twitter_btn = (Button) findViewById(R.id.twitter_btn);
		
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

	                MyTime gettime = new MyTime();
	              
	            	count++;
	            	params.putString("message", "嗨！我是你的手機，我掉了！\n剛剛我看了系統時間是 "+gettime.getHHMMSS()+
	            			"\n我的座標在 "+MyLocation.getLocation(SetNotify.this)+"\n快用GoogleMap貼上這個經緯度找我！");

	            	Log.i(tag, "count: "+count);
	            	mAsyncRunner.request("me/feed", params, "POST", new PostRequestListener());
					
					break;
				}
				super.handleMessage(msg);
			}
			
		};
		
		twitter_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MyLocation.getLocation(SetNotify.this);
			}
			
		});
		message_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SmsManager smsmanager=SmsManager.getDefault();
//				smsmanager.sendTextMessage(send_msg__to_number, null, "test", null, null);
			}
			
		});
		
		
		
		facebook_btn.setOnClickListener(new OnClickListener(){

			

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
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, "設定Provider");
//		menu.add(0, 1, 1, "查看中獎號");
//		menu.add(0, 2, 2, "更新");
//		menu.add(0, 3, 3, "設定");
//		menu.add(0, 4, 4, "關於");
		
		
//		menu.getItem(0).setIcon(R.drawable.setmonth);
//		menu.getItem(1).setIcon(R.drawable.targetnum);
//		menu.getItem(2).setIcon(R.drawable.refresh);
//		menu.getItem(3).setIcon(R.drawable.setting);
//		menu.getItem(4).setIcon(R.drawable.about);
	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch(item.getItemId()){
		
		case 0:
			showDialog(0);
		break;	
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch(id){
		case 0:
			sp = getSharedPreferences("data", 0);  
			String recprovider=sp.getString("provider", "auto");
			int cycle=0;
			if(recprovider.equals("auto")){
				cycle=0;
			}else if(recprovider.equals("gps")){
				cycle=1;
			}else if(recprovider.equals("network")){
				cycle=2;
			}
			builder
			.setTitle("選擇Provider")
			.setSingleChoiceItems(new String[]{"自動","GPS","AGPS",},cycle,new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					
					switch(which){
						case 0:
							sharedata = getSharedPreferences("data", 0).edit();
	    	                sharedata.putString("provider","auto");
	    	                sharedata.commit();
	    	            	dismissDialog(0);
	    					break;
						case 1:		
							sharedata = getSharedPreferences("data", 0).edit();
			                sharedata.putString("provider","gps");
			                sharedata.commit();
							dismissDialog(0);
							break;
						case 2:							
							sharedata = getSharedPreferences("data", 0).edit();
	    	                sharedata.putString("provider","network");
	    	                sharedata.commit();
							dismissDialog(0);
							break;
					}
					
				}
				
			})
			.setPositiveButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	
	        }
			});
			AlertDialog alert = builder.create();
			return alert;
		
		}
		return super.onCreateDialog(id);
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
