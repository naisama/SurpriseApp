package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class BluetoothDataRecyclerViewAdapter extends RecyclerView.Adapter<BluetoothDataRecyclerViewAdapter.BluetoothDataViewHolder>{

    final public String TAG_APP = "BluetoothDataRecycler";

    public List<BluetoothData> dataList;
    private Context context;

    public BluetoothDataRecyclerViewAdapter(Context context, List<BluetoothData> bluetoothDataList) {
        this.context = context;
        this.dataList = bluetoothDataList;
    }

    @NonNull
    @Override
    public BluetoothDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View bluetooth_row = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device, null);
        return new BluetoothDataViewHolder(bluetooth_row);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDataViewHolder holder, int position) {
        holder.btNameText.setText(dataList.get(position).getName());
        holder.btMacText.setText(dataList.get(position).getMac());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class BluetoothDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView btNameText;
        public TextView btMacText;

        public BluetoothDataViewHolder(@NonNull View itemView) {
            super(itemView);
            btNameText = itemView.findViewById(R.id.device_name);
            btMacText = itemView.findViewById(R.id.device_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            String btName = btNameText.getText().toString();
            String btMac = btMacText.getText().toString();
            BluetoothDevice btDevice = null;
            for (int i = 0; i < dataList.size(); i++) {
                if (Objects.equals(dataList.get(i).getName(), btName) && Objects.equals(dataList.get(i).getMac(), btMac)) {
                    btDevice = dataList.get(i).getDevice();
                    break;
                }
            }

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("BTdevice", btDevice);
            Log.d(TAG_APP, "starting activity...");
            ((Activity) context).setResult(RESULT_OK, intent);
            ((Activity) context).finish();

        }
    }
}
