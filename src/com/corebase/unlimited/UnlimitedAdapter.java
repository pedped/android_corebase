package com.corebase.unlimited;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.corebase.imageloader.ImageLoader;
import com.corebase.interfaces.OnUnlimitedCheckChangeListner;
import com.corebase.unlimited.UnlimitedList.ItemObject;

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
			CheckBox, TextView, EditText, ImageView, RatingBar
		}

	}

	private Context context;
	private Activity activity;
	private int listViewResCode;
	private View viewitem;
	private ImageLoader imageLoader;

	private class UnlimitedViewHolder {
		public Hashtable<String, ImageView> imageViews = new Hashtable<String, ImageView>();
		public Hashtable<String, TextView> textViews = new Hashtable<String, TextView>();
		public Hashtable<String, CheckBox> checkboxViews = new Hashtable<String, CheckBox>();
		public Hashtable<String, RatingBar> ratingViews = new Hashtable<String, RatingBar>();
	}

	public UnlimitedAdapter(Context context, int listViewResCode) {
		this.context = context;
		this.listViewResCode = listViewResCode;
		this.imageLoader = new ImageLoader(context);
		this.activity = (Activity) context;

	}

	public UnlimitedAdapter(Context context, View view) {
		this.context = context;
		this.viewitem = view;
		this.imageLoader = new ImageLoader(context);
		this.activity = (Activity) context;

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
	public View getView(int pos, View convertView, ViewGroup parent) {

		View rowView = convertView;
		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(listViewResCode, null);
			// configure view holder
			UnlimitedViewHolder viewHolder = new UnlimitedViewHolder();

			// ref man
			List<UnlimitListAdapterItem> showItems = new ArrayList<UnlimitedAdapter.UnlimitListAdapterItem>(
					this.showingItem.values());

			// load views that we have to use for
			for (UnlimitListAdapterItem showItem : showItems) {
				switch (showItem.type) {

				case CheckBox:
					CheckBox chb = (CheckBox) rowView
							.findViewWithTag(showItem.tagName);
					viewHolder.checkboxViews.put(showItem.tagName, chb);
					break;
				case EditText:
					break;
				case ImageView:
					ImageView imgView = (ImageView) rowView
							.findViewWithTag(showItem.tagName);

					viewHolder.imageViews.put(showItem.tagName, imgView);
					break;
				case TextView:
					TextView txtView = (TextView) rowView
							.findViewWithTag(showItem.tagName);
					viewHolder.textViews.put(showItem.tagName, txtView);
					break;
				case RatingBar:
					RatingBar rateView = (RatingBar) rowView
							.findViewWithTag(showItem.tagName);
					viewHolder.ratingViews.put(showItem.tagName, rateView);
					break;

				}
			}

			rowView.setTag(viewHolder);
		}

		// fill data
		UnlimitedViewHolder holder = (UnlimitedViewHolder) rowView.getTag();

		// get object
		final ItemObject item = this.getItem(pos);

		// ref man
		List<UnlimitListAdapterItem> showItems = new ArrayList<UnlimitedAdapter.UnlimitListAdapterItem>(
				this.showingItem.values());
		for (final UnlimitListAdapterItem showItem : showItems) {
			switch (showItem.type) {

			case CheckBox:
				final CheckBox chb = holder.checkboxViews.get(showItem.tagName);
				try {
					chb.setChecked(Boolean.valueOf(item.get(showItem.fieldName)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				chb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {

						try {

							// change field value
							JSONObject jsoned = (new JSONObject(item.jsonObject));
							jsoned.put(showItem.fieldName, chb.isChecked());
							item.jsonObject = jsoned.toString();

							// change show item is checked
							showItem.isChecked = chb.isChecked();

							// check if item is checked
							if (onCheckChangeListner != null) {
								List<ItemObject> checkedItems = getCheckedItemAsItemObject(checkChangeTagName);
								onCheckChangeListner.onCheckChange(
										checkedItems.size(), checkedItems);
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				break;
			case EditText:
				break;
			case ImageView:
				ImageView imgView = holder.imageViews.get(showItem.tagName);

				String urlPath = item.get(showItem.fieldName);
				imageLoader.DisplayImage(urlPath, imgView);
				break;
			case TextView:
				TextView txtView = holder.textViews.get(showItem.tagName);
				txtView.setText(item.get(showItem.fieldName));
				break;
			case RatingBar:
				RatingBar rateView = holder.ratingViews.get(showItem.tagName);
				rateView.setRating(Float.valueOf(item.get(showItem.fieldName)));
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

	public List<ItemObject> items = new ArrayList<ItemObject>();
	private OnUnlimitedCheckChangeListner onCheckChangeListner;
	private String checkChangeTagName;

	public void add(ItemObject item) {
		this.items.add(item);
	}

	public void setCheckChangeListner(String tagName,
			OnUnlimitedCheckChangeListner onCheckChangeListner) {
		this.onCheckChangeListner = onCheckChangeListner;
		this.checkChangeTagName = tagName;

	}

	public List<String> getCheckedItem(String fieldName) throws JSONException {
		List<String> items = new ArrayList<String>();
		for (ItemObject item : this.items) {
			if ((new JSONObject(item.jsonObject)).getBoolean(fieldName) == true) {
				items.add(item.jsonObject);
			}
		}
		return items;
	}

	public List<ItemObject> getCheckedItemAsItemObject(String fieldName)
			throws JSONException {
		List<ItemObject> items = new ArrayList<ItemObject>();
		for (ItemObject item : this.items) {
			if ((new JSONObject(item.jsonObject)).getBoolean(fieldName) == true) {
				items.add(item);
			}
		}
		return items;
	}

}
