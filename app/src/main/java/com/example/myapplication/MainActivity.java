package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    final public String TAG_APP = "Surprise App";

    //Request codes
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_SELECT_DEVICE = 2;


    //General
    TextView statusLabel;


    //Actions
    Button connectButton;
    Button disconnectButton;

    ImageButton deliverGiftsButton;

    ImageButton forwardButton;
    ImageButton backwardButton;


    //Speed
    Button increaseSpeed;
    Button decreaseSpeed;
    TextView speedLabel;
    private int speed;


    //Bluetooth
    /*BluetoothAdapter bluetooth;
    BluetoothSocket btSocket;
    private InputStream inputStream;
    private OutputStream outputStream;*/

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket btSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    boolean bluetoothActive = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);

        deliverGiftsButton = (ImageButton) findViewById(R.id.deliverGiftsButton);

        forwardButton = (ImageButton) findViewById(R.id.forwardsButton);
        backwardButton = (ImageButton) findViewById(R.id.backwardButton);

        speedLabel = (TextView) findViewById(R.id.speedText);
        increaseSpeed = (Button) findViewById(R.id.incSpeedButton);
        decreaseSpeed = (Button) findViewById(R.id.decSpeedButton);

        statusLabel = (TextView) findViewById(R.id.statusLabel);

        //BLUETOOTH CONFIG
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Action listeners association
        connectButton.setOnClickListener(v -> connectBT());
        disconnectButton.setOnClickListener(v -> disconnect());
        deliverGiftsButton.setOnClickListener(v -> deliverGiftsButton());

        forwardButton.setOnClickListener(v -> forward());
        backwardButton.setOnClickListener(v -> backward());

        increaseSpeed.setOnClickListener(v -> increaseSpeed());
        decreaseSpeed.setOnClickListener(v -> decreaseSpeed());

        //Initial button status
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);



    }

    public void connectBT() {

        statusLabel.setText("Connect pressed");

        if (bluetoothAdapter.isEnabled()) {
            bluetoothActive = true;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                //
            }
            String address = bluetoothAdapter.getAddress();
            String name = bluetoothAdapter.getName();
            //Mostramos la datos en pantalla
            Toast.makeText(this, "Bluetooth ENABLED:" + name + ":" + address, Toast.LENGTH_SHORT).show();
            //startDiscovery();
            statusLabel.setText("Connecting...");
            Intent intent = new Intent(MainActivity.this, DiscoveredBluetoothDevicesActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_DEVICE);


        } else {
            bluetoothActive = false;
            Toast.makeText(this, "Bluetooth NOT enabled", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }


    }

    protected void connect(BluetoothDevice device) {
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
            }
            btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            Log.d(TAG_APP, "Socket: " + btSocket + " name: " + btSocket.getRemoteDevice().getName());
            Log.d(TAG_APP, "State socket: " + btSocket);
            btSocket.connect();
            Log.d(TAG_APP, "Client connected");
            inputStream = btSocket.getInputStream();
            outputStream = btSocket.getOutputStream();

            statusLabel.setText(String.format("Connected to %s successfully.", bluetoothDevice.getName()));

        }catch (Exception e) {
            Log.e("ERROR: connect", ">>", e);
        }
    }

    protected void disconnect() {
        statusLabel.setText("Disconnect pressed");
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (btSocket != null && btSocket.isConnected()) {
                try {
                    btSocket.close();
                    connectButton.setEnabled(true);
                    disconnectButton.setEnabled(false);
                    statusLabel.setText(String.format("Disconnected succesfully from %s", bluetoothDevice));
                    Log.d(TAG_APP, "Client disconnected");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                bluetoothActive = true;
                Toast.makeText(this, "User Enabled Bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                // El Bluetooth no se ha activado
                bluetoothActive = false;
                Toast.makeText(this, "User Did not enable Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_SELECT_DEVICE) {
            if (resultCode == RESULT_OK) {
                // Se ha seleccionado un dispositivo Bluetooth
                bluetoothDevice = intent.getParcelableExtra("BTdevice");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO:Consider calling
                }
                Toast.makeText(this, "Device selected: " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                // Crea el socket Bluetooth con el dispositivo seleccionado
                statusLabel.setText("Connecting to " + bluetoothDevice.getName());
                connect(bluetoothDevice);
            } else {
                Toast.makeText(this, "No device selected", Toast.LENGTH_SHORT).show();
                statusLabel.setText("No device selected");
            }
        }
    }





    //Action methods


    public void deliverGiftsButton() {
        try {
            speed = Integer.parseInt(speedLabel.getText().toString());
            String tmpStr = "a" + speed;
            byte bytes[] = tmpStr.getBytes();
            if (outputStream != null) outputStream.write(bytes);
            if (outputStream != null) outputStream.flush();
            Log.d(TAG_APP, "Petition sent");
            statusLabel.setText("Presents are coming!");
        } catch (Exception e) {
            Log.e(TAG_APP, "GIVE PRESENTS ERROR:" + e);
        }
    }


    public void forward() {
        try {
            String tmpStr = "f";
            byte[] bytes = tmpStr.getBytes();
            if (outputStream != null) outputStream.write(bytes);
            if (outputStream != null) outputStream.flush();
            Log.d(TAG_APP, "Forward sent");
            statusLabel.setText("Forward");

        } catch (Exception e) {
            Log.e(TAG_APP, "FORWARD ERROR:" + e);
        }
    }

    public void backward() {
        try {
            String tmpStr = "b";
            byte[] bytes = tmpStr.getBytes();
            if (outputStream != null) outputStream.write(bytes);
            if (outputStream != null) outputStream.flush();
            Log.d(TAG_APP, "Backward sent");
            statusLabel.setText("Backward");

        } catch (Exception e) {
            Log.e(TAG_APP, "BACKWARD ERROR:" + e);
        }
    }

    public void increaseSpeed(){
        String currentSpeed = speedLabel.getText().toString();
        speed = Integer.parseInt(currentSpeed);

        if (speed < 255) speed++;
        else {
            Log.d(TAG_APP, "Maximum speed reached");
            Toast.makeText(getApplicationContext(), "Maximum speed reached", Toast.LENGTH_SHORT).show();
        }

        speedLabel.setText(String.valueOf(speed));

    }

    public void decreaseSpeed(){
        String currentSpeed = speedLabel.getText().toString();
        speed = Integer.parseInt(currentSpeed);

        if (speed > 155) speed--; //TODO probar a que valor en ENA el motor ya no tira
        else {
            Log.d(TAG_APP, "Minimum speed reached");
            Toast.makeText(getApplicationContext(), "Minimun speed reached", Toast.LENGTH_SHORT).show();
        }

        speedLabel.setText(String.valueOf(speed));

    }


}