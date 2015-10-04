package com.studiotyche.apps.android.jobrunner.persistence;

import android.provider.BaseColumns;

/**
 * Created by AnudeepSamaiya on 01-10-2015.
 */
public interface AlertFeedTable extends BaseColumns {
    String NAME = "alertFeed";

    String COLUMN_ID = _ID;
    String COLUMN_TITLE = "Title";
    String COLUMN_DESCRIPTION = "Description";
    String COLUMN_LINK = "Link";
    String COLUMN_TIMESTAMP = "timestamp";
    String COLUMN_STATE = "state";

    String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_TITLE,
            COLUMN_DESCRIPTION, COLUMN_LINK, COLUMN_TIMESTAMP};

    String CREATE = "CREATE TABLE " + NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_LINK + " TEXT NOT NULL, " + COLUMN_TIMESTAMP + " TEXT NOT NULL, "
            + COLUMN_STATE + " INTEGER NOT NULL " + ");";

    String DROP_TABLE = "DROP TABLE IF EXISTS " + NAME;
}
