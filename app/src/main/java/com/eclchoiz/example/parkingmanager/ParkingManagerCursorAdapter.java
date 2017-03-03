package com.eclchoiz.example.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;

public class ParkingManagerCursorAdapter extends CursorAdapter {
    public ParkingManagerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.parking_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView partTextView = (TextView) view.findViewById(R.id.partText);
        TextView plateTextView = (TextView) view.findViewById(R.id.plateText);
        TextView numberTextView = (TextView) view.findViewById(R.id.numberText);
        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumber);
        TextView keyTextView = (TextView) view.findViewById(R.id.keyParkTextView);

        ImageView phoneCallImageView = (ImageView) view.findViewById(R.id.phoneCall);
        ImageView mmsSendImageView = (ImageView) view.findViewById(R.id.sendMessage);

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

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneCallImageView.setImageResource(R.drawable.disable_phone);
            phoneCallImageView.setEnabled(false);
            mmsSendImageView.setImageResource(R.drawable.disalbe_mail);
            mmsSendImageView.setEnabled(false);
        } else {
            phoneCallImageView.setImageResource(R.drawable.enable_phone);
            phoneCallImageView.setEnabled(true);
            mmsSendImageView.setImageResource(R.drawable.enable_mail);
            mmsSendImageView.setEnabled(true);
        }

        partTextView.setText(part);
        plateTextView.setText(plate);
        numberTextView.setText(number);
        phoneNumberTextView.setText(phoneNumber);
        keyTextView.setText(key);
    }
}
