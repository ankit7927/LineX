package com.x64technology.linex.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converter {
    public static String MillisToDateTime(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy hh:mm a", Locale.getDefault());
        Date date = new Date(millis);
        return sdf.format(date);
    }
}
