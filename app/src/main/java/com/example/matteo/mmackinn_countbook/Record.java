package com.example.matteo.mmackinn_countbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by matteo on 2017-09-25.
 */

public class Record {
    public static final String DATE_FORMAT = "yyyy'-'MM'-'dd";
    public String name;
    public String comment;
    public int init;
    public int count;
    public Date cDate;

    public String fDate;

    public Record(String n) {
        // Field variables
        SimpleDateFormat strDate;
        name = n;
        cDate = new Date();
        comment = "";
        init = 0;
        count = 0;

        // Based on https://www.tutorialspoint.com/java/java_date_time.htm
        // on Sept 25, 2017
        strDate = new SimpleDateFormat(DATE_FORMAT);
        fDate = strDate.format(cDate);

    }

    // Updates date to current value
    // Also based on https://www.tutorialspoint.com/java/java_date_time.htm
    // on Sept 25, 2017
    public void upDate() {
        cDate = new Date();
        SimpleDateFormat strDate;
        strDate = new SimpleDateFormat(DATE_FORMAT);
        fDate = strDate.format(cDate);
    }

    @Override
    public String toString() {
        return name;
    }


}
