package com.dama.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TableLayout;
import com.dama.controller.Controller;
import com.dama.customkeyboardbase.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyboardView extends TableLayout {
    private HashMap<Integer, KeyboardRowView> rows;

    public KeyboardView(Context context) {
        super(context);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initKeyboardView(HashMap<Integer, ArrayList<Key>> allKeys){
        rows = new HashMap<>();

        //create rows and bars
        for(int i=0; i< Controller.ROWS; i++){
            KeyboardRowView row = new KeyboardRowView(getContext());
            ArrayList<Key> keys = allKeys.get(i);
            Drawable key_drawable = getResources().getDrawable(R.drawable.key_background);
            for(int j=0; j<keys.size(); j++){
                KeyView kv = new KeyView(getContext(), key_drawable, keys.get(j).getLabel(), "#FBFBFB");
                kv.addKeyParams(keys.get(j).getCode(), false);
                row.addKeyView(kv);
            }
            rows.put(i, row);
            addView(row);
        }
    }

    public KeyView getKeyViewAtCell(Cell cell){
        return rows.get(cell.getRow()).getKeyView(cell.getCol());
    }

    public void destroyAll(){
        for(int i=0 ; i< rows.size(); i++)
            removeView(rows.get(i));
    }
}
