package com.afonseca.skrik;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Msgs_Helper extends SQLiteOpenHelper {

    private static DB_Msgs_Helper instance;

    public static final String DATABASE_NAME = "MSGS_DB";
    public static final String DATABASE_TABLE = "MSGS";
    public static final int DATABASE_VERSION = 3;

    public static final String COLUMN1 = "id";
    public static final String COLUMN2 = "userid_from";
    public static final String COLUMN3 = "userid_to";
    public static final String COLUMN4 = "message";
    public static final String COLUMN5 = "status";
    public static final String COLUMN6 = "timestamp";
    public static final String COLUMN7 = "backend_id";
    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + DATABASE_TABLE + " ("
            + COLUMN1 + " integer primary key autoincrement, "
            + COLUMN2 + " text not null, "
            + COLUMN3 + " text not null, "
            + COLUMN4 + " text not null, "
            + COLUMN5 + " text not null, "
            + COLUMN6 + " text not null, "
            + COLUMN7 + " text not null "
            + ");";


    public DB_Msgs_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub

    }

    public static synchronized DB_Msgs_Helper getInstance(Context context) {
        if (instance == null)
            instance = new DB_Msgs_Helper(context);

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

