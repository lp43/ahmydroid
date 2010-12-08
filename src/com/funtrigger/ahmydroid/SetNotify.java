package com.funtrigger.ahmydroid;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

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
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MyTime;
import com.funtrigger.tools.ResponseDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
	MySetButton message_btn,facebook_btn,pick_btn/*,twitter_btn*/;
	Facebook facebook;
	protected static String tag="tag";
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

//	/**
//	 * 使用者設定欲接收簡訊的電話號碼
//	 */
//	private String send_msg__to_number="0953672003";
	RelativeLayout rl_set_notify;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_notify);
		
//		message_btn=(Button) findViewById(R.id.msg_btn);
//		facebook_btn = (Button) findViewById(R.id.fb_btn);
//		twitter_btn = (Button) findViewById(R.id.twitter_btn);
		rl_set_notify = (RelativeLayout) findViewById(R.id.rl_set_notify);
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

		
		//動態新增message button////////////////////////////
		message_btn=new MySetButton(this,R.drawable.message_icon);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,110);
		params.setMargins(0, 10, 0, 0);
		message_btn.setLayoutParams(params);
		
		message_btn.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
		message_btn.setText(Html.fromHtml("<b>" + getString(R.string.data)+getString(R.string.not_set) + "</b>" +  "<br />" +
				"<b>" + getString(R.string.status)+getString(R.string.not_run) + "</b>" /*+  "<br />" + "<br />" +
	            "<small>" + getString(R.string.instruction_head)+  "<br />" 
	            +getString(R.string.message_instruction)+ "</small>"*/));
		if(MySharedPreferences.getPreference(SetNotify.this, "message_status", "").equals("")){
			MySharedPreferences.addPreference(SetNotify.this, "message_status", "false");
		}
		setMessageButtonStatus();
		
		message_btn.setOnClickListener(new OnClickListener(){
		
					@Override
					public void onClick(View v) {

						if(MySharedPreferences.getPreference(SetNotify.this, "message_status", "").equals("false")){
							MySharedPreferences.addPreference(SetNotify.this, "message_status", "true");
						}else{
							MySharedPreferences.addPreference(SetNotify.this, "message_status", "false");
						}
					
						setMessageButtonStatus();
					}
					
				});
		message_btn.setOnLongClickListener(new OnLongClickListener(){

			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(SetNotify.this)
                .setTitle(R.string.please_choice)
                .setItems(new String[]{getString(R.string.set),getString(R.string.test)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	switch(which){
                    	case 0:
                    		LayoutInflater factory=LayoutInflater.from(SetNotify.this);
                        	final View message_context=factory.inflate(R.layout.context_to_message, null);
                        	final EditText type_message_number=(EditText) message_context.findViewById(R.id.type_message_number);
                        	
                        	//如果不為空，則將存放在SharedPreferences的值設定到EditText
                        	if(!MySharedPreferences.getPreference(SetNotify.this, "message_number", "").equals("")){
                        		type_message_number.setText(MySharedPreferences.getPreference(SetNotify.this, "message_number",getString(R.string.default_message_context)));
                        	}
                        	
                        	new AlertDialog.Builder(SetNotify.this)
                        	.setTitle(R.string.please_type)
                        	.setView(message_context)
                        	.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

    							@Override
    							public void onClick(DialogInterface dialog,
    									int which) {
    								
    								//如果輸入的字串是數字，才新增進SharedPreferences
    								if(SetNotify.isNumeric(type_message_number.getText().toString())){
    									MySharedPreferences.addPreference(SetNotify.this, "message_number", type_message_number.getText().toString());
    								}
    									
    									setMessageButtonStatus();
    								
    							}
                        		
                        	})
                        	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    							
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    							}
    						})
                        	.show();
                    		break;
                    		
                    	case 1:
                    		MySharedPreferences.addPreference(SetNotify.this, "message_status", "true");
                    		setMessageButtonStatus();
    						
                            new AlertDialog.Builder(SetNotify.this)
                            .setTitle(getString(R.string.attention))
                		    .setIcon(R.drawable.warning)
                		    .setMessage(R.string.attention_context)
                		    
                		    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                		
                		    @Override
                		    public void onClick(DialogInterface dialog, int which) {
//                		    	SmsManager smsmanager=SmsManager.getDefault();
//        						smsmanager.sendTextMessage(MySharedPreferences.getPreference(SetNotify.this,"message_number",""), null, getString(R.string.message_context).replace("#location", MyLocation.getLocation(SetNotify.this)), null, null);
        						ResponseDialog.newToast(SetNotify.this, getString(R.string.message_response), R.drawable.message_icon);
                		    }})
                		    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                		
                		    @Override
                		    public void onClick(DialogInterface dialog, int which) {
                		    }})
                		    .show();
                    		
    						
                    		break;
                    	}
                    	
                    	
                    }
                })
                .setPositiveButton(R.string.cancel, new  DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {	
					}
                	
                })
                .show();
			
			       			return false;
			}
			
		});
		rl_set_notify.addView(message_btn);
		//////////////////////////////////////////////
		
		//動態新增Facebook button///////////////////////////
		facebook_btn=new MySetButton(this,R.drawable.facebook_icon);
		RelativeLayout.LayoutParams params_facebook=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,110);
		params_facebook.setMargins(0, message_btn.getCompoundPaddingTop()+message_btn.getLayoutParams().height+10, 0, 0);
		facebook_btn.setLayoutParams(params_facebook);
		facebook_btn.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
		facebook_btn.setText(Html.fromHtml("<b>" + getString(R.string.data)+getString(R.string.not_set) + "</b>" +  "<br />" +
				"<b>" + getString(R.string.status)+getString(R.string.not_run) + "</b>" /*+  "<br />" + "<br />" +
	            "<small>" + getString(R.string.instruction_head)+  "<br />" 
	            +getString(R.string.facebook_instruction)+ "</small>"*/));

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
		rl_set_notify.addView(facebook_btn);	
		////////////////////////////////////////////
		
		//動態新增拾獲者button///////////////////////////////
		pick_btn=new MySetButton(this,R.drawable.pickup_icon);
		RelativeLayout.LayoutParams params_pick=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,110);
		params_pick.setMargins(0, message_btn.getCompoundPaddingTop()+message_btn.getLayoutParams().height+facebook_btn.getCompoundPaddingTop()+facebook_btn.getLayoutParams().height+10, 0, 0);

		pick_btn.setLayoutParams(params_pick);
		pick_btn.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
		if(MySharedPreferences.getPreference(this, "pick_status", "").equals("")){
			MySharedPreferences.addPreference(SetNotify.this, "pick_status", "false");
		}
		
		setPickButtonStatus();

		pick_btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(MySharedPreferences.getPreference(SetNotify.this, "pick_status", "").equals("false")){
					MySharedPreferences.addPreference(SetNotify.this, "pick_status", "true");
				}else{
					MySharedPreferences.addPreference(SetNotify.this, "pick_status", "false");
				}
			
				setPickButtonStatus();
			}
			
		});
		
		//設定pick_btn長按事件
		pick_btn.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				new AlertDialog.Builder(SetNotify.this)
                .setTitle(R.string.please_choice)
                .setItems(new String[]{getString(R.string.set),getString(R.string.test)}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	switch(which){
                    	case 0:
                    		LayoutInflater factory=LayoutInflater.from(SetNotify.this);
                        	final View pick_context=factory.inflate(R.layout.context_to_pick, null);
                        	
                        	final EditText type_pick_context=(EditText) pick_context.findViewById(R.id.type_pick_context);
                        	
                        	//如果不為空，則將存放在SharedPreferences的值設定到EditText
                        	if(!MySharedPreferences.getPreference(SetNotify.this, "pick_message_context", "").equals("")){
                        		type_pick_context.setText(MySharedPreferences.getPreference(SetNotify.this, "pick_message_context",getString(R.string.default_pick_context)));
                        	}
                        	
                        	new AlertDialog.Builder(SetNotify.this)
                        	.setTitle(R.string.please_type)
                        	.setView(pick_context)
                        	.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){

    							@Override
    							public void onClick(DialogInterface dialog,
    									int which) {
    								
    									MySharedPreferences.addPreference(SetNotify.this, "pick_message_context", type_pick_context.getText().toString());
    									setPickButtonStatus();
    								
    							}
                        		
                        	})
                        	.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    							
    							@Override
    							public void onClick(DialogInterface dialog, int which) {
    							}
    						})
                        	.show();
                    		break;
                    	case 1:
                    		MySharedPreferences.addPreference(SetNotify.this, "pick_status", "true");
                    		setPickButtonStatus();
                    		Intent intent = new Intent();
                    		intent.setClass(SetNotify.this, Fallen.class);
                    		startActivity(intent);
                    		break;
                    	}
                    	
                    	
                    }
                })
                .setPositiveButton(R.string.cancel, new  DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {	
					}
                	
                })
                .show();
			
			       			return false;
			       			}
						
		});
		
		rl_set_notify.addView(pick_btn);	
		/////////////////////////////////////////////
	
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, "設定Provider");
	
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
	 * 該函式用來重設拾獲按鈕是否已設定
	 */
	private void setPickButtonStatus(){
		//如果SharedPreferences裡有設定，資料就顯示為[已設定]
		String pick_data_status="";
		if(MySharedPreferences.getPreference(SetNotify.this, "pick_message_context", "").equals("")){
			pick_data_status=getString(R.string.not_set);
			MySharedPreferences.addPreference(SetNotify.this, "pick_status", "false");
		}else{
			pick_data_status=getString(R.string.been_set);
		}
		
		String pick_status="";
		if(MySharedPreferences.getPreference(SetNotify.this, "pick_status", "").equals("")|
				MySharedPreferences.getPreference(SetNotify.this, "pick_status", "").equals("false")){
			pick_status=getString(R.string.not_run);
		}else{
			pick_status=getString(R.string.runnning);
		}

		
		pick_btn.setText(Html.fromHtml("<b>" + getString(R.string.data)+pick_data_status
				+ "</b>" +  "<br />" +
				"<b>" + getString(R.string.status)+pick_status + "</b>" /*+  "<br />" + "<br />" +
	            "<small>" + getString(R.string.instruction_head)+  "<br />" 
	            +getString(R.string.facebook_instruction)+ "</small>"*/));
	}
	
	/**
	 * 該函式用來重設簡訊發送按鈕是否已設定
	 */
	private void setMessageButtonStatus(){
		//如果SharedPreferences裡有設定，資料就顯示為[已設定]
		String message_data_status="";
		if(MySharedPreferences.getPreference(SetNotify.this, "message_number", "").equals("")){
			message_data_status=getString(R.string.not_set);
			MySharedPreferences.addPreference(SetNotify.this, "message_status", "false");
		}else{
			message_data_status=getString(R.string.been_set);
		}
		
		String message_status="";
		if(MySharedPreferences.getPreference(SetNotify.this, "message_status", "").equals("")|
				MySharedPreferences.getPreference(SetNotify.this, "message_status", "").equals("false")){
			message_status=getString(R.string.not_run);
		}else{
			message_status=getString(R.string.runnning);
		}

		
		message_btn.setText(Html.fromHtml("<b>" + getString(R.string.data)+message_data_status
				+ "</b>" +  "<br />" +
				"<b>" + getString(R.string.status)+message_status + "</b>" /*+  "<br />" + "<br />" +
	            "<small>" + getString(R.string.instruction_head)+  "<br />" 
	            +getString(R.string.facebook_instruction)+ "</small>"*/));
	}
	
	/**
	 * 檢查輸入是否為數字
	 * @param 傳入欲檢查的字串
	 * @return 若符合，回傳true
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
//	    Log.i(tag, "isNumeric:"+String.valueOf(pattern.matcher(str).matches()));
	    return pattern.matcher(str).matches();   
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
