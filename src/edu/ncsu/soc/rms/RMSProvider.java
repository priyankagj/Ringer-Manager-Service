package edu.ncsu.soc.rms;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class RMSProvider extends ContentProvider {

  public static final Uri CONTENT_URI = Uri.parse("content://edu.ncsu.soc.rms/RMS");

  //Create the constants used to differentiate between the different URI requests.
  private static final int RMS = 1;
  private static final int RMS_ID = 2;

  private static final UriMatcher uriMatcher;
  // Allocate the UriMatcher object, where a URI ending in 'RMS' will
  // correspond to a request for all rows, and 'RMS' with a trailing
  // '/[rowID]' will represent a single row.
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI("edu.ncsu.soc.rms", "RMS", RMS);
    uriMatcher.addURI("edu.ncsu.soc.rms", "RMS/#", RMS_ID);
  }

  @Override
  public boolean onCreate() {
    Context context = getContext();

    RMSDatabaseHelper dbHelper;
    dbHelper = new RMSDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    rmsDB = dbHelper.getWritableDatabase();
    return (rmsDB == null) ? false : true;
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
    case RMS: return "vnd.android.cursor.dir/vnd.ncsu.RMS";
    case RMS_ID: return "vnd.android.cursor.item/vnd.ncsu.RMS";
    default: throw new IllegalArgumentException("Unsupported URI: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sort) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(RMS_TABLE);

    // If this is a row query, limit the result set to the passed in row.
    switch (uriMatcher.match(uri)) {
    case RMS_ID:
      qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
      break;
    default:
      break;
    }

    // If no sort order is specified sort by date / time
    String orderBy;
    if (TextUtils.isEmpty(sort)) {
      orderBy = KEY_RINGER_MODE;
    } else {
      orderBy = sort;
    }

    // Apply the query to the underlying database.
    Cursor c = qb.query(rmsDB, projection, selection, selectionArgs, null, null, orderBy);

    // Register the contexts ContentResolver to be notified if
    // the cursor result set changes.
    c.setNotificationUri(getContext().getContentResolver(), uri);

    // Return a cursor to the query result.
    return c;
  }

  @Override
  public Uri insert(Uri _uri, ContentValues _initialValues) {
    // Insert the new row, will return the row number if successful.
    long rowID = rmsDB.insert(RMS_TABLE, "RMS", _initialValues);

    // Return a URI to the newly inserted row on success.
    if (rowID > 0) {
      Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
      getContext().getContentResolver().notifyChange(uri, null);
      return uri;
    }
    throw new SQLException("Failed to insert row into " + _uri);
  }

  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    int count;

    switch (uriMatcher.match(uri)) {
    case RMS:
      count = rmsDB.delete(RMS_TABLE, where, whereArgs);
      break;

    case RMS_ID:
      String segment = uri.getPathSegments().get(1);
      count = rmsDB.delete(RMS_TABLE, KEY_ID + "="
          + segment
          + (!TextUtils.isEmpty(where) ? " AND ("
              + where + ')' : ""), whereArgs);
      break;

    default: throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    int count;
    switch (uriMatcher.match(uri)) {
    case RMS: count = rmsDB.update(RMS_TABLE, values, where, whereArgs);
    break;

    case RMS_ID: String segment = uri.getPathSegments().get(1);
    count = rmsDB.update(RMS_TABLE, values, KEY_ID
        + "=" + segment
        + (!TextUtils.isEmpty(where) ? " AND ("
            + where + ')' : ""), whereArgs);
    break;

    default: throw new IllegalArgumentException("Unknown URI " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  /** The underlying database */
  private SQLiteDatabase rmsDB;

  private static final String TAG = "RMSProvider";
  private static final String DATABASE_NAME = "RMS.db";
  private static final int DATABASE_VERSION = 1;
  private static final String RMS_TABLE = "RMS";

  // Column Names
  public static final String KEY_ID = "_id";
  public static final String KEY_RINGER_MODE = "ringerMode";
  public static final String KEY_PLACE_LAT = "latitude";
  public static final String KEY_PLACE_LNG = "longitude";
  public static final String KEY_MODE_NAME = "modeName";

  // Column indexes
  public static final int RINGER_MODE_COLUMN = 1;
  public static final int LATITUDE_COLUMN = 2;
  public static final int LONGITUDE_COLUMN = 3;
  public static final int MODE_NAME_COLUMN = 4;

  // Helper class for opening, creating, and managing database version control
  private static class RMSDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_CREATE =
      "create table " + RMS_TABLE + " ("
      + KEY_ID + " integer primary key autoincrement, "
      + KEY_RINGER_MODE + " INTEGER, "
      + KEY_PLACE_LAT + " INTEGER, "
      + KEY_PLACE_LNG + " INTEGER, " 
      + KEY_MODE_NAME + " TEXT);";

    /** Helper class for managing the Earthquake database */
    public RMSDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
          + newVersion + ", which will destroy all old data");

      db.execSQL("DROP TABLE IF EXISTS " + RMS_TABLE);
      onCreate(db);
    }
  }
}