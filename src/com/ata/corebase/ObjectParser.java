package com.ata.corebase;

import org.json.JSONException;
import org.json.JSONObject;

import com.ata.classes.LoginResult;
import com.ata.classes.User;

public class ObjectParser {
	
	public static LoginResult LoginResult_Parse(String json) throws JSONException {
		
		JSONObject obj = new JSONObject(json);
		
		// create new login result
		LoginResult result = new LoginResult();
		
		// get token
		result.token  = obj.getString("token");
		
		// load the result
		result.user  = ObjectParser.User_Parse(obj.getJSONObject("User").toString());
		
		// return result
		return result;
	}

	public static User User_Parse(String string) throws JSONException {
		
		
		// create Json Object
		JSONObject obj = new JSONObject(string);
		
		// find infos
		User user = new User();
		user.firstName = obj.getString("fname");
		user.firstName = obj.getString("lname");
		user.imagelink = obj.getString("imagelink");
		user.gender = obj.getInt("gender");
		user.email = obj.getString("email");
		return user;
	}
}
