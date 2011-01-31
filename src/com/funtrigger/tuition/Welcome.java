package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.How;
import com.funtrigger.tools.MySharedPreferences;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class Welcome extends Activity{
	Button tuition_previous,tuition_next;
	CheckBox tuition_open_or_not;
	
	
//    public static void actionShowWelcome(Context context) {
//        Intent i = new Intent(context, Welcome.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(i);
//    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_welcome);
		super.onCreate(savedInstanceState);
		

		TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
		TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
		tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
		tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		tuition_title.setText(com.funtrigger.ahmydroid.R.string.welcome);
		tuition_open_or_not=(CheckBox)findViewById(com.funtrigger.ahmydroid.R.id.tuition_open_or_not);
		
		tv.setText(com.funtrigger.ahmydroid.R.string.how_to_use_1);
		Button btn_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
		
		if(MySharedPreferences.getPreference(Welcome.this, "tuition_open_or_not", true)==false){
			tuition_open_or_not.setChecked(true);
		}else{
			tuition_open_or_not.setChecked(false);
		}
		
		
		tuition_open_or_not.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		        
		        if (((CheckBox) v).isChecked()) {
		        	MySharedPreferences.addPreference(Welcome.this, "tuition_open_or_not", false);
		        } else {
		        	MySharedPreferences.addPreference(Welcome.this, "tuition_open_or_not", true);
		        }
		    }
		});
		
		tuition_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Welcome.this.finish();
				Welcome.this.startActivity(new Intent(Welcome.this, DropUI.class));
				
			}
			
		});
		
		tuition_previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
//				Ahmydroid.actionShowMain(Welcome.this);
				Intent i = new Intent(Welcome.this, Ahmydroid.class);
		        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(i);
			}
			
		});
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


}
