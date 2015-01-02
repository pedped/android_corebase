package com.ata.corebase;

import com.corebase.interfaces.Logout;
import com.corebase.unlimited.UnlimitedListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class CoreActivity extends ActionBarActivity {

	public TextView findTextView(int ResCode) {
		return (TextView) this.findViewById(ResCode);
	}

	public Context getContext() {
		return this;
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

	public LinearLayout findLinearLayout(int ResCode) {
		return (LinearLayout) this.findViewById(ResCode);
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

	protected void setLoading() {

		Activity ac = this;
		LinearLayout main = (LinearLayout) ac.findViewById(R.id.main);

		try {
			// try to hide the tabs if possible
			ac.getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
		} catch (Exception e) {

		}

		// load the loading XML
		View loadingView = ac.getLayoutInflater().inflate(R.layout.loading,
				null);

		loadingView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0f));
		// add the loading view
		main.addView(loadingView, 0);

	}

	protected void setLoading(boolean showLoading) {

		if (showLoading) {
			setLoading();
		} else {
			// we have to remove the loading layout
			Activity ac = ((Activity) this);
			LinearLayout ll = (LinearLayout) ac.findViewById(R.id.main);
			ll.removeViewAt(0);
		}

	}

	protected void setLoading(boolean showLoading, boolean ShowTabs) {

		if (showLoading) {
			setLoading();
		} else {
			// we have to remove the loading layout
			Activity ac = ((Activity) this);
			LinearLayout ll = (LinearLayout) ac.findViewById(R.id.main);

			if (ll.getChildCount() > 1) {
				// check if that is really the loading layout
				View view = ll.getChildAt(0);
				if (view.getId() == R.id.loadingview) {
					// that is it
					ll.removeViewAt(0);
				}
			}

			if (ShowTabs) {

				try {
					// try to show the tabs if possible
					ac.getActionBar().setNavigationMode(
							ActionBar.NAVIGATION_MODE_TABS);
				} catch (Exception e) {

				}
			}
		}

	}

	protected String getSettingValue(String key) {
		return sf.SettingManager_ReadString(getContext(), key);
	}

	protected void setSettingValue(String key, String value) {
		sf.SettingManager_WriteString(this, key, value);
	}

	protected void hideLogo(Context context) {

		((CoreActivity) context).getActionBar().setIcon(
				new ColorDrawable(context.getResources().getColor(
						android.R.color.transparent)));

	}

	/**
	 * start activiy with new class name
	 * 
	 * @param ActivityClass
	 */
	public void startActivityWithName(Class<?> ActivityClass) {
		startActivity(new Intent(this, ActivityClass));
	}

	/**
	 * this function will request logout
	 */
	public void requestLogout(final Logout logoutInterface) {
		new AlertDialog.Builder(getContext())
				.setTitle(R.string.logout)
				.setMessage(
						R.string.are_you_sure_you_want_logout_of_your_account)
				.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {

					}
				}).setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				})
				.setPositiveButton(R.string.yes_logout, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						// user really want to logout, we have to clear settings
						sf.SettingManager_WriteString(CoreActivity.this,
								"token", "");
						sf.SettingManager_WriteString(CoreActivity.this,
								"userid", "");

						// call the interface
						logoutInterface.onLogout();

					}
				}).create().show();
	}

}
