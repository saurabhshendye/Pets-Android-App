package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;

/**
 * Created by Saurabh on 11/11/2017.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent , false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView breedView = (TextView) view.findViewById(R.id.summary);

        int nameIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_NAME);
        int breedIndex = cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_BREED);

        String name = cursor.getString(nameIndex);
        String breed = cursor.getString(breedIndex);

        nameView.setText(name);
        breedView.setText(breed);
    }
}
