package com.funtrigger.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 該類別簡化了使用SharedPreferences的步驟
 * @author simon
 */
public class MySharedPreferences {
	/**
	 * 放入字串進SharedPreferences
	 * @param context 呼叫SharedPreferences的主體
	 * @param putKey 欲放入的key值
	 * @param putValue 欲放入的value值
	 */
	public static void addPreference(Context context,String putKey,String putValue){
		final Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.putString(putKey,putValue);
		sharedata.commit();
	}
	
	/**
	 * 取得SharedPreferences資料
	 * @param context 呼叫SharedPreferences的主體
	 * @param getKey 欲取得內容的相對應key值
	 * @return 取到的value值，如果沒取到值回傳null字串
	 */
	public static String getPreference(Context context,String getKey,String defaultValue){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
	
		return sharedata.getString(getKey, defaultValue);
	}
	
	/**
	 * 移除指定的Key值
	 * @param context 呼叫SharedPreferences的主體
	 * @param indiateKeyName 指定的Key值名稱
	 * @return 刪除成功，回傳true,否則為false
	 */
	public static boolean removeKey(Context context,String indiateKeyName){
		boolean returnValue=false;
		final Editor sharedata = context.getSharedPreferences("data", 0).edit();
		if(sharedata.remove(indiateKeyName)!=null){
			sharedata.commit();
			returnValue=true;
		}

		return returnValue;
	}
}
