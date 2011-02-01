package com.funtrigger.tuition;

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

import com.funtrigger.ahmydroid.*;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;

public class DropUI extends Activity{
	Button tuition_previous,tuition_next;
	static AnimationDrawable aniimg;
	private final String tag="tag";
//	Handler handler;
	Runnable run_animation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(tag, "DropUI.onCreate");
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_dropui);
		
		super.onCreate(savedInstanceState);
		
		if(MySystemManager.checkMemory(this.getApplicationContext())==true){
			finish();
		}else{
			TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
			tuition_title.setText(com.funtrigger.ahmydroid.R.string.warning_ui);
			
			ImageView bk=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_bk2);
			ImageView fall=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_fall2);
			TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx2);
			tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous2);
			tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next2);
			
			tv.setText(com.funtrigger.ahmydroid.R.string.how_to_use_2);
			
			bk.setBackgroundResource(com.funtrigger.ahmydroid.R.drawable.background);
			fall.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.falling_animation);
			
			
			aniimg=(AnimationDrawable) fall.getBackground();
			
			//之所以建立Handler是因為發現如果一開始就直接aniimg.start()
			//會發生動畫已經啟動，但畫面沒動的BUG
			//所以只好讓動畫在View出現後再開始動
//			handler=new Handler();
//			run_animation=new Runnable(){
	//
//				@Override
//				public void run() {
	//
//					aniimg.start();
//				}	
//			};
			
//			handler.postDelayed(new Runnable(){
	//
//				@Override
//				public void run() {
	//
//					aniimg.start();
//				}	
//			}, 1000);
//			handler.postDelayed(run_animation, 300);

		
			
			tuition_previous.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
//					DropUI.this.finish();
					startActivity(new Intent(DropUI.this, Welcome.class));
				}
				
			});
			
			tuition_next.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					DropUI.this.finish();
					startActivity(new Intent(DropUI.this, PickUp.class));
				}
				
			});
		}
		
		
		
		
	
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus==true){
			aniimg.start();
		}else{
			aniimg.stop();
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	protected void onResume() {
		Log.i(tag, "DropUI.onResume");

		
		super.onResume();
	}

	@Override
	public void onPause() {
//		aniimg.stop();
		System.gc();
//		handler.removeCallbacks(run_animation);
		finish();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i(tag, "DropUI.onDestroy");
		super.onDestroy();
	}


}
