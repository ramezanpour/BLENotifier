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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class DeviceAdapter  extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{

    private List<ScanResult> data;
    private Context context;

    public DeviceAdapter(List<ScanResult> data, Context context) {
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
        ScanResult model = data.get(i);

        viewHolder.macAdress.setText(model.getDevice().getAddress());
        viewHolder.rssi.setText(String.valueOf(model.getRssi()));
        if (model.getScanRecord()!=null) {

            int txPowerInt = model.getScanRecord().getTxPowerLevel();
            viewHolder.txpower.setText(String.valueOf(txPowerInt));
            if (txPowerInt == -12 || txPowerInt == -16 || txPowerInt == -20)
                viewHolder.row.setBackgroundColor(Color.parseColor("#ff0000"));
            else
                viewHolder.row.setBackgroundColor(Color.WHITE);

           // model.getScanRecord().getServiceUuids().get(0)
        }
        viewHolder.serviceName.setText(model.getDevice().getName());
        viewHolder.timeStampNano.setText(String.valueOf(model.getTimestampNanos()));





    }

    public void updateData(List<ScanResult> updatedData){
        data.clear();
        data.addAll(updatedData);
        notifyDataSetChanged();
    }

    public void addOneData(ScanResult result){
        data.add(result);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView macAdress;
        private TextView rssi;
        private TextView txpower;
        private TextView timeStampNano;
        private TextView serviceName;
        private LinearLayout row;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            macAdress = itemView.findViewById(R.id.macAdress);
            rssi = itemView.findViewById(R.id.rssi);
            txpower = itemView.findViewById(R.id.txPower);
            timeStampNano = itemView.findViewById(R.id.timeStampNano);
            serviceName = itemView.findViewById(R.id.serviceName);
            row = itemView.findViewById(R.id.row);
        }
    }
}
