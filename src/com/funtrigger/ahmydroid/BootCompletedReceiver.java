package com.funtrigger.ahmydroid;

import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.SwitchService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 該廣播接收專門偵測是否為剛開完機狀態<br/>
 * 如果接收到廣播，則啟動Service : FallDetector
 * @author simon
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	private String tag="tag";

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//			Log.i(tag, "boot_completed");
			//當使用者先前有把小安打開，開機才需要啟動摔落偵測告知
			if(MySharedPreferences.getPreference(context, "falldetector_status", "false").equals("true")){
				SwitchService.startService(context, FallDetector.class);
			}
			
		}

	}

}
