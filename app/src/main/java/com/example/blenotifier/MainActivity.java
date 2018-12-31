package com.example.blenotifier;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;

    BluetoothAdapter bluetoothAdapter;
    private HashMap<String, WScanResult> mScanResults;
    private BtleScanCallback mScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = getApplicationContext();


        adapter = new DeviceAdapter(new ArrayList<WScanResult>(),context);
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        switch (requestCode) {
            case 101:
                if (!permissionLocation)
                    Toast.makeText(context, "FAILED! ... reopen the application and accept the location access", Toast.LENGTH_LONG).show();
                else {
                    initView();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 210:
                    startScanBluetooth();
                    break;
            }
        }
    }

    private void initView() {
        Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show();
        BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();


        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 210);
        }else {
            startScanBluetooth();
        }
    }

    private void startScanBluetooth() {
        Toast.makeText(context, "Bluetooth is on ...good", Toast.LENGTH_SHORT).show();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build();


        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback();

        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        ScanFilter scanFilterKontact = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(Config.mUuidIbeaconKontact)).build();



        ScanFilter scanFilterSensoro = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(Config.mUuidIbeaconSensoro)).build();


        //filters.add(scanFilterKontact);
        //filters.add(scanFilterSensoro);


        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
    }




    private class BtleScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }
        @Override
        public void onScanFailed(int errorCode) {
            Log.e("SCAN_FAILED", "BLE Scan Failed with code " + errorCode);
        }
        private void addScanResult(ScanResult result) {
            if (result.getScanRecord()!=null) {
                WScanResult wScanResult = Converter.parseBeaconData(result.getScanRecord().getBytes(), result.getRssi(),result.getTimestampNanos(),result.getDevice().getAddress());
                if (wScanResult!=null) {
                    //checking the Sensoro ibeacon uuid
                    String s2 = wScanResult.BSSID.split(",")[2];
                    String s = s2.substring(0, s2.length() - 1);
                    if (s.equals("23A01AF0-232A-4518-9C0E-323FB773F5EF")) {
                        BluetoothDevice device = result.getDevice();
                        String deviceAddress = device.getAddress();
                        mScanResults.put(deviceAddress, wScanResult);
                        List<WScanResult> list = new ArrayList<>(mScanResults.values());
                        adapter.updateData(list);
                    }
                }
            }
        }
    }

}
