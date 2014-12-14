package com.ata.corebase;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.ata.corebase.UnlimitedList.ItemObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class UnlimitedAdapter<T> extends BaseAdapter {

	public static class UnlimitListAdapterItem {

		public String fieldName;
		public String tagName;
		public boolean isChecked;
		public String imageUrlPrefix;
		public UnlimitListAdapterItemType type;

		public UnlimitListAdapterItem(String fieldName, String tagName,
				UnlimitListAdapterItemType type) {
			super();
			this.fieldName = fieldName;
			this.tagName = tagName;
			this.type = type;
		}

		public UnlimitListAdapterItem(String fieldName, String tagName,
				boolean isChecked) {
			super();
			this.fieldName = fieldName;
			this.tagName = tagName;
			this.isChecked = isChecked;
		}

		public UnlimitListAdapterItem(String fieldName, String tagName,
				boolean isChecked, String imageUrlPrefix,
				UnlimitListAdapterItemType type) {
			super();
			this.fieldName = fieldName;
			this.tagName = tagName;
			this.isChecked = isChecked;
			this.imageUrlPrefix = imageUrlPrefix;
			this.type = type;
		}

		public static enum UnlimitListAdapterItemType {
			CheckBox, TextView, EditText, ImageView
		}

	}

	private Context context;
	private int listViewResCode;
	private View viewitem;

	public UnlimitedAdapter(Context context, int listViewResCode) {
		this.context = context;
		this.listViewResCode = listViewResCode;

	}

	public UnlimitedAdapter(Context context, View view) {
		this.context = context;
		this.viewitem = view;

	}

	private String link;

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public ItemObject getItem(int pos) {
		// TODO Auto-generated method stub
		return items.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View arg1, ViewGroup arg2) {
		// LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View rowView = LayoutInflater.from(context).inflate(listViewResCode,
				null);

		// get object
		ItemObject item = this.getItem(pos);

		// ref man
		List<UnlimitListAdapterItem> showItems = new ArrayList<UnlimitedAdapter.UnlimitListAdapterItem>(
				this.showingItem.values());
		for (UnlimitListAdapterItem showItem : showItems) {
			switch (showItem.type) {

			case CheckBox:
				CheckBox chb = (CheckBox) rowView
						.findViewWithTag(showItem.tagName);
				chb.setChecked(showItem.isChecked);
				break;
			case EditText:
				break;
			case ImageView:
				break;
			case TextView:
				TextView txtView = (TextView) rowView
						.findViewWithTag(showItem.tagName);
				txtView.setText(item.get(showItem.fieldName));
				break;

			}
		}

		return rowView;
	}

	public void Refresh() {
		this.notifyDataSetChanged();
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getLink() {
		return this.link;
	}

	private Hashtable<String, UnlimitListAdapterItem> showingItem = new Hashtable<String, UnlimitListAdapterItem>();

	public void addShowItem(String resourceTag, UnlimitListAdapterItem className) {
		showingItem.put(resourceTag, className);
	}

	public List<ItemObject> items = new ArrayList<UnlimitedList.ItemObject>();

	public void add(List<ItemObject> items) {
		for (ItemObject itemObject : items) {
			this.items.add(itemObject);
		}
	}

}
