package com.funtrigger.ahmydroid;

import com.facebook.android.R;
//import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MyDialog;
import com.funtrigger.tools.MySharedPreferences;

import android.app.Dialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends PreferenceActivity {
	PreferenceScreen preferenceScreen;
	EditTextPreference unlock_password;
	protected String tag="tag";
	Dialog dialog;
	EditText password1,password2,oldpassword;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
        
        preferenceScreen=this.getPreferenceScreen();  
        
        unlock_password=(EditTextPreference)preferenceScreen.findPreference("password");
        
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
}
