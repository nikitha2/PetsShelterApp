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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.Data.PetProvider;
import com.example.android.pets.Data.PetsContract;
import com.example.android.pets.Data.PetsDbHelper;

import java.io.Serializable;

import static com.example.android.pets.Data.Constants.SQL_SELECT_PETS_TABLE;
import static com.example.android.pets.Data.Constants.URI;
import static com.example.android.pets.Data.Constants.petCounter;
import static com.example.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_BREED;
import static com.example.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_GENDER;
import static com.example.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_NAME;
import static com.example.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_WEIGHT;
import static com.example.android.pets.Data.PetsContract.PetsEntry.TABLE_NAME;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    Cursor cursor;
    /** Tag for the log messages */
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;
    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        cursor = readDatabaseInfo();
        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetsEntry.gender_Pet_Male; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender =PetsContract.PetsEntry.gender_Pet_Female; // Female
                    } else {
                        mGender = PetsContract.PetsEntry.gender_Pet_Unknown; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                mGender = PetsContract.PetsEntry.gender_Pet_Unknown;; // Unknown
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                ContentValues values = new ContentValues();
                values.put(COLUMN_PET_NAME, String.valueOf(mNameEditText.getText()));
                values.put(COLUMN_PET_BREED, String.valueOf(mBreedEditText.getText()));
                values.put(COLUMN_PET_GENDER,mGender);
                values.put(COLUMN_PET_WEIGHT, String.valueOf(mWeightEditText.getText()));
                long newRowId= insertPetData(values);
                Toast toast;
                if(newRowId!=-1) {
                    toast = Toast.makeText(this,R.string.petAdded, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    toast=Toast.makeText(this,R.string.errorWhenpetAdd, Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long insertPetData(ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        Uri result= getContentResolver().insert(Uri.parse(URI),values);
        return ContentUris.parseId(result);
    }

    private Cursor readDatabaseInfo() {
            Uri uri1;
            String[] columns={COLUMN_PET_NAME,COLUMN_PET_BREED,COLUMN_PET_WEIGHT,COLUMN_PET_GENDER};
            //"ORDER BY col "+ PetsContract.PetsEntry._ID
            // to get a Cursor that contains all rows from the pets table.
            cursor=getContentResolver().query(Uri.parse(URI),columns,null,null,null);
            return cursor;
        }
}