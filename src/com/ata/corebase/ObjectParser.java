package com.ata.corebase;

import org.json.JSONException;
import org.json.JSONObject;

import com.corebase.classes.LoginResult;
import com.corebase.classes.User;

public class ObjectParser {

	public static LoginResult LoginResult_Parse(String json)
			throws JSONException {

		JSONObject obj = new JSONObject(json);

		// create new login result
		LoginResult result = new LoginResult();

		// get token
		result.token = obj.getString("Token");

		// load the result
		result.user = ObjectParser.User_Parse(obj.getJSONObject("User")
				.toString());

		// return result
		return result;
	}

	public static User User_Parse(String string) throws JSONException {

		// create Json Object
		JSONObject obj = new JSONObject(string);

		// find infos
		User user = new User();
		user.userid = obj.getString("userid");
		user.firstName = obj.getString("firstname");
		user.firstName = obj.getString("lastname");
		user.imagelink = obj.getString("imagelink");
		user.gender = obj.getInt("gender");
		return user;
	}
}
