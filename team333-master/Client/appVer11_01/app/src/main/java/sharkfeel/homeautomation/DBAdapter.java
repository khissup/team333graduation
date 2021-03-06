package sharkfeel.homeautomation;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    public static final String KEY_ROWID = "_id";       //row
    public static final String KEY_SAY = "say";     //say
    public static final String KEY_SAY_REPLACE = "say_replace";     //say
    public static final String KEY_SERVER = "server";     //say
    public static final String KEY_URL = "url";     //say
    public static final String KEY_MEANS = "means";     //PUT, GET
    public static final String KEY_MESSAGE = "message";     //LED ON


    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "voicecontrol";
    private static final int DATABASE_VERSION = 1;

    //λλΉ μμ± Create
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + "("
                    + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_SAY + " text not null, "
                    + KEY_SAY_REPLACE + " text not null, "
                    + KEY_SERVER +  " text not null, "
                    + KEY_URL +  " text not null, "
                    + KEY_MEANS + " text not null, "
                    + KEY_MESSAGE + " text not null" + ");";



    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "μκ·Έλ μ΄λ " + oldVersion + " to "
                    + newVersion + ", μ­μ νκ³  μλ‘­κ²");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }


    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long createNote(String say, String server, String url, String means, String message) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SAY, say);
        initialValues.put(KEY_SAY_REPLACE, say.replaceAll(" ", ""));
        initialValues.put(KEY_SERVER, server);
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_MEANS, means);
        initialValues.put(KEY_MESSAGE, message);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }


    //λΈνΈ μ­μ 
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //λͺ¨λ  λΈνΈ μ­μ  //μ­μ μ μ μμ¬ν­
    //λ°λ‘ μ­μ λμ€λ AlterDIalogλ₯Ό μ¬μ©ν΄μ μ­μ ν κ±΄μ§
    //λ¬Όμ΄λ³΄λκ² μμ.
    public boolean deleteAllNote() {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID, null) > 0;
    }

    //λͺ¨λ  λΈνΈ λ³΄μ¬μ£ΌκΈ°
    public Cursor fetchAllNotes(String key, String str) {
        //μ²«λ²μ§Έ μΈμκ°μ μ΄λ€κ²μ μ λ ¬ν κ²μΈκ°
        //λλ²μ§Έ μΈμκ°μ λ΄λ¦Όμ°¨μμΈκ° μ€λ¦μ°¨μμΈκ°
        //λ΄λ¦Όμ°¨μμΈλ° KEY_TIMEκ°μ΄ μ μΌ μ΅κ·Όμ΄ μλ‘κ°
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_SAY, KEY_SERVER,
                        KEY_URL, KEY_MEANS, KEY_MESSAGE}, null, null, null,
                null, key + " " + str, null);    //ASC
        // Order by (λ΄λ¦Όμ°¨μ μ λ ¬κΈ°λ₯)
    }


    //ν λΈνΈλ§ λ³΄μ¬μ£ΌκΈ°
    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                                KEY_SAY, KEY_SERVER, KEY_URL, KEY_MEANS, KEY_MESSAGE}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //λΈνΈ μλ°μ΄νΈ
    public boolean updateNote(long rowId, String say, String url) {
        ContentValues args = new ContentValues();
        args.put(KEY_SAY, say);
        args.put(KEY_URL, url);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


    //λΈνΈκ²μ
    public Cursor searchNote (String strKeyTitle) {
        //λ©λͺ¨ λ΄μ©κΉμ§ κ²μν©λλ€.
        Cursor mCursor = mDb.query(DATABASE_TABLE,
                new String[] {KEY_ROWID,
                        KEY_SAY, KEY_SAY_REPLACE, KEY_SERVER, KEY_URL, KEY_MEANS, KEY_MESSAGE},
                KEY_SAY  + " like ? " + " or " + KEY_SAY_REPLACE + " like? ",
                new String[]{"%" + strKeyTitle + "%", "%" + strKeyTitle + "%"}, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}
