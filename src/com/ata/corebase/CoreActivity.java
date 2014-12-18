package com.ata.corebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CoreActivity extends ActionBarActivity {

	public TextView findTextView(int ResCode) {
		return (TextView) this.findViewById(ResCode);
	}

	public EditText findEditText(int ResCode) {
		return (EditText) this.findViewById(ResCode);
	}

	public Button findButton(int ResCode) {
		return (Button) this.findViewById(ResCode);
	}

	public ImageButton findImageButton(int ResCode) {
		return (ImageButton) this.findViewById(ResCode);
	}

	public ImageView findImageView(int ResCode) {
		return (ImageView) this.findViewById(ResCode);
	}

	public UnlimitedListView findUnlimitedListView(int ResCode) {
		return (UnlimitedListView) this.findViewById(ResCode);
	}

	public int LogDebug(String info) {
		return Log.d(this.getClass().getSimpleName(), info);
	}

	public int LogInfo(String info) {
		return Log.i(this.getClass().getSimpleName(), info);
	}

	public boolean setLoading(boolean Load) {
		return true;
	}

	/**
	 * start activiy with new class name
	 * 
	 * @param ActivityClass
	 */
	public void startActivityWithName(Class<?> ActivityClass) {
		startActivity(new Intent(this, ActivityClass));
	}

}
