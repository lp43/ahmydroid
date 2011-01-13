package com.funtrigger.ahmydroid;

import com.funtrigger.tools.MyDispatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	SmsMessage[] resSMS;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		pdusToSmsMessage(context,intent.getExtras());
		if(getSMScontext().equals("where r u?")){
			Toast.makeText(context, "equals:where r u?", Toast.LENGTH_SHORT).show();
			
			MyDispatcher.messageDispatcher(context,getPhoneNum());
		}

	}
	
	private void pdusToSmsMessage(Context context,Bundle bundle){
		
		
		Object[] pdus=(Object[]) bundle.get("pdus");
		
		resSMS=null;
		resSMS=new SmsMessage[pdus.length];
		
		for(int i=0;i<pdus.length;i++){
			byte[] buffer=(byte[]) pdus[i];
			resSMS[i]=SmsMessage.createFromPdu(buffer);
		}
		
		
//		Toast.makeText(context, "phone is: "+resSMS[0].getDisplayOriginatingAddress(), Toast.LENGTH_SHORT).show();
//		Toast.makeText(context, "context is: "+resSMS[0].getMessageBody(), Toast.LENGTH_SHORT).show();
		
	}
	
	private String getPhoneNum(){
		String num="";
		if(resSMS.length==0){
			num="";
		}else{
			num=resSMS[0].getDisplayOriginatingAddress();
		}
		return num;
	}

	private String getSMScontext(){
		String context="";
		if(resSMS.length==0){
			context="";
		}else{
			context=resSMS[0].getMessageBody();
		}
		return context;
	}
}
