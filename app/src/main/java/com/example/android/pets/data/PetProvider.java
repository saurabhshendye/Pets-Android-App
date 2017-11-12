package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Saurabh on 11/10/2017.
 */

public class PetProvider extends ContentProvider {

    private PetsDBHelper petsDBHelper;
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS, PETS);

        sUriMatcher.addURI(PetsContract.CONTENT_AUTHORITY, PetsContract.PATH_PETS + "/#", PET_ID);

    }

    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        petsDBHelper = new PetsDBHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = petsDBHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                cursor = db.query(PetsContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
                        sortOrder);
                break;
            case PET_ID:
                selection = PetsContract.PetEntry.COLUMN_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetsContract.PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,
                        null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet(uri,contentValues);

            default:
                throw new IllegalArgumentException();
        }
    }

    private Uri insertPet(Uri uri,ContentValues contentValues){
        String name = contentValues.getAsString(PetsContract.PetEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet needs to have name");
        }

        String breed = contentValues.getAsString(PetsContract.PetEntry.COLUMN_BREED);
        if (breed == null) {
            throw new IllegalArgumentException("Pet needs to have breed");
        }

        Integer gender = contentValues.getAsInteger(PetsContract.PetEntry.COLUMN_GENDER);
        if (gender==null || !PetsContract.PetEntry.isValidGender(gender) ) {
            throw new IllegalArgumentException("Invalid Gender");
        }

        Integer weight = contentValues.getAsInteger(PetsContract.PetEntry.COLUMN_WEIGHT);
        if (weight==null || weight < 0) {
            throw new IllegalArgumentException("Weight should be non-negative");
        }


        SQLiteDatabase db = petsDBHelper.getWritableDatabase();
        long id = db.insert(PetsContract.PetEntry.TABLE_NAME,null, contentValues);
        if (id==-1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        if (values.containsKey(PetsContract.PetEntry.COLUMN_NAME)) {
            String name = values.getAsString(PetsContract.PetEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if (values.containsKey(PetsContract.PetEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(PetsContract.PetEntry.COLUMN_GENDER);
            if (gender == null || !PetsContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (values.containsKey(PetsContract.PetEntry.COLUMN_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetsContract.PetEntry.COLUMN_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        SQLiteDatabase db = petsDBHelper.getWritableDatabase();

        rowsUpdated = db.update(PetsContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        // Get writeable database
        SQLiteDatabase database = petsDBHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(PetsContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted!=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
