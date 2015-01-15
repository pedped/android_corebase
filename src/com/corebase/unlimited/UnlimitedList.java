package com.corebase.unlimited;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ata.corebase.R;
import com.ata.corebase.interfaces.OnResponseListener;
import com.ata.corebase.nc;
import com.ata.corebase.sf;
import com.corebase.interfaces.OnUnlimitedCheckChangeListner;
import com.corebase.interfaces.OnUnlimitedListClickListner;
import com.corebase.interfaces.OnUnlimitedListLoadListner;
import com.corebase.unlimited.UnlimitedAdapter.UnlimitListAdapterItem;
import com.corebase.unlimited.UnlimitedDatabase.UnlimitedDatabaseItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;

/**
 * base class for unlimited class
 * 
 * @author ataalla
 * 
 */
public class UnlimitedList {

	String TAG = "UnlimitedList";
	private Context context;
	private UnlimitedAdapter<Object> adapter;
	private UnlimitedListView listview;
	private int startPos = 0;
	private int limit = 100;
	private boolean storeOffline = false;
	private boolean readingInOfflineMode = false;
	private boolean switchOffline = false;
	private String databaseName = "";
	private DatabaseOrder databaseOrder;
	private Hashtable<String, String> jsonItems = new Hashtable<String, String>();
	private List<UnlimitedDatabaseItem> offlineData = new ArrayList<UnlimitedDatabaseItem>();
	private String checkChangeTag = "";
	private OnUnlimitedCheckChangeListner onCheckChangeListner;
	private boolean hasMoreData = true;

	/**
	 * checks whetere we are sending request to server at this time or not
	 */
	private boolean flagRequestInProgress;
	private UnlimitedDatabase database;
	private OnUnlimitedListClickListner onItemClickListner;
	private OnUnlimitedListLoadListner onLoadListner;

	public static enum DatabaseOrder {
		Asc, Desc
	}

	public static class ItemObject {

		public String jsonObject = "";

		public Dictionary<String, String> items = new Hashtable<String, String>();

		public void addItem(String key, String value) {
			items.put(key, value);
		}

		public String get(String fieldName) {
			return items.get(fieldName);
		}

		public String getFieldValue(String fieldName) throws JSONException {
			return new JSONObject(this.jsonObject).getString(fieldName);
		}
	}

	public UnlimitedList(Context context, UnlimitedListView listView,
			int resViewCode) {
		UnlimitedListConstructor(context, listView, resViewCode);
	}

	public UnlimitedList(Context context, int listViewResCode, int resViewCode) {

		// create list view
		UnlimitedListView listview = (UnlimitedListView) ((Activity) context)
				.findViewById(listViewResCode);
		UnlimitedListConstructor(context, listview, resViewCode);

	}

	private void UnlimitedListConstructor(Context context,
			UnlimitedListView listView, int resViewCode) {

		this.context = context;

		// create adapter
		this.adapter = new UnlimitedAdapter<Object>(context, resViewCode);

		// create list view
		this.listview = listView;

		// set adapter for listView
		this.listview.getRefreshableView().setAdapter(adapter);

		// set on end Listner
		this.listview
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {

						beginFetch();

					}
				});

	}

	private void beginFetch() {

		// start fetching items
		if ((this.switchOffline && !sf.hasConnection(context))
				|| offlineData.size() > 0) {

			if (offlineData.size() == 0) {
				// we have to load offline mode
				requestOffline(startPos, getLimit());
			} else {
				// we have to use offline data
				requestData(startPos, getLimit());
			}

		} else {
			// we have to load online mode, check if we have more data to load
			// for first load , more data is enable, so we can fetch more data
			if (hasMoreData()) {
				requestServer(getLink(), startPos, getLimit());
			}
		}

	}

	private void requestData(int start, int limit) {

		// add items
		for (int i = start; i < ((start + limit) < offlineData.size() ? (start + limit)
				: offlineData.size()); i++) {

			// load object
			UnlimitedDatabaseItem object = offlineData.get(i);

			try {

				JSONObject json = new JSONObject(object.Data);
				Iterator<String> keys = json.keys();
				ItemObject items = new ItemObject();
				while (keys.hasNext()) {
					String key = keys.next();
					items.addItem(key, json.getString(key));
				}

				items.jsonObject = json.toString();

				// store data in hashtable
				jsonItems.put(json.getString("id"), json.toString());

				// add to adapter and refresh the list
				adapter.add(items);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Refresh list
		Log.d(TAG, "Item read from internal data for UnlimiteList");

		// increase the items
		startPos += limit;

		// refresh adapter
		adapter.Refresh();
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

		params.add(new BasicNameValuePair("start", start + ""));
		params.add(new BasicNameValuePair("limit", limit + ""));

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

					// check if have to store data in database
					if (storeOffline) {
						database.open();
					}

					// start fetching keys and add each key value
					// as name value pair
					JSONArray array = new JSONArray(result);

					// check if recieved data are lower than limit, set more
					// data off
					if (array.length() < limit) {
						setHasMoreData(false);
					}

					List<ItemObject> loadedItems = new ArrayList<UnlimitedList.ItemObject>();
					for (int i = 0; i < array.length(); i++) {
						JSONObject json = array.getJSONObject(i);
						Iterator<String> keys = json.keys();
						ItemObject item = new ItemObject();
						while (keys.hasNext()) {
							String key = keys.next();
							item.addItem(key, json.getString(key));
						}

						item.jsonObject = json.toString();

						// add to adapter and refresh the list
						adapter.add(item);

						// add to load listner event
						loadedItems.add(item);

						// check if have to store data in database
						if (storeOffline) {
							database.Add(json.getString("id"), json.toString());
						}

						// store data in hashtable
						jsonItems.put(json.getString("id"), json.toString());

					}
					Log.d(TAG, array.length() + " array length");

					// send data to load listner
					if (onLoadListner != null) {
						onLoadListner.onFirstLoad(loadedItems);
					}

					// increase the items
					startPos += limit;

					// check if have to store data in database
					if (storeOffline) {
						database.close();
					}

					// refresh adapter
					adapter.Refresh();
				} catch (Exception e) {
					e.printStackTrace();

					// call error listner
					if (onLoadListner != null) {
						onLoadListner.OnError();
					}
				}

				// call anytime function
				if (onLoadListner != null) {
					onLoadListner.Anytime();
				}

			}

			@Override
			public void onError() {

			}

			@Override
			public void Anytime() {
				// TODO Auto-generated method stub
				flagRequestInProgress = false;
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
		// check if we have to store data in database
		boolean useOffline = this.isStoreOffline();
		if (useOffline && offlineData.size() == 0) {
			this.database = new UnlimitedDatabase(context, this.databaseName);
		}

		// begin read from database or server
		beginFetch();
	}

	/**
	 * request offline data reading
	 * 
	 * @param startPos2
	 * @param limit2
	 */
	private void requestOffline(int startPos2, int limit2) {
		try {

			// check if table name is empty
			if (databaseName.length() == 0) {
				Log.e(TAG,
						"Empty Database Name, Please Define Database Name for UnlimitedList");
				return;
			}

			// open database
			database.open();

			// load values from database
			List<UnlimitedDatabaseItem> values = database.GetAllItems(startPos,
					getLimit(), databaseOrder);

			// read each value
			for (UnlimitedDatabaseItem unlimitedDatabaseItem : values) {
				JSONObject json = new JSONObject(unlimitedDatabaseItem.Data);
				Iterator<String> keys = json.keys();
				ItemObject items = new ItemObject();
				while (keys.hasNext()) {
					String key = keys.next();
					items.addItem(key, json.getString(key));
				}

				items.jsonObject = json.toString();

				// store data in hashtable
				jsonItems.put(json.getString("id"), json.toString());

				// add to adapter and refresh the list
				adapter.add(items);
			}

			Log.d(TAG, values.size()
					+ " items read from internal database for UnlimiteList");

			// increase the items
			startPos += getLimit();

			// close connection
			database.close();

			// refresh adapter
			adapter.Refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isSwitchOffline() {
		return switchOffline;
	}

	/**
	 * switch to offline mode when we have not internet access
	 * 
	 * @param switchOffline
	 */
	public void setSwitchOffline(boolean switchOffline) {
		this.switchOffline = switchOffline;
	}

	public boolean isStoreOffline() {
		return storeOffline;
	}

	/**
	 * indicate wheter we have to store loaded object in database for offline
	 * useage
	 * 
	 * @param storeOffline
	 */
	public void setStoreOffline(boolean storeOffline) {
		this.storeOffline = storeOffline;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public DatabaseOrder getDatabaseOrder() {
		return databaseOrder;
	}

	public void setDatabaseOrder(DatabaseOrder databaseOrder) {
		this.databaseOrder = databaseOrder;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public OnUnlimitedListClickListner getOnItemClickListner() {
		return onItemClickListner;
	}

	/**
	 * Get Refreshable ListViews
	 * 
	 * @return
	 */
	public ListView getListView() {
		return this.listview.getRefreshableView();
	}

	/**
	 * walk into items in adapter and check for items that have be checked
	 * 
	 * @param fieldName
	 * @return
	 * @throws JSONException
	 */
	public List<String> getCheckedItems(String fieldName) throws JSONException {

		return this.adapter.getCheckedItem(fieldName);
	}

	public void setOnLoadListner(OnUnlimitedListLoadListner listner) {
		this.onLoadListner = listner;
	}

	public void setOnItemClickListner(
			OnUnlimitedListClickListner onItemClickListnerIntreface) {
		this.onItemClickListner = onItemClickListnerIntreface;

		// set on click Listner
		if (this.onItemClickListner != null) {

			this.listview.getRefreshableView().setOnItemClickListener(
					new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {

							// call event listner
							onItemClickListner.onClickListner(
									adapter.getItem(position - 1).jsonObject,
									arg0, arg1, position, arg3);

						}
					});
		}
	}

	public List<UnlimitedDatabaseItem> getOfflineData() {
		return offlineData;
	}

	public void setOfflineData(List<UnlimitedDatabaseItem> offlineData) {
		this.offlineData = offlineData;
	}

	public String getCheckChangeTag() {
		return checkChangeTag;
	}

	public void setCheckChangeTag(String checkChangeTag) {
		this.checkChangeTag = checkChangeTag;
	}

	public void setOnCheckChangeListner(String fieldName,
			OnUnlimitedCheckChangeListner listner) {
		this.onCheckChangeListner = listner;
		this.checkChangeTag = fieldName;

		// set check change listner in adapter
		adapter.setCheckChangeListner(fieldName, this.onCheckChangeListner);
	}

	/**
	 * indicate wheter we have more data
	 * 
	 * @return
	 */
	public boolean hasMoreData() {
		return hasMoreData;
	}

	public void setHasMoreData(boolean hasMoreData) {
		this.hasMoreData = hasMoreData;
	}
}
