package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PetProvider extends ContentProvider {
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper petDbHelper;
    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate() {
        petDbHelper = new PetDbHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = petDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                //TODO:perform database query on pets table
                cursor = database.query(
                        PetContract.PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(
                        PetContract.PetEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri,
                      ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        if (match == PETS) {
            return insertPet(uri, contentValues);
        }
        throw new IllegalArgumentException("Insertion is not support for " + uri);
    }


    private Uri insertPet(Uri uri, ContentValues values) {
        //check data valid or not
        checkInputData(values);
        // TODO: Insert a new pet into the pets database table with the given ContentValues
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        System.out.println("id = " + id);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        } else {
            getContext().getContentResolver().notifyChange(uri,null);
            return ContentUris.withAppendedId(uri, id);
        }
    }

    private void checkInputData(ContentValues values) {
        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
        if (name == null) {
            throw new IllegalArgumentException("Name = null, Pet requires a name");
        } else if (gender == null || !PetContract.PetEntry.checkGenderValid(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        } else if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
    }

    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {

        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int count;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                count = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete unknown URI: " + uri);
        }
        if(count != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return count;
    }

    @Override
    public int update(Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs) {

        if (contentValues != null) {
            checkInputData(contentValues);
        } else {
            throw new IllegalArgumentException("Null contentValues at URI: " + uri);
        }
        SQLiteDatabase database = petDbHelper.getWritableDatabase();
        int count = 0;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                count = database.update(
                        PetContract.PetEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = database.update(
                        PetContract.PetEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
        }
        if(count != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}
