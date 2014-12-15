package com.ata.classes;

import com.phonegap.FileUtils;

import android.content.Context;

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

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
