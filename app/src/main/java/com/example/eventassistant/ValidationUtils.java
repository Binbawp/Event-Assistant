package com.example.eventassistant;

import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/** Input validation rules shared by every form in the app. */
public final class ValidationUtils {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{7,15}$");

    private ValidationUtils() { }

    /** True only for a real calendar date written as DD/MM/YYYY (so 31/02/2026 is rejected). */
    public static boolean isValidDate(String date) {
        if (date == null || !DATE_PATTERN.matcher(date).matches()) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        format.setLenient(false);   // strict: do not roll 31/02 over to 03/03
        try {
            format.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** True for a 24-hour time written as HH:mm, e.g. 09:30. */
    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /** Accepts 7-15 digits with an optional leading + ; spaces, dashes and brackets are ignored. */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String digitsOnly = phone.replaceAll("[\\s\\-()]", "");
        return PHONE_PATTERN.matcher(digitsOnly).matches();
    }
}
