package com.outerspace.poclocalizeddates;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        StringBuilder sb = new StringBuilder();

        String dateStr = sdf.format(new Date());
        sb.append("Today is ").append(dateStr).append('\n');

        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            date = null;
            sb.append("Error parsing date").append('\n');
        }
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

        display.setText(sb.toString());

        feedback = findViewById(R.id.feedback);
        dateInput = findViewById(R.id.date_input);

        init();

        dateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                feedback.setText(String.format("start=%d before=%d count=%d", start, before, count));
                if(count <= 1) {
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
                    String textDisplay = new String(charDisplay);
                    dateInput.setText(textDisplay);
                    dateInput.setSelection(idx);
                    chooseColors(textDisplay, delimiter, charMask);
                }
            }

            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        });
    }

    private void chooseColors(String dateText, char delimiter, char[] mask) {
        if(dateText.length() == charMask.length && verifyDate(dateText.toString(), delimiter, charMask))
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
            return true;
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

}
