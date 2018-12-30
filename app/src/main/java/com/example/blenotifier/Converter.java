package com.example.blenotifier;

import java.util.Locale;

public class Converter {


    public static WScanResult parseBeaconData(byte[] data, int rssi,long timeStampNano,String macAddress) {
        try {
            if (data != null && data.length != 0) {
                StringBuilder uuid = new StringBuilder();
                String name = "";
                int major = 0;
                int minor = 0;
                int power = 0;
                int battery = 0;
                boolean ok = false;

                int len;
                for (int n = 0; n < data.length; n += len + 1) {
                    len = data[n] & 255;
                    if (len == 0) {
                        break;
                    }

                    if (n + len >= data.length) {
                        ok = false;
                        break;
                    }

                    int type = data[n + 1] & 255;
                    int vendor;
                    switch (type) {
                        case 22:
                            if (len >= 10) {
                                vendor = (data[n + 2] & 255) * 256 + (data[n + 3] & 255);
                                switch (vendor) {
                                    case 3536:
                                        name = new String(data, n + 4, 4);
                                        battery = data[n + 10] & 255;
                                        if (battery > 100) {
                                            battery = 0;
                                        }
                                        break;
                                    case 27390:
                                        if (len >= 13) {
                                            name = new String(data, n + 9, 4);
                                            battery = data[n + 8] & 255;
                                            if (battery > 100) {
                                                battery = 0;
                                            }
                                        }
                                        break;
                                    case 53456:
                                        if (len >= 12) {
                                            battery = (data[n + 11] & 255) + (data[n + 12] & 255) * 256;
                                            battery = (battery - 255) * 100 / 256;
                                            battery = Math.max(Math.min(battery, 100), 0);
                                        }
                                }
                            }
                            break;
                        case 255:
                            if (len >= 26) {
                                vendor = (data[n + 2] & 255) + (data[n + 3] & 255) * 256;
                                if (vendor == 76 || vendor == 89) {
                                    uuid.append(Parser.byteArrayToHex(data, n + 6, 4, '\u0000'));
                                    uuid.append("-");
                                    uuid.append(Parser.byteArrayToHex(data, n + 10, 2, '\u0000'));
                                    uuid.append("-");
                                    uuid.append(Parser.byteArrayToHex(data, n + 12, 2, '\u0000'));
                                    uuid.append("-");
                                    uuid.append(Parser.byteArrayToHex(data, n + 14, 2, '\u0000'));
                                    uuid.append("-");
                                    uuid.append(Parser.byteArrayToHex(data, n + 16, 6, '\u0000'));
                                    major = (data[n + 22] & 255) * 256 + (data[n + 23] & 255);
                                    minor = (data[n + 24] & 255) * 256 + (data[n + 25] & 255);
                                    power = (data[n + 26] & 255) - 256;
                                    ok = true;
                                }
                            }
                    }
                }

                if (ok && uuid.length() == 36) {
                    WScanResult result = new WScanResult();
                    result.type = 3;
                    result.BSSID = String.format(Locale.ENGLISH, "(%05d,%05d,%s)", major, minor, uuid.toString());
                    result.SSID = name;
                    result.level = rssi;
                    result.power = power < 0 && power >= -127 ? power : 0;
                    result.battery = battery;
                    result.distance = calculateBeaconDistance(rssi, result.power);
                    result.time = timeStampNano;
                    result.macAddress = macAddress;
                    return result;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Throwable var16) {
            return null;
        }
    }


    private static float calculateBeaconDistance(int rssi, int power) {
        if (power == 0) {
            power = -72;
        }
        return (float) Math.pow(10.0D, (double) (power - rssi) / 20.0D);
    }
}
