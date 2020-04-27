package com.nikitha.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.nikitha.android.pets.Data.PetsContract;
import com.nikitha.android.pets.R;

import static com.nikitha.android.pets.Data.Constants.URI;
import static com.nikitha.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_BREED;
import static com.nikitha.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_GENDER;
import static com.nikitha.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_NAME;
import static com.nikitha.android.pets.Data.PetsContract.PetsEntry.COLUMN_PET_WEIGHT;

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

     /*OnTouchListener that listens for any user touches on a View, implying that they are modifying
     the view, and we change the mPetHasChanged boolean to true.*/

    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };
    Intent intent;
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

        intent = getIntent();
        if (intent.hasExtra("id")) { //Null Checking
            setTitle(getResources().getText(R.string.editPet));
            String StrData= intent.getStringExtra("petName");
            if(!intent.getStringExtra("petName").isEmpty()) {
                mNameEditText.setText(intent.getStringExtra("petName"));
            }
            if(!intent.getStringExtra("petBreed").isEmpty()) {
                mBreedEditText.setText(intent.getStringExtra("petBreed"));
            }
            if(!intent.getStringExtra("petWeight").isEmpty()) {
                mWeightEditText.setText(intent.getStringExtra("petWeight"));
            }
            if(!intent.getStringExtra("petGender").isEmpty()) {
                String value=intent.getStringExtra("petGender");
                mGenderSpinner.setSelection(Integer.parseInt(value));
            }
        }
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (!(intent.hasExtra("id")) ) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean clickedSaveBtn=false;
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                clickedSaveBtn = true;
                ContentValues values = new ContentValues();
                values.put(COLUMN_PET_NAME, String.valueOf(mNameEditText.getText()));
                values.put(COLUMN_PET_BREED, String.valueOf(mBreedEditText.getText()));
                values.put(COLUMN_PET_GENDER, mGender);
                values.put(COLUMN_PET_WEIGHT, String.valueOf(mWeightEditText.getText()));
                long newRowId = 0;
                Toast toast;
                if (intent.hasExtra("id")) {
                    newRowId = updatePetData(values);
                    if (newRowId > -1) {
                        Log.e(LOG_TAG, String.valueOf(R.string.petupdated));
                        toast = Toast.makeText(this, R.string.petupdated, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.e(LOG_TAG, String.valueOf(R.string.errorpetupdate));
                    }
                } else {
                    newRowId = insertPetData(values);
                    if (newRowId != -1) {
                        Log.e(LOG_TAG, String.valueOf(R.string.petAdded));
                        toast = Toast.makeText(this, R.string.petAdded, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Log.e(LOG_TAG, String.valueOf(R.string.errorWhenpetAdd));
                    }
                }
//                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if (intent.hasExtra("id")) {
                    Uri uriWithid = ContentUris.withAppendedId(Uri.parse(URI), Long.parseLong(intent.getStringExtra("id")));
                    getContentResolver().delete(uriWithid, null, null);
                    toast = Toast.makeText(this, R.string.petdelete, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Log.e(LOG_TAG, String.valueOf(R.string.errorWhenpetDelete));
                    toast = Toast.makeText(this, R.string.errorWhenpetDelete, Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged || clickedSaveBtn) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                if (clickedSaveBtn) {
                    DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        }
                    };
                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
                else{
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private long insertPetData(ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        Uri result= getContentResolver().insert(Uri.parse(URI),values);
        if(result!=null) {
            return ContentUris.parseId(result);
        }
        return -1;
    }

    private long updatePetData(ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        String selection=PetsContract.PetsEntry._ID+"=?";
        String[] selectionArg=new String[]{intent.getStringExtra("id")};
        int result = getContentResolver().update(Uri.parse(URI), values, selection, selectionArg);
        return Long.valueOf(result);
    }

    private Cursor readDatabaseInfo() {
            Uri uri1;
            String[] columns={COLUMN_PET_NAME,COLUMN_PET_BREED,COLUMN_PET_WEIGHT,COLUMN_PET_GENDER};
            //"ORDER BY col "+ PetsContract.PetsEntry._ID
            // to get a Cursor that contains all rows from the pets table.
            cursor=getContentResolver().query(Uri.parse(URI),columns,null,null,null);
            return cursor;
        }

    /**
     * This code makes a AlertDialog using the AlertDialogBuilder. The method accepts a OnClickListener for the discard button.
     * We do this because the behavior for clicking back or up is a little bit different.
     * @param discardButtonClickListener
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}