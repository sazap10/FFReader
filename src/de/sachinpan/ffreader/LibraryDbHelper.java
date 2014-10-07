package de.sachinpan.ffreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LibraryDbHelper extends SQLiteOpenHelper {
	private static LibraryDbHelper sInstance;
	private static final String TABLE_NAME = "library";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_TITLE = "title";
	private static final String COLUMN_AUTHOR = "author";
	private static final String COLUMN_DESCRIPTION = "description";
	private static final String COLUMN_CHAPTERNO = "chapters";
	private static final String COLUMN_LAST_UPDATED = "lastupdated";
	private static final String COLUMN_CATEGORY = "category";
	private static final String COLUMN_SUB_CATEGORY = "subcategory";
	private static final String COLUMN_CROSSOVER = "iscrossover";
	private static final String COLUMN_GENRE_A = "genre1";
	private static final String COLUMN_GENRE_B = "genre2";
	private static final String COLUMN_ERROR = "error";
	private static final String COLUMN_PATH = "path";

	private static final String DATABASE_NAME = "library.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_NAME
			+ "(" + COLUMN_ID + "integer primary key, " + COLUMN_TITLE
			+ "text null, " + COLUMN_AUTHOR + "text null, "
			+ COLUMN_DESCRIPTION + "text null, " + COLUMN_CHAPTERNO
			+ "integer not null, " + COLUMN_LAST_UPDATED + "text null, "
			+ COLUMN_CATEGORY + "text null, " + COLUMN_SUB_CATEGORY
			+ "text null, " + COLUMN_CROSSOVER + "integer null, "
			+ COLUMN_GENRE_A + "text null, " + COLUMN_GENRE_B + "text null, "
			+ COLUMN_ERROR + "text null, " + COLUMN_PATH + "text not null"
			+ ");";

	public static LibraryDbHelper getInstance(Context context) {

		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		// See this article for more information: http://bit.ly/6LRzfx
		if (sInstance == null) {
			sInstance = new LibraryDbHelper(context.getApplicationContext());
		}
		return sInstance;
	}

	/**
	 * Constructor should be private to prevent direct instantiation. make call
	 * to static factory method "getInstance()" instead.
	 */
	private LibraryDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(LibraryDbHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public void open() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.close();
	}
}
