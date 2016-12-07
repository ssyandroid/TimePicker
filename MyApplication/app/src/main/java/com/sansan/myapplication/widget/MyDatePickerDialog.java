/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sansan.myapplication.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.sansan.myapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple dialog containing .
 */
public class MyDatePickerDialog extends AlertDialog implements OnClickListener, NumberPicker.OnValueChangeListener, NumberPicker.Formatter {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final OnDateSetListener mDateSetListener;
    private final Calendar mCalendar;
    private final NumberPicker mYear;
    private final NumberPicker mMonth;
    private final NumberPicker mDay;
    private final Context context;
    private View view;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view        控件的View
         * @param dateStr     返回简单的日期字符串 yyyy-MM-dd
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *                    with {@link Calendar}.
         * @param dayOfMonth  The day of the month that was set.
         */
        void onDateSet(View view, String dateStr, int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * @param context     The context the dialog is to run in.
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public MyDatePickerDialog(Context context, String title,
                              OnDateSetListener callBack,
                              int year,
                              int monthOfYear,
                              int dayOfMonth) {
        this(context, title, callBack, null, year, monthOfYear, dayOfMonth);
    }

    /**
     * @param context  The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param dateStr  日期字符串 yyyy-MM-dd
     */
    public MyDatePickerDialog(Context context, String title,
                              OnDateSetListener callBack, String dateStr) {
        this(context, title, callBack, dateStr, -1, -1, -1);
    }

    /**
     * @param context     The context the dialog is to run in.
     * @param title       the title
     * @param listener    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public MyDatePickerDialog(Context context, String title, OnDateSetListener listener, String dateStr, int year,
                              int monthOfYear, int dayOfMonth) {
        super(context);

        this.context = context;

        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        mDateSetListener = listener;

        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

        if (year < 0 || monthOfYear < 0 || dayOfMonth < 0) {
            if (!TextUtils.isEmpty(dateStr)) {
                try {
                    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateformat.parse(dateStr);
                    mCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            year = mCalendar.get(Calendar.YEAR);
            monthOfYear = mCalendar.get(Calendar.MONTH);
            dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        }

        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        mYear = (NumberPicker) view.findViewById(R.id.year);
        mMonth = (NumberPicker) view.findViewById(R.id.month);
        mDay = (NumberPicker) view.findViewById(R.id.day);

        //年
        mYear.setMaxValue(2100);
        mYear.setMinValue(1900);
        mYear.setOnLongPressUpdateInterval(100);
        mYear.setFocusable(true);
        mYear.setFocusableInTouchMode(true);
        mYear.setFormatter(this);
        mYear.setValue(year);
        mYear.setOnValueChangedListener(this);

        //月
        mMonth.setMaxValue(12);
        mMonth.setMinValue(1);
        mMonth.setFocusable(true);
        mMonth.setFocusableInTouchMode(true);
        mMonth.setFormatter(this);
        mMonth.setValue(monthOfYear + 1);
        mMonth.setOnValueChangedListener(this);

        //日
        mDay.setMaxValue(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDay.setMinValue(1);
        mDay.setOnLongPressUpdateInterval(200);
        mDay.setFocusable(true);
        mDay.setFocusableInTouchMode(true);
        mDay.setFormatter(this);
        mDay.setValue(dayOfMonth);
        mDay.setOnValueChangedListener(this);
    }


    @Override
    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        // take care of wrapping of days and months to update greater fields
        if (picker == mDay) {
            int maxDayOfMonth = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            if (oldVal == maxDayOfMonth && newVal == 1) {
                mCalendar.add(Calendar.DAY_OF_MONTH, 1);
            } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                mCalendar.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                mCalendar.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
            }
        } else if (picker == mMonth) {
            if (oldVal == 12 && newVal == 1) {
                mCalendar.add(Calendar.MONTH, 1);
            } else if (oldVal == 1 && newVal == 12) {
                mCalendar.add(Calendar.MONTH, -1);
            } else {
                mCalendar.add(Calendar.MONTH, newVal - oldVal);
            }
        } else if (picker == mYear) {
            mCalendar.set(Calendar.YEAR, newVal);
        } else {
            throw new IllegalArgumentException();
        }
        // now set the date to the adjusted one
        setDate(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));

//        if (picker == mYear) {
//            mCalendar.set(Calendar.YEAR, newVal);
//            mDay.setMaxValue(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        } else if (picker == mMonth) {
//            mCalendar.set(Calendar.MONTH, newVal - 1);
//            mDay.setMaxValue(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        } else if (picker == mDay) {
//            mCalendar.set(Calendar.DAY_OF_YEAR, newVal);
//        }
    }

    /**
     * 设置日期
     *
     * @param year       年
     * @param month      月
     * @param dayOfMonth 日
     */
    private void setDate(int year, int month, int dayOfMonth) {
        mCalendar.set(year, month, dayOfMonth);
        mDay.setMaxValue(mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDay.setValue(mCalendar.get(Calendar.DAY_OF_MONTH));
        mMonth.setValue(mCalendar.get(Calendar.MONTH) + 1);
        mYear.setValue(mCalendar.get(Calendar.YEAR));
    }

    /**
     * 设置年最小值
     *
     * @param minYear int
     */
    public void setMinYear(int minYear) {
        mYear.setMinValue(minYear);
    }

    /**
     * 设置年最大值
     *
     * @param maxYear int
     */
    public void setMaxYear(int maxYear) {
        mYear.setMaxValue(maxYear);
    }

    /**
     * 设置月最小值
     *
     * @param minMonth int
     */
    public void setMinMonth(int minMonth) {
        mMonth.setMinValue(minMonth);
    }

    /**
     * 设置月最大值
     *
     * @param maxMonth int
     */
    public void setMaxMonth(int maxMonth) {
        mMonth.setMaxValue(maxMonth);
    }

    /**
     * 设置日最小值
     *
     * @param minDay int
     */
    public void setMinDay(int minDay) {
        mMonth.setMinValue(minDay);
    }

    /**
     * 设置日最大值
     *
     * @param maxDay int
     */
    public void setMaxDay(int maxDay) {
        mMonth.setMaxValue(maxDay);
    }

    /**
     * 得到当前年份
     *
     * @return int
     */
    public int getYear() {
        return mCalendar.get(Calendar.YEAR);
    }

    /**
     * 得到月份
     *
     * @return int
     */
    public int getMonth() {
        return mCalendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 得到日份
     *
     * @return int
     */
    public int getDayOfMonth() {
        return mCalendar.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    String dateStr = format(getYear()) + "-" + format(getMonth()) + "-" + format(getDayOfMonth());
                    mDateSetListener.onDateSet(view, dateStr, getYear(), getMonth(), getDayOfMonth());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    /**
     * Sets the current date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mYear.setValue(year);
        mMonth.setValue(monthOfYear);
        mDay.setValue(dayOfMonth);
    }


    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, getYear());
        state.putInt(MONTH, getMonth());
        state.putInt(DAY, getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mYear.setValue(year);
        mMonth.setValue(month);
        mDay.setValue(day);
    }
}
