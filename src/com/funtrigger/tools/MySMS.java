package com.funtrigger.tools;

import android.content.Context;
import android.telephony.SmsManager;

/**
 * 該類別用來處理簡訊發送
 * @author simon
 *
 */
public class MySMS {

	/**
	 * 發送SMS簡訊
	 * @param context 呼叫簡訊服務的主體
	 * @param phoneNumber 收件者手機號碼
	 * @param messageContext 收件內文
	 */
	public static void sendSMS(Context context,String phoneNumber,String messageContext){
    	SmsManager smsmanager=SmsManager.getDefault();
		smsmanager.sendTextMessage(phoneNumber, null, messageContext, null, null);
	}
}
