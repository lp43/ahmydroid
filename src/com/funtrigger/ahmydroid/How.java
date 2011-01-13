package com.funtrigger.ahmydroid;

import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tuition.Welcome;

import android.app.Activity;
import android.os.Bundle;

public class How extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		if(MySharedPreferences.getPreference(How.this, "tuition_open_or_not", true)==true){
			finish();
			Welcome.actionShowWelcome(this);
		}else{
			finish();
			Ahmydroid.actionShowMain(this);
		}
		
	}

	
}
