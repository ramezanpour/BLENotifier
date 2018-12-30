package com.example.blenotifier;


public class Parser {

    public static String byteArrayToHex(byte[] a, int pos, int len, char sep) {
        StringBuilder sb = new StringBuilder(len * 2);
        int i;
        if (sep == 0) {
            for(i = pos; i < pos + len && i < a.length; ++i) {
                sb.append(String.format("%02X", a[i] & 255));
            }
        } else {
            for(i = pos; i < pos + len && i < a.length; ++i) {
                sb.append(String.format("%02X%c", a[i] & 255, sep));
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }

        return sb.toString();
    }

    public static String byteArrayToHex(byte[] a, char sep) {
        return byteArrayToHex(a, 0, a.length, sep);
    }
}

