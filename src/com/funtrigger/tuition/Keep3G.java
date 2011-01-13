package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Keep3G extends Activity{
	Button tuition_previous,tuition_next,gotoset;
	

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_keep3g);
		super.onCreate(savedInstanceState);
		
		ImageView iv=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		gotoset=(Button)findViewById(com.funtrigger.ahmydroid.R.id.gotoset);
		
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard_int1);
		iv.setImageResource(com.funtrigger.ahmydroid.R.drawable.m3g_en);
		tv.setText(com.funtrigger.ahmydroid.R.string.please_3g);
		Button btn_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		
		gotoset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				//TODO 還沒找到真正設定的路徑
				intent.setAction(android.provider.Settings.ACTION_SETTINGS);
				startActivity(intent);
				
			}
			
		});
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Keep3G.this.finish();
				Keep3G.this.startActivity(new Intent(Keep3G.this, TurnOnLocation.class));
				
			}
			
		});
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
				Keep3G.this.startActivity(new Intent(Keep3G.this, SendLocation.class));
			}
			
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
