package com.funtrigger.ahmydroid;

import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;
import com.funtrigger.tuition.Welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;

public class How extends Activity {

	private String tag="tag";



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
//		String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);

		
		if(usedMegs>10){
			Log.i(tag, "Memory is: "+usedMegs+"MB, cancel into Welcome.class");
			finish();
			Ahmydroid.actionShowMain(How.this);
			MySharedPreferences.addPreference(How.this, "into_ahmydroid", true);
		}else{
			if(MySharedPreferences.getPreference(How.this, "into_ahmydroid", false)==true){
				finish();
				Ahmydroid.actionShowMain(How.this);
			
			}else{
				finish();
				Welcome.actionShowWelcome(this);
				
			
			}
		}
		
		
		
		
	}

	
}
