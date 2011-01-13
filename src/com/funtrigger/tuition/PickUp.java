package com.funtrigger.tuition;

import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MySharedPreferences;

import android.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class PickUp extends Activity{

	Button tuition_previous,tuition_next,set_pick_up_now;
	AnimationDrawable aniimg;
	protected String tag="tag";
	static TextView tuition_pick_up;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_pickup);
		super.onCreate(savedInstanceState);
		
		
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.pick_feature);
		
		ImageView bk=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic2);
		ImageView fall=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_fall3);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx2);
		tuition_pick_up=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pick_up);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous2);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next2);
		set_pick_up_now=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set_pick_up_now);
		
		bk.setBackgroundResource(com.funtrigger.ahmydroid.R.drawable.background);
		fall.setBackgroundResource(com.funtrigger.ahmydroid.R.anim.falling_animation);
		tv.setText(com.funtrigger.ahmydroid.R.string.pick_up_wizard);
		tuition_pick_up.setText(MySharedPreferences.getPreference(PickUp.this, "pick_context", getString(com.funtrigger.ahmydroid.R.string.pick_default_context)));
		
		aniimg=(AnimationDrawable) fall.getBackground();
		
	
		Animation pick_up_animation=AnimationUtils.loadAnimation(this, com.funtrigger.ahmydroid.R.anim.fade);
		tuition_pick_up.startAnimation(pick_up_animation);
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PickUp.this.finish();
				startActivity(new Intent(PickUp.this, DropUI.class));
			}
			
		});
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {	
				PickUp.this.finish();
				startActivity(new Intent(PickUp.this, SendLocation.class));				
			}
			
		});
		
		set_pick_up_now.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				MyDialog.modifyPickUP(PickUp.this);
				
			}
			
		});
	}
	

	
	@Override
	protected void onResume() {
		new Thread(){
			public void run(){
				for(int i=0;i<50;i++){
					if(aniimg.isRunning()==false){
						aniimg.start();
					}
				
				}
			}
			
		}.start();
		super.onResume();
	}

	@Override
	public void onPause() {
		aniimg.stop();
		super.onPause();
	}
	
	/**
	 * 提供給Mydialog.modifyPickUP(PickUp.this)即時更改拾獲告知內文
	 * @param text 要修改的文字內容
	 */
	public static void modify_pick_up(String text){
		tuition_pick_up.setText(text);
	}
}
