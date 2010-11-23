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
import com.facebook.android.LoginPage;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.facebook.android.LoginPage.SampleDialogListener;
import com.facebook.android.LoginPage.SampleRequestListener;
import com.facebook.android.LoginPage.WallPostDeleteListener;
import com.facebook.android.LoginPage.WallPostRequestListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 這個類別用來設定當手機掉落時通知使用者的方式<br/>
 * 目前規劃的提示告知方式有︰
 * 1.Gmail告知
 * 2.簡訊告知
 * 3.Twitter告知
 * 4.Facebook告知
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
		
		facebook_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClass(SetNotify.this, LoginPage.class);
//				startActivity(intent);
			
				
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

		            	Bundle params = new Bundle();
		            	Date date=new Date();

		            	count++;
//		            	params.putString("message", "嗨！我是你的手機，我掉了！\n剛剛我看了系統時間是︰"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+
//		            			"\n你還記得這個時候我在哪嗎？");
		            	params.putString("message", "test "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
		            	Log.i(tag, "count: "+count);
		            	mAsyncRunner.request("me/feed", params, "POST", new SampleRequestListener());
						
					}else if(facebook.isSessionValid()==false){
						Log.i(tag, "session valid: "+facebook.isSessionValid());
						facebook.authorize(SetNotify.this, new String[]{"publish_stream"}, new SampleDialogListener());//這行要加，因為有時候Session會過期
					}
					
	            	
				}else{
					Log.i(tag, "facebook entity is null");
				}
				
			}
			
		});

	}
    public class SampleRequestListener extends BaseRequestListener {

        public void onComplete(final String response) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Example", "Response: " + response.toString());
//                Log.i(tag, "reponse2-4: "+response.toString().subSequence(2, 4));
                
//                if(response.toString().subSequence(2, 4).equals("id")){
//                	Toast.makeText(SetNotify.this, "發送成功", Toast.LENGTH_SHORT).show();
//                }
                JSONObject json = Util.parseJson(response);
                
                final String name = json.getString("name");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                SetNotify.this.runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(SetNotify.this, "hello, "+name, Toast.LENGTH_SHORT).show();
//                        mText.setText("Hello there, " + name + "!");
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
    
    public class SampleDialogListener extends BaseDialogListener {

        public void onComplete(Bundle values) {
            final String postId = values.getString("post_id");
            if (postId != null) {
                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
//                mAsyncRunner.request(postId, new WallPostRequestListener());
//                mDeleteButton.setOnClickListener(new OnClickListener() {
//                    public void onClick(View v) {
//                        mAsyncRunner.request(postId, new Bundle(), "DELETE",
//                                new WallPostDeleteListener());
//                    }
//                });
//                mDeleteButton.setVisibility(View.VISIBLE);
            } else {
                Log.d("Facebook-Example", "No wall post made");
            }
        }
    }
}
