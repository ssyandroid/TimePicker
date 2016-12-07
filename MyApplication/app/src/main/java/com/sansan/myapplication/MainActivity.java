package com.sansan.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sansan.myapplication.widget.MyDatePickerDialog;
import com.sansan.myapplication.widget.MyTimePickerDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void date(View v) {
        new MyDatePickerDialog(this, "日期", new MyDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(View view, String dateStr, int year, int monthOfYear, int dayOfMonth) {
                        Toast.makeText(MainActivity.this, dateStr, Toast.LENGTH_LONG).show();
                    }
                }, null).show();
    }

    public void time(View v) {

        new MyTimePickerDialog(this, "时间", new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(View view, String time, int hourOfDay, int minute) {
                Toast.makeText(MainActivity.this, time, Toast.LENGTH_LONG).show();
            }
        }, null).show();
    }
}
