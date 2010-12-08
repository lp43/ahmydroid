package com.funtrigger.ahmydroid;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class MySetButton extends Button {
	/*public LinearLayout.LayoutParams param;*/
	
//	public MySetButton(Context context) {
//		super(context);
//		createButton(context,icon);
//	}
	public MySetButton(Context context,int icon) {
		super(context);
		createButton(context,icon);
	}
	

	public MySetButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MySetButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private void createButton(Context context,int icon){
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_ground));
		setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		setSingleLine(false);
	}
	
	private void createButton(Context context, String icon, String line1,
			String line2) {
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_ground));
		
	}

}
