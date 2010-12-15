package com.funtrigger.tools;

import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseDialogListener;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;
import com.funtrigger.ahmydroid.Fallen;
import com.funtrigger.ahmydroid.SetNotify;

/**
 * 系統發送各種訊息的管理類別
 * @author simon
 *
 */
public class MyDispatcher {
	Activity activity;
	Context context;
	AsyncFacebookRunner mAsyncRunner;
	private String tag="tag";
	public static String id="";
	
	/**
	 * SMS簡訊發射器
	 */
	public static void messageDispatcher(Context context){
//		MySMS.sendSMS(context, MySharedPreferences.getPreference(context,"message_number",""), 
//		context.getString(R.string.message_context).replace("#location", MyLocation.getLocation(context))
//		+MySharedPreferences.getPreference(context, "message_context", context.getString(R.string.message_last_sentence_ifusernotype)));

	Toast.makeText(context, context.getString(R.string.message_context).replace("#location", MyLocation.getLocation(context))
	+MySharedPreferences.getPreference(context, "message_context", context.getString(R.string.message_last_sentence_ifusernotype)), Toast.LENGTH_LONG).show();
	MyDialog.newToast(context, context.getString(R.string.message_response), R.drawable.message_pic);		
	}


	/**
	 * Facebook訊息發射器
	 */
	public void facebookDispatcher(Context context,Activity activity){
		this.activity=activity;
		this.context=context;
		Facebook facebook=new Facebook("171403682887181");//Facebook網站上別摔小安的應用程式ID
		 mAsyncRunner = new AsyncFacebookRunner(facebook);
//		if(facebook!=null){
//			Log.i(tag, "into facebook!=null");
		
			if(facebook.isSessionValid()==true){
				Log.i(tag, "session valid: "+facebook.isSessionValid());
				//當Session存在時，就不用登入了，直接發文
				sendFacebookMessageContext();
            	
			}else if(facebook.isSessionValid()==false){
				Log.i(tag, "session valid: "+facebook.isSessionValid());
				facebook.authorize(activity, new String[]{"publish_stream"}, new LoginSuccessListener());//這行要加，因為有時候Session會過期
			}
			
        	
//		}else{
//			Log.i(tag, "facebook entity is null");
//		}
	}
	
	/**
	 * 寄送Facebook的發文內容
	 */
	private void sendFacebookMessageContext(){
		Bundle params = new Bundle();	

        MyTime mytime = new MyTime();

    	params.putString("message", context.getString(R.string.facebook_message_head)+"\n"+
    			context.getString(R.string.facebook_message_time).replace("#time", mytime.getHHMMSS())+"\n"+
    			context.getString(R.string.facebook_message_location).replace("#location", MyLocation.getLocation(context))+"\n"+
    			context.getString(R.string.facebook_message_last));

    	mAsyncRunner.request("me/feed", params, "POST", new PostRequestListener());
		
    	MySharedPreferences.addPreference(context, "facebook_data_status", "true");
    	
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
                id = json.getString("id");
                Log.i(tag, "post id: "+id);
                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                    	Toast.makeText(context, context.getString(R.string.post_success), Toast.LENGTH_SHORT).show();
                    	MyDialog.newToast(context, context.getString(R.string.post_success2), R.drawable.facebook_pic);

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
        	sendFacebookMessageContext();
        }
    }
}
