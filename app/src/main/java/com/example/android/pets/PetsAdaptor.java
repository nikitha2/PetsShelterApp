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
import android.widget.TextView;

import com.example.android.pets.Data.PetsContract;

import java.util.ArrayList;
import java.util.Collections;
import androidx.annotation.NonNull;

import static com.example.android.pets.Data.Constants.URI;
import static com.example.android.pets.Data.PetsContract.PetsEntry.*;

class PetsAdaptor extends ArrayAdapter<ListItems> {
    ArrayList<ListItems> listItemsArray=new ArrayList<ListItems>();
    Context context;
    Activity contextActivity;
    ListItems currentword;

    public PetsAdaptor(@NonNull Context context, @NonNull ArrayList<ListItems> objects) {
        super(context, 0,objects);
        listItemsArray=objects;
        this.context= context;
        this.contextActivity= (Activity) context;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);
        }
        final String[] parts = (parent.toString()).split("app:id/", 2);
        currentword = getItem(position);

        TextView name=listItemView.findViewById(R.id.petName);
        name.setText(currentword.getPetName());

        TextView breed=listItemView.findViewById(R.id.petBreed);
        breed.setText(currentword.getPetBreed());

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(contextActivity, EditorActivity.class);
                String[] columns={PetsContract.PetsEntry._ID};
                String selection=COLUMN_PET_NAME+"='"+currentword.getPetName()+"'AND "+COLUMN_PET_BREED+"='"+currentword.getPetBreed()+"' AND  "+COLUMN_PET_WEIGHT+"="+currentword.getPetWeight()+" AND "+COLUMN_PET_GENDER+"="+currentword.getPetGender()+"";
                //String[] selectionArg=new String[] {String.valueOf(),String.valueOf(currentword.getPetBreed()),String.valueOf(currentword.getPetGender()),String.valueOf(currentword.getPetWeight())};

//                selection=PetsContract.PetsEntry._ID+"=?";
//                selectionArgs=new String[] { String.valueOf(ContentUris.parseId(uri)) };

               Cursor queryResult = context.getContentResolver().query(Uri.parse(URI),columns,selection,null,null);
               int i=queryResult.getColumnIndex(_ID);
               int count=queryResult.getCount();
               queryResult.moveToFirst();
               String id = queryResult.getString(i);

                columns= new String[]{COLUMN_PET_NAME, COLUMN_PET_BREED, COLUMN_PET_WEIGHT, COLUMN_PET_GENDER};
                selection=PetsContract.PetsEntry._ID+"=?";
                String[] selectionArg=new String[]{id};

                queryResult = context.getContentResolver().query(Uri.parse(URI),columns,selection,selectionArg,null);
                queryResult.moveToFirst();
                count=queryResult.getCount();

                intent.putExtra("petName",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_NAME)));
                intent.putExtra("petBreed",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_BREED)));
                intent.putExtra("petGender",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_GENDER)));
                intent.putExtra("petWeight",queryResult.getString(queryResult.getColumnIndex(COLUMN_PET_WEIGHT)));
                intent.putExtra("id",id);

                context.startActivity(intent);
            }
        });
        return listItemView;
    }
}
