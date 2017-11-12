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

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;
import com.example.android.pets.data.PetsDBHelper;

import java.net.URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity
        implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final static int PET_LOADER = 0;

    PetCursorAdapter petCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView petListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Attaching adapter to list View
        petCursorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(petCursorAdapter);

        // Get the loader instance
        getLoaderManager().initLoader(PET_LOADER,null,this);

        // Setting an OnItemClick listener to each pet
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                Uri contentURI = Uri.withAppendedPath(PetsContract.PetEntry.CONTENT_URI, String.valueOf(id));
                intent.setData(contentURI);
                startActivity(intent);
            }
        });

    }

//    private void displayDatabaseInfo() {
//
//        // Perform this SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
//        String [] SelectionColumns = new String[]
//                {PetsContract.PetEntry.COLUMN_ID,
//                        PetsContract.PetEntry.COLUMN_NAME,
//                        PetsContract.PetEntry.COLUMN_BREED,
//                        PetsContract.PetEntry.COLUMN_WEIGHT,
//                        PetsContract.PetEntry.COLUMN_GENDER};
//
//
//        Cursor cursor= getContentResolver().query(PetsContract.PetEntry.CONTENT_URI,
//                SelectionColumns, null,null,null);
//
//        PetCursorAdapter petCursorAdapter = new PetCursorAdapter(this, cursor);
//
//        ListView categoryView = (ListView) findViewById(R.id.list);
//        categoryView.setAdapter(petCursorAdapter);
//
//    }

    private void insertDummy(){
        // Building the insert query
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetEntry.COLUMN_NAME, "Garfield");
        values.put(PetsContract.PetEntry.COLUMN_BREED, "Tabby");
        values.put(PetsContract.PetEntry.COLUMN_GENDER, PetsContract.PetEntry.GENDER_MALE);
        values.put(PetsContract.PetEntry.COLUMN_WEIGHT, 7);


        PetsDBHelper petsDBHelper = new PetsDBHelper(this);
        SQLiteDatabase db = petsDBHelper.getWritableDatabase();
        getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI, values);
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
                // Insert Dummy data
                insertDummy();
//                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetsContract.PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String [] SelectionColumns = new String[]
                {PetsContract.PetEntry.COLUMN_ID,
                        PetsContract.PetEntry.COLUMN_NAME,
                        PetsContract.PetEntry.COLUMN_BREED,
                        };


        return new CursorLoader(this,
                PetsContract.PetEntry.CONTENT_URI,
                SelectionColumns,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        petCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        petCursorAdapter.swapCursor(null);
    }
}
