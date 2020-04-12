package com.example.android.pets.Data;

import android.net.Uri;
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


        public static final String CONTENT_AUTHORITY = "com.example.android.pets.Data";
        /**
         * To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
         */
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        /**
         * PATH_TableName
         * This constants stores the path for each of the tables which will be appended to the base content URI.
         */
        public static final String PATH_PETS = "pets";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
    }
}
