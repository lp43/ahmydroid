package com.funtrigger.ahmydroid;

import java.util.regex.Pattern;

import com.facebook.android.R;
//import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MyDispatcher;
import com.funtrigger.tools.MyLocation;
import com.funtrigger.tools.MySharedPreferences;
import com.funtrigger.tools.MyTime;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends PreferenceActivity {
	PreferenceScreen preferenceScreen;
	EditTextPreference unlock_password,message_context_setting;
	CheckBoxPreference location,message_checkbox,facebook_checkbox;
	Preference facebook_set;
	protected String tag="tag";
	Dialog dialog;
	EditText password1,password2,oldpassword,msg_num,msg_context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "Settings.onCreate");
        addPreferencesFromResource(R.xml.settings);
        
        preferenceScreen=this.getPreferenceScreen();  
        
        unlock_password=(EditTextPreference)preferenceScreen.findPreference("password");
        
        location=(CheckBoxPreference) preferenceScreen.findPreference("location");
        message_checkbox=(CheckBoxPreference) preferenceScreen.findPreference("message");
        facebook_checkbox=(CheckBoxPreference) preferenceScreen.findPreference("facebook");
        
        message_context_setting=(EditTextPreference)preferenceScreen.findPreference("message_context_setting");
        facebook_set=preferenceScreen.findPreference("facebook_set");
        
         //設定單擊unlock_password的視窗顯示狀態
        if(!MySharedPreferences.getPreference(Settings.this, "unlock_password", "").equals("")){
        	Log.i(tag, "into password!=null");
        	//若儲存槽有值
        	setUnlock_PasswordClickIsOLD();
        	
        }else{
        	//儲存槽沒值
        	setUnlock_PasswordClickIsNEW();
        }
        
   
        unlock_password.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				EditText pass1=(EditText) dialog.findViewById(R.id.password_first_type);
					//用視窗是否抓的到雙密碼的第1欄位去判斷是否為新增密碼的視窗
					if(pass1!=null){
						if(checkPassword()){
							Log.i(tag, "password1:"+password1.getText().toString());
							Log.i(tag, "password2:"+password2.getText().toString());
							MySharedPreferences.addPreference(Settings.this, "unlock_password", password1.getText().toString());
							MyDialog.newToast(Settings.this,getString(R.string.password_saved),R.drawable.verify);					
			        	
				        	setUnlock_PasswordClickIsOLD();	      
				        	
						}else{
							MyDialog.newDialog(Settings.this, getString(R.string.attention), getString(R.string.type_dismatch), "warning");
						}
					}else{
				     //就是輸入舊密碼的視窗
						if(oldpassword.getText().toString().equals(MySharedPreferences.getPreference(Settings.this, "unlock_password", ""))){
							//如果帳密相符，彈出修改視窗
							MyDialog.createNewPassword(Settings.this,R.string.reset_new_password);			
							
						}else{
							//如果輸入的密碼與舊密碼不符，則跳出輸入錯誤視窗
							MyDialog.newDialog(Settings.this,getString(R.string.attention),getString(R.string.type_wrong_password),"warning");
						}
					}
				
				return true;
			}
        	
        });
        
        location.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(MyLocation.getBestProvider(Settings.this)==null){
				//系統若沒有開啟任何的定位，則將[定位功能]、[簡訊告知]、[Facebook告知]3項checkbox都取消掉
				location.setChecked(false);
				message_checkbox.setChecked(false);
				facebook_checkbox.setChecked(false);
				
				MyDialog.newOneBtnDialog(Settings.this, R.drawable.warning,getString(R.string.attention), getString(R.string.location_dialog_context), getString(R.string.go_to),new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						gotoSettingPage();
					}
				
				});				
					
				
				}else{
					if(location.isChecked()==true){
//						Log.i(tag, "set location checkbox is true");
					}else{
						Log.i(tag, "set location checkbox is false");
						message_checkbox.setChecked(false);
						facebook_checkbox.setChecked(false);
					}
				}
				
				return true;
			}
        	
        });
        
        message_context_setting.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				dialog=message_context_setting.getDialog();
				//讓鍵盤暫時隱藏
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

				msg_num=(EditText) dialog.findViewById(R.id.type_message_number);
				msg_context=(EditText) dialog.findViewById(R.id.type_message_context);
				TextView msg_sys_cnx=(TextView) dialog.findViewById(R.id.msg_sys_ctx);
				
				MyTime mytime=new MyTime();
				//將時間和地點換成當下的值
				msg_sys_cnx.setText(msg_sys_cnx.getText().toString().replace("#time", mytime.getHHMM()));
				msg_sys_cnx.setText(msg_sys_cnx.getText().toString().replace("#location", MyLocation.getLocation(Settings.this)));
				
				if(!MySharedPreferences.getPreference(Settings.this, "message_number", "").equals("")){
					msg_num.setText(MySharedPreferences.getPreference(Settings.this, "message_number", ""));
				}

					msg_context.setText(MySharedPreferences.getPreference(Settings.this, "message_context", ""));

				
				return true;
			}
        	
        });
        
        message_context_setting.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
					MySharedPreferences.addPreference(Settings.this, "message_context", msg_context.getText().toString());
				if(isNumeric(msg_num.getText().toString())){
					MySharedPreferences.addPreference(Settings.this, "message_number", msg_num.getText().toString());	
				}else{
					MyDialog.newDialog(Settings.this, getString(R.string.attention), getString(R.string.wrong_phone_number), "warning");	
				}
				Log.i(tag, "msg_num: "+MySharedPreferences.getPreference(Settings.this, "message_number", ""));
				Log.i(tag, "msg_context: "+MySharedPreferences.getPreference(Settings.this, "message_context", ""));
				
				return true;
			}
        	
        });      
        
//        if(MySharedPreferences.getPreference(Settings.this, "facebook_set", false).equals(true)){
//        	facebook_set.setSummary(R.string.facebook_data_set);		
//        }else{
//        	facebook_set.setSummary(R.string.facebook_data_not_set);
//        }
        
        
        
        CookieSyncManager.createInstance(Settings.this);
        final CookieManager cookie=CookieManager.getInstance();
        Log.i(tag, "Cookie: "+cookie.getCookie("http://www.facebook.com"));
        
        facebook_set.setSummary(cookie.getCookie("http://www.facebook.com")==null?R.string.facebook_data_not_set:R.string.facebook_data_set);
        
//        if(!cookie.getCookie("http://www.facebook.com").equals("")){
//        	facebook_set.setSummary(R.string.facebook_data_set);
//        }else{
//        	facebook_set.setSummary(R.string.facebook_data_not_set);
//        }
        
        
        facebook_set.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				new AlertDialog.Builder(Settings.this)
				.setTitle(R.string.please_choice)
				.setItems(new String[]{getString(R.string.set)+getString(R.string.and)+getString(R.string.test),getString(R.string.clear_username),getString(R.string.help)}, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:
							MyDispatcher mydispatcher = new MyDispatcher();
							mydispatcher.facebookDispatcher(Settings.this.getApplicationContext(), Settings.this);
							new Thread(){
								public void run(){
									while(MyDispatcher.id.equals("")){}
									Settings.this.runOnUiThread(new Runnable(){
										public void run(){
//											MySharedPreferences.addPreference(Settings.this, "facebook_set", true);
											facebook_set.setSummary(R.string.facebook_data_set);		
										}
									});
								}
							}.start();
							
							break;
						case 1:
							CookieSyncManager.createInstance(Settings.this);							
							CookieManager cookie=CookieManager.getInstance();
							cookie.removeAllCookie();
							
//							MySharedPreferences.addPreference(Settings.this, "facebook_set", false);
							facebook_set.setSummary(R.string.facebook_data_not_set);
							
							//AccountManager管不到FB
//							AccountManager am=AccountManager.get(SetNotify.this);
//							
//							for(Account ac:am.getAccounts()){
//								Log.i(tag, "ac name: "+ac.name);
//								am.removeAccount(ac, null, null);
//							}
							MyDialog.newToast(Settings.this, getString(R.string.username_cleared), 0);
							break;
						case 2:
							MyDialog.helpDialog(Settings.this,R.drawable.facebook_spic, getString(R.string.facebook_feature),getString(R.string.facebook_instruction));
							break;
						}
						
					}	
					
				})
				.setPositiveButton(R.string.cancel, new  DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {	
				}
                	
                })
				.show();		
				return true;
			}
        });
    }

    
	@Override
	protected void onResume() {
		Log.i(tag, "Settings.onResume");
		//如果程式發現系統GPS被關，而程式還是開著的設定時的動作
		if(location.isChecked()==true & MyLocation.getBestProvider(Settings.this)==null){
			Log.i(tag,"location & provider different,into setChecked(false)");
			//如果偵測到統沒開定位，而程式有開，則將[定位功能]、[簡訊告知]、[Facebook告知]都關閉
			location.setChecked(false);
			message_checkbox.setChecked(false);
			facebook_checkbox.setChecked(false);
		}
		super.onResume();
	}
	

	/**
     * 檢查輸入2次的密碼是否相符
     * @return 若相符，回傳True，否則回傳False
     */
    private boolean checkPassword(){
    	boolean equal_value=false;
    	if(password1.getText().toString().equals(password2.getText().toString())){
    		equal_value=true;
    	}
    	Log.i(tag, "pass1==pass2? "+String.valueOf(equal_value));
		return equal_value;
    }
    
    private void setUnlock_PasswordClickIsNEW(){
    	 unlock_password.setOnPreferenceClickListener(new OnPreferenceClickListener(){

   			@Override
   			public boolean onPreferenceClick(Preference preference) {
   				Log.i(tag, "into new password onPreferenceClick");
  				Log.i(tag, "unlock pass: "+MySharedPreferences.getPreference(Settings.this, "unlock_password", ""));
  				    dialog=unlock_password.getDialog();
//      			Log.i(tag, "windows: "+String.valueOf(dialog.isShowing()));
      				password1=(EditText) dialog.findViewById(R.id.password_first_type);
      				password2=(EditText) dialog.findViewById(R.id.password_second_type);
      				

					return true;  
   			};
    	 });

    	}
   
    private void setUnlock_PasswordClickIsOLD(){
		unlock_password.setDialogIcon(R.drawable.about);
    	unlock_password.setDialogLayoutResource(R.layout.password_to_exit);
    	unlock_password.setDialogTitle(R.string.need_old_password);
    	
     	 unlock_password.setOnPreferenceClickListener(new OnPreferenceClickListener(){

  			@Override
  			public boolean onPreferenceClick(Preference preference) {
  			  					
					//如果不是空值，要求使用者輸入舊密碼
	   	        	unlock_password.setDialogIcon(R.drawable.about);
	   	        	unlock_password.setDialogLayoutResource(R.layout.password_to_exit);
	   	        	unlock_password.setDialogTitle(R.string.need_old_password);
	   	        	
	   	        	
	   	        	dialog=unlock_password.getDialog();
					oldpassword=(EditText) dialog.findViewById(R.id.password_to_exit);
					
	   	        	Log.i(tag, "MySharedPreferences:"+MySharedPreferences.getPreference(Settings.this, "unlock_password", ""));
	   	        	
	   	        	
	   				return true;
  				}
     	 });
    }
    
    private void gotoSettingPage(){
    	Intent intent = new Intent();
		intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
    }
    
	/**
	 * 檢查輸入是否為數字
	 * @param 傳入欲檢查的字串
	 * @return 若符合，回傳true
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
//	    Log.i(tag, "isNumeric:"+String.valueOf(pattern.matcher(str).matches()));
	    return pattern.matcher(str).matches();   
	 } 
}
