package com.example.myapplication;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

public class BluetoothData implements Serializable {

    BluetoothDevice device;
    String name;
    String mac;


    public BluetoothData(BluetoothDevice device) {
        this.device = device;
        this.name = device.getName();
        this.mac = device.getAddress();
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}
