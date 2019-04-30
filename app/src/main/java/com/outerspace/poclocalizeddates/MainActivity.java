package com.outerspace.poclocalizeddates;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView display;

    EditText dateInput;
    TextView feedback;

    char[] charPattern;
    char[] charDisplay;
    char[] charMask;
    char delimiter;

    DateFormat sdf = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.FRENCH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        dateInput = findViewById(R.id.date_input);
        feedback = findViewById(R.id.feedback);    }

    @Override
    protected void onStart() {
        super.onStart();

        display.setText(exerciseWithDate(new Date()).toString());

        init();
        dateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                feedback.setText(String.format("start=%d before=%d count=%d", start, before, count));
                String textDisplay = "";
                if(count <= 1 && start < charMask.length) {
                    int idx = start;
                    if(count == 1) {
                        while(idx < charMask.length && charMask[idx] != '*') { idx++; }
                        charDisplay[idx] = text.charAt(start);
                        idx++;
                    }
                    if(count == 0 && before == 1) {
                        while(idx >= 0 && charMask[idx] != '*') { idx--; }
                        charDisplay[idx] = charPattern[idx];
                    }
                    textDisplay = new String(charDisplay);
                    dateInput.setText(textDisplay);
                    dateInput.setSelection(idx);
                } else if(start >= charMask.length) {
                    textDisplay = text.subSequence(0, charMask.length).toString();
                    dateInput.setText(textDisplay);
                    dateInput.setSelection(charMask.length);
                }
                chooseColors(textDisplay, delimiter, charMask);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void chooseColors(String dateText, char delimiter, char[] mask) {
        if(dateText.length() == mask.length && verifyDate(dateText.toString(), delimiter, mask))
            dateInput.setBackgroundColor(getColor(R.color.colorItsADate));
        else
            dateInput.setBackgroundColor(getColor(R.color.colorNotADate));
    }

    private boolean verifyDate(String dateText, char delimiter, char[] mask) {
        if(dateText.length() != mask.length)
            return false;
        if(! dateText.matches(
                "[0-9]+#[0-9]+#[0-9]+".replace('#', delimiter)))
            return false;
        try {
            Date date = sdf.parse(dateText);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return (cal.get(Calendar.YEAR) > 1900 && date.before(new Date()));
        } catch (ParseException e) {
            return false;
        }
    }

    private void init() {
        String inputMask = getString(R.string.date_input_mask);
        charPattern = inputMask.toCharArray();
        charDisplay = inputMask.toCharArray();
        charMask = getString(R.string.date_input_mask)
                .replaceAll("[A-Za-z]", "*").toCharArray();
        delimiter = inputMask.replaceAll("[A-Za-z]", "").charAt(0);
        dateInput.setText(inputMask);
    }

    public void onClickButtonReset(View view) {
        init();
    }

    private StringBuilder exerciseWithDate(Date date) {
        String dateStr = sdf.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("Today is ").append(dateStr).append('\n');

        if (date != null) {
            sb.append("day:").append(new SimpleDateFormat("dd", Locale.getDefault()).format(date)).append('\n');
            sb.append("month:").append(new SimpleDateFormat("MM", Locale.getDefault()).format(date)).append('\n');
            sb.append("year:").append(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date)).append('\n');
        }

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
        String pattern = ((SimpleDateFormat) formatter).toPattern();
        String lPattern= ((SimpleDateFormat) formatter).toLocalizedPattern();

        sb.append("Pattern: ").append(pattern).append('\n');
        sb.append("Local Pattern: ").append(lPattern).append('\n');
        return sb;
    }

}
