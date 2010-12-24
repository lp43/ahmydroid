package com.funtrigger.tools;

import com.facebook.android.R;
import com.funtrigger.ahmydroid.Fallen;
import com.funtrigger.ahmydroid.TimeService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 這個類別專用來產生和管理各種中獎的Dialog視窗
 * @author simon
 *
 */
public class MyDialog {

	private static String tag="tag"; 
	public static Toast toast;
	
	/**
	 * 顯示訊息視窗，並接收傳來的參數<br/>
	 * @param context 產生此視窗的主體
     * @param title 想要顯示的視窗標題
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要顯示的icon
	 */
	public static void newDialog(Context context,String title,String message,String icon){
		Log.i(tag, "into MyDialog.newDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(context.getResources().getIdentifier(icon,"drawable",context.getPackageName()))
		    .setMessage(message)
		    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
		
		    @Override
		    public void onClick(DialogInterface dialog, int which) {

		    }})

   .show();
   }
	
	/**
	 * 新增一個訊息視窗，OK鍵的名稱、listener皆自訂
	 * @param context 產生此視窗的主體
	 * @param icon 想要顯示的icon
	 * @param title 想要顯示的視窗標題
	 * @param message 想要顯示的訊息內容
	 * @param positiveText 原本OK位置的命名
	 * @param positivelistener OK按鈕的監聽事件
	 */
	public static void newOneBtnDialog(Context context,int icon,String title,String message,String positiveText,DialogInterface.OnClickListener positivelistener){
		Log.i(tag, "into MyDialog.newOneBtnDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(icon)
		    .setMessage(message)
		    .setPositiveButton(positiveText, positivelistener)

   .show();
   }
	
	
	public static void newTwoBtnDialog(Context context,int icon,String title,String message,String positiveText,DialogInterface.OnClickListener positivelistener,String negativeText,DialogInterface.OnClickListener negativelistener){
		Log.i(tag, "into MyDialog.newTwoBtnDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(icon)
		    .setMessage(message)
		    .setPositiveButton(positiveText, positivelistener)
		    .setNegativeButton(negativeText, negativelistener)
   .show();
   }
	/**
	 * 該視窗是Toast視窗
	 * @param context 產生此視窗的主體
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要產生的icon
	 */
	public static void newToast(Context context,String message,int icon){

			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		    View originView=toast.getView();
		    LinearLayout layout= new LinearLayout(context);
		    layout.setOrientation(LinearLayout.VERTICAL);
		    ImageView view = new ImageView(context);
//		    view.setImageResource(R.drawable.again);
		    view.setImageResource(icon);
		    layout.addView(view);
		    layout.addView(originView);
		    toast.setView(layout);
		    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		    toast.show();
		
	}
	
	/**
	 * 當畫面進入Fallen時，呼叫該視窗，並輸入密碼，才能完全解密並關閉Fallen模式
	 * @param context 要做事情的呼叫主體
	 */
	public static void passwordToExit(final Context context){
		
		 LayoutInflater factory = LayoutInflater.from(context);
         final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.password_to_exit), null);
         new AlertDialog.Builder(context)
             .setIcon(context.getResources().getDrawable(com.funtrigger.ahmydroid.R.drawable.verify))
             .setTitle(context.getResources().getText(com.funtrigger.ahmydroid.R.string.exit))
             .setView(EntryView)
             .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 EditText password=(EditText) EntryView.findViewById(R.id.password_to_exit);
                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(context, "unlock_password", ""))){
                		  Activity fallen=(Activity)context;
                          fallen.finish();
                     
                          
                	 }else{
                		  newDialog(context,context.getString(R.string.attention),context.getString(R.string.type_wrong_password),"warning");
                	 }
                   
                 }
             })
             .setNegativeButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                 }
             })
             .show();

	}
	/**
	 * 如果之前有存密碼，當使用者又按一次設定解鎖密碼，會進來這裡要求先輸入之前的密碼，
	 * 才能重新設定密碼
	 * @param context 要做事情的呼叫主體
	 */
	public static void passwordToChangePass(final Context context){
		 LayoutInflater factory = LayoutInflater.from(context);
         final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.password_to_exit), null);
         new AlertDialog.Builder(context)
             .setTitle(context.getResources().getText(com.funtrigger.ahmydroid.R.string.please_type))
             .setView(EntryView)
             .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 EditText password=(EditText) EntryView.findViewById(R.id.password_to_exit);
                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(context, "unlock_password", ""))){
                		 createNewPassword(context,R.string.password_modify);
                	 }else{
                		  newDialog(context,context.getString(R.string.attention),context.getString(R.string.type_wrong_password),"warning");
                	 }
                   
                 }
             })
             .setNegativeButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.cancel), new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                 }
             })
             .show();
	}
	/**
	 * 使用者在設定新帳號密碼時，會被呼叫的視窗
	 * @param context 啟動該視窗的呼叫主體
	 */
	public static void createNewPassword(final Context context,int title){
		LayoutInflater factory = LayoutInflater.from(context);
        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.create_new_password), null);
        new AlertDialog.Builder(context)
            .setIcon(context.getResources().getDrawable(com.funtrigger.ahmydroid.R.drawable.verify))
            .setTitle(title)
            .setView(EntryView)
            .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	

                		Log.i(tag, "into unlock_password no data");
                		EditText password1=(EditText) EntryView.findViewById(R.id.password_first_type);
	                	EditText password2=(EditText) EntryView.findViewById(R.id.password_second_type);
	                	if(password1.getText().toString().equals(password2.getText().toString())){
	                		MySharedPreferences.addPreference(context, "unlock_password", password1.getText().toString());
	                		newToast(context,context.getString(R.string.password_saved),R.drawable.verify);
	                	}else{
	                		MyDialog.newDialog(context, context.getString(R.string.attention), context.getString(R.string.type_dismatch), "warning");
	                	}
                	
	                	
                	
                }
            })
            .setNegativeButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked cancel so do some stuff */
                }
            })
            .show();
	}
	
	/**
	 * 該訊息視窗用來顯示每種告知功能的介紹
	 * @param context
	 */
	public static void helpDialog(final Context context,int icon,String title,String content){
		LayoutInflater factory = LayoutInflater.from(context);
        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.help), null);
        TextView helpContent=(TextView) EntryView.findViewById(R.id.help_context);
        helpContent.setText(content);
        new AlertDialog.Builder(context)
            .setIcon(icon)
            .setTitle(title)
            .setView(EntryView)
            .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })

            .show();
	}
    /**
     * 這個函式專用來清除已顯示中的橘子Toast
     */
    public static void cancelToast(){
    	
		if(toast!=null){
			toast.cancel();
			Log.i(tag, "into cancelToast");
			toast=null;
		}
    
    }
}
