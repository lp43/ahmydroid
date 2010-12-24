package com.funtrigger.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

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
		//將Preferences改指派到總資源裡，而不是自創的data檔裡，這樣才取的到PreferenceActivity的值
		final Editor sharedata = PreferenceManager.getDefaultSharedPreferences(context).edit();
//		final Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.putString(putKey,putValue);
		sharedata.commit();
	}

	public static void addPreference(Context context,String putKey,Boolean putValue){
		//將Preferences改指派到總資源裡，而不是自創的data檔裡，這樣才取的到PreferenceActivity的值
		final Editor sharedata = PreferenceManager.getDefaultSharedPreferences(context).edit();
//		final Editor sharedata = context.getSharedPreferences("data", 0).edit();
		sharedata.putBoolean(putKey,putValue);
		sharedata.commit();
	}
	/**
	 * 取得SharedPreferences資料
	 * @param context 呼叫SharedPreferences的主體
	 * @param getKey 欲取得內容的相對應key值
	 * @return 取到的value值，如果沒取到值回傳defaultValue值
	 */
	public static String getPreference(Context context,String getKey,String defaultValue){
		//將Preferences改指派到總資源裡，而不是自創的data檔裡，這樣才取的到PreferenceActivity的值
		SharedPreferences sharedata=PreferenceManager.getDefaultSharedPreferences(context);
//		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
	
		return sharedata.getString(getKey, defaultValue);
	}
	
	/**
	 * 取得SharedPreferences資料
	 * @param context 呼叫SharedPreferences的主體
	 * @param getKey 欲取得內容的相對應key值
	 * @param defaultValue 若沒抓到值的預設回傳值
	 * @return 取到的value值，如果沒取到值回傳defaultValue值
	 */
	public static Boolean getPreference(Context context,String getKey,boolean defaultValue){
		SharedPreferences sharedata=PreferenceManager.getDefaultSharedPreferences(context);
//		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
	
		return sharedata.getBoolean(getKey, defaultValue);
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
