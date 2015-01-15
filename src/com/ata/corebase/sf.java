package com.ata.corebase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.DatePicker;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.corebase.config.BaseConfig;

public class sf {

	private static final String TAG = "Static Functions";

	public static void ScrollView_ScrollToEnd(final ScrollView view) {
		view.post(new Runnable() {

			@Override
			public void run() {
				view.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	public static float ConvertDPtoPX(Context context, int DP) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP,
				r.getDisplayMetrics());
		return px;
	}

	public static void SetLocal(Context context, String local) {
		Locale locale = new Locale("fa");
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,
				context.getResources().getDisplayMetrics());
	}

	public static void ResizeImage(Context context, String path,
			int CompressRatio, double MaxWidth, double MaxHeight) {

		Bitmap b = BitmapFactory.decodeFile(path);

		double calcWidth = 0;
		double calcHeight = 0;
		// get ideal size
		if (b.getWidth() > b.getHeight()) {
			// landscape image
			double ratio = MaxWidth / b.getWidth();
			double expectedHeight = ratio * b.getHeight();
			if (expectedHeight <= MaxHeight) {
				// ideal size valid
				calcWidth = MaxWidth;
				calcHeight = expectedHeight;
			} else {
				ratio = b.getHeight() * MaxHeight;
				calcWidth = b.getWidth() * ratio;
				calcHeight = MaxHeight;
			}
		} else {
			// pot image
			double ratio = MaxWidth / b.getWidth();
			double expectedHeight = ratio * b.getHeight();
			if (expectedHeight <= MaxHeight) {
				// ideal size valid
				calcWidth = MaxWidth;
				calcHeight = expectedHeight;
			} else {
				ratio = b.getHeight() * MaxHeight;
				calcWidth = b.getWidth() * ratio;
				calcHeight = MaxHeight;
			}
		}

		Log.d("Image Rezie", "Original Width : " + b.getWidth()
				+ " Original Height : " + b.getHeight());
		Log.d("Image Rezie", "Ideal Width : " + calcWidth + " Ideal Height : "
				+ calcHeight);

		Bitmap out = Bitmap.createScaledBitmap(b, (int) calcWidth,
				(int) calcHeight, false);

		File file = new File(path);
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			out.compress(Bitmap.CompressFormat.JPEG, CompressRatio, fOut);
			fOut.flush();
			fOut.close();
			b.recycle();
			out.recycle();

		} catch (Exception e) { // TODO

		}
	}

	public static Bitmap Image_Load(int Scale, String internalPath)
			throws FileNotFoundException {
		File cacheFile = new File(internalPath);
		InputStream fileInputStream = new FileInputStream(cacheFile);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = Scale;
		bitmapOptions.inJustDecodeBounds = false;
		Bitmap loadedBitmap = BitmapFactory.decodeStream(fileInputStream, null,
				bitmapOptions);
		return loadedBitmap;
	}

	public static Object ExpandableList_GetLongClickItem(
			ExpandableListView listView, ExpandableListAdapter adapter,
			int position) {

		long pos = listView.getExpandableListPosition(position);

		// get type and correct positions
		int itemType = ExpandableListView.getPackedPositionType(pos);
		int groupPos = ExpandableListView.getPackedPositionGroup(pos);
		int childPos = ExpandableListView.getPackedPositionChild(pos);

		// if child is long-clicked
		if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {

			return adapter.getChild(groupPos, childPos);
		} else {
			return null;
		}
	}

	public static Point GetDeviceWidthHeight(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static String combine(String[] s, String glue) {
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

	private static boolean File_SaveString(Context context, String path,
			String string) {

		FileOutputStream fop = null;
		File file;

		try {

			file = new File(path);
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = string.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			if (fop != null) {
				fop.close();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static boolean SDCART_isReadable() {

		boolean mExternalStorageAvailable = false;
		try {
			String state = Environment.getExternalStorageState();

			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = true;
				Log.i("isSdReadable", "External storage card is readable.");
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				Log.i("isSdReadable", "External storage card is readable.");
				mExternalStorageAvailable = true;
			} else {
				// Something else is wrong. It may be one of many other
				// states, but all we need to know is we can neither read nor
				// write
				mExternalStorageAvailable = false;
			}
		} catch (Exception ex) {

		}
		return mExternalStorageAvailable;
	}

	public static String File_Read(Context context, String fileName) {
		String stringToReturn = " ";
		try {
			if (SDCART_isReadable()) // isSdReadable()e method is define at
										// bottom of
			// the post
			{

				FileReader fis = new FileReader(new File(fileName));
				BufferedReader reader = new BufferedReader(fis);
				StringBuilder stringBuilder = new StringBuilder();
				String receiveString = "";
				while ((receiveString = reader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}
				fis.close();
				return stringBuilder.toString();

			}
		} catch (FileNotFoundException e) {
			Log.e("TAG", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("TAG", "Can not read file: " + e.toString());
		}

		return stringToReturn;
	}

	public static Bitmap Notification_GetResizedIcon(Context context,
			Bitmap bitmap) {
		Resources res = context.getResources();
		int height = (int) res
				.getDimension(android.R.dimen.notification_large_icon_height);
		int width = (int) res
				.getDimension(android.R.dimen.notification_large_icon_width);
		bitmap = Bitmap.createScaledBitmap(bitmap, width * 8 / 10,
				height * 8 / 10, false);
		return bitmap;
	}

	public enum ApplicationNotificationType {
		Player, Sync, Recorder
	}

	public static List<ApplicationNotificationType> stayOnTop_NotificationsList = new ArrayList<sf.ApplicationNotificationType>();

	public static void StayOnTop(ApplicationNotificationType type,
			int NotificationID, Context context, Intent resultIntent,
			String title, String description, int iconID) {

		// as application can have many types of notification, we have to check
		// for the list and check if there is any notification before, if there
		// was, we have to keep number one as foreground and make the new one as
		// only notification, if there is no, just start the notification as
		// forground

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(iconID)
				.setContentTitle(title)
				.setLights(
						context.getResources().getColor(
								BaseConfig.NOTIFICATION_COLOR), 800, 400)
				.setContentText(description);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		if (stayOnTop_NotificationsList.size() == 0) {
			// we have to just start notification as forground
			Notification notification = mBuilder.build();
			notification.flags |= Notification.FLAG_ONGOING_EVENT
					| Notification.FLAG_FOREGROUND_SERVICE
					| Notification.FLAG_NO_CLEAR;

			((IntentService) context).startForeground(NotificationID,
					notification);

			// add the type to the list
			stayOnTop_NotificationsList.add(type);

		} else {

			// we just need to add the new item as notification
			Notification notification = mBuilder.build();
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(ns);

			mNotificationManager.notify(NotificationID, notification);

			// the size is not zero, check if we have not added that
			// notification to the list before
			if (!stayOnTop_NotificationsList.contains(type)) {
				// add the type to the list
				stayOnTop_NotificationsList.add(type);
			} else {
				// already on top we do not need to show that again
				Log.d(TAG, "Notification Already On Top");
			}
		}

	}

	public static void StayOnTop_Cancel(ApplicationNotificationType type,
			int NotificationID, Context context) {

		if (stayOnTop_NotificationsList.size() == 0) {
			// we have problem, the stay on top should not be zero
			Log.e(TAG, "StayOnTop_Cancel can not be empty");
		} else if (stayOnTop_NotificationsList.size() == 1) {
			if (stayOnTop_NotificationsList.get(0) == type) {
				((IntentService) context).stopForeground(true);
				stayOnTop_NotificationsList.clear();

			} else {
				Log.e(TAG, "Wrong Notification Type 1");
			}
		} else {

			if (stayOnTop_NotificationsList.contains(type)) {

				Notification_Cancel(context, NotificationID);

				// we have to only remove the notification item
				stayOnTop_NotificationsList.remove(type);
			} else {
				Log.e(TAG, "Wrong Notification Type 2");
			}
		}

	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
		}
	}

	public static Drawable getNotificationIcon(Context context,
			int notificatonCount) {

		int imageW = 64;
		int imageH = 64;

		int x = 32;
		int y = 32;
		int r = 28;

		Paint mPaintBack = new Paint();
		mPaintBack.setAntiAlias(true);
		mPaintBack.setColor(Color.WHITE);

		// Background
		Bitmap bitmap = Bitmap.createBitmap(imageW, imageH, Config.ARGB_8888);
		Canvas mCanvas = new Canvas(bitmap);
		mCanvas.drawCircle(x, y, r, mPaintBack);

		// Text
		Paint mPaintText = new Paint();
		mPaintText.setColor(Color.RED);
		mPaintText.setAntiAlias(true);

		int size = 0;
		do {
			mPaintText.setTextSize(++size);
		} while (mPaintText.measureText(notificatonCount + "") < (imageW * 0.4f));

		float TextWidth = mPaintText.measureText(notificatonCount + "");

		mCanvas.drawText(notificatonCount + "", (imageW - TextWidth) / 2,
				imageH * 0.8f, mPaintText);

		BitmapDrawable drawable = new BitmapDrawable(context.getResources(),
				bitmap);

		return drawable;
	}

	public static boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public static void DownloadFileWithAndroidDownloader(Context context,
			String url, String title, String Description, String SaveFileName) {

		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));

		request.setDescription(Description);
		request.setTitle(title);
		// in order for this if to run, you must use the android 3.2 to compile
		// your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, SaveFileName);

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);

	}

	public static String getSimpleDurationFormat(int size) {
		if (size < 60) {
			if (size == 1)
				return size + " Sec";
			else
				return size + " Secs";
		} else if (size < 60 * 60) {

			int minute = size / 60;
			return minute + " minutes " + (size % 60) + " secounds";
		} else if (size > 60 * 60) {

			int hour = size / 3600;
			int minute = (size % 3600) / 60;
			int secound = (size % 3600) % 60;
			return hour + " hours" + minute + " minutes " + secound
					+ " secounds";
		} else {
			return "more than one day";
		}
	}

	public class RoundedImageView extends ImageView {

		public RoundedImageView(Context context) {
			super(context);

		}

		public RoundedImageView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		public RoundedImageView(Context context, AttributeSet attrs,
				int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			Drawable drawable = getDrawable();

			if (drawable == null) {
				return;
			}

			if (getWidth() == 0 || getHeight() == 0) {
				return;
			}
			Bitmap b = ((BitmapDrawable) drawable).getBitmap();
			Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

			int w = getWidth(), h = getHeight();

			Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
			canvas.drawBitmap(roundBitmap, 0, 0, null);

		}

		public Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
			Bitmap sbmp;
			if (bmp.getWidth() != radius || bmp.getHeight() != radius)
				sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
			else
				sbmp = bmp;
			Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
					sbmp.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xffa19774;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			paint.setDither(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.parseColor("#BAB399"));
			canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f,
					sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f,
					paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(sbmp, rect, rect, paint);

			return output;
		}
	}

	public static Bitmap CircleBitmap(Bitmap bitmapimg) {

		Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
				bitmapimg.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
				bitmapimg.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawCircle(bitmapimg.getWidth() / 2, bitmapimg.getHeight() / 2,
				bitmapimg.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmapimg, rect, rect, paint);
		return output;

	}

	public static String GetDayName(int day) {
		switch (day) {
		case 0:
			return "Sunday";
		case 1:
			return "Monday";
		case 2:
			return "Tusday";
		case 3:
			return "Wednesday";
		case 4:
			return "Thirsday";
		case 5:
			return "Friday";
		case 6:
			return "Saturday";
		}
		return "";
	}

	public static void File_Copy(File src, File dst) throws IOException {

		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static String getDate(int year, int month, int day) {

		if (day == 0 && month == 0 && year == 0) {
			return "";
		}

		String result = "";
		result += year + "/";
		result += getMonthName(month) + "/";
		result += day + "";
		return result;
	}

	private static String getMonthName(int month) {
		switch (month) {
		case 0:
			return "January";
		case 1:
			return "February";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "August";
		case 8:
			return "September";
		case 9:
			return "October";
		case 10:
			return "November";
		case 11:
			return "December";
		default:
			return "invalid month";
		}
	}

	public static void Notification_Show(int NotificationID, Context context,
			Intent resultIntent, String title, String description, int iconID,
			Bitmap largeIcon) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(iconID)
				.setContentTitle(title)
				.setLights(
						context.getResources().getColor(
								BaseConfig.NOTIFICATION_COLOR), 800, 400)
				.setContentText(description);

		if (largeIcon != null) {
			mBuilder.setLargeIcon(largeIcon);
		}

		// Creates an explicit intent for an Activity in your app

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		// stackBuilder.addParentStack(ac_Notifiaction.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NotificationID, mBuilder.build());

	}

	public static void Notification_Show(int NotificationID, Context context,
			Intent resultIntent, String title, String description, int iconID) {

		Notification_Show(NotificationID, context, resultIntent, title,
				description, iconID, null);
	}

	public static void Notification_Cancel(Context context, int NotifiactionID) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NotifiactionID);
	}

	public static void SwitchFragment(Context context, int LayoutID,
			Fragment frg) {

		SwitchFragment(context, LayoutID, frg, false);

	}

	public static void SwitchFragment(Context context, int LayoutID,
			Fragment frg, boolean startFragment) {

		FragmentManager fragmentManager = ((FragmentActivity) context)
				.getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();

		// get classname of fragment
		String frg_ClassName = frg.getClass().toString();

		// Log.d(TAG, "fragmentManager.getFragments().size() : "
		// + fragmentManager.getFragments().size());
		//
		if (fragmentManager.getBackStackEntryCount() > 0) {

			BackStackEntry backStack = fragmentManager
					.getBackStackEntryAt(fragmentManager
							.getBackStackEntryCount() - 1);
			if (backStack.getName().equals(frg_ClassName)) {
				// we already have that item in list
				Toast.makeText(context, "Task Already in front",
						Toast.LENGTH_SHORT).show();
			} else {
				if (startFragment) {
					ft.replace(LayoutID, frg);
				} else {
					ft.replace(LayoutID, frg);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.addToBackStack(frg_ClassName);
				}
			}
		} else {
			if (startFragment) {
				ft.replace(LayoutID, frg);
			} else {
				ft.replace(LayoutID, frg);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.addToBackStack(frg_ClassName);
			}
		}
		//
		// Fragment lastFrg = fragmentManager.getFragments().get(
		// fragmentManager.getFragments().size() - 1);
		//
		// Log.d(TAG, "fr " + frg.g + " - " + lastFrg.getId());
		// if (lastFrg.equals(frg)) {

		// return;
		// }
		//
		// }

		ft.commit();

	}

	public static Bitmap RotateBitmap(Bitmap bitmap, int degree) {
		Matrix matrix = new Matrix();

		matrix.postRotate(degree);

		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
				bitmap.getWidth(), bitmap.getHeight(), true);

		return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
				scaledBitmap.getHeight());
	}

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

	/**
	 * Vibrate for some millisecond
	 * 
	 * @param ctx
	 *            Context
	 * @param time
	 *            in millisecond
	 */
	public static void vibrate(Context ctx, long time) {
		Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		// Vibrate for 500 milliseconds
		v.vibrate(time);
	}

	/**
	 * Vibrate Device
	 * 
	 * @param ctx
	 * @param time
	 *            long pattern to vibrate
	 */
	public static void vibrate(Context ctx, long[] time) {
		Vibrator v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(time, -1);
	}

	public static void cancelNotification(Context ctx, int notifyId) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nMgr = (NotificationManager) ctx
				.getSystemService(ns);
		nMgr.cancel(notifyId);
	}

	/**
	 * Open URL
	 * 
	 * @param context
	 * @param url
	 */
	public static void OpenUrl(Context context, String url) {

		// fix url
		if (!url.startsWith("http://") && !url.startsWith("https://"))
			url = "http://" + url;
		// open in browser
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		context.startActivity(browserIntent);
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

	public static void SettingManager_WriteString(Context context,
			String PropertyName, String value) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
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

	// public static void setFragment(Activity context, int container,
	// Fragment fragment) {
	// FragmentManager fragmentManager = context.getFragmentManager();
	// fragmentManager.beginTransaction().replace(container, fragment)
	// .commit();
	//
	// }

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

	public static JSONObject ConvertBasicNameValuePairToJSON(
			List<NameValuePair> list) throws JSONException {

		JSONObject json = new JSONObject();
		for (NameValuePair basicNameValuePair : list) {
			json.put(basicNameValuePair.getName(),
					basicNameValuePair.getValue());
		}
		return json;
	}

	public static List<NameValuePair> ConvertJSON_TO_BasicNameValuePair(
			String item) throws JSONException {

		List<NameValuePair> items = new ArrayList<NameValuePair>();
		JSONObject json = new JSONObject(item);
		Iterator<String> keys = json.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			items.add(new BasicNameValuePair(key, json.getString(key)));
		}
		return items;
	}

	/**
	 * 
	 * @return current time in sec like 14656121541
	 */
	public static long getUnixTime() {
		return System.currentTimeMillis() / 1000L;
	}

}
