package com.example.android.pets.Data;

public final class Constants {

    public static int DATABASE_VERSION =1;
    public static String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetsContract.PetsEntry.TABLE_NAME + " ("
            + PetsContract.PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PetsContract.PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT, "
            + PetsContract.PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
            + PetsContract.PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";



    public static String SQL_DELETE_PETS_TABLE ="DELETE TABLE "+ PetsContract.PetsEntry.TABLE_NAME;
}
