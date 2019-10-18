package com.judopay.samples.util;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BindingAdapters {
    @BindingAdapter({"dateFormat", "dateToFormat"})
    public static void formatDate(TextView textView, String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        textView.setText(simpleDateFormat.format(date));
    }
}
