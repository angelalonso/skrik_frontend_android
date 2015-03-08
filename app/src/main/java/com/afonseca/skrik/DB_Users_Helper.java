package com.afonseca.skrik;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DB_Users_Helper extends SQLiteOpenHelper {

    private static DB_Users_Helper instance;

    public static final String DATABASE_NAME = "DB_USERS";
    public static final String DATABASE_TABLE = "USERS";
    public static final int DATABASE_VERSION = 5;

    public static final String COLUMN1 = "id";
    public static final String COLUMN2 = "username";
    public static final String COLUMN3 = "blacklisted";
    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + DATABASE_TABLE + " ("
            + COLUMN1 + " integer not null, "
            + COLUMN2 + " text not null, "
            + COLUMN3 + " integer not null "
            + ");";

    public DB_Users_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub

    }

    public static synchronized DB_Users_Helper getInstance(Context context) {
        if (instance == null)
            instance = new DB_Users_Helper(context);

        return instance;
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


