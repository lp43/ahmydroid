package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.LocationUpdateService;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;
import com.funtrigger.tools.SwitchService;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SetSendData extends Activity{
	static Button tuition_previous/*,tuition_next*/;
	Button setSMS, setFacebook;
	protected String tag="tag";
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_set_send_data);
		super.onCreate(savedInstanceState);
		
		ImageView int1=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		ImageView int2=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic2);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		TextView tuition_cnx1=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_cnx2=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx2);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
//		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		setSMS=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set_sms);
		setFacebook=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set_facebook);
		
		tuition_cnx1.setText(com.funtrigger.ahmydroid.R.string.default_number_context);
		tuition_cnx2.setText(com.funtrigger.ahmydroid.R.string.facebook_feature_instroduction);
		int1.setImageResource(com.funtrigger.ahmydroid.R.drawable.message_pic);
		int2.setImageResource(com.funtrigger.ahmydroid.R.drawable.facebook_pic);
		tv.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard_int3);
		
		//會點設定，直到進來這頁，
		//代表使用者已經前開完系統的定位，也想開GPS定位
		//所以直接幫它把GPS/AGPS選項打開
		MySharedPreferences.addPreference(SetSendData.this, "location", true);
		SwitchService.startService(this, LocationUpdateService.class);
		
		//記錄使用者本來想不想要開啟小安的定位功能
		 MySharedPreferences.addPreference(this, "previous_location_setting", true);
		
		setSMS.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i(tag, "setSMS.onClick");
				//當使用者將3G開啟的初始，這頁的[上一頁]按鈕會被取消，
				//以防使用者忘記繼續設定[Facebook]或[簡訊]
				setPreviousToDisable(SetSendData.this,true);
				
				MyDialog.startMsgDialog(SetSendData.this);
				
			}
			
		});
		
		setFacebook.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//當使用者將3G開啟的初始，這頁的[上一頁]按鈕會被取消，
				//以防使用者忘記繼續設定[Facebook]或[簡訊]
				setPreviousToDisable(SetSendData.this,true);
				
				MyDialog.startFacebookDialog(SetSendData.this, SetSendData.this);
				
			}
			
		});
		
		
		
		/*tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SetSendData.this.finish();
				SetSendData.this.startActivity(new Intent(SetSendData.this, InterrupSending.class));
				
			}
			
		});*/
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				SetSendData.this.finish();
				SetSendData.this.startActivity(new Intent(SetSendData.this, SendLocation.class));
			}
			
		});
	}
	
	/**
	 * 如果使用者進入了3G設定的頁面，也按了3G設定，馬上將SetSendData的上一頁鎖住
	 * 以提示使用者要繼續將[簡訊]和[Facebook]設定完成。
	 * 如果使用者設定了[簡訊]和[Facebook]，才打開
	 * @param status 要將SetSendData的上一頁按鈕設成什麼狀態
	 */
	public static void setPreviousToDisable(Context context,Boolean status){
		if(MySystemManager.checkTaskExist(context, ".SetSendData")==true)tuition_previous.setEnabled(status);
	}
	
	

	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK|keyCode==KeyEvent.KEYCODE_SEARCH){
			if(tuition_previous.isEnabled()==false){
				MyDialog.newDialog(SetSendData.this, getString(com.funtrigger.ahmydroid.R.string.attention), getString(com.funtrigger.ahmydroid.R.string.did_u_set), "warning");
			}
			
		}
		return false;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
