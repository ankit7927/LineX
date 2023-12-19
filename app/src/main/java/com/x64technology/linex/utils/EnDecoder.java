package com.x64technology.linex.utils;

public class EnDecoder {
    public static int EncodeUserId(String userId) {
        int result = 0;
        for (char c : userId.toCharArray())
            result = (result << 5) + c;
        return Math.abs(result) % 100000000;
    }

    public static String DecodeUserId(int code) {
        StringBuilder builder = new StringBuilder();
        while (code > 0) {
            char c = (char) (code % 32);
            builder.insert(0, c);
            code >>= 5;
        }
        return builder.toString();
    }
}
