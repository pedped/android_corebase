package com.corebase.interfaces;

import java.util.List;

import com.corebase.unlimited.UnlimitedList.ItemObject;

public interface OnUnlimitedCheckChangeListner {
	public void onCheckChange(int totalChecked, List<ItemObject> checkedItems);
}
