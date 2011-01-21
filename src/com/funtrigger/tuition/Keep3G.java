package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.tools.InternetInspector;

import android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Keep3G extends Activity{
	Button tuition_previous,/*tuition_next,*/gotoset;
	private String tag="tag";
//	private boolean interrup=false;

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_keep3g);
		super.onCreate(savedInstanceState);
		
		ImageView iv=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
//		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		gotoset=(Button)findViewById(com.funtrigger.ahmydroid.R.id.gotoset);
		
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard_int1);
		iv.setImageResource(com.funtrigger.ahmydroid.R.drawable.m3g_en);
		tv.setText(com.funtrigger.ahmydroid.R.string.please_3g);
		
//		tuition_previous.setTag(null);
		
		gotoset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				goto3Gpage();
				
				//當使用者按了點選開啟，代表真的想要設3G，將SetSendData的上一頁鎖死，
				//以防使用者忘記繼續設定「簡訊 」或「Facebook」
				SetSendData.setPreviousToDisable(Keep3G.this,false);
				
				
//				tuition_previous.setTag("lock");
				
			}
			
		});
		
/*		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Keep3G.this.finish();
				Keep3G.this.startActivity(new Intent(Keep3G.this, TurnOnLocation.class));
				
			}
			
		});*/
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
				Keep3G.this.startActivity(new Intent(Keep3G.this, SetSendData.class));
			}
			
		});
	}

	@Override
	protected void onResume() {
//		//如果畫面裡的[前往開啟]被按了
//		//才將[上一頁]鎖起來，否則不鎖
//		if(tuition_previous.getTag()!=null
//				&&tuition_previous.getTag().toString().equals("lock")){
			if(tuition_previous.isEnabled()==false){
			Log.i(tag, "into tuition_previous.getTag()!=null");
			
			final ProgressDialog progress= new ProgressDialog(Keep3G.this);
			progress.setMessage(getString(com.funtrigger.ahmydroid.R.string.wait_for_3g));
			progress.setButton(getString(com.funtrigger.ahmydroid.R.string.reset), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					interrup=true;	
//					tuition_previous.setTag(null);
					goto3Gpage();
					
				}
				
			});
			progress.setOnKeyListener(new OnKeyListener(){

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if(keyCode==KeyEvent.KEYCODE_BACK|keyCode==KeyEvent.KEYCODE_SEARCH){
						goto3Gpage();
					}
					return false;
				}
				
			});
			progress.show();
			
			new Thread(){
				public void run(){
					while(!InternetInspector.checkInternet(Keep3G.this).equals("mobile")
							&&/*interrup==false*/tuition_previous.isEnabled()==false){
					}
					Keep3G.this.runOnUiThread(new Runnable(){
						public void run(){
							progress.dismiss();
							tuition_previous.setEnabled(!InternetInspector.checkInternet(Keep3G.this).equals("mobile")?false:true);
						}
					});
					
				}
			}.start();
			
			
			
			
			
		}else{
			tuition_previous.setEnabled(true);
		}
		
		super.onResume();
	}

	@Override
	public void onPause() {
		
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		finish();
		super.onDestroy();
	}
	
	private void goto3Gpage(){
//		interrup=false;
		//順便將這頁的[上一頁]鈕也鎖起來，讓使用者一定設完3G才回來
		tuition_previous.setEnabled(false);	
		
		Intent intent = new Intent();
		//TODO 還沒找到真正設定的路徑
		intent.setAction(android.provider.Settings.ACTION_SETTINGS);
//		intent.setClass(Keep3G.this, android.provider.Settings.)
//		intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
		
		
		startActivity(intent);
	}

}
