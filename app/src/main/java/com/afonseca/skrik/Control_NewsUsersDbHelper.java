package com.afonseca.skrik;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by afonseca on 2/1/2015.
 */

public class Control_NewsUsersDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_TABLE = "NEWSUSERS";

    public static final String COLUMN1 = "id";
    public static final String COLUMN2 = "username";
    public static final String COLUMN3 = "blacklisted";
    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + DATABASE_TABLE + " ("
            + COLUMN1 + " integer not null, "
            + COLUMN2 + " text not null, "
            + COLUMN3 + " integer not null "
            + ");";

    public Control_NewsUsersDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                                int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SCRIPT_CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

}


