package com.ata.corebase;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class sf {

	public static Object getFieldValue(Object item, String name) {
		try {
			return item.getClass().getField(name).get(item);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// public static String getFied

	public static boolean hasConnection(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static String ConvertMiliSecoundIntToTime(int value) {
		int millis = value;
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(tz);
		String time = df.format(new Date(millis));
		return time;
	}

	public static String GetTimeFromUnix(int time, int offset) {
		time = (time * 1000) + (offset * 1000);
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(date);
	}

	public static String GetDateFromUnix(int time, int offset) {
		time = (time * 1000) + (offset * 1000);
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy\\MMM\\dd HH:mm:ss a");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(date);
	}

	public static void GetLocationByNetwork(Context cont) {

		String provider = LocationManager.NETWORK_PROVIDER;

		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) cont
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		final LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {

				Log.d("sf", location.getLatitude() + "");
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				Log.d("sf", status + "");
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		Looper myLooper = Looper.myLooper();
		locationManager.requestSingleUpdate(provider, locationListener,
				myLooper);
		final Handler myHandler = new Handler(myLooper);
		myHandler.postDelayed(new Runnable() {
			public void run() {
				locationManager.removeUpdates(locationListener);
			}
		}, 30 * 1000);

	}

	public static class MCrypt {

		// private String iv = "fedcba9876543210";
		private String iv = "umr";
		private String iv2 = "52k6b";
		private String iv3 = "hu8sa";
		private String iv4 = "4dji";
		private String iv5 = "17pc";
		private String iv6 = "qwz9";
		private String iv7 = "pwz9";

		private String SecretKey = "H&B#*XH#(KJ)#*(*";
		private IvParameterSpec ivspec;
		private SecretKeySpec keyspec;
		private Cipher cipher;

		public MCrypt() {
			ivspec = new IvParameterSpec((iv + iv2 + iv5 + iv6).getBytes());

			keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

			try {
				cipher = Cipher.getInstance("AES/CBC/NoPadding");
			} catch (NoSuchAlgorithmException e) {

				e.printStackTrace();
			} catch (NoSuchPaddingException e) {

				e.printStackTrace();
			}
		}

		public byte[] encrypt(String text) throws Exception {
			if (text == null || text.length() == 0)
				throw new Exception("Empty string");

			byte[] encrypted = null;

			try {
				cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

				encrypted = cipher.doFinal(padString(text).getBytes());
			} catch (Exception e) {
				throw new Exception("[encrypt] " + e.getMessage());
			}

			return encrypted;
		}

		public byte[] decrypt(String code) throws Exception {
			if (code == null || code.length() == 0)
				throw new Exception("Empty string");

			byte[] decrypted = null;

			try {
				cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

				decrypted = cipher.doFinal(hexToBytes(code));
			} catch (Exception e) {
				throw new Exception("[decrypt] " + e.getMessage());
			}
			return decrypted;
		}

		public static String bytesToHex(byte[] data) {
			if (data == null) {
				return null;
			}

			int len = data.length;
			String str = "";
			for (int i = 0; i < len; i++) {
				if ((data[i] & 0xFF) < 16)
					str = str + "0"
							+ java.lang.Integer.toHexString(data[i] & 0xFF);
				else
					str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
			}
			return str;
		}

		public static byte[] hexToBytes(String str) {
			if (str == null) {
				return null;
			} else if (str.length() < 2) {
				return null;
			} else {
				int len = str.length() / 2;
				byte[] buffer = new byte[len];
				for (int i = 0; i < len; i++) {
					buffer[i] = (byte) Integer.parseInt(
							str.substring(i * 2, i * 2 + 2), 16);
				}
				return buffer;
			}
		}

		private static String padString(String source) {
			char paddingChar = ' ';
			int size = 16;
			int x = source.length() % size;
			int padLength = size - x;

			for (int i = 0; i < padLength; i++) {
				source += paddingChar;
			}

			return source;
		}
	}

	public static final String PREFS_NAME = "internal";

	public static String GetMD5(String filename) {

		String result = "";
		try {
			InputStream fis = new FileInputStream(filename);

			byte[] buffer = new byte[1024];
			MessageDigest complete;

			complete = MessageDigest.getInstance("MD5");

			int numRead;

			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);

			fis.close();

			byte[] b = complete.digest();

			for (int i = 0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100, 16)
						.substring(1);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return result;

	}

	public static void PerfernceManager_Write(Activity activity,
			String PropertyName, String value) {

		PerfernceManager_Write((Context) activity, PropertyName, value);

	}

	public static String SettingManager_ReadString(Context context,
			String PropertyName) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(PropertyName, "");

	}

	public static boolean SettingManager_ReadBoolean(Context activity,
			String PropertyName, boolean defau) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		return prefs.getBoolean(PropertyName, defau);

	}

	public static void SettingManager_WriteString(Activity activity,
			String PropertyName, String value) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		prefs.edit().putString(PropertyName, value).commit();

	}

	public static void SettingManager_WriteBoolean(Activity activity,
			String PropertyName, boolean value) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		prefs.edit().putBoolean(PropertyName, value).commit();

	}

	public static String PerfernceManager_Read(Context context,
			String PropertyName) {

		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String value = prefs.getString(PropertyName, "");
		if (value.equals("")) {
			return value;
		} else {
			try {
				return EncodeDecodeAES.decrypt("main activity", value);
			} catch (Exception e) {
				e.printStackTrace();
				return value;
			}
		}
	}

	public static String PerfernceManager_Read(Activity activity,
			String PropertyName) {

		return PerfernceManager_Read((Context) activity, PropertyName);

	}

	public static void PerfernceManager_Write(Context context,
			String PropertyName, String value) {

		if (value.equals("")) {
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,
					Context.MODE_PRIVATE);
			prefs.edit().putString(PropertyName, value).commit();
		} else {
			try {
				SharedPreferences prefs = context.getSharedPreferences(
						PREFS_NAME, Context.MODE_PRIVATE);
				prefs.edit()
						.putString(PropertyName,
								EncodeDecodeAES.encrypt("main activity", value))
						.commit();

			} catch (Exception e) {
				SharedPreferences prefs = context.getSharedPreferences(
						PREFS_NAME, Context.MODE_PRIVATE);
				prefs.edit().putString(PropertyName, value).commit();
				e.printStackTrace();

			}
		}

	}

	public static class EncodeDecodeAES {

		private final static String HEX = "0123456789ABCDEF";
		private final static int JELLY_BEAN_4_2 = 17;
		private final static byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0 };

		// static {
		// Security.addProvider(new BouncyCastleProvider());
		// }

		public static String encrypt(String seed, String cleartext)
				throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = encrypt(rawKey, cleartext.getBytes());
			String fromHex = toHex(result);
			String base64 = new String(Base64.encodeToString(
					fromHex.getBytes(), 0));
			return base64;
		}

		public static String decrypt(String seed, String encrypted)
				throws Exception {
			byte[] seedByte = seed.getBytes();
			System.arraycopy(seedByte, 0, key, 0,
					((seedByte.length < 16) ? seedByte.length : 16));
			String base64 = new String(Base64.decode(encrypted, 0));
			byte[] rawKey = getRawKey(seedByte);
			byte[] enc = toByte(base64);
			byte[] result = decrypt(rawKey, enc);
			return new String(result);
		}

		public static byte[] encryptBytes(String seed, byte[] cleartext)
				throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = encrypt(rawKey, cleartext);
			return result;
		}

		public static byte[] decryptBytes(String seed, byte[] encrypted)
				throws Exception {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] result = decrypt(rawKey, encrypted);
			return result;
		}

		private static byte[] getRawKey(byte[] seed) throws Exception {
			KeyGenerator kgen = KeyGenerator.getInstance("AES"); // , "SC");
			SecureRandom sr = null;
			if (android.os.Build.VERSION.SDK_INT >= JELLY_BEAN_4_2) {
				sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
			} else {
				sr = SecureRandom.getInstance("SHA1PRNG");
			}
			sr.setSeed(seed);
			try {
				kgen.init(256, sr);
				// kgen.init(128, sr);
			} catch (Exception e) {
				// Log.w(LOG,
				// "This device doesn't suppor 256bits, trying 192bits.");
				try {
					kgen.init(192, sr);
				} catch (Exception e1) {
					// Log.w(LOG,
					// "This device doesn't suppor 192bits, trying 128bits.");
					kgen.init(128, sr);
				}
			}
			SecretKey skey = kgen.generateKey();
			byte[] raw = skey.getEncoded();
			return raw;
		}

		private static byte[] encrypt(byte[] raw, byte[] clear)
				throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES"); // /ECB/PKCS7Padding", "SC");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(clear);
			return encrypted;
		}

		private static byte[] decrypt(byte[] raw, byte[] encrypted)
				throws Exception {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES"); // /ECB/PKCS7Padding", "SC");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted = cipher.doFinal(encrypted);
			return decrypted;
		}

		public static String toHex(String txt) {
			return toHex(txt.getBytes());
		}

		public static String fromHex(String hex) {
			return new String(toByte(hex));
		}

		public static byte[] toByte(String hexString) {
			int len = hexString.length() / 2;
			byte[] result = new byte[len];
			for (int i = 0; i < len; i++)
				result[i] = Integer.valueOf(
						hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
			return result;
		}

		public static String toHex(byte[] buf) {
			if (buf == null)
				return "";
			StringBuffer result = new StringBuffer(2 * buf.length);
			for (int i = 0; i < buf.length; i++) {
				appendHex(result, buf[i]);
			}
			return result.toString();
		}

		private static void appendHex(StringBuffer sb, byte b) {
			sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
		}

	}

	/**
	 * checkes if the user is logged in
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUserLoggedIn(Context context) {
		String userid = sf.SettingManager_ReadString(context, "userid");
		return userid != "";
	}

	public static boolean requireUserLogin(Context context) {

		String value = sf
				.SettingManager_ReadString(context, "requireuserlogin");
		return value != null && value.equals("1");
	}

	public static String ConvertLongToFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.##").format(size
				/ Math.pow(1024, digitGroups))
				+ " " + units[digitGroups];
	}

	public static void ShowNoConnectionMessage(Context context,
			boolean ShowAsToast) {

		if (ShowAsToast) {
			Toast.makeText(context, "عدم دسترسی به اینترنت", Toast.LENGTH_SHORT)
					.show();
		} else {
			ShowNoConnectionMessage(context);
		}

	}

	public static void ShowNoConnectionMessage(Context context) {
		new AlertDialog.Builder(context)
				.setTitle("No Internet Connection")
				.setMessage(
						"You need to connect to internet before continue, would you like to open connection settings?")
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setPositiveButton("Check Internet Connection",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {

							}
						}).create().show();

	}

	public static String getDeviceID(Context context) {
		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();
		return deviceId;
	}

	public static void setFragment(Activity context, int container,
			Fragment fragment) {
		FragmentManager fragmentManager = context.getFragmentManager();
		fragmentManager.beginTransaction().replace(container, fragment)
				.commit();

	}

	public static String getDate(long date) {
		return date + "";
	}

	public static void sendTextMessage(Activity activity, String message,
			String phone) {
		activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
				+ phone)));

	}

	public static void call(Activity activity, String publicPhone) {

		String number = "tel:" + publicPhone.toString().trim();
		Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
		activity.startActivity(callIntent);

	}

	public static void sendEmail(Activity activity, String email, String subject) {

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		i.putExtra(Intent.EXTRA_SUBJECT, subject);

		try {
			activity.startActivity(Intent.createChooser(i, "ارسال ایمیل"));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "سیستمی برای ارسال یافت نگردید",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static String JoinString(String[] s, String glue) {

		int k = s.length;
		if (k == 0) {
			return null;
		}
		StringBuilder out = new StringBuilder();
		out.append(s[0]);
		for (int x = 1; x < k; ++x) {
			out.append(glue).append(s[x]);
		}
		return out.toString();

	}

	public static void sendMessageInternal(Context context, String phone,
			String message) {
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("0" + phone, null, message, null, null);
			Toast.makeText(context, "ارسال گردید", Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Toast.makeText(context, "SMS faild, please try again.",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	public static void sendMesageIntent(Context context, String phone,
			String message) {
		// add the phone number in the data
		Uri uri = Uri.parse("smsto:" + phone.toString());

		Intent smsSIntent = new Intent(Intent.ACTION_SENDTO, uri);
		// add the message at the sms_body extra field
		smsSIntent.putExtra("sms_body", message.toString());
		try {
			context.startActivity(smsSIntent);
		} catch (Exception ex) {
			Toast.makeText(context, "Your sms has failed...", Toast.LENGTH_LONG)
					.show();
			ex.printStackTrace();
		}
	}

	public static String priceToHuman(String totalPrice) {
		double value = Double.valueOf(totalPrice);
		if (value == 0) {
			return "رایگان";
		} else if (value < 1) {
			return value * 1000 + " هزار تومان";
		} else if (value < 1000) {
			return value + " میلیون تومان";
		} else {
			return value / 1000 + " میلیارد تومان";
		}
	}

}
