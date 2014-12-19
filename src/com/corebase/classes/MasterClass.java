package com.corebase.classes;

import org.json.JSONException;

public abstract class MasterClass<T> {

	public abstract T ParseJson(String json) throws JSONException;

	public abstract void ReadFromJson(String json) throws JSONException;

}
