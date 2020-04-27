package com.example.android.pets;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.Data.PetsContract;

import java.util.ArrayList;
import java.util.Collections;
import androidx.annotation.NonNull;

import static com.example.android.pets.Data.Constants.URI;
import static com.example.android.pets.Data.PetsContract.PetsEntry.*;

class PetsCursorAdapter extends CursorAdapter {
    Context contextNew;
    Activity contextActivity;

    public PetsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
        this.contextNew= context;
        this.contextActivity= (Activity) context;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        TextView name=view.findViewById(R.id.petName);
        name.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PET_NAME)));
        TextView breed=view.findViewById(R.id.petBreed);
        breed.setText(cursor.getString(cursor.getColumnIndex(COLUMN_PET_BREED)));
        view.setTag(cursor.getString(cursor.getColumnIndex(_ID)));

        // because I set the onclickLister in adaptor we are using onclickListener.
        // AlternativeLY I can use setOnItemClickListener on the listView directly in EditorActivity.
        // the method will have pos and id as inputs,so we can perform query with id directly there.
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                //to get the ID of the list item clicked. We tagged the item when it was being created
                String id = (String)v.getTag();

                String[] columns= new String[]{COLUMN_PET_NAME, COLUMN_PET_BREED, COLUMN_PET_WEIGHT, COLUMN_PET_GENDER};
                Uri newURI = ContentUris.withAppendedId(Uri.parse(URI), Long.parseLong(id));
                Cursor queryResult = contextNew.getContentResolver().query(newURI,columns,null,null,null);
                queryResult.moveToFirst();

                Intent intent = new Intent(contextActivity, EditorActivity.class);
                intent.putExtra("petName",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_NAME)));
                intent.putExtra("petBreed",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_BREED)));
                intent.putExtra("petGender",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_GENDER)));
                intent.putExtra("petWeight",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_WEIGHT)));
                intent.putExtra("id",id);
                contextNew.startActivity(intent);
            }
        });
    }
}
