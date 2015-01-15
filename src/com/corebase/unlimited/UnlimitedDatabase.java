package com.corebase.unlimited;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ata.corebase.sf;
import com.corebase.unlimited.UnlimitedList.DatabaseOrder;

public class UnlimitedDatabase {

	private Context context;

	private String TableName;
	private int DATABASE_VER = 2;
	private SQLiteOpenHelper helper;
	private SQLiteDatabase database;

	public static class UnlimitedDatabaseItem {
		public int ID;
		public String Data;
		public long Date;
	}

	public UnlimitedDatabase(Context con, String tableName) {
		this.context = con;
		this.TableName = tableName;
	}

	public class DB_Helper extends SQLiteOpenHelper {

		private String TableName;

		public DB_Helper(Context context, String tableName) {
			super(context, tableName, null, DATABASE_VER);
			this.TableName = tableName;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			try {
				// ORDER ITEM
				db.execSQL("CREATE TABLE " + this.TableName + " ("
						+ "id INTEGER PRIMARY KEY , " + "data STRING, "
						+ "date LONG );");

				// TODO , add index

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + this.TableName);
			onCreate(db);
		}

	}

	public UnlimitedDatabase open() {
		helper = new DB_Helper(this.context, this.TableName);
		database = helper.getWritableDatabase();
		return this;
	}

	public void close() {
		helper.close();
	}

	public long Add(String id, String data) {
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("data", data);
		values.put("date", sf.getUnixTime() + "");

		if (!this.Exist(id)) {
			// it is new item
			return database.insert(this.TableName, null, values);
		} else {
			// its is old item
			return database.update(this.TableName, values, "id = '" + id + "'",
					null);
		}
	}

	public boolean Exist(String ID) {
		Cursor cr = database.rawQuery("SELECT COUNT(*) FROM " + this.TableName
				+ " WHERE id = '" + ID + "'", null);

		if (cr.moveToNext()) {
			return cr.getInt(0) == 1;
		}

		return false;
	}

	public UnlimitedDatabaseItem GetByID(String ID) {

		String whereFunction = "id = '" + ID + "'";
		Cursor cr = database.query(this.TableName, new String[] { "id", "data",
				"date" }, whereFunction, null, null, null, null);

		if (cr.moveToNext()) {
			UnlimitedDatabaseItem item = new UnlimitedDatabaseItem();
			item.ID = cr.getInt(0);
			item.Data = cr.getString(1);
			item.Date = cr.getLong(2);
			return item;
		}

		return null;

	}

	public List<UnlimitedDatabaseItem> GetAllItems() {

		// fetch default values
		return this.GetAllItems(0, 1000, DatabaseOrder.Asc);

	}

	public void Property_Remove(int propertyID) {

		database.delete(this.TableName, "id =" + propertyID, null);

	}

	public List<UnlimitedDatabaseItem> GetAllItems(int[] ids) {

		List<String> searchIDS = new ArrayList<String>();
		for (int i : ids) {
			searchIDS.add(i + "");
		}

		// create search query
		String idsQuery = "id IN ("
				+ sf.combine(searchIDS.toArray(new String[searchIDS.size()]),
						",") + ")";

		Cursor cr = database.query(this.TableName, new String[] { "id", "data",
				"date" }, idsQuery, null, null, null, "id DESC");

		List<UnlimitedDatabaseItem> items = new ArrayList<UnlimitedDatabase.UnlimitedDatabaseItem>();
		while (cr.moveToNext()) {
			UnlimitedDatabaseItem item = new UnlimitedDatabaseItem();
			item.ID = cr.getInt(0);
			item.Data = cr.getString(1);
			item.Date = cr.getLong(2);
			items.add(item);
		}

		return items;
	}

	public void DeleteAllItem() {
		database.delete(TableName, "", null);
	}

	public List<UnlimitedDatabaseItem> GetAllItems(int startPos, int limit,
			DatabaseOrder databaseOrder) {

		Cursor cr = database.query(this.TableName, new String[] { "id", "data",
				"date" }, null, null, null, null, "id "
				+ (databaseOrder == DatabaseOrder.Desc ? "DESC" : "ASC"),
				startPos + "," + limit);

		List<UnlimitedDatabaseItem> items = new ArrayList<UnlimitedDatabase.UnlimitedDatabaseItem>();
		while (cr.moveToNext()) {
			UnlimitedDatabaseItem item = new UnlimitedDatabaseItem();
			item.ID = cr.getInt(0);
			item.Data = cr.getString(1);
			item.Date = cr.getLong(2);
			items.add(item);
		}

		return items;
	}
}
