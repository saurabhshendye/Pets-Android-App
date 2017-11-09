package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.pets.data.PetsContract.PetEntry.*;


/**
 * Created by Saurabh on 11/8/2017.
 */

public class PetsDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_QUERY = "CREATE TABLE " + PetsContract.PetEntry.TABLE_NAME +
            "( " + PetsContract.PetEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PetsContract.PetEntry.COLUMN_NAME + " TEXT NOT NULL," +
            PetsContract.PetEntry.COLUMN_BREED + " TEXT, " +
            PetsContract.PetEntry.COLUMN_GENDER + " INTEGER NOT NULL, " +
            PetsContract.PetEntry.COLUMN_WEIGHT + " INTEGER NOT NULL);";

    public PetsDBHelper(Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
