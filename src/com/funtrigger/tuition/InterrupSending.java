package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.TryFallen;

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


public class InterrupSending extends Activity{
	Button tuition_previous,tuition_next,practice;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_interrup_sending);
		super.onCreate(savedInstanceState);
		
		ImageView img=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		ImageView title_icon=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.title_icon);
		
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		practice=(Button)findViewById(com.funtrigger.ahmydroid.R.id.practice);
		
		
		img.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.interrup_ui_animation);
		title_icon.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.interrup_animation);
		final AnimationDrawable frameAnimation = (AnimationDrawable) img.getBackground();
		final AnimationDrawable frameAnimation_title = (AnimationDrawable) title_icon.getBackground();
		Handler handler=new Handler();
		handler.postDelayed(new Runnable(){

			@Override
			public void run() {
				frameAnimation.start();
				frameAnimation_title.start();
			}
			
		}, 500);
	

		
		practice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
//				InterrupSending.this.finish();
				InterrupSending.this.startActivity(new Intent(InterrupSending.this, TryFallen.class));
			}
			
		});
		
		
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				InterrupSending.this.finish();
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

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
