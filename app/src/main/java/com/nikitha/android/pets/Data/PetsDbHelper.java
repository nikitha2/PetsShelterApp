package com.nikitha.android.pets.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.nikitha.android.pets.Data.Constants.*;
import static com.nikitha.android.pets.Data.PetsContract.PetsEntry.*;

public class PetsDbHelper extends SQLiteOpenHelper {


    public PetsDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DATABASE_VERSION =newVersion;
        db.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteAllEntries(SQLiteDatabase db){
        db.execSQL(SQL_DELETE_ALL_FROM_TABLE);
    }
}
