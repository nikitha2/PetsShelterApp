package com.example.android.pets.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static com.example.android.pets.Data.Constants.*;
import static com.example.android.pets.Data.PetsContract.PetsEntry.*;

/**
 * ContentProvider for App: Pets
 */
public class PetProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetsDbHelper petsDbHelper;
    Cursor cursor;
    SQLiteDatabase db;
    int match;
    Toast toast;
    public static final UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY,TABLE_NAME,PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY,TABLE_NAME+"/#",PETS_ID);
    }
    @Override
    public boolean onCreate() {
        petsDbHelper=new PetsDbHelper(getContext());
        db = petsDbHelper.getWritableDatabase();
        return false;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        match=sUriMatcher.match(uri);
        switch(match) {
            case PETS: cursor = db.query(TABLE_NAME, projection,  selection, selectionArgs, null,null, sortOrder);break;
            case PETS_ID:selection=PetsContract.PetsEntry._ID+"=?";
                         selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                         cursor = db.query(TABLE_NAME, projection,  selection, selectionArgs, null,null, sortOrder); break;
            default:cursor=null;break;
        }
        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        match=sUriMatcher.match(uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return CONTENT_LIST_TYPE;
            case PETS_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        match=sUriMatcher.match(uri);
        Uri newUri = null;
        long newId;
        values= wtValidationCheck(values);
        switch(match) {
            case PETS:
                      if(validationChecksPass(values)) {
                          newId=db.insert(TABLE_NAME, null, values);
                          newUri = ContentUris.withAppendedId(uri, newId);
                      }
                       break;
            default:
                String e= R.string.insetNotPossible+uri.toString();
                Log.e(LOG_TAG,e);
        }
        return newUri;
    }




    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        match=sUriMatcher.match(uri);

        switch(match) {
            case PETS:db.delete(TABLE_NAME,selection,selectionArgs);break;
            case PETS_ID:selection=PetsContract.PetsEntry._ID+"=?";
                         selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                         db.delete(TABLE_NAME,selection,selectionArgs);break;
            default:
        }
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        match=sUriMatcher.match(uri);
        int noOfRowsUpdated=-1;
        if(valueChanges(values)) {
            values = wtValidationCheck(values);
        }
        switch(match) {
            case PETS:if(validationChecksPass(values)) {
                        noOfRowsUpdated=db.update(TABLE_NAME,values,selection,selectionArgs);
                        break;
                       }
            case PETS_ID:if(validationChecksPass(values)) {
                            selection=PetsContract.PetsEntry._ID+"=?";
                            selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                            noOfRowsUpdated=db.update(TABLE_NAME,values,selection,selectionArgs);break;
                          }
            default:cursor=null;
                    String e= R.string.insetNotPossible+uri.toString();
                    throw new IllegalArgumentException(e);
        }
        return noOfRowsUpdated;
    }

    private boolean validationChecksPass(ContentValues values) {
        if(values.containsKey(COLUMN_PET_NAME)) {
            String name = values.getAsString(COLUMN_PET_NAME);
            if (name == null || name.isEmpty()) {
                toast = Toast.makeText(getContext(), R.string.nameRequired, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
        if(values.containsKey(COLUMN_PET_BREED)) {
            String breed = values.getAsString(COLUMN_PET_BREED);
            if (breed == null || breed.isEmpty()) {
                toast = Toast.makeText(getContext(), R.string.breedRequired, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }
        if(values.containsKey(COLUMN_PET_WEIGHT)) {
            Integer wt = values.getAsInteger(COLUMN_PET_WEIGHT);
                if (wt == null || !isValidWeight(wt)) {
//                    toast = Toast.makeText(getContext(), R.string.wtRequired, Toast.LENGTH_SHORT);
//                    toast.show();
//                    return false;
                }
        }
        if(values.containsKey(COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(COLUMN_PET_GENDER);
            if (gender == null || !isValidGender(gender)) {
                return false;
            }
        }
        return true;
    }
    private ContentValues wtValidationCheck(ContentValues values) {

        if(values.containsKey(COLUMN_PET_WEIGHT)) {
            Integer wt = values.getAsInteger(COLUMN_PET_WEIGHT);
            if (wt == null || !isValidWeight(wt)) {
                wt=0;
                values.put(COLUMN_PET_WEIGHT,wt);            }
        }
        return values;
    }

    private boolean valueChanges(ContentValues values) {
        if (values.size() == 0) {
            return false;
        }
        return true;
    }
}
