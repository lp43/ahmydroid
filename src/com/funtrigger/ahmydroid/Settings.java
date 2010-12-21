package com.funtrigger.ahmydroid;

import com.facebook.android.R;
//import com.funtrigger.tools.MyDialog;
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
//	EditTextPreference unlock_password;
	PreferenceScreen preferenceScreen;
	protected String tag="tag";
	Dialog dialog;
	EditText password1,password2,oldpassword;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
        
        preferenceScreen=this.getPreferenceScreen();
        
        
        final EditTextPreference unlock_password=(EditTextPreference)preferenceScreen.findPreference("password");
        if(!MySharedPreferences.getPreference(Settings.this, "unlock_password", "true").equals("")){
        	unlock_password.setDialogIcon(R.drawable.about);
        	unlock_password.setDialogLayoutResource(R.layout.password_to_exit);
        	unlock_password.setDialogTitle(R.string.need_old_password);
        	
        	Log.i(tag, "MySharedPreferences:"+MySharedPreferences.getPreference(Settings.this, "unlock_password", ""));
        	
        	
        	unlock_password.setOnPreferenceClickListener(new OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference preference) {
					oldpassword=(EditText) dialog.findViewById(R.id.password_to_exit);
					return true;
				}
        		
        	});
        }else{
        	 unlock_password.setOnPreferenceClickListener(new OnPreferenceClickListener(){

     			@Override
     			public boolean onPreferenceClick(Preference preference) {
             		
             			Dialog dialog=unlock_password.getDialog();
         				Log.i(tag, "windows: "+String.valueOf(dialog.isShowing()));
         				password1=(EditText) dialog.findViewById(R.id.password_first_type);
         				password2=(EditText) dialog.findViewById(R.id.password_second_type);             		             		     				
         				
     				return true;
     			}
             	
             });
        }
        
        
        
       
        unlock_password.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				if(!password1.equals("")&&!password2.equals("")){
					Log.i(tag, "password1:"+password1.getText().toString());
					Log.i(tag, "password2:"+password2.getText().toString());
					MySharedPreferences.addPreference(Settings.this, "unlock_password", password1.getText().toString());
				}else{
					//TODO 開啟更改視窗
				}
				
				
				
				return true;
			}
        	
        });
    }
}
