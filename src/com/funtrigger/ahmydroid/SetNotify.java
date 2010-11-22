package com.funtrigger.ahmydroid;

import com.facebook.android.LoginPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 這個類別用來設定當手機掉落時通知使用者的方式<br/>
 * 目前規劃的提示告知方式有︰
 * 1.Gmail告知
 * 2.簡訊告知
 * 3.Twitter告知
 * 4.Facebook告知
 * 
 * @author simon
 */
public class SetNotify extends Activity {
	Button gmail_btn,message_btn,twitter_btn,facebook_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_notify);
		gmail_btn=(Button) findViewById(R.id.gmail_btn);
		message_btn=(Button) findViewById(R.id.msg_btn);
		twitter_btn=(Button) findViewById(R.id.twitter_btn);
		facebook_btn=(Button) findViewById(R.id.facebook_btn);
		
		//設定初始為灰色
		gmail_btn.getBackground().setAlpha(50);
		message_btn.getBackground().setAlpha(50);
		twitter_btn.getBackground().setAlpha(50);
		facebook_btn.getBackground().setAlpha(50);
		
		gmail_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SetNotify.this, LoginPage.class);
				startActivity(intent);
			}
			
		});
	}

}
