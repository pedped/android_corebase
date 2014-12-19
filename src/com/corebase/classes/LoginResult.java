package com.corebase.classes;

import android.app.Activity;
import android.content.Context;

import com.ata.corebase.sf;

/**
 * hold information about login request
 * 
 * @author ataalla
 * 
 */
public class LoginResult {
	public User user;
	public String token;

	/**
	 * store the current login and token information in database
	 * 
	 * @param context
	 * @return
	 */
	public boolean Store(Context context) {
		try {

			// store in prefs manager
			sf.SettingManager_WriteString((Activity) context, "userid",
					user.userid + "");
			sf.SettingManager_WriteString((Activity) context, "firstname",
					user.firstName + "");
			sf.SettingManager_WriteString((Activity) context, "lastname",
					user.lastName + "");
			sf.SettingManager_WriteString((Activity) context, "gender",
					user.gender + "");
			sf.SettingManager_WriteString((Activity) context, "imagelink",
					user.imagelink + "");
			sf.SettingManager_WriteString((Activity) context, "token", token
					+ "");
			
			// store last notification see
			sf.SettingManager_WriteString((Activity) context, "lastnotificationsee", sf.getUnixTime()
					+ "");


		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
