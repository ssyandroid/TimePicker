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
import android.widget.TimePicker;

import com.sansan.myapplication.R;

import java.util.Calendar;


/**
 * 自定义选择时间对话框，仿4.0的时间选择框简单形式
 * A dialog that prompts the user for the time of day using a
 * {@link TimePicker}.
 * <p>
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 */
public class MyTimePickerDialog extends AlertDialog implements OnClickListener, NumberPicker.Formatter, NumberPicker.OnValueChangeListener {
    private static final String TAG = "MyTimePickerDialog";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    private final OnTimeSetListener mTimeSetListener;

    private NumberPicker mHour;
    private NumberPicker mMinute;
    private View view;

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (e.g. they clicked on the 'OK' button).
     */
    public interface OnTimeSetListener {
        /**
         * Called when the user is done setting a new time and the dialog has
         * closed.
         *
         * @param view      the view associated with this listener
         * @param time      返回已经组装好的时间，格式如12:02
         * @param hourOfDay 时 the hour that was set
         * @param minute    分 the minute that was set
         */
        public void onTimeSet(View view, String time, int hourOfDay, int minute);
    }

    /**
     * Creates a new time picker dialog.
     *
     * @param context  the parent context
     * @param listener the listener to call when the time is set
     * @param dateTime 时间比如12:01，一定要这个格式，如果为空则为默认当前时间
     */
    public MyTimePickerDialog(Context context, String title, OnTimeSetListener listener, String dateTime) {
        this(context, title, listener, dateTime, -1, -1);
    }

    /**
     * Creates a new time picker dialog.
     *
     * @param context   the parent context
     * @param listener  the listener to call when the time is set
     * @param hourOfDay the initial hour
     * @param minute    the initial minute
     */
    public MyTimePickerDialog(Context context, String title, OnTimeSetListener listener, int hourOfDay, int minute) {
        this(context, title, listener, null, hourOfDay, minute);
    }

    /**
     * Creates a new time picker dialog with the specified theme.
     *
     * @param context   the parent context
     * @param listener  the listener to call when the time is set
     * @param dateTime  时间比如12:01，一定要这个格式，如果为空则为默认当前时间
     * @param hourOfDay the initial hour
     * @param minute    the initial minute
     */
    private MyTimePickerDialog(Context context, String title, OnTimeSetListener listener, String dateTime, int hourOfDay, int minute) {
        super(context);

        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        mTimeSetListener = listener;

        if (hourOfDay < 0 || minute < 0) {
            if (TextUtils.isEmpty(dateTime)) {
                //初始化时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            } else {
                try {
                    hourOfDay = Integer.valueOf(dateTime.substring(0, 2));
                    minute = Integer.valueOf(dateTime.substring(3, 5));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        final Context themeContext = getContext();

        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        view = inflater.inflate(R.layout.time_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        mHour = (NumberPicker) view.findViewById(R.id.hour);
        mMinute = (NumberPicker) view.findViewById(R.id.minute);

        mHour.setMaxValue(23);
        mHour.setMinValue(0);
        mHour.setFocusable(true);
        mHour.setFocusableInTouchMode(true);
        mHour.setFormatter(this);
        mHour.setValue(hourOfDay);
        mHour.setOnValueChangedListener(this);

        mMinute.setMaxValue(59);
        mMinute.setMinValue(0);
        //设置分别长按向上和向下按钮时数字增加和减少的速度。默认值为300 ms
        mMinute.setOnLongPressUpdateInterval(100);
        mMinute.setFocusable(true);
        mMinute.setFocusableInTouchMode(true);
        mMinute.setFormatter(this);
        mMinute.setValue(minute);
        mMinute.setOnValueChangedListener(this);

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker == mHour) {
            //时发生变化时候，分不变
        } else if (picker == mMinute) {
            //分发生变化时候，如果是从最大到最小，时加一，最小到最大减一
            int minValue = mMinute.getMinValue();
            int maxValue = mMinute.getMaxValue();
            int newHour = mHour.getValue();
            if (oldVal == maxValue && newVal == minValue) {
                newHour = newHour + 1;
            } else if (oldVal == minValue && newVal == maxValue) {
                newHour = newHour - 1;
            }
            setValue(mHour, newHour);
        }
    }

    /**
     * 设置值，并且进行值的比较，如果值小于最小取最小，值大于最大取最大
     *
     * @param picker  NumberPicker
     * @param current 当前值
     */
    public void setValue(NumberPicker picker, int current) {
        if (picker.getValue() == current) {
            return;
        }
        current = Math.max(current, picker.getMinValue());
        current = Math.min(current, picker.getMaxValue());
        picker.setValue(current);
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
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mTimeSetListener != null) {
                    int hour = mHour.getValue();
                    int minute = mMinute.getValue();
                    String timeStr = format(hour) + ":" + format(minute);
                    mTimeSetListener.onTimeSet(view, timeStr, hour, minute);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    /**
     * Sets the current time.
     *
     * @param hourOfDay    The current hour within the day.
     * @param minuteOfHour The current minute within the hour.
     */
    public void updateTime(int hourOfDay, int minuteOfHour) {
        mHour.setValue(hourOfDay);
        mMinute.setValue(minuteOfHour);
    }

    @Override
    public Bundle onSaveInstanceState() {
        final Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mHour.getValue());
        state.putInt(MINUTE, mMinute.getValue());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int hour = savedInstanceState.getInt(HOUR);
        final int minute = savedInstanceState.getInt(MINUTE);
        mHour.setValue(hour);
        mMinute.setValue(minute);
    }


}
