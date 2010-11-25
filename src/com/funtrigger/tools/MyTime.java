package com.funtrigger.tools;

import java.util.Calendar;
import android.util.Log;

/**
 * 使用系統的Calendar類別來取得時間<br/>
 * 這個類別可以取得「時」「分」「秒」「HHMM」「HHMMSS」，
 * 並且是以雙位數呈獻時間
 * @author simon
 *
 */
public class MyTime {
	
	Calendar calendar;
	private String tag="tag";
	
	public MyTime(){
		calendar= Calendar.getInstance();
	}
	
	/**
	 * 該Method除了取得「小時」外，還會將個位數字前面補上0
	 * @return 「小時」字串
	 */
	public String getHour(){
		return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
	}
	
	/**
	 * 該Method除了取得「分鐘」外，還會將個位數字前面補上0
	 * @return 轉換完的「分鐘」字串
	 */
	public String getMin(){
		String minBuffer=String.valueOf(calendar.get(Calendar.MINUTE));
		String minChanged=minBuffer.length()==1?"0"+minBuffer:minBuffer;
		return minChanged;
	}
	
	/**
	 * 該Method除了取得「秒」外，還會將個位數字前面補上0
	 * @return 轉換完的「秒」字串
	 */
	public String getSec(){
		String secBuffer=String.valueOf(calendar.get(Calendar.SECOND));
		String secChanged=secBuffer.length()==1?"0"+secBuffer:secBuffer;
		return secChanged;
	}
	
	/**
	 * 取得系統時間︰「HH:MM」
	 * @return HH:MM字串
	 */
	public String getHHMM(){
		return getHour()+":"+getMin();
	}
	
	/**
	 * 取得系統時間︰「HH:MM:SS」
	 * @return HH:MM:SS字串
	 */
	public String getHHMMSS(){
		return getHour()+":"+getMin()+":"+getSec();
	}
}
