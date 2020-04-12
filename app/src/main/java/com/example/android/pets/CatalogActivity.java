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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.pets.Data.Constants;
import com.example.android.pets.Data.PetProvider;
import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.Data.PetsDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.pets.Data.Constants.*;
import static com.example.android.pets.Data.PetsContract.PetsEntry.*;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {
    PetsDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    Uri uri=Uri.parse(URI);


    /** Tag for the log messages */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // to test if db is created as expected- open view->ToolsWindow->Device File Explorer
        // goto Data->Data -> your package com.example.android.pets and see if DB is created. If below method runs statement will be created.
        //download it into your computer and in terminal goto the path
        //type sqlite3 PetsData.db (db name here)
        //.tables  // will give the list of tables in the db
        //PLASMA TABLE_INFO(Pets) //pets is the table name - will give the columns created. check if table created as expected.
        //.schema will give the command used to create the table.
        Cursor cursor=readDatabaseInfo();
        displayDatabaseInfo(cursor);

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
    protected void onStart() {
        super.onStart();
        cursor=readDatabaseInfo();
        displayDatabaseInfo(cursor);
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
                    cursor=readDatabaseInfo();
                    displayDatabaseInfo(cursor);
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
                cursor=readDatabaseInfo();
                displayDatabaseInfo(cursor);
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
        String[] columns={COLUMN_PET_NAME,COLUMN_PET_BREED,COLUMN_PET_WEIGHT,COLUMN_PET_GENDER};
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
        String name,breed,gender;
        String weight;
        int rowCount=cursor.getCount();
        int colCount=cursor.getColumnCount();
        ArrayList<String[]> rowLists = new ArrayList<>();
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            cursor.moveToPosition(-1);
            while(cursor.moveToNext()){
                name=cursor.getString(cursor.getColumnIndex("Name"));
                breed=cursor.getString(cursor.getColumnIndex("Breed"));
                gender=cursor.getString(cursor.getColumnIndex("Gender"));
                weight=cursor.getString(cursor.getColumnIndex("Weight"));
                rowLists.add(new String[]{name, breed, gender, weight});
            }
            StringBuilder output=new StringBuilder();
            output.append("\n ");
            for(int row=0;row<rowLists.size();row++){
                for(int col=0;col<rowLists.get(0).length;col++){
                    output.append(rowLists.get(row)[col]+"    ");
                }
                output.append("\n ");
            }
            displayView.setText("Number of rows in pets database table: " + output);
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }
}
