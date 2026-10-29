package com.example.eventassistant;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/** Small UI helpers used by several activities (avoids duplicating the picker code). */
public final class UiUtils {

    private UiUtils() { }

    /** Returns the trimmed text of a TextView/EditText, never null. */
    public static String text(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }

    /** Opens a date picker and writes the chosen date into the field as DD/MM/YYYY. */
    public static void showDatePicker(Context context, TextView target) {
        Calendar cal = Calendar.getInstance();
        String current = text(target);
        if (ValidationUtils.isValidDate(current)) {          // start on the date already in the field
            String[] parts = current.split("/");
            cal.set(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]) - 1,
                    Integer.parseInt(parts[0]));
        }
        new DatePickerDialog(context,
                (view, year, month, day) -> target.setText(
                        String.format(Locale.UK, "%02d/%02d/%04d", day, month + 1, year)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /** Opens a 24-hour time picker and writes the chosen time into the field as HH:mm. */
    public static void showTimePicker(Context context, TextView target) {
        Calendar cal = Calendar.getInstance();
        String current = text(target);
        if (ValidationUtils.isValidTime(current)) {
            String[] parts = current.split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        }
        new TimePickerDialog(context,
                (view, hour, minute) -> target.setText(
                        String.format(Locale.UK, "%02d:%02d", hour, minute)),
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
                .show();
    }
}
