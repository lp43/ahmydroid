package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.TryFallen;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class InterrupSending extends Activity{
	Button tuition_previous,tuition_next,practice;
	static AnimationDrawable frameAnimation,frameAnimation_title;
	private String tag="tag";
//	Handler handler;
//	Runnable run_animation;
	static ImageView img,title_icon;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_interrup_sending);
		super.onCreate(savedInstanceState);
		
		if(MySystemManager.checkMemory(this.getApplicationContext())==true){
			finish();
		}else{
			/*ImageView*/ img=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
			/*ImageView*/ title_icon=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.title_icon);
			
			tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
			tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
			practice=(Button)findViewById(com.funtrigger.ahmydroid.R.id.practice);
			
			
			img.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.interrup_ui_animation);
			title_icon.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.interrup_animation);
			frameAnimation = (AnimationDrawable) img.getBackground();
			frameAnimation_title = (AnimationDrawable) title_icon.getBackground();
//			handler=new Handler();
//			run_animation=new Runnable(){
	//
//				@Override
//				public void run() {
//					frameAnimation.start();
//					frameAnimation_title.start();
//				}
//				
//			};
//			handler.postDelayed(new Runnable(){
	//
//				@Override
//				public void run() {
//					frameAnimation.start();
//					frameAnimation_title.start();
//				}
//				
//			}, 500);
			
//			handler.postDelayed(run_animation,500);
		

			
			practice.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
//					InterrupSending.this.finish();
					InterrupSending.this.startActivity(new Intent(InterrupSending.this, TryFallen.class));
				}
				
			});
			
			
			
			tuition_next.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					InterrupSending.this.startActivity(new Intent(InterrupSending.this, End.class));
					
				}
				
			});
			
			tuition_previous.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					InterrupSending.this.finish();
					InterrupSending.this.startActivity(new Intent(InterrupSending.this, SendLocation.class));
				}
				
			});
		}
		
	
	}

	@Override
	public void onPause() {

//		handler.removeCallbacks(run_animation);
		
		img=null;
		title_icon=null;
		
		System.gc();
		finish();
		super.onPause();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus==true){
			frameAnimation.start();
			frameAnimation_title.start();
		}else{
			frameAnimation.stop();
			frameAnimation_title.stop();
			frameAnimation=null;
			frameAnimation_title=null;
		}
		super.onWindowFocusChanged(hasFocus);
	}

}
