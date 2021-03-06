package com.eclchoiz.example.parkingmanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;
import com.eclchoiz.example.parkingmanager.utils.ParkingUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ParkingActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_MANAGER_LOADER = 0;

    private static final String PARKING_BASE_NODE = "parking";

    private Uri mCurrentManagerUri;

    private DatabaseReference mDatabaseReference;

    private TextView mKeyStoreTextView;
    private EditText mPartEditText;
    private EditText mNameEditText;
    private EditText mPlateEditText;
    private EditText mNumberEditText;
    private EditText mRegNumberEditText;
    private EditText mPhoneNumberEditText;
    private Button mDbSaveButton;

    private boolean mManagerChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mManagerChanged = true;
            mDbSaveButton.setEnabled(true);
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        Intent intent = getIntent();
        mCurrentManagerUri = intent.getData();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(PARKING_BASE_NODE);

        mKeyStoreTextView = (TextView) findViewById(R.id.keyParkingStore);
        mPartEditText = (EditText) findViewById(R.id.partEdit);
        mNameEditText = (EditText) findViewById(R.id.nameEdit);
        mPlateEditText = (EditText) findViewById(R.id.plateEdit);
        mNumberEditText = (EditText) findViewById(R.id.numberEdit);
        mRegNumberEditText = (EditText) findViewById(R.id.regNumberEdit);
        mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEdit);
        mDbSaveButton = (Button) findViewById(R.id.dbParkingSave);

        mPartEditText.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPlateEditText.setOnTouchListener(mTouchListener);
        mNumberEditText.setOnTouchListener(mTouchListener);
        mRegNumberEditText.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);

        if (mCurrentManagerUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_data));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_data));
            getLoaderManager().initLoader(EXISTING_MANAGER_LOADER, null, this);
        }
    }

    public void dbParkingCancelClicked(View view) {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void dbParkingSaveClicked(View view) {
        saveNewCar();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveNewCar();
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mManagerChanged) {
                    NavUtils.navigateUpFromSameTask(ParkingActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(ParkingActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewCar() {
        String part = mPartEditText.getText().toString().trim();
        String name = mNameEditText.getText().toString().trim();
        String plate = mPlateEditText.getText().toString().trim();
        String number = mNumberEditText.getText().toString().trim();
        String regNumber = mRegNumberEditText.getText().toString().trim();
        String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
        String key = mKeyStoreTextView.getText().toString().trim();

        if (mCurrentManagerUri == null &&
                TextUtils.isEmpty(part) && TextUtils.isEmpty(number)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ManagerEntry.COLUMN_NAME_PART, part);
        values.put(ManagerEntry.COLUMN_NAME_NAME, name);
        values.put(ManagerEntry.COLUMN_NAME_PLATE, plate);
        values.put(ManagerEntry.COLUMN_NAME_NUMBER, number);
        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, regNumber);
        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, phoneNumber);

        if (mCurrentManagerUri == null) {
            mDatabaseReference.push().setValue(ParkingUtils.valueToObject(values));
        } else {
            values.put(ManagerEntry.COLUMN_NAME_KEY, key);
            int rowsAffected = getContentResolver().update(mCurrentManagerUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_data_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                mDatabaseReference.child(key).setValue(ParkingUtils.valueToObject(values));
                Toast.makeText(this, getString(R.string.editor_update_data_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentManagerUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mManagerChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ManagerEntry._ID,
                ManagerEntry.COLUMN_NAME_KEY,
                ManagerEntry.COLUMN_NAME_PART,
                ManagerEntry.COLUMN_NAME_NAME,
                ManagerEntry.COLUMN_NAME_PLATE,
                ManagerEntry.COLUMN_NAME_NUMBER,
                ManagerEntry.COLUMN_NAME_REG_NUMBER,
                ManagerEntry.COLUMN_NAME_PHONE_NUMBER};

        return new CursorLoader(this,
                mCurrentManagerUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int keyColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_KEY);
            int partColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PART);
            int nameColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_NAME);
            int plateColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PLATE);
            int numberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_NUMBER);
            int regNumberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_REG_NUMBER);
            int phoneNumberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PHONE_NUMBER);

            String key = cursor.getString(keyColumnIndex);
            String part = cursor.getString(partColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String plate = cursor.getString(plateColumnIndex);
            String number = cursor.getString(numberColumnIndex);
            String regNumber = cursor.getString(regNumberColumnIndex);
            String phoneNumber = cursor.getString(phoneNumberColumnIndex);

            mKeyStoreTextView.setText(key);
            mPartEditText.setText(part);
            mNameEditText.setText(name);
            mPlateEditText.setText(plate);
            mNumberEditText.setText(number);
            mRegNumberEditText.setText(regNumber);
            mPhoneNumberEditText.setText(phoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKeyStoreTextView.setText("");
        mPartEditText.setText("");
        mNameEditText.setText("");
        mPlateEditText.setText("");
        mNumberEditText.setText("");
        mRegNumberEditText.setText("");
        mPhoneNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteData();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteData() {
        if (mCurrentManagerUri != null) {
            String key = mKeyStoreTextView.getText().toString();
            int rowsDeleted = getContentResolver().delete(mCurrentManagerUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_data_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                mDatabaseReference.child(key).removeValue();
                Toast.makeText(this, getString(R.string.editor_delete_data_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}