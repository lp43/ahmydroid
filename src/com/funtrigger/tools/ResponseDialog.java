package com.funtrigger.tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 這個類別專用來產生和管理各種中獎的Dialog視窗
 * @author simon
 *
 */
public class ResponseDialog {

	private static String tag="tag"; 
	public static Toast toast;
	
	/**
	 * 顯示訊息視窗，並接收傳來的參數<br/>
	 * @param context 產生此視窗的主體
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要顯示的icon
	 */
	public static void newDialog(Context context,String title,String message,String icon){
		Log.i(tag, "into CongraturationsDialog.newDialog");
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
	 * 該視窗是Toast視窗
	 * @param context 產生此視窗的主體
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要產生的icon
	 */
	public static void newToast(Context context,String message,int icon){
		
		if(toast==null){
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
                	 //TODO 這裡必須還要寫核對帳密的機制
                     Activity fallen=(Activity)context;
                     fallen.finish();
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
	public static void createNewPassword(Context context){
		 LayoutInflater factory = LayoutInflater.from(context);
        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.password_to_exit), null);
        new AlertDialog.Builder(context)
            .setIcon(context.getResources().getDrawable(com.funtrigger.ahmydroid.R.drawable.verify))
            .setTitle(context.getResources().getText(com.funtrigger.ahmydroid.R.string.exit))
            .setView(EntryView)
            .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked OK so do some stuff */
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
