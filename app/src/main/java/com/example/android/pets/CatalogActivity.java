package com.example.android.pets;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PET_LOADER_ID = 0;
    private PetCursorAdapter cursorAdapter;

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

        ListView pet_list = findViewById(R.id.pet_list);
        View empty_view = findViewById(R.id.empty_view);
        pet_list.setEmptyView(empty_view);

        PetCursorAdapter adapter = new PetCursorAdapter(this,null);
        pet_list.setAdapter(adapter);

        //use loader to add data
        getLoaderManager().initLoader(PET_LOADER_ID, null, this);
    }

    private void insertPet() {
        ContentValues insertValues = new ContentValues();
        insertValues.put(PetEntry.COLUMN_PET_NAME, "Toto_1");
        insertValues.put(PetEntry.COLUMN_PET_BREED, "Terrier_MIX");
        insertValues.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        insertValues.put(PetEntry.COLUMN_PET_WEIGHT, 8);
        Uri insertUri = getContentResolver().insert(PetEntry.CONTENT_URI, insertValues);
        Toast.makeText(this, "InserUri is: " + insertUri, Toast.LENGTH_SHORT).show();
    }

    private void deleteTables() {
        getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
//        getContentResolver().delete(PetEntry.CONTENT_URI,PetEntry.COLUMN_PET_NAME + "=?",new String[] {"Xijingping"} );
        Toast.makeText(this, "Delete the table!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteTables();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
        };
        return new CursorLoader(this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
