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


public class TurnOnLocation extends Activity{
	Button tuition_previous,tuition_next,gotoset;
	

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_turnon_location);
		super.onCreate(savedInstanceState);
		
		ImageView iv=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		gotoset=(Button)findViewById(com.funtrigger.ahmydroid.R.id.gotoset);
		
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard_int2);
		iv.setImageResource(com.funtrigger.ahmydroid.R.drawable.mlocation_en);
		tv.setText(com.funtrigger.ahmydroid.R.string.please_location);
		
		gotoset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				
			}
			
		});
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				TurnOnLocation.this.finish();
				TurnOnLocation.this.startActivity(new Intent(TurnOnLocation.this, SetSendData.class));
				
			}
			
		});
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
				TurnOnLocation.this.startActivity(new Intent(TurnOnLocation.this, Keep3G.class));
			}
			
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
