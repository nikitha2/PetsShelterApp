package com.example.android.pets.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;

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
        return null;
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
        switch(match) {
            case PETS: newId=db.insert(TABLE_NAME, null, values);
                       newUri= ContentUris.withAppendedId(uri,newId);
                       break;
            default:
                String e= R.string.insetNotPossible+uri.toString();
                throw new IllegalArgumentException(e);
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
        int updateStatus=-1;
        switch(match) {
            case PETS: updateStatus=db.update(TABLE_NAME,values,selection,selectionArgs);break;
            case PETS_ID:selection=PetsContract.PetsEntry._ID+"=?";
                         selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };
                         updateStatus=db.update(TABLE_NAME,values,selection,selectionArgs);break;
            default:cursor=null;break;
        }
        return updateStatus;
    }

}
