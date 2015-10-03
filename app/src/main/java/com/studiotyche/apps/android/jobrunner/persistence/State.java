package com.studiotyche.apps.android.jobrunner.persistence;

/**
 * Created by AnudeepSamaiya on 03-10-2015.
 */
public interface State {
    /*

    public void addSavedFeedRecord(Alert alert) {

        String title, description, link, timestamp;
        title = alert.getTitle();
        description = alert.getDesc();
        link = alert.getLink();
        timestamp = alert.getTimeStamp();

        Log.i("Joberio","DBHelper: "+title+" "+description+" "+link+" "+timestamp);

        SQLiteDatabase sqliteDatabase = getWritableDatabase(context);
        sqliteDatabase.beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(SavedFeedTable.COLUMN_TITLE, title);
        cv.put(SavedFeedTable.COLUMN_DESCRIPTION, description);
        cv.put(SavedFeedTable.COLUMN_LINK, link);
        cv.put(SavedFeedTable.COLUMN_TIMESTAMP, timestamp);

        sqliteDatabase.insert(SavedFeedTable.NAME, null, cv);
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();

        Log.d(TAG, "Saved new alert " + title);
    }
*/
/*

    public ArrayList<Alert> getAllSavedAlerts() {
        String id, title, description, link, timestamp;
        ArrayList<Alert> allAlerts = new ArrayList<>();

        SQLiteDatabase sqliteDatabase = getReadableDatabase(context);
        Cursor cursor = sqliteDatabase.rawQuery("SELECT * FROM " + SavedFeedTable.NAME + " ORDER BY "
                + SavedFeedTable.COLUMN_TIMESTAMP + " DESC", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                id = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_ID));
                title = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_TITLE));
                description = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_DESCRIPTION));
                link = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_LINK));
                timestamp = cursor.getString(cursor.getColumnIndex(SavedFeedTable.COLUMN_TIMESTAMP));

                allAlerts.add(new Alert(id, title, description, link, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        sqliteDatabase.close();
        return allAlerts;
    }
*/

    /*

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

*/


}
