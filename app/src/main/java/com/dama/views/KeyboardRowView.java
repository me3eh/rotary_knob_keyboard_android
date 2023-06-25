package com.dama.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableRow;

import java.util.ArrayList;

public class KeyboardRowView extends TableRow {
    private ArrayList<KeyView> keyViews;
    public KeyboardRowView(Context context) {
        super(context);
        initRow();
    }

    public KeyboardRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRow();
    }

    private void initRow(){
        keyViews = new ArrayList<>();
    }

    public void addKeyView(KeyView keyView){
        keyViews.add(keyView);
        addView(keyView);
    }

    public KeyView getKeyView(int index){
        return keyViews.get(index);
    }


    public void clearBar(){
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            KeyView child = keyViews.get(i);
            removeView(child);
        }
        keyViews.clear();
    }
}
