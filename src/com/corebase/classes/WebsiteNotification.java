package com.corebase.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class WebsiteNotification extends MasterClass<WebsiteNotification> {
	public int id;
	public String title;
	public String message;
	public String link;
	public String linkText;
	public long date;

	@Override
	public WebsiteNotification ParseJson(String json) throws JSONException {
		WebsiteNotification notification = new WebsiteNotification();
		notification.ReadFromJson(json);
		return notification;
	}

	@Override
	public void ReadFromJson(String json) throws JSONException {

		JSONObject item = new JSONObject(json);
		this.id = item.getInt("id");
		this.title = item.getString("title");
		this.message = item.getString("message");
		this.link = item.getString("link");
		this.linkText = item.getString("linktext");
		this.date = item.getLong("date");
	}

}
