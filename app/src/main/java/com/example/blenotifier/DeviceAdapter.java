package com.example.blenotifier;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowContentFrameStats;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class DeviceAdapter  extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{

    private List<WScanResult> data;
    private Context context;

    public DeviceAdapter(List<WScanResult> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_device_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WScanResult model = data.get(i);
        viewHolder.macAddress.setText(model.macAddress);
        viewHolder.rssi.setText(String.valueOf(model.level));
        viewHolder.distance.setText(String.valueOf(model.distance));
        viewHolder.timeStampNano.setText(String.valueOf(String.valueOf(model.time)));

        //TODO: major minor and txPower is remained
    }

    public void updateData(List<WScanResult> updatedData){
        data.clear();
        data.addAll(updatedData);
        notifyDataSetChanged();
    }

    public void addOneData(WScanResult result){
        data.add(result);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView macAddress;
        private TextView rssi;
        private TextView txpower;
        private TextView major;
        private TextView minor;
        private TextView distance;
        private TextView timeStampNano;
        private LinearLayout row;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            macAddress = itemView.findViewById(R.id.macAddress);
            rssi = itemView.findViewById(R.id.rssi);
            txpower = itemView.findViewById(R.id.txPower);
            timeStampNano = itemView.findViewById(R.id.timeStampNano);
            major = itemView.findViewById(R.id.major);
            minor = itemView.findViewById(R.id.minor);
            distance = itemView.findViewById(R.id.distance);
            row = itemView.findViewById(R.id.row);
        }
    }
}
