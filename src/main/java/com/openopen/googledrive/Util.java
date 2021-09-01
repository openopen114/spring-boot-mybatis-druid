package com.openopen.googledrive;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

    public String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();

        String dateResult = simpleDateFormat.format(date);
        StringBuffer sb = new StringBuffer();
        dateResult = sb.append("(").append(dateResult).append(")").toString();

        return dateResult;
    }
}
