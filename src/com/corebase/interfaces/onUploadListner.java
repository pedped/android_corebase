package com.corebase.interfaces;

public interface onUploadListner {

	public void onPercentChange(long procceed, long length, int percent);

	public void onStart();

	public void onCompleted(String result);

	public void onError();

	public void onUnSuccess(String message);

	public void Anytime();

}
