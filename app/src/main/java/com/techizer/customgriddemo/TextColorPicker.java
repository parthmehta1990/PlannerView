package com.techizer.customgriddemo;


import androidx.annotation.ColorInt;

public interface TextColorPicker {

    @ColorInt
    int getTextColor(WeekViewEvent event);

}
