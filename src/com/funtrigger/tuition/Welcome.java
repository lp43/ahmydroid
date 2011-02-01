package com.funtrigger.tuition;

import com.funtrigger.ahmydroid.Ahmydroid;
import com.funtrigger.ahmydroid.How;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MySystemManager;

import android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


public class Welcome extends Activity{
	Button tuition_previous,tuition_next;
	CheckBox into_ahmydroid;
	String tag="tag";
	
    public static void actionShowWelcome(Context context) {
        Intent i = new Intent(context, Welcome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(com.funtrigger.ahmydroid.R.layout.wizard_welcome);
		super.onCreate(savedInstanceState);
		

		if(MySystemManager.checkMemory(this.getApplicationContext())==true){

			finish();
		}else{
			TextView tv=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_cnx1);
			TextView tuition_title=(TextView)findViewById(com.funtrigger.ahmydroid.R.id.tuition_title);
			tuition_previous=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_previous);
			tuition_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
			tuition_title.setText(com.funtrigger.ahmydroid.R.string.welcome);
			into_ahmydroid=(CheckBox)findViewById(com.funtrigger.ahmydroid.R.id.tuition_open_or_not);
			
			tv.setText(com.funtrigger.ahmydroid.R.string.how_to_use_1);
//			Button btn_next=(Button)findViewById(com.funtrigger.ahmydroid.R.id.tuition_next);
			
			if(MySharedPreferences.getPreference(Welcome.this, "into_ahmydroid", false)==false){
				into_ahmydroid.setChecked(false);
			}else{
				into_ahmydroid.setChecked(true);
			}
			
			
			into_ahmydroid.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			        
			        if (((CheckBox) v).isChecked()) {
			        	MySharedPreferences.addPreference(Welcome.this, "into_ahmydroid", true);
			        } else {
			        	MySharedPreferences.addPreference(Welcome.this, "into_ahmydroid", false);
			        }
			    }
			});
			
			tuition_next.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					Welcome.this.startActivity(new Intent(Welcome.this, DropUI.class));
					
				}
				
			});
			
			tuition_previous.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					finish();
//					Ahmydroid.actionShowMain(Welcome.this);
					Intent i = new Intent(Welcome.this, Ahmydroid.class);
			        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			        startActivity(i);
				}
				
			});
		}
		
		
		

			
	}

	@Override
	public void onPause() {
		finish();
		super.onPause();
	}


}
