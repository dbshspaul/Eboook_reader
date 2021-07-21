package com.example.test.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utility {

    /**
     *
     * @param s
     * @param length
     * @apiNote Minimum abbreviation length should be 3.
     * @return
     */
    public static String abbreviate(String s, int length) {
        if (length<3){
            throw new IllegalArgumentException("Minimum abbreviation length should be 3.");
        }
        if (s.length()<=length){
            return s;
        }
        return s.substring(0, length - 3) + "...";
    }

    public static String digest(byte[] input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        byte[] result = md.digest(input);
        return bytesToHex(result);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
