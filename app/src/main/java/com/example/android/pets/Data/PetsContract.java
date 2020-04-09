package com.example.android.pets.Data;

import android.provider.BaseColumns;

public final class PetsContract {

    public PetsContract() {
    }

    public static final class PetsEntry implements BaseColumns {

        public final static String DATABASE_NAME ="PetsData.db";
        public final static String TABLE_NAME ="Pets";
        //public final static String COLUMN_PET_ID="id";
        public final static String COLUMN_PET_NAME="Name";
        public final static String COLUMN_PET_BREED="Breed";
        public final static String COLUMN_PET_WEIGHT="Weight";
        public final static String COLUMN_PET_GENDER="Gender";

        public final static int gender_Pet_Unknown=0;
        public final static int gender_Pet_Male=1;
        public final static int gender_Pet_Female=2;

    }
}
