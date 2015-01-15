package com.corebase.interfaces;

import java.util.List;

import com.corebase.unlimited.UnlimitedList.ItemObject;

public interface OnUnlimitedListLoadListner {

	public void onFirstLoad(List<ItemObject> items);
	
	public void onMoreLoad(List<ItemObject> items);
	
	public void Anytime();
	
	public void OnError();
}
