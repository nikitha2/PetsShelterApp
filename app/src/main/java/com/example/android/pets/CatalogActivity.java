/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.pets.Data.Constants.*;
import static com.example.android.pets.Data.PetsContract.PetsEntry.*;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    Cursor cursor;
    Uri uri=Uri.parse(URI);
    PetsCursorAdapter adapter;
    /** Tag for the log messages */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        //setting displayView.onItemClickListener will enable us to set onclickListener on each item of the list. it is specific method
        //for listviews so its easier.
        ListView displayView = (ListView) findViewById(R.id.list_view_pet);
        adapter = new PetsCursorAdapter(this, null, 0);
        displayView.setAdapter(adapter);
        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);

        Loader<Cursor> cursorLoader = getSupportLoaderManager().initLoader(1, null, this);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                long newRowId = insertDummyData();
                Log.v("Catalog Activity","newRowId = " +newRowId);
                Toast toast ;
                if(newRowId!=-1) {
//                    cursor=readDatabaseInfo();
//                    displayDatabaseInfo(cursor);
                    toast = Toast.makeText(this, " Pet added with new row id :" + newRowId, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    toast=Toast.makeText(this," Error when adding pet ", Toast.LENGTH_SHORT);
                    toast. show();
                }
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllRowsInDatabase();
//                cursor=readDatabaseInfo();
//                displayDatabaseInfo(cursor);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllRowsInDatabase() {
        //returns the number of rows deleted
        getContentResolver().delete(uri,null,null);
        petCounter=1;
    }

    private long insertDummyData() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PET_NAME, "Pet "+petCounter);
        values.put(COLUMN_PET_BREED, "golden retriver "+petCounter);
        values.put(COLUMN_PET_GENDER, 2);
        values.put(COLUMN_PET_WEIGHT, 20);
        petCounter++;
        Uri result = getContentResolver().insert(uri, values);
        // Insert the new row, returning the primary key value of the new row
        return ContentUris.parseId(result);
    }

    /**
     * Method to create the database
     */
    private Cursor readDatabaseInfo() {
        Uri uri1;
        String[] columns={_ID,COLUMN_PET_NAME,COLUMN_PET_BREED,COLUMN_PET_WEIGHT,COLUMN_PET_GENDER};
        //"ORDER BY col "+ PetsContract.PetsEntry._ID
        // to get a Cursor that contains all rows from the pets table.
        cursor=getContentResolver().query(uri,columns,null,null,null);
        return cursor;
    }

    /**
     * Helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo(Cursor cursor) {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
        ListView displayView = (ListView) findViewById(R.id.list_view_pet);
        adapter = new PetsCursorAdapter(this, cursor, 0);
        displayView.setAdapter(adapter);

        View emptyView = findViewById(R.id.empty_view);
        displayView.setEmptyView(emptyView);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] columns={_ID,COLUMN_PET_NAME,COLUMN_PET_BREED,COLUMN_PET_WEIGHT,COLUMN_PET_GENDER};
        CursorLoader petsCursorLoader = new CursorLoader(this, uri, columns,null, null, null);
        return petsCursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }
}
