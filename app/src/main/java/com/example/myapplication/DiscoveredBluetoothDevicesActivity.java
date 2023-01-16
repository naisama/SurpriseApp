package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DiscoveredBluetoothDevicesActivity extends AppCompatActivity {

    final public String TAG_APP = "BT Activity";

    private static final int RC_BLUETOOTH = 1;
    private static final int RC_LOCATION = 3;

    private final List<BluetoothData> discoveryList = new ArrayList<>();
    private BluetoothDataRecyclerViewAdapter btDiscoveryAdapter;
    RecyclerView discoveryDevicesListView;

    private final List<BluetoothData> bondedList = new ArrayList<>();
    private BluetoothDataRecyclerViewAdapter btBondedAdapter;
    RecyclerView bondedDevicesListView;

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovered_bluetooth_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Inicializar Adapter y las RecyclerView
        btDiscoveryAdapter = new BluetoothDataRecyclerViewAdapter(DiscoveredBluetoothDevicesActivity.this, discoveryList);
        //Dispositivos descubiertos
        discoveryDevicesListView = findViewById(R.id.discoveryDevicesListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DiscoveredBluetoothDevicesActivity.this);
        discoveryDevicesListView.setLayoutManager(linearLayoutManager);
        discoveryDevicesListView.setAdapter(btDiscoveryAdapter);

        //Dispositivos vinculados
        btBondedAdapter = new BluetoothDataRecyclerViewAdapter(DiscoveredBluetoothDevicesActivity.this, bondedList);
        bondedDevicesListView = findViewById(R.id.bondedDevicesListView);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(DiscoveredBluetoothDevicesActivity.this);
        bondedDevicesListView.setLayoutManager(linearLayoutManager1);
        bondedDevicesListView.setAdapter(btBondedAdapter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice d : bondedDevices){
            bondedList.add(new BluetoothData(d));
            btBondedAdapter.notifyItemInserted(bondedList.size() - 1);
        }


        startDiscovery();

    }

    private void startDiscovery() {

        btDiscoveryAdapter.notifyItemRangeRemoved(0, discoveryList.size());
        discoveryList.clear();
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            checkBTPermissions();
        }
        bluetoothAdapter.startDiscovery();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

            if (remoteDeviceName != null) {
                discoveryList.add(new BluetoothData(remoteDevice));
                btDiscoveryAdapter.notifyItemInserted(discoveryList.size() - 1);
            }

            Log.d(TAG_APP, "Discovered " + remoteDeviceName);
            Log.d(TAG_APP, "RSSI " + rssi + "dBm");


        }
    };

    public void checkBTPermissions() {
        switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            case PackageManager.PERMISSION_DENIED:
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_LOCATION);
                    }
                }
                break;
            case PackageManager.PERMISSION_GRANTED:
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            switch (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.BLUETOOTH_CONNECT)) {
                case PackageManager.PERMISSION_DENIED:
                    if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT}, RC_BLUETOOTH);
                    }
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }
}