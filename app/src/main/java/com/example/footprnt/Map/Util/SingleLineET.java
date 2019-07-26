package com.example.footprnt.Map.Util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SingleLineET implements TextWatcher {
    EditText et;

    public SingleLineET(EditText et){
        this.et = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String converted = s.toString();
        if(converted.contains("\n")){
            //There are occurences of the newline character
            converted = converted.replace("\n", "");
            et.setText(converted);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}