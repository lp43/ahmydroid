package com.funtrigger.ahmydroid;

import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;
import com.funtrigger.tuition.Welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class How extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		if(MySharedPreferences.getPreference(How.this, "tuition_open_or_not", true)==true){
			finish();
//			Ahmydroid.actionShowMain(How.this);
			Intent i = new Intent(this, Welcome.class);
	        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(i);
		}else{
			finish();
//			Ahmydroid.actionShowMain(this);
			
			Intent i = new Intent(How.this, Ahmydroid.class);
	        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(i);
		}
		
	}

	
}
