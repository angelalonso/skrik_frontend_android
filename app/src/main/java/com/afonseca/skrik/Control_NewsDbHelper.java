package com.afonseca.skrik;

/**
 * Created by aaf on 1/24/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Control_NewsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_TABLE = "NEWS";

    public static final String COLUMN1 = "id";
    public static final String COLUMN2 = "userid_from";
    public static final String COLUMN3 = "userid_to";
    public static final String COLUMN4 = "message";
    public static final String COLUMN5 = "status";
    public static final String COLUMN6 = "timestamp";
    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + DATABASE_TABLE + " ("
            + COLUMN1 + " integer primary key autoincrement, "
            + COLUMN2 + " text not null, "
            + COLUMN3 + " text not null, "
            + COLUMN4 + " text not null, "
            + COLUMN5 + " text not null, "
            + COLUMN6 + " text not null, "
            + ");";

    public Control_NewsDbHelper(Context context, String name, CursorFactory factory,
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

