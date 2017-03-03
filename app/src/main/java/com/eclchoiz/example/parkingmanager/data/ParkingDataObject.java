package com.eclchoiz.example.parkingmanager.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ParkingDataObject {

    private String key;
    private String part;
    private String name;
    private String plate;
    private String number;
    private String regNumber;
    private String phoneNumber;

    public ParkingDataObject() {
    }

    public ParkingDataObject(String part, String name, String plate, String number, String regNumber, String phoneNumber) {
        this.part = part;
        this.name = name;
        this.plate = plate;
        this.number = number;
        this.regNumber = regNumber;
        this.phoneNumber = phoneNumber;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String registerNumber) {
        this.regNumber = regNumber;
    }

    @Override
    public String toString() {
        return "ParkingDataObject{" +
                "key='" + key + '\'' +
                ", part='" + part + '\'' +
                ", name='" + name + '\'' +
                ", plate='" + plate + '\'' +
                ", number='" + number + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("part", part);
        result.put("plate", plate);
        result.put("number", number);
        result.put("regNumber", regNumber);
        result.put("phoneNumber", phoneNumber);

        return result;
    }
}
