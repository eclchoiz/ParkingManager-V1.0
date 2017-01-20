package com.eclchoiz.example.parkingmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.eclchoiz.example.parkingmanager.data.ParkingMangerContract.ManagerEntry;

public class OverTimeCursorAdapter extends CursorAdapter {
    public OverTimeCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.new_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView partTextView = (TextView) view.findViewById(R.id.partText);
//        TextView nameTextView = (TextView) view.findViewById(R.id.nameText);
        TextView plateTextView = (TextView) view.findViewById(R.id.plateText);
        TextView numberTextView = (TextView) view.findViewById(R.id.numberText);
//        TextView regNumberTextView = (TextView) view.findViewById(R.id.regNumber);
        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumber);

        int partColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PART);
        int nameColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_NAME);
        int plateColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PLATE);
        int numberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_NUMBER);
        int regNumberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_REG_NUMBER);
        int phoneNumberColumnIndex = cursor.getColumnIndex(ManagerEntry.COLUMN_NAME_PHONE_NUMBER);

        String part = cursor.getString(partColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        String plate = cursor.getString(plateColumnIndex);
        String number = cursor.getString(numberColumnIndex);
        String regNumber = cursor.getString(regNumberColumnIndex);
        String phoneNumber = cursor.getString(phoneNumberColumnIndex);

        partTextView.setText(part);
//        nameTextView.setText(name);
        plateTextView.setText(plate);
        numberTextView.setText(number);
//        regNumberTextView.setText(regNumber);
        phoneNumberTextView.setText(phoneNumber);
    }
}
