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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends PreferenceActivity {
	private PreferenceScreen preferenceScreen;
//	private EditTextPreference unlock_password;
	private CheckBoxPreference location,message_checkbox;
	private static CheckBoxPreference facebook_checkbox;
	private static Preference facebook_set;
	private Preference message_context_setting;
	private String tag="tag";
	private Dialog dialog;
	private EditText password1,password2,oldpassword,msg_num,msg_context;
	private static CookieManager cookie;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag, "Settings.onCreate");
        addPreferencesFromResource(R.xml.settings);
        
        preferenceScreen=this.getPreferenceScreen();  
        
//        unlock_password=(EditTextPreference)preferenceScreen.findPreference("password");
        
        location=(CheckBoxPreference) preferenceScreen.findPreference("location");
        message_checkbox=(CheckBoxPreference) preferenceScreen.findPreference("message");
        facebook_checkbox=(CheckBoxPreference) preferenceScreen.findPreference("facebook");
        
        message_context_setting=preferenceScreen.findPreference("message_context_setting");
        facebook_set=preferenceScreen.findPreference("facebook_set");
        
        //將Cookie先叫出來，好讓等等的Facebook能用
        CookieSyncManager.createInstance(Settings.this);
        cookie=CookieManager.getInstance();

        
        //顯示Message和Facebook為未設定或已設定
        message_context_setting.setSummary(MySharedPreferences.getPreference(this, "message_number", "").equals("")?R.string.data_not_set:R.string.data_set);
        if(cookie.getCookie("http://www.facebook.com")==null||
				cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0)==-1){	
        	facebook_set.setSummary(R.string.data_not_set);
        }else{
        	facebook_set.setSummary(R.string.data_set);
        }
        
        
       /*  //設定單擊unlock_password的視窗顯示狀態
        if(!MySharedPreferences.getPreference(Settings.this, "unlock_password", "").equals("")){
        	Log.i(tag, "into password!=null");
        	//若儲存槽有值
        	setUnlock_PasswordClickIsOLD();
        	
        }else{
        	//儲存槽沒值
        	setUnlock_PasswordClickIsNEW();
        }*/
        
   
      /*  unlock_password.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

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
        	
        });*/
        
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
        
        //本來點選message_checkbox只要純勾選和反勾選
        //但為了讓第1次使用的使用者能更方便，程式這一段做到
        //當偵測到是第1次使用，沒有值時，點message_checkbox可以直接開設定值
        message_checkbox.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				if(MySharedPreferences.getPreference(Settings.this, "message_number", "").equals("")){
					Log.i(tag, "into message_checkbox==null");				
					message_checkbox.setChecked(false);
					startMsgDialog();
				}
				return false;
			}
        	
        });
        
        
        message_context_setting.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {				
				
				startMsgDialog();
				return true;
			}
        	
        });
   

        //Facebook勾選選項
        facebook_checkbox.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {

				if(cookie.getCookie("http://www.facebook.com")==null||
						cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0)==-1){	
					setFacebookStatus(false);
					
					Log.i(tag, "cookie: "+cookie.getCookie("http://www.facebook.com"));
					startFacebookDialog();
					
				}
				return false;
			}
        	
        });
        
        //Facebook設定
        facebook_set.setOnPreferenceClickListener(new OnPreferenceClickListener(){

			@Override
			public boolean onPreferenceClick(Preference preference) {
				startFacebookDialog();
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
    
   /* private void setUnlock_PasswordClickIsNEW(){
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

    	}*/
   
   /* private void setUnlock_PasswordClickIsOLD(){
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
    }*/
    
    /**
     * 前往Android系統的AGPS/GPS開啟畫面
     */
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
	
	/**
	 * 將Message的設定畫面叫出來
	 */
	private void startMsgDialog(){
		LayoutInflater factory= LayoutInflater.from(Settings.this);
		final View EntryView = factory.inflate(R.layout.context_to_message, null);
		
		MyTime mytime=new MyTime();
		//將時間和地點換成當下的值
		
		//將簡訊內文的第1行裡的經緯度抓一抓
		TextView sys_cnx=(TextView) message_checkbox.getView(EntryView,null).findViewById(R.id.msg_sys_ctx);
		sys_cnx.setText(sys_cnx.getText().toString().replace("#time", mytime.getHHMM()));
		sys_cnx.setText(sys_cnx.getText().toString().replace("#location", MyLocation.getLocation(Settings.this)));
		
		final EditText num=(EditText) message_checkbox.getView(EntryView,null).findViewById(R.id.type_message_number);
		final EditText msg_cnx=(EditText) message_checkbox.getView(EntryView,null).findViewById(R.id.type_message_context);
		
		//如果手機號碼和內文之前有存，也顯示出來
		num.setText(MySharedPreferences.getPreference(this, "message_number", ""));
		msg_cnx.setText(MySharedPreferences.getPreference(this, "message_context", getString(R.string.type_message_context)));
		
		 new AlertDialog.Builder(Settings.this)
            .setTitle(getString(R.string.message_set))
		    .setIcon(R.drawable.message_spic)
		    .setView(EntryView)
		    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,
						int which) {				
					
					//不管電話號碼對不對，簡訊的內文都先存進去再說
					MySharedPreferences.addPreference(Settings.this, "message_context", msg_cnx.getText().toString());
					
//					Log.i(tag, "edittext= "+num.getText().toString());
					if(num.getText().toString().equals("")){
						MySharedPreferences.addPreference(Settings.this, "message_number", "");
						message_checkbox.setChecked(false);
						message_context_setting.setSummary(R.string.data_not_set);
					}else if(!num.getText().toString().equals("")){
						
						if(isNumeric(num.getText().toString())){
							MySharedPreferences.addPreference(Settings.this, "message_number", num.getText().toString());
							message_context_setting.setSummary(R.string.data_set);
							message_checkbox.setChecked(true);
						}else{
							MyDialog.newDialog(Settings.this, getString(R.string.attention), getString(R.string.wrong_phone_number), "warning");
							message_checkbox.setChecked(false);
						}
						
					}
					
				}
		    	
		    })
		    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					
						if(MySharedPreferences.getPreference(Settings.this, "message_number", "").equals("")){
							//如果沒資料了，則將勾勾取消
							message_checkbox.setChecked(false);
						}			
					
				}
		    }
		    )
   .show();
	}
	
	
	/**
	 * 將Facebook設定畫面叫出來
	 */
	private void startFacebookDialog(){
		
		MyDialog.newOneBtnDialog(Settings.this, R.drawable.facebook_spic, getString(R.string.facebook_set), getString(R.string.facebook_instruction), getString(R.string.ok), new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//如果檢查Cookie沒有資料,設定帳密
				if(cookie.getCookie("http://www.facebook.com")==null||
						cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0)==-1){	
					setFacebookStatus(false);
					
					//連到Facebook去設定帳密
					MyDispatcher mydispatcher = new MyDispatcher();
					mydispatcher.facebookDispatcher(Settings.this.getApplicationContext(), Settings.this);
					
				}else{
					//出現視窗問是否要清除的詢問畫面
					Log.i(tag, "cookie: "+cookie.getCookie("http://www.facebook.com"));
					Log.i(tag, "cookie INDEX: "+cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0));
					
					
					MyDialog.newTwoBtnDialog(Settings.this, R.drawable.warning, getString(R.string.attention), getString(R.string.has_old_cookie), 
							getString(R.string.ok), new DialogInterface.OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//先清除Cookie，並將顯示畫面的值設一設
									cookie.removeAllCookie();

									facebook_set.setSummary(R.string.data_not_set);
									facebook_checkbox.setChecked(false);
									
									//再連到Facebook去設定帳密
									MyDispatcher mydispatcher = new MyDispatcher();
									mydispatcher.facebookDispatcher(Settings.this.getApplicationContext(), Settings.this);
								}
						
					}, getString(R.string.cancel), new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//按取消時，什麼事也不做
						}
						
					});
				}
					
				
				
			}
			
		});
		
//		new AlertDialog.Builder(Settings.this)
//		.setTitle(R.string.please_choice)
//		.setItems(new String[]{getString(R.string.set)+getString(R.string.and)+getString(R.string.test),getString(R.string.clear_username),getString(R.string.help)}, new DialogInterface.OnClickListener(){
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				switch(which){
//				case 0:
//					
//					facebook_checkbox.setChecked(false);
//					
//					MyDispatcher mydispatcher = new MyDispatcher();
//					mydispatcher.facebookDispatcher(Settings.this.getApplicationContext(), Settings.this);
//					new Thread(){
//						public void run(){
//							while(MyDispatcher.post_id.equals("")){}
//							Settings.this.runOnUiThread(new Runnable(){
//								public void run(){
//									facebook_checkbox.setChecked(true);
//									facebook_set.setSummary(R.string.data_set);		
//								}
//							});
//						}
//					}.start();
//					
//					break;
//				case 1:
//
//					cookie.removeAllCookie();
//					
////					MySharedPreferences.addPreference(Settings.this, "facebook_set", false);
//					facebook_set.setSummary(R.string.data_not_set);
//					facebook_checkbox.setChecked(false);
//					//AccountManager管不到FB
////					AccountManager am=AccountManager.get(SetNotify.this);
////					
////					for(Account ac:am.getAccounts()){
////						Log.i(tag, "ac name: "+ac.name);
////						am.removeAccount(ac, null, null);
////					}
//					MyDialog.newToast(Settings.this, getString(R.string.username_cleared), 0);
//					break;
//				case 2:
//					MyDialog.helpDialog(Settings.this,R.drawable.facebook_spic, getString(R.string.facebook_feature),getString(R.string.facebook_instruction));
//					if(Settings.cookie.getCookie("http://www.facebook.com")==null){
//						Log.i(tag, "cookie: "+Settings.cookie.getCookie("http://www.facebook.com"));
//						facebook_checkbox.setChecked(false);
//					}
//					
//					break;
//				}
//				
//			}	
//			
//		})
//		.setPositiveButton(R.string.cancel, new  DialogInterface.OnClickListener(){
//
//		@Override
//		public void onClick(DialogInterface dialog, int which) {	
//		}
//	    	
//	    })
//		.show();		
	}
	
	/**
	 * 設定Facebook在Settings裡的設定狀態
	 * @param 傳進來若為true,則將畫面設為打勾和已設定
	 */
	public static void setFacebookStatus(boolean status){
		if(status==true){
			facebook_set.setSummary(R.string.data_set);
			facebook_checkbox.setChecked(true);
		}else{
			facebook_set.setSummary(R.string.data_not_set);
			facebook_checkbox.setChecked(false);
		}
		
	}
	
}
