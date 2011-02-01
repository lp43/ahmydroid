package com.funtrigger.tuition;

import java.lang.ref.WeakReference;

import com.funtrigger.ahmydroid.Ahmydroid;
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


public class End extends Activity{
	Button tuition_previous,tuition_next;
	static AnimationDrawable frameAnimation;
	private String tag="tag";
//	Handler handler;
//	Runnable run_animation;
	static ImageView img;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_end);
		super.onCreate(savedInstanceState);
		
		
		if(MySystemManager.checkMemory(this.getApplicationContext())==true){
			finish();
		}else{
			/*ImageView */img=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
			tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
			tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);

			
			img.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.touch_android_animation);
			frameAnimation = (AnimationDrawable) img.getBackground();
			
//			handler=new Handler();
			
//			handler.postDelayed(new Runnable(){
	//
//				@Override
//				public void run() {
//					frameAnimation.start();
//				}
//				
//			}, 500);
//			run_animation=new Runnable(){
	//
//				@Override
//				public void run() {
//					frameAnimation.start();
//				}
//				
//			};
			
//			handler.postDelayed(run_animation,500);
			
			tuition_next.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
				
					End.this.startActivity(new Intent(End.this, Ahmydroid.class));
					
				}
				
			});
			
			tuition_previous.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					End.this.finish();
					End.this.startActivity(new Intent(End.this, InterrupSending.class));
				}
				
			});
		}

	}

	@Override
	public void onPause() {
		Log.i(tag,"End.opPuase");
//		frameAnimation.stop();
//		handler.removeCallbacks(run_animation);
		img.clearAnimation();
	
		
		img=null;
		
		System.gc();
		finish();
		
		super.onPause();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus==true){
			frameAnimation.start();
		}else{
			frameAnimation.stop();
			frameAnimation=null;
		}
		super.onWindowFocusChanged(hasFocus);
	}

}
