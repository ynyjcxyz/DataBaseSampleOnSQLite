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

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /**
     * Database helper that will provide us access to the database
     */
//    private PetDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
//        mDbHelper = new PetDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */

    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT
        };

        // Perform a query on the pets table
        // The table to query
        // The columns to return
        // The columns for the WHERE clause
        // The values for the WHERE clause
        // Don't group the rows
        // Don't filter by row groups
        // The sort order

        Cursor cursor = getContentResolver().query(
                PetContract.PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.text_view_pet);
        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
            displayView.append(PetEntry._ID + " - " +
                    PetEntry.COLUMN_PET_NAME + " - " +
                    PetEntry.COLUMN_PET_BREED + " - " +
                    PetEntry.COLUMN_PET_GENDER + " - " +
                    PetEntry.COLUMN_PET_WEIGHT + "\n");

            // Figure out the index of each column

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" +
                        cursor.getInt(cursor.getColumnIndex(PetEntry._ID))
                        + " - " +
                        cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME))
                        + " - " +
                        cursor.getString(cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED))
                        + " - " +
                        cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER))
                        + " - " +
                        cursor.getInt(cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT))));
            }
        } finally {
            cursor.close();
        }
        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {
        // Gets the database in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues insertValues = new ContentValues();
        insertValues.put(PetEntry.COLUMN_PET_NAME, "Toto_1");
        insertValues.put(PetEntry.COLUMN_PET_BREED, "Terrier_MIX");
        insertValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        insertValues.put(PetEntry.COLUMN_PET_WEIGHT, 8);
        Uri insertUri = getContentResolver().insert(PetEntry.CONTENT_URI,insertValues);
        Toast.makeText(this, "InserUri is: " + insertUri, Toast.LENGTH_SHORT).show();

        // Insert a new row for Toto in the database, returning the ID of that new row.
        // The first argument for db.insert() is the pets table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for Toto.

//        long newRowId = db.insert(PetEntry.TABLE_NAME, null, insertValues);
//        Toast.makeText(this, "newRowId = " + newRowId, Toast.LENGTH_SHORT).show();
//        System.out.println("newRowId = " + newRowId);
    }

    private void deleteTables() {
        //clear current table,caution!
//        mDbHelper.getWritableDatabase().execSQL("delete from " + PetEntry.TABLE_NAME);
        getContentResolver().delete(PetEntry.CONTENT_URI,null,null);
//        getContentResolver().delete(PetEntry.CONTENT_URI,PetEntry.COLUMN_PET_NAME + "=?",new String[] {"Xijingping"} );
        Toast.makeText(this,"Delete the table!",Toast.LENGTH_SHORT).show();
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
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteTables();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
