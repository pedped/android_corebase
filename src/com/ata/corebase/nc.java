package com.ata.corebase;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
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
import com.ata.corebase.async.Async_WebUpload;
import com.ata.corebase.interfaces.OnDownloadListner;
import com.ata.corebase.interfaces.OnResponseListener;
import com.corebase.interfaces.onUploadListner;

public class nc {

	public static final int REQUEST_SEARC_USER = 100;

	private static final String CNAME = "Network Connection";

	private static Hashtable<Integer, AsyncTask<String, String, String>> runningAsyncs = new Hashtable<Integer, AsyncTask<String, String, String>>();

	public static String uploadfile(Context context, String upLoadServerUri,
			List<String> sourceFileUri, List<NameValuePair> params,
			onUploadListner uploadListner) throws IOException {

		// check if user logged in, add user id and token to request
		if (sf.isUserLoggedIn(context)) {
			String userid = sf.SettingManager_ReadString(context, "userid");
			String token = sf.SettingManager_ReadString(context, "token");
			params.add(new BasicNameValuePair("auth_userid", userid));
			params.add(new BasicNameValuePair("auth_token", token));
		}

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(upLoadServerUri);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// find total file length and add to items
		long totalFilesLength = 0;
		int startItem = 1;
		for (String fileItem : sourceFileUri) {
			final File file = new File(fileItem);
			FileBody fb = new FileBody(file);
			builder.addPart("file" + startItem, fb);
			totalFilesLength += file.length();
			startItem++;
		}

		// add parameters
		for (NameValuePair nameValuePair : params) {
			builder.addTextBody(nameValuePair.getName(),
					nameValuePair.getValue(),
					ContentType.create("text/plain", MIME.UTF8_CHARSET)); //
		}

		final HttpEntity yourEntity = builder.build();

		final long TotalLength = totalFilesLength;
		class ProgressiveEntity implements HttpEntity {

			public long TotalLength = 0;
			protected onUploadListner uploadL;

			@Override
			public void consumeContent() throws IOException {
				yourEntity.consumeContent();
			}

			@Override
			public InputStream getContent() throws IOException,
					IllegalStateException {
				return yourEntity.getContent();
			}

			@Override
			public Header getContentEncoding() {
				return yourEntity.getContentEncoding();
			}

			@Override
			public long getContentLength() {
				return yourEntity.getContentLength();
			}

			@Override
			public Header getContentType() {
				return yourEntity.getContentType();
			}

			@Override
			public boolean isChunked() {
				return yourEntity.isChunked();
			}

			@Override
			public boolean isRepeatable() {
				return yourEntity.isRepeatable();
			}

			@Override
			public boolean isStreaming() {
				return yourEntity.isStreaming();
			} // CONSIDER put a _real_ delegator into here!

			@Override
			public void writeTo(OutputStream outstream) throws IOException {

				class ProxyOutputStream extends FilterOutputStream {
					/**
					 * @author Stephen Colebourne
					 */

					public ProxyOutputStream(OutputStream proxy) {
						super(proxy);
					}

					public void write(int idx) throws IOException {
						out.write(idx);
					}

					public void write(byte[] bts) throws IOException {
						out.write(bts);
					}

					public void write(byte[] bts, int st, int end)
							throws IOException {
						out.write(bts, st, end);
					}

					public void flush() throws IOException {
						out.flush();
					}

					public void close() throws IOException {
						out.close();
					}
				} // CONSIDER import this class (and risk more Jar File Hell)

				class ProgressiveOutputStream extends ProxyOutputStream {

					private long procced = 0;

					public ProgressiveOutputStream(OutputStream proxy) {
						super(proxy);
					}

					public void write(byte[] bts, int st, int end)
							throws IOException {

						procced += (end - st);
						long done = (int) (((double) procced)
								/ ((double) TotalLength) * 100L);
						;
						Log.d("progress", done + "%");

						if (uploadL != null)
							uploadL.onPercentChange(procced, TotalLength,
									(int) done);

						out.write(bts, st, end);
					}
				}

				yourEntity.writeTo(new ProgressiveOutputStream(outstream));
			}

		}
		ProgressiveEntity myEntity = new ProgressiveEntity();
		myEntity.TotalLength = TotalLength;
		myEntity.uploadL = uploadListner;

		post.setEntity(myEntity);

		if (uploadListner != null) {
			uploadListner.onStart();
		}
		HttpResponse response = client.execute(post);
		if (uploadListner != null) {
			String result = getContent(response);

			Log.i("WEB RESPONSE",
					"=================================================================");
			Log.i("WEB RESPONSE", "requests: \n ");

			// echo url
			Log.i("WEB RESPONSE", "URL: " + upLoadServerUri);

			// echo parameters
			for (int i = 0; i < params.size(); i++) {
				Log.i("WEB RESPONSE", "* " + params.get(i).getName() + " : "
						+ params.get(i).getValue());
			}

			Log.i("WEB RESPONSE",
					"+ + + + + + + + + + + + + + + + + + + + + + + + + + +");
			Log.i("WEB RESPONSE", "result: " + result);
			Log.i("WEB RESPONSE",
					"==================================================================");

			return result;
		}
		return "";

		// String fileName = sourceFileUri;
		//
		// HttpURLConnection conn = null;
		// DataOutputStream dos = null;
		// String lineEnd = "\r\n";
		// String twoHyphens = "--";
		// String boundary = "*****";
		// int bytesRead, bytesAvailable, bufferSize;
		// byte[] buffer;
		// int maxBufferSize = 1 * 1024 * 1024;
		// File sourceFile = new File(sourceFileUri);
		// if (!sourceFile.isFile()) {
		// Log.e("uploadFile", "Source File Does not exist");
		// return null;
		// }
		// int serverResponseCode;
		// try { // open a URL connection to the Servlet
		// FileInputStream fileInputStream = new FileInputStream(sourceFile);
		// URL url = new URL(upLoadServerUri);
		// conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
		// // connection to
		// // the URL
		// conn.setDoInput(true); // Allow Inputs
		// conn.setDoOutput(true); // Allow Outputs
		// conn.setUseCaches(false); // Don't use a Cached Copy
		// conn.setRequestMethod("POST");
		// conn.setRequestProperty("Connection", "Keep-Alive");
		// conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		// conn.setRequestProperty("Content-Type",
		// "multipart/form-data;boundary=" + boundary);
		// conn.setRequestProperty("uploaded_file", fileName);
		// dos = new DataOutputStream(conn.getOutputStream());
		//
		// dos.writeBytes(twoHyphens + boundary + lineEnd);
		// dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
		// + fileName + "\"" + lineEnd);
		// dos.writeBytes(lineEnd);
		//
		// bytesAvailable = fileInputStream.available(); // create a buffer of
		// // maximum size
		//
		// bufferSize = Math.min(bytesAvailable, maxBufferSize);
		// buffer = new byte[bufferSize];
		//
		// // read file and write it into form...
		// bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		//
		// while (bytesRead > 0) {
		// dos.write(buffer, 0, bufferSize);
		// bytesAvailable = fileInputStream.available();
		// bufferSize = Math.min(bytesAvailable, maxBufferSize);
		// bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		// }
		//
		// // send multipart form data necesssary after file data...
		// dos.writeBytes(lineEnd);
		// dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		//
		// // Responses from the server (code and message)
		// serverResponseCode = conn.getResponseCode();
		// String serverResponseMessage = conn.getResponseMessage();
		//
		// // get response
		// BufferedReader r = new BufferedReader(new InputStreamReader(
		// conn.getInputStream()));
		// StringBuilder total = new StringBuilder();
		// String line;
		// while ((line = r.readLine()) != null) {
		// total.append(line);
		// }
		//
		// String result = total.toString();
		//
		// Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
		// + ": " + serverResponseCode);
		// // close the streams //
		// fileInputStream.close();
		// dos.flush();
		// dos.close();
		// if (serverResponseCode == 200) {
		// return result;
		// }
		//
		// } catch (MalformedURLException ex) {
		// ex.printStackTrace();
		// Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		// return null;
		// } catch (Exception e) {
		// return null;
		// }
		// return null;

	}

	public static void UploadFile(Context context, String url,
			List<String> filePath, List<NameValuePair> params,
			onUploadListner listner) {

		new Async_WebUpload(context, url, filePath, params, listner)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
	}

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

			// check if user logged in, add user id and token to request
			if (sf.isUserLoggedIn(cont)) {
				String userid = sf.SettingManager_ReadString(cont, "userid");
				String token = sf.SettingManager_ReadString(cont, "token");
				params.add(new BasicNameValuePair("auth_userid", userid));
				params.add(new BasicNameValuePair("auth_token", token));
			}

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

				// echo url
				Log.i("WEB RESPONSE", "URL: " + url);

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
