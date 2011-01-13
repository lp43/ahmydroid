package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MySystemManager;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class SetSendData extends Activity{
	Button tuition_previous,tuition_next,setSMS,setFacebook;
	protected String tag="tag";
	
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_set_send_data);
		super.onCreate(savedInstanceState);
		
		ImageView int1=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic1);
		ImageView int2=(ImageView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_pic2);
		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		TextView tuition_cnx1=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_cnx2=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx2);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		setSMS=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set_sms);
		setFacebook=(Button)findViewById(com.funtrigger.ahmydroid.R.id.set_facebook);
		
		tuition_cnx1.setText(com.funtrigger.ahmydroid.R.string.default_number_context);
		tuition_cnx2.setText(com.funtrigger.ahmydroid.R.string.facebook_feature_instroduction);
		int1.setImageResource(com.funtrigger.ahmydroid.R.drawable.message_pic);
		int2.setImageResource(com.funtrigger.ahmydroid.R.drawable.facebook_pic);
		tv.setText(com.funtrigger.ahmydroid.R.string.sendlocation_wizard_int3);
		
		
		setSMS.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i(tag, "setSMS.onClick");
				MyDialog.startMsgDialog(SetSendData.this);
				
			}
			
		});
		
		setFacebook.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				MyDialog.startFacebookDialog(SetSendData.this, SetSendData.this);
				
			}
			
		});
		
		
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				SetSendData.this.finish();
				SetSendData.this.startActivity(new Intent(SetSendData.this, InterrupSending.class));
				
			}
			
		});
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				SetSendData.this.finish();
				SetSendData.this.startActivity(new Intent(SetSendData.this, TurnOnLocation.class));
			}
			
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
