package com.studiotyche.apps.android.jobrunner.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.IntDef;
import android.util.Log;

import com.studiotyche.apps.android.jobrunner.models.Alert;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;


/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */

public final class DatabaseHelper extends SQLiteOpenHelper {

    @IntDef({RECENT, SAVED, INACTIVE, REMOVED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    public static final int RECENT = 0;
    public static final int SAVED = 1;
    public static final int INACTIVE = 2;
    public static final int REMOVED = 3;

    private Context context;
    private static final String TAG = "DatabaseHelper";

    private static final String DB_NAME = "JOBRUNNER";
    private static final String DB_SUFFIX = ".DB";
    private static final int DB_VERSION = 1;

    private static DatabaseHelper mInstance;
    private OnDatabaseChangeListener databaseChangeListener;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME + DB_SUFFIX, null, DB_VERSION);
        this.context = context;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new DatabaseHelper(context.getApplicationContext());
            Log.i(TAG, "Created a new Instance");
        }
        Log.i(TAG, "Returning current Instance");
        return mInstance;
    }

    public void setDatabaseChangeListener(OnDatabaseChangeListener databaseChangeListener) {
        this.databaseChangeListener = databaseChangeListener;
    }

    public void addNewAlertToDB(Alert alert) {
        String title, description, link, timestamp;
        title = alert.getTitle();
        description = alert.getDesc();
        link = alert.getLink();
        timestamp = alert.getTimeStamp();

        if (!checkIfRecordExists(AlertFeedTable.NAME, AlertFeedTable.COLUMN_LINK, link)) {

            SQLiteDatabase sqliteDatabase = getWritableDatabase(context);
            sqliteDatabase.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put(AlertFeedTable.COLUMN_TITLE, title);
            cv.put(AlertFeedTable.COLUMN_DESCRIPTION, description);
            cv.put(AlertFeedTable.COLUMN_LINK, link);
            cv.put(AlertFeedTable.COLUMN_TIMESTAMP, timestamp);
            cv.put(AlertFeedTable.COLUMN_STATE, DatabaseHelper.RECENT);

            sqliteDatabase.insert(AlertFeedTable.NAME, null, cv);
            sqliteDatabase.setTransactionSuccessful();
            sqliteDatabase.endTransaction();
            sqliteDatabase.close();

//        databaseChangeListener.onChange();

            Log.d(TAG, "Added new alert " + title);

        }
    }

    public ArrayList<Alert> getAllAlerts(@State int state, int limit) {
        String title, description, link, timestamp;
        Long id;

        ArrayList<Alert> allAlerts = new ArrayList<>();

        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + AlertFeedTable.NAME + " WHERE STATE = " + state + " ORDER BY "
                + AlertFeedTable.COLUMN_TIMESTAMP + " DESC LIMIT " + limit, null);

        Log.d(TAG, " getALlAlerts() " + "Cursor size " + cursor.getCount());
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                id = cursor.getLong(cursor.getColumnIndex(AlertFeedTable.COLUMN_ID));
                title = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_TITLE));
                description = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_DESCRIPTION));
                link = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_LINK));
                timestamp = cursor.getString(cursor.getColumnIndex(AlertFeedTable.COLUMN_TIMESTAMP));

                allAlerts.add(new Alert(id, title, description, link, timestamp));
                Log.d(TAG, " getALlAlerts() " + "added new alert to allalerts");
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqliteDatabase.close();
        Log.d(TAG, " getALlAlerts() " + "Returning allalerts " + allAlerts.size());
        return allAlerts;
    }

    public void removeAlert(Alert alert) {
        updateAlertState(alert, DatabaseHelper.INACTIVE);
    }

    public void saveAlert(Alert alert) {
        updateAlertState(alert, DatabaseHelper.SAVED);
    }

    public void updateAlertState(Alert alert, @State int state) {
        String query = AlertFeedTable.COLUMN_ID + " = " + alert.id;
        SQLiteDatabase sqliteDatabase = getWritableDatabase(context);
        ContentValues cv = new ContentValues();

        cv.put(AlertFeedTable.COLUMN_STATE, state);

        sqliteDatabase.beginTransaction();

        int i = sqliteDatabase.update(AlertFeedTable.NAME, cv, query, null);
        Log.d(TAG, "Number of Rows saved " + i);

        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();

//        databaseChangeListener.onChange();
    }

    public boolean checkIfTableExists(String tableName) {
        boolean tableExists = false;
        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("Select * from " + tableName, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                tableExists = true;
            }
        }
        cursor.close();
        sqliteDatabase.close();
        return tableExists;
    }

    public boolean checkIfRecordExists(String tableName, String columnName, String recordValue) {
        Cursor cursor = getReadableDatabase(context)
                .rawQuery("Select " + columnName + " From " + tableName +
                        " Where " + columnName + " = '" + recordValue + "' ", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AlertFeedTable.CREATE);
        Log.i(TAG, " Table Created " + AlertFeedTable.NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(AlertFeedTable.DROP_TABLE);
        onCreate(db);
    }

    private static SQLiteDatabase getReadableDatabase(Context context) {
        return getInstance(context).getReadableDatabase();
    }

    private static SQLiteDatabase getWritableDatabase(Context context) {
        return getInstance(context).getWritableDatabase();
    }
}
