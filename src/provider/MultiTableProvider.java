package provider;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import provider.table.Table;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;
import cn.hjmao.specialfriend.SpecialFriend;

public class MultiTableProvider extends ContentProvider {

	// When use it, this variables should be modified.
	public static final String AUTHORITY = SpecialFriend.AUTHORITY;
	public static final String DATABASE_NAME = SpecialFriend.DATABASE_NAME;
	public static String WEIBO_CLASS_NAME = "Weibo";
	public static String USER_CLASS_NAME = "User";
	public static String[] tablenames = {WEIBO_CLASS_NAME, USER_CLASS_NAME};

	
	
	private static String TAG = "MultiTableProvider";
	private static final int DATABASE_VERSION = 1;
	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final HashMap<Integer, Table> uriCode2Table = new HashMap<Integer, Table>();
	private static final HashMap<String, Table> tables = new HashMap<String, Table>();
	private DatabaseHelper mOpenHelper;

	static {
		loadTables();
	}

	private static void loadTables() {
		ArrayList<String> classNames = new ArrayList<String>();
		String tablePackage = Table.class.getPackage().getName();
		
		for (int i=0; i<tablenames.length; i++) {
			classNames.add(tablenames[i]);
		}
		
		try {
			for (int i = 0; i < classNames.size(); i++) {
				String className = classNames.get(i);
				String tableName = className;
				sUriMatcher.addURI(AUTHORITY, tableName, i);
				sUriMatcher.addURI(AUTHORITY, tableName + "/#", i);

				Class<?> tableClass = Class.forName(tablePackage + "." + className);
				Constructor<?> constructor = tableClass.getConstructor();
				Table table = (Table) constructor.newInstance();
				uriCode2Table.put(i, table);
				tables.put(tableName, table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri rowUri;

		int code = sUriMatcher.match(uri);
		if (uriCode2Table.containsKey(code)) {
			Table table = uriCode2Table.get(code);
			rowUri = table.insert(uri, values);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(rowUri, null);
		return rowUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		int code = sUriMatcher.match(uri);
		if (uriCode2Table.containsKey(code)) {
			Table table = uriCode2Table.get(code);
			count = table.delete(uri, selection, selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		
		int code = sUriMatcher.match(uri);
		if (uriCode2Table.containsKey(code)) {
			Table table = uriCode2Table.get(code);
			count = table.update(uri, values, selection, selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor;
		
		int code = sUriMatcher.match(uri);
		if (uriCode2Table.containsKey(code)) {
			Table table = uriCode2Table.get(code);
			cursor = table.query(uri, projection, selection, selectionArgs, sortOrder);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return cursor;
	}

	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		if (tables.size() <= 0) {
			loadTables();
		}
		for (Table table: tables.values()) {
			table.setDBHelper(mOpenHelper);
		}
		return false;
	}

	@Override
	public String getType(Uri uri) {
		String type = null;
		
		int code = sUriMatcher.match(uri);
		if (uriCode2Table.containsKey(code)) {
			Table table = uriCode2Table.get(code);
			type = table.getType(uri);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		return type;
	}
	
	public static HashMap<String, Table> getTables() {
		if (tables.size() <= 0) {
			loadTables();
		}
		return tables;
	}
	
	public static Table getTable(String name) {
		if (tables.size() <= 0) {
			loadTables();
		}
		return tables.get(name);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		private static void initTables (SQLiteDatabase db) {
			HashMap<String, Table> tables = MultiTableProvider.getTables();
			for (Table table: tables.values()) {
				table.create(db);
			}
		}
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			Log.v(TAG, "Provider.DatabaseHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			initTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	}
}
