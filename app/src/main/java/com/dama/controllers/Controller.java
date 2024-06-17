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
            COLS = 13;
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
        Cell curFocus = focusController.getCurrentFocus();
        Cell newFocus = focusController.calculateNewFocus(code);

        switch (code){
            case KeyEvent.KEYCODE_DPAD_UP:
                //todo mod
                //last row behaviour focus
                if((curFocus.getRow() == ROWS-1)){
                    switch (curFocus.getCol()){
                        case 4:
                            newFocus.setCol(7);
                            break;
                        case 5:
                            newFocus.setCol(8);
                            break;
                        case 6:
                            newFocus.setCol(9);
                            break;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //last row behaviour focus
                if((curFocus.getRow() == ROWS-2)){
                    switch (curFocus.getCol()){
                        case 4:
                        case 5:
                        case 6:
                            newFocus.setCol(3);
                            break;
                        case 7:
                            newFocus.setCol(4);
                            break;
                        case 8:
                            newFocus.setCol(5);
                            break;
                        case 9:
                            newFocus.setCol(6);
                            break;
                    }
                }
                break;
        }

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
