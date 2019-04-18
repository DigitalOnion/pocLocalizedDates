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

    String dateDisplayPattern;
    String dateInputMask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Calendar now = Calendar.getInstance(Locale.FRANCE);
//        String displayDate = now.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.SHORT, Locale.FRANCE);

        StringBuilder sb = new StringBuilder();

        DateFormat sdf = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, Locale.FRENCH);
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

        dateDisplayPattern = getString(R.string.date_input_mask);
        dateInputMask = dateDisplayPattern.replaceAll("[A-Za-z]", "*");
        char[] charPattern = dateDisplayPattern.toCharArray();
        char[] charMask = dateInputMask.toCharArray();

        dateInput.setText(dateDisplayPattern);
        dateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                feedback.setText(String.format("start=%d before=%d count=%d", start, before, count));
                if(count == 1) {
                    int idx = start;
                    while(idx < charMask.length && charMask[idx] != '*') { idx++; }
                    charPattern[idx] = text.charAt(start);
                    dateInput.setText(new String(charPattern));
                    dateInput.setSelection(++idx);
                }
                if(count == 0 && before == 1) {

                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });


    }



}
