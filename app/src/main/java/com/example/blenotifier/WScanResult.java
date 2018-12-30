package com.example.blenotifier;


public class WScanResult implements Comparable<WScanResult> {
    public String SSID = "";
    public String BSSID = "";
    public int type = 0;
    public int level = 0;
    public int power = 0;
    public int battery = 0;
    public int frequency = 0;
    public float distance = 0.0F;
    public long time = 0L;
    public String macAddress;
    boolean real = true;

     WScanResult() {
    }


    public int compareTo(WScanResult result) {
        if (this.time < result.time) {
            return -1;
        } else if (this.time > result.time) {
            return 1;
        } else if (this.type < result.type) {
            return -1;
        } else {
            return this.type > result.type ? 1 : this.BSSID.compareTo(result.BSSID);
        }
    }
}

