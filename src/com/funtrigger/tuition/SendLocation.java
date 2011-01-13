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


public class SendLocation extends Activity{
	Button tuition_previous,tuition_next,set3G;
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_send_location);
		super.onCreate(savedInstanceState);
		
		ImageView iv=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		set3G=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set3G);
		
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.send_txt);
		iv.setImageResource(com.funtrigger.ahmydroid.R.drawable.notify_all);
		tv.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard);

		
		set3G.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SendLocation.this.finish();
				SendLocation.this.startActivity(new Intent(SendLocation.this, Keep3G.class));
			}
			
		});
		
		
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SendLocation.this.finish();
				SendLocation.this.startActivity(new Intent(SendLocation.this, InterrupSending.class));
				
			}
			
		});
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				SendLocation.this.finish();
				SendLocation.this.startActivity(new Intent(SendLocation.this, PickUp.class));
			}
			
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
