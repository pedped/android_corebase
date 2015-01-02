package com.corebase.element;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CustomCheckBox extends CheckBox {

	public CustomCheckBox(Context context) {
		super(context);
		this.settype();
	}

	public CustomCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.settype();
	}

	public CustomCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.settype();
	}

	private void settype() {
		if (!this.isInEditMode()) {
			Typeface font = Typeface.createFromAsset(getContext().getAssets(),
					"fonts/byekan.ttf");
			this.setTypeface(font);
		}
	}

}
