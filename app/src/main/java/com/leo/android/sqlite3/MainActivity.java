package com.leo.android.sqlite3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "myLog";

    private int[] mPositionId = {1, 2, 3, 4};
    private String[] mPositionName = {"Директор", "Программист", "Бухгалтер", "Охранник"};
    private int[] mPositionSalary = {800000, 60000, 40000, 20000};

    private String[] mPeopleName = {"Максим", "Руслан", "Сергей", "Наталья", "Иван", "Мария", "Светлана", "Денис", };
    private int[] mPeoplePositionId = {2, 3, 2, 2, 3, 1, 2, 4};

    private DBHelper mDBHelper;
    private SQLiteDatabase mDatabase;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDBHelper = new DBHelper(this);
        mDatabase  = mDBHelper.getWritableDatabase();

        Log.d(LOG_TAG, "---Table position---");
        mCursor = mDatabase.query("position", null, null, null, null, null, null);
        logcursor(mCursor);
        mCursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "---Table People---");
        mCursor = mDatabase.query("people", null, null, null, null, null, null);
        logcursor(mCursor);
        mCursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "---INNER JOIN with rawQuery---");
        String sqlQuery = "select PL.name as Name, PS.name as Position, salary as Salary "
                + "from people as PL "
                +"inner join position as PS "
                +"on PL.posid = PS.id "
                +"where salary > ?";
        mCursor = mDatabase.rawQuery(sqlQuery, new String[] {"40000"});
        logcursor(mCursor);
        mCursor.close();
        Log.d(LOG_TAG, "--- ---");

        Log.d(LOG_TAG, "---INNER JOIN with query");
        String table = "people as PL inner join position as PS on PL.posid = PS.id";
        String[] columns = {"PL.name as Name", "PS.name as Position", "salary as Salary"};
        String selection = "salary < ?";
        String[] selectionArgs = {"40000"};
        mCursor = mDatabase.query(table, columns, selection, selectionArgs, null, null, null);
        logcursor(mCursor);
        mCursor.close();
        Log.d(LOG_TAG, "--- ---");

        mDatabase.close();

    }

    void logcursor(Cursor cursor){
        if (cursor != null){
            if (cursor.moveToFirst()){
                String str;
                do {
                    str = "";
                    for (String cn: cursor.getColumnNames()){
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(LOG_TAG, str);
                } while (cursor.moveToNext());
            }
        } else Log.d(LOG_TAG, "Cursor is null");

    }

    class DBHelper extends SQLiteOpenHelper {


        public DBHelper(Context context) {
            super(context, "myDb", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d(LOG_TAG, "---onCreate database---");

            ContentValues contentValues = new ContentValues();

            // создаем таблицу должностей
            sqLiteDatabase.execSQL("create table position (" + "id integer primary key, " + "name text, "
            + "salary integer" + ");");

            //заполняем таблицу должностей
            for (int i = 0; i < mPositionId.length; i++){
                contentValues.clear();
                contentValues.put("id", mPositionId[i]);
                contentValues.put("name", mPositionName[i]);
                contentValues.put("salary", mPositionSalary[i]);
                sqLiteDatabase.insert("position", null, contentValues);
            }

            sqLiteDatabase.execSQL("create table people (" + "id integer primary key autoincrement, "
            + "name text, " + "posid integer" + ");");

            for (int i = 0; i < mPeopleName.length; i++){
                contentValues.clear();
                contentValues.put("name", mPeopleName[i]);
                contentValues.put("posid", mPeoplePositionId[i]);
                sqLiteDatabase.insert("people", null, contentValues);
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
