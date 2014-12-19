package com.ata.corebase;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ata.corebase.interfaces.OnResponseListener;

public class async {

	public static class Async_WebRequest extends
			AsyncTask<String, String, String> {

		// Define Interfaces
		private OnResponseListener Listner;
		private Context context;
		private List<NameValuePair> params;
		private String url;

		public Async_WebRequest(Context Context, String url,
				List<NameValuePair> Params, OnResponseListener Listner) {
			this.context = Context;
			this.Listner = Listner;
			this.params = Params;
			this.url = url;
		}

		public void setOnResponseListner(OnResponseListener listener) {
			this.Listner = listener;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
		}

		protected String doInBackground(String... urls) {
			return nc.request(url, params, context);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Log.d("AysyncTask", "Task Cancelled");
		}

		protected void onPostExecute(String result) {
			if (isCancelled()) {
				return;
			}

			if (result != null && result != "") {
				try {
					// parse the output
					JSONObject jsoned = new JSONObject(result);
					switch (jsoned.getInt("statuscode")) {
					case -1:
						// user token is not valid
						if (Listner != null) {
							Listner.onError();
						}
						break;
					case 0:
						// there is a problem
						if (Listner != null) {
							Listner.onUnSuccess(jsoned.getString("statustext"));
						}
						break;
					case 1:
						// Success
						if (Listner != null) {
							Listner.onSuccess(jsoned.getString("result"));
						}
						break;
					}

				} catch (Exception e) {
					// there is a bad problem in parsing the request
					e.printStackTrace();
					Log.w("json result parse problem", result);
					// e.printStackTrace();

					if (Listner != null) {
						Listner.onError();
					} 
				}
				// call any time function
				// this is a test
				if (Listner != null) {
					Listner.Anytime();
				}
			} else {
				if (Listner != null) {
					Log.d("web request", "we get empty response from server");
					Listner.onError();
				}
			}
		}
	}

}
