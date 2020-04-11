package com.example.android.pets.Data;

import android.net.Uri;

public final class Constants {

    public static int petCounter=1;
    public static int DATABASE_VERSION =1;
    public static String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetsContract.PetsEntry.TABLE_NAME + " ("
            + PetsContract.PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetsContract.PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT, "
            + PetsContract.PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    public static String SQL_SELECT_PETS_TABLE ="SELECT _ID FROM TABLE "+ PetsContract.PetsEntry.TABLE_NAME+ " WHERE ";


    public static String SQL_DELETE_PETS_TABLE ="DELETE TABLE "+ PetsContract.PetsEntry.TABLE_NAME;

    public static String SQL_DELETE_ALL_FROM_TABLE ="DELETE * FROM TABLE "+ PetsContract.PetsEntry.TABLE_NAME;


}
