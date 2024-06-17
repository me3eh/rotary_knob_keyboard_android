package com.dama.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TableLayout;
import androidx.core.content.ContextCompat;
import com.dama.controllers.Controller;
import com.dama.customkeyboardbase.R;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyboardView  extends TableLayout {
    private HashMap<Integer, KeyboardRowView> rows;
    private HashMap<Integer, KeyboardRowView> firstRows = new HashMap<>();;

    public KeyboardView(Context context) {
        super(context);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initKeyboardView(HashMap<Integer, ArrayList<Key>> allKeys){
        rows = new HashMap<>();
        String colorLabel = Utils.colorToString(ContextCompat.getColor(getContext(), R.color.label));
        Drawable keyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.key_background);
        Drawable hintDrawable = ContextCompat.getDrawable(getContext(), R.drawable.hint_background);
        Drawable firstHintDrawable = ContextCompat.getDrawable(getContext(), R.drawable.first_hint_background);
        Drawable hiddenKeyDrawable = ContextCompat.getDrawable(getContext(), R.drawable.key_hidden_background);

        //create rows and bars
//        for(int j=0; j<keys.size(); j++){
//            if(keys.get(j).getCode()==Controller.HIDDEN_KEY){
//                background = hiddenKeyDrawable;
//            }else {
//            }\
//        KeyboardRowView firstRow = new KeyboardRowView(getContext());
//        for(int i=0; i< 15; ++i){
//            KeyView kv = new KeyView(getContext(), hintDrawable, "dupa", colorLabel);
//                //            if(keys.get(j).getIcon()!=null)
//                //                kv.setIcon(keys.get(j).getIcon())
//            kv.addKeyParams(4000 + i, false);
//            firstRow.addKeyView(kv);
//            //        }
//            firstRows.put(4000 + i, firstRow);
//        }
//        addView(firstRow);

        for(int i = 0; i< Controller.ROWS; i++){
            KeyboardRowView row = new KeyboardRowView(getContext());
            ArrayList<Key> keys = allKeys.get(i);
//            if(i == 0){
////                for(int j=0; j<keys.size(); j++){
//                Drawable background;
////                if(keys.get(j).getCode()==Controller.HIDDEN_KEY){
////                    background = hiddenKeyDrawable;
////                }else {
//                background = keyDrawable;
////                }
//                KeyView kv = new KeyView(getContext(), background, "dupa", colorLabel);
////                if(keys.get(j).getIcon()!=null)
////                    kv.setIcon(keys.get(j).getIcon());
//                kv.addKeyParams(4000, false);
//                row.addKeyView(kv);
////                }
//            }
//            else{
            for(int j=0; j<keys.size(); j++){
                Drawable background;
                if(keys.get(j).getCode() == 4000){
                    background = firstHintDrawable;
                }
                else if(keys.get(j).getCode()>=4001){
                    background = hintDrawable;
                }else {
                    background = keyDrawable;
                }
                KeyView kv = new KeyView(getContext(), background, keys.get(j).getLabel(), colorLabel);
                if(keys.get(j).getIcon()!=null)
                    kv.setIcon(keys.get(j).getIcon());
                kv.addKeyParams(keys.get(j).getCode(), false);
                row.addKeyView(kv);
            }
//            }

            rows.put(i, row);
            addView(row);
        }
    }

    public KeyView getKeyViewAtCell(Cell cell){
        return rows.get(cell.getRow()).getKeyView(cell.getCol());
    }

    public void deleteAll(){
        //remove views
        for(int i=0; i<rows.size(); i++){
            removeView(rows.get(i));
        }
        rows = null;
    }
}
