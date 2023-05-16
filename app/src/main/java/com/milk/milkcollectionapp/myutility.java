package com.milk.milkcollectionapp;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

/**
 * Created by alfa on 7/30/2017.
 */

public final class myutility {
    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (byte b2 : b) {
            result = new StringBuilder(String.valueOf(result)).append(Integer.toString((b2 & 255) + 256, 16).substring(1)).toString();
        }
        return result;
    }

    public static String convertStringToHex(String str) {
        String str2 = "";
        byte[] chars = str.getBytes();
       // byte[] chars = (byte[]) str2;
        try {
            chars = str.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            str2 = getHexString(chars);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return str2;
    }

    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            int decimal = Integer.parseInt(hex.substring(i, i + 2), 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    public static final boolean ValidateIPAddress(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        int length = parts.length;
        int i = 0;
        while (i < length) {
            try {
                int i2 = Integer.parseInt(parts[i]);
                if (i2 < 0 || i2 > 255) {
                    return false;
                }
                i++;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
