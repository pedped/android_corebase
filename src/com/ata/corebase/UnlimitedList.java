package com.ata.corebase;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ata.corebase.UnlimitedAdapter.UnlimitListAdapterItem;
import com.ata.corebase.interfaces.OnResponseListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;

/**
 * base class for unlimited class
 * 
 * @author ataalla
 * 
 */
public class UnlimitedList {

	public static class ItemObject {

		public Dictionary<String, String> items = new Hashtable<String, String>();

		public void addItem(String key, String value) {
			items.put(key, value);
		}

		public String get(String fieldName) {
			return items.get(fieldName);
		}
	}

	private Context context;
	private UnlimitedAdapter<Object> adapter;
	private UnlimitedListView listview;
	private int startPos = 0;
	private int limit = 100;
	/**
	 * checks whetere we are sending request to server at this time or not
	 */
	private boolean flagRequestInProgress;

	public UnlimitedList(Context context, int listViewResCode, int resViewCode) {

		this.context = context;

		// create adapter
		this.adapter = new UnlimitedAdapter<Object>(context, resViewCode);

		// create list view
		this.listview = (UnlimitedListView) ((Activity) context)
				.findViewById(listViewResCode);

		// set adapter for listView
		this.listview.getRefreshableView().setAdapter(adapter);

		// set on end Listner
		this.listview
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						// start fetching items
						requestServer(adapter.getLink(), startPos, limit);
					}
				});

	}

	private void requestServer(String link, int start, final int limit) {
		// TODO implant web request

		if (this.flagRequestInProgress) {
			return;
		}

		this.flagRequestInProgress = true;
		/*
		 * Create a web request Load Items Sep 28, 2014
		 */
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("request", getLink()));
		nc.WebRequest(context, getLink(), params, new OnResponseListener() {

			@Override
			public void onUnSuccess(String message) {
				new AlertDialog.Builder(context)
						.setTitle(R.string.khata)
						.setMessage(message)
						.setPositiveButton(R.string.motevajehshodam,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {

									}
								}).create().show();
				flagRequestInProgress = false;
			}

			@Override
			public void onSuccess(String result) {
				try {

					// start fetching keys and add each key value
					// as name value pair
					JSONArray array = new JSONArray();
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						Iterator<String> keys = json.keys();
						List<ItemObject> items = new ArrayList<UnlimitedList.ItemObject>();
						while (keys.hasNext()) {
							String key = keys.next();
							ItemObject item = new ItemObject();
							item.addItem(key, json.getString(key));
							items.add(item);
						}

						// add to adapter and refresh the list
						adapter.add(items);
						adapter.Refresh();

						// increase the items
						startPos += limit;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					flagRequestInProgress = false;
				}

			}

			@Override
			public void onError() {
				flagRequestInProgress = true;
			}

			@Override
			public void Anytime() {
				// TODO Auto-generated method stub
				
			}
		});

	}

	public void addItem(String resourceTag, UnlimitListAdapterItem className) {
		this.adapter.addShowItem(resourceTag, className);
	}

	public void Refresh() {
		this.adapter.Refresh();
	}

	public void setLink(String link) {
		this.adapter.setLink(link);
	}

	public String getLink() {
		return this.adapter.getLink();
	}

	public void Begin() {
		// start fetching items
		requestServer(getLink(), startPos, limit);
	}

}
