package provider.table;

import java.util.HashMap;

import provider.MultiTableProvider;
import provider.MultiTableProvider.DatabaseHelper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

abstract public class Table implements BaseColumns {
	private UriMatcher uriMatcher;
	private static final int ENTRIES = 1;
	private static final int ENTRY_ID = 2;
	private static final int ENTRY_ID_PATH_POSITION = 1;
	private static final String SCHEME = "content://";
	private static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd.phonemaster.";
	private static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd.phonemaster.";
	private DatabaseHelper dbHelper;

	protected String createTableSQL;
	protected HashMap<String, String> attrNameTypeMap;
	protected HashMap<String, String> projectionMap;
	
	abstract public String[] getAttributes();

	public Table() {
		initAttr();
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(MultiTableProvider.AUTHORITY, getTableName(), ENTRIES);
		uriMatcher.addURI(MultiTableProvider.AUTHORITY, getTableName() + "/#", ENTRY_ID);
	}

	public int initAttr() {
		attrNameTypeMap = new HashMap<String, String> ();
		projectionMap = new HashMap<String, String> ();
		
		createTableSQL = "CREATE TABLE " + getTableName() + " (";
		createTableSQL += Table._ID + " INTEGER PRIMARY KEY, ";

		String[] attrs = getAttributes();
		for (int i=0; i<attrs.length; i++) {
			String nameType = attrs[i];
			String[] nameAndType = nameType.split(":");
			if (nameAndType.length == 2) {
				String attrName = nameAndType[0];
				String attrType = nameAndType[1];
				createTableSQL += attrName + " " + attrType + ", ";
				attrNameTypeMap.put(attrName, attrType);
				projectionMap.put(attrName, attrName);
			}
		}
		if (createTableSQL.length() >= 2) {
			createTableSQL = createTableSQL.substring(0, createTableSQL.length()-2);
		}
		createTableSQL += ");";

		return 0;
	}

	public Uri insert(Uri uri, ContentValues initialValues) {		
		if (uriMatcher.match(uri) != ENTRIES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values = setInitialValues(initialValues);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = db.insert(getTableName(), null, values);
		if (rowId > 0) {
			Uri rowUri = ContentUris.withAppendedId(this.getContentUri(), rowId);
			return rowUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	public int delete(Uri uri, String where, String[] whereArgs) {
		String finalWhere;

		int count;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case ENTRIES:
			count = db.delete(getTableName(), where, whereArgs);
			break;

		case ENTRY_ID:
			finalWhere = Table._ID + " = " + uri.getPathSegments().get(Table.ENTRY_ID_PATH_POSITION);
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			count = db.delete(getTableName(), finalWhere, whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return count;
	}

	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		String finalWhere;

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case ENTRIES:
			count = db.update(getTableName(), values, where, whereArgs);
			break;
		case ENTRY_ID:
			String entryID = uri.getPathSegments().get(Table.ENTRY_ID_PATH_POSITION);
			finalWhere = Table._ID + " = " + entryID;
			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}
			count = db.update(getTableName(), values, finalWhere, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return count;
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(getTableName());

		switch (this.uriMatcher.match(uri)) {
		case ENTRIES:
			qb.setProjectionMap(this.projectionMap);
			break;
		case ENTRY_ID:
			qb.setProjectionMap(this.projectionMap);
			qb.appendWhere(Table._ID + "=" + uri.getPathSegments().get(Table.ENTRY_ID_PATH_POSITION));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = null;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		return c;
	}

	public boolean exists(SQLiteDatabase db) {
		boolean exist = false;
		String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' and name=?";

		Cursor cursor = null;
		
		synchronized(this) {
			cursor = db.rawQuery(sql, new String[] { getTableName() });
		}
		
		if (cursor != null && cursor.getCount() >= 1) {
			cursor.moveToFirst();
			int tableNum = cursor.getInt(0);
			if (tableNum == 1) {
				exist = true;
			}
		}
		try {
			cursor.close();
		} catch (Exception e) {
		}
		return exist;
	}

//	protected String attrArrayToCreateTableSQL1() {
//
//		String sql = "CREATE TABLE " + getTableName() + " (";
//		HashMap<String, String> attributes = getAttributes();
//		for (String attrName: attributes.keySet()) {
//			String attrType = attributes.get(attrName);
//			sql += attrName + " " + attrType + ", ";
//		}
//		if (sql.length() >= 2) {
//			sql = sql.substring(0, sql.length()-2);
//		}
//		sql += ");";
//		return sql;
//	}

	public void create(SQLiteDatabase db) {
		if (!this.exists(db)) {
			synchronized(this) {
				db.execSQL(this.createTableSQL);
			}
		}
	}

	public void drop(SQLiteDatabase db) {
		synchronized(this) {
			db.execSQL("DROP TABLE IF EXISTS " + getTableName());
		}
	}

	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ENTRIES:
			return getContentType();
		case ENTRY_ID:
			return getContentItemType();
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	public String getTableName() {
		String tableName = this.getClass().getSimpleName().toLowerCase();
		return tableName;
	}

	public ContentValues setInitialValues(ContentValues values) {
		if (values == null) {
			values = new ContentValues();
		}
		return values;
	}

	public String getContentType() {
		return CONTENT_TYPE_BASE + getTableName();
	}

	public String getContentItemType() {
		return CONTENT_ITEM_TYPE_BASE + getTableName();
	}

	public Uri getContentUri() {
		return Uri.parse(SCHEME + MultiTableProvider.AUTHORITY + "/" + getTableName() + "/");
	}

	public Uri getContentIDUriPattern() {
		return Uri.parse(SCHEME + MultiTableProvider.AUTHORITY + "/" + getTableName() + "/#");
	}

	public void setDBHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}
}
