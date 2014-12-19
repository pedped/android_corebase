package com.ata.corebase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ata.corebase.async.Async_WebRequest;
import com.ata.corebase.interfaces.OnDownloadListner;
import com.ata.corebase.interfaces.OnResponseListener;

public class nc {

	public static final int REQUEST_SEARC_USER = 100;

	private static final String CNAME = "Network Connection";

	private static Hashtable<Integer, AsyncTask<String, String, String>> runningAsyncs = new Hashtable<Integer, AsyncTask<String, String, String>>();

	public static void WebRequest(int code, String url, Context context,
			List<NameValuePair> params, OnResponseListener listner) {

		if (code > 0) {

			// check if the aysnc is exist in the list, fetch and stop that
			if (runningAsyncs.containsKey(code)) {
				AsyncTask<String, String, String> task = (AsyncTask<String, String, String>) runningAsyncs
						.get(code);

				// cancel the task
				task.cancel(true);

				Log.d("WebRequest", "task with id " + code + " canceled");

			}

			// we have to add that to the list
			Async_WebRequest async = new Async_WebRequest(context, url, params,
					listner);

			// run the async
			async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

			// add that to the list
			runningAsyncs.put(code, async);

		} else {
			new Async_WebRequest(context, url, params, listner)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		}

	}

	public static void WebRequest(Context context, String url,
			List<NameValuePair> params, OnResponseListener listner) {

		WebRequest(0, url, context, params, listner);
	}

	/**
	 * 
	 * @param PageName
	 *            : the page name if the web site we request : test.php
	 * @return The string which is Response text
	 */
	public static String request(String url, List<NameValuePair> params,
			Context cont) {

		try {
			DefaultHttpClient client = new DefaultHttpClient();
			String postURL = url;
			HttpPost post = new HttpPost(postURL);
			post.addHeader("Accept-Encoding", "gzip");
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
					HTTP.UTF_8);
			post.setEntity(ent);

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 10000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			client.setParams(httpParameters);

			// post.setHeader("Cache-Control", "no-cache, no-store");
			HttpResponse responsePOST = client.execute(post);
			HttpEntity resEntity = responsePOST.getEntity();
			//
			if (resEntity != null) {

				// check if the request is gziped
				InputStream instream = responsePOST.getEntity().getContent();
				Header contentEncoding = responsePOST
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				BufferedReader r = new BufferedReader(new InputStreamReader(
						instream));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}

				String result = total.toString();

				Log.i("WEB RESPONSE",
						"=================================================================");
				Log.i("WEB RESPONSE", "requests: \n ");

				// echo parameters
				for (int i = 0; i < params.size(); i++) {
					Log.i("WEB RESPONSE", "* " + params.get(i).getName()
							+ " : " + params.get(i).getValue());
				}

				Log.i("WEB RESPONSE",
						"+ + + + + + + + + + + + + + + + + + + + + + + + + + +");
				Log.i("WEB RESPONSE", "result: " + result);
				Log.i("WEB RESPONSE",
						"==================================================================");

				return result;

			}

		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	public static boolean DownloadFromUrl(String DownloadUrl, String fileName,
			OnDownloadListner listner) {

		try {

			URL url = new URL(DownloadUrl); // you can write here any link
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			long startTime = System.currentTimeMillis();

			listner.onStart();

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			int totalLength = is.available();

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(8192);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);

				// Calculate current progress percent
				int percent = baf.length() / totalLength;
				// send to the listener
				listner.onPercentChange(percent);

				Log.d("total file length is ", baf.length() + " of "
						+ totalLength);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			Log.d("DownloadManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

			listner.onCompleted();

			return true;

		} catch (IOException e) {
			Log.d("DownloadManager", "Error: " + e);
			listner.onError(e.getMessage());
		}

		return false;

	}

	public static boolean DownloadFromUrl(String DownloadUrl, String fileName) {

		try {

			URL url = new URL(DownloadUrl); // you can write here any link
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			long startTime = System.currentTimeMillis();
			// Log.d("DownloadManager", "download begining");
			// Log.d("DownloadManager", "download url:" + url);
			// Log.d("DownloadManager", "downloaded file name:" + fileName);

			/* Open a connection to that URL. */
			URLConnection ucon = url.openConnection();

			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(4096);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			Log.d("DownloadManager",
					"download ready in"
							+ ((System.currentTimeMillis() - startTime) / 1000)
							+ " sec");

			return true;

		} catch (IOException e) {
			Log.d("DownloadManager", "Error: " + e);
		}

		return false;

	}

	public static String getContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String body = "";
		String content = "";

		while ((body = rd.readLine()) != null) {
			content += body + "\n";
		}
		return content.trim();
	}

	public static String getWebsiteUrl(Context context) {
		return sf.SettingManager_ReadString((Activity) context, "url");
	}

}
