package com.dama.controllers;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;

import com.dama.customkeyboardbase.R;

import com.dama.utils.Cell;

public class Controller {
    public static int COLS = 11;
    public static final int ROWS = 2;
    public static final int INVALID_KEY = -1;
    public static final int HIDDEN_KEY = -3;
    public static final int SPACE_KEY = 32;
    public static final int ENTER_KEY = 66;
    public static final int FAKE_KEY = -66;
    public static final int DEL_KEY = 24;
    private FocusController focusController;
    private KeysController keysController;
    private ViewsController viewsController;

    public Controller(Context context, FrameLayout rootView, int version) {
        if(version == 3) {
            COLS = 14;
            keysController = new KeysController(new Keyboard(context, R.xml.qwerty));
        }
        else{
            COLS = 11;
            keysController = new KeysController(new Keyboard(context, R.xml.qwerty4row));
        }
        focusController = new FocusController();
        focusController.setCurrentFocus(new Cell(1,0)); //q
        viewsController = new ViewsController(rootView);
        //viewsController.drawKeyboard(keysController.getAllKeys(), focusController.getCurrentFocus());
    }

    public void drawKeyboard(){

        Log.d("czeka1", String.valueOf(keysController.getAllKeys()));
        Log.d("ilosc: ", "ilosc: " + COLS + "");
        viewsController.drawKeyboard(keysController.getAllKeys(), focusController.getCurrentFocus());
    }

    /*********************FOCUS**********************/
    public boolean isNextFocusable(Cell newFocus){
        if(focusController.isFocusInRange(newFocus)
                && !(keysController.isInvalidKey(newFocus))
                && !(keysController.isHiddenKey(newFocus))){
            return true;
        }
        return false;
    }

    public Cell findNewFocus(int code){
        Cell newFocus = focusController.calculateNewFocus(code);

        return newFocus;
    }

    public void moveFocusOnKeyboard(Cell position){
        viewsController.moveCursorPosition(position);
    }


    /*********************GETTERS**********************/
    public FocusController getFocusController_() {
        return focusController;
    }

    public KeysController getKeysController() {
        return keysController;
    }

    /*********************OTHER**********************/
    public void modifyKeyContent(Cell position, String label){
        keysController.modifyKeyAtPosition(position, label);
        viewsController.modifyKeyLabel(position, label);
    }
    public String getLabelAtPosition(Cell position){
        return keysController.getLabelAtPosition(position);
    }

    public void modifyKeyBackgroundColor(Cell position, @ColorInt int color){
        viewsController.setBackgroundColor(position, color);
//        keysController.modifyKeyAtPosition(position, label);
//        viewsController.modifyKeyLabel(position, label);
    }
}
