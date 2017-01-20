package com.eclchoiz.example.parkingmanager;

import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MmsDialogFragment.NoticeDialogListener {

    private static final int MANAGER_LOADER = 0;
    private static final int REQUEST_CODE_FOR_PHONE_CALL = 123;
    private static final int REQUEST_CODE_FOR_SEND_MMS = 456;
    ParkingManagerCursorAdapter mCursorAdapter;
    String mPhoneNumber;
    String mMessage;
    String mCursorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView managerListView = (ListView) findViewById(R.id.list);

        mCursorAdapter = new ParkingManagerCursorAdapter(this, null);
        managerListView.setAdapter(mCursorAdapter);

        managerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentManagerUri = ContentUris.withAppendedId(ManagerEntry.CONTENT_URI, id);
                intent.setData(currentManagerUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(MANAGER_LOADER, null, this);
    }

    // 전화 걸기 관련 시작
    public void phoneCallButtonClicked(View view) {
        if (!TextUtils.isEmpty(getPhoneNumber(view))) {
            mPhoneNumber = getPhoneNumber(view);
            onCall();
        } else {
            mPhoneNumber = null;
            Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
        }
    }

    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_PHONE_CALL);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + mPhoneNumber)));
        }
    }
    // 전화 걸기 관련 끝

    // 문자 보내기 관련 시작
    public void mmsButtonClicked(View view) {

        if (!TextUtils.isEmpty(getPhoneNumber(view))) {
            mPhoneNumber = getPhoneNumber(view);
        } else {
            mPhoneNumber = null;
            Toast.makeText(this, R.string.no_phone_number, Toast.LENGTH_SHORT).show();
            return;
        }

        DialogFragment mmsDialog = new MmsDialogFragment();
        mmsDialog.show(getSupportFragmentManager(), "mms_send_dialog");
    }

    @Override
    public void onDialogPositiveClick(int id) {
        selectMMS(id);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    public String getPhoneNumber(View view) {
        LinearLayout viewParentRow = (LinearLayout) view.getParent();
        TextView phoneNumberTextView = (TextView) viewParentRow.findViewById(R.id.phoneNumber);

        return phoneNumberTextView.getText().toString().trim();
    }

    public void selectMMS(int id) {

        switch (id) {
            case 0:
                mMessage = getString(R.string.mms_not_register_car);
                break;
            case 1:
                mMessage = getString(R.string.mms_no_register_card);
                break;
            case 2:
                mMessage = getString(R.string.mms_wrong_place);
                break;
            default:
                mMessage = null;
                break;
        }

        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "본 기기에서는 문자 메세지를 사용할 수 없습니다.\n" + "MMS Sent to : " + mPhoneNumber + "\n" + mMessage, Toast.LENGTH_LONG).show();
        } else {
            sendMMS();
        }
    }

    public void sendMMS() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_FOR_SEND_MMS);
        } else {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> messageParts = sms.divideMessage(mMessage);
            sms.sendMultipartTextMessage(mPhoneNumber, null, messageParts, null, null);

            Toast.makeText(this, "MMS Sent to : " + mPhoneNumber, Toast.LENGTH_SHORT).show();
        }
    }
    // 문자 보내기 관련 끝


    // 전화걸기 및 문자 보내기 permission 체크
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_FOR_PHONE_CALL:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall();
                } else {
                    Log.d("TAG", "Call Permission Not Granted");
                }
                break;

            case REQUEST_CODE_FOR_SEND_MMS:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    sendMMS();
                } else {
                    Log.d("TAG", "SEND SMS Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // SearchView를 등록하고 리스너를 등록하기.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mCursorFilter = !TextUtils.isEmpty(newText) ? newText : null;
                getLoaderManager().restartLoader(0, null, MainActivity.this);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummy();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllCars();
                return true;
            case R.id.action_import_db:
                importDB();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllCars() {
        int rowsDeleted = getContentResolver().delete(ManagerEntry.CONTENT_URI, null, null);
    }

    private void insertDummy() {
        ContentValues values = new ContentValues();
        values.put(ManagerEntry.COLUMN_NAME_PART, "이마트");
        values.put(ManagerEntry.COLUMN_NAME_NAME, "최진원");
        values.put(ManagerEntry.COLUMN_NAME_PLATE, "32노");
        values.put(ManagerEntry.COLUMN_NAME_NUMBER, "2288");
        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, "e-74");
        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, "010-9913-4131");

        Uri newUri = getContentResolver().insert(ManagerEntry.CONTENT_URI, values);
    }

    // CSV 파일을 읽어 DB에 저장하기
    private void importDB() {

        deleteAllCars();

        try {
            InputStreamReader is = new InputStreamReader(getAssets().open("working_csv.txt")); // assets 폴더의 파일 읽기

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {
                String[] str = line.split(",");
                int size = str.length;
                ContentValues values = new ContentValues();

                switch (size) {
                    case 9:
                    case 8:
                    case 7:
                    case 6:
                        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, str[4]);
                        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, str[5]);
                        break;
                    case 5:
                        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, str[4]);
                        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, "");
                        break;
                    case 4:
                        values.put(ManagerEntry.COLUMN_NAME_REG_NUMBER, "");
                        values.put(ManagerEntry.COLUMN_NAME_PHONE_NUMBER, "");
                        break;
                }

                values.put(ManagerEntry.COLUMN_NAME_PART, str[0]);
                values.put(ManagerEntry.COLUMN_NAME_NAME, str[1]);
                values.put(ManagerEntry.COLUMN_NAME_PLATE, str[2]);
                values.put(ManagerEntry.COLUMN_NAME_NUMBER, str[3]);

                Uri newUri = getContentResolver().insert(ManagerEntry.CONTENT_URI, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selectionClause = null;
        String[] selectionArgs = null;
        Uri baseUri = ManagerEntry.CONTENT_URI;

        if (mCursorFilter != null) {
            selectionClause = ManagerEntry.COLUMN_NAME_NUMBER + " like ?";
            selectionArgs = new String[]{"%" + mCursorFilter + "%"};
        }

        String[] projection = {
                ManagerEntry._ID,
                ManagerEntry.COLUMN_NAME_PART,
                ManagerEntry.COLUMN_NAME_NAME,
                ManagerEntry.COLUMN_NAME_PLATE,
                ManagerEntry.COLUMN_NAME_NUMBER,
                ManagerEntry.COLUMN_NAME_REG_NUMBER,
                ManagerEntry.COLUMN_NAME_PHONE_NUMBER};

        return new CursorLoader(this,
                baseUri,
                projection,
                selectionClause,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
