package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class End extends Activity{
	Button tuition_previous,tuition_next;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_end);
		super.onCreate(savedInstanceState);
		
		ImageView img=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);

		
		img.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.touch_android_animation);
		final AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
		
		Handler handler=new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				frameAnimation.start();
			}
			
		}, 500);
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				End.this.finish();
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

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
