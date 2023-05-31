package com.dama.controller;

import android.inputmethodservice.Keyboard;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import com.dama.utils.Cell;
import com.dama.utils.Key;
import com.dama.views.CursorSpaceView;
import com.dama.views.KeyboardView;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Controller {
    public static final int COLS = 10;
    public static final int ROWS = 5;
    public static final int INVALID_KEY = -1;
    public static final int HIDDEN_KEY = -3;
    public static final int SPACE_KEY = 32;

    private Cell focus;

    private HashMap<Integer, ArrayList<Key>> keys;
    private HashMap<Character, Cell> letterPositions;

    private CursorSpaceView cursorSpaceView;
    private KeyboardView keyboardView;

    /*********************************INIT*************************************/
    public Controller(Keyboard keyboard, CursorSpaceView cursorSpaceView, KeyboardView keyboardView) {
        this.keys = new HashMap<>();
        this.letterPositions = new HashMap<>();

        this.focus = new Cell(1,0);     //first focus on Q

        this.cursorSpaceView = cursorSpaceView;
        this.keyboardView = keyboardView;

        loadKeys(keyboard);
        drawKeyboard();
    }

    private void loadKeys(Keyboard keyboard){
        if(keys.size()==0){
            int cols = COLS;
            for(int i=0, w=0; i<ROWS; i++) {
                ArrayList<Key> rowKeys = new ArrayList<>();
                if(i == (ROWS-1)) cols = 7;
                for (int j = 0; j < cols; j++) {
                    Keyboard.Key k = keyboard.getKeys().get(getKeyIndex(new Cell(w, j)));
                    Key key = new Key(k.codes[0], k.label.toString(), null);
                    letterPositions.put(key.getLabel().charAt(0), new Cell(i, j));
                    rowKeys.add(key);
                }
                w++;
                this.keys.put(i, rowKeys);
            }
        }
    }

    private int getKeyIndex(Cell cell){
        return ((cell.getRow()*this.COLS))+cell.getCol();
    }

    private void drawKeyboard(){
        this.keyboardView.initKeyboardView(keys);
        this.cursorSpaceView.initCursorSpaceView(this.keyboardView.getKeyViewAtCell(focus));
    }

    /*********************************FOCUS*************************************/
    public Cell newFocus(int direction){
        Cell newCell = new Cell(0,0);
        switch (direction){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                newCell.setRow(this.focus.getRow());
                newCell.setCol(this.focus.getCol()-1);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                newCell.setRow(this.focus.getRow());
                newCell.setCol(this.focus.getCol()+1);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                int r1 = this.focus.getRow() - 1;
                int c1 = this.focus.getCol();
                //if i have to exit from space bar
                /*if(this.focus.getRow() == (ROWS-1) && this.focus.getCol()==3) {
                    c1 = 3; //in col 3 row
                }*/
                newCell.setRow(r1);
                newCell.setCol(c1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                int r2 = this.focus.getRow() + 1;
                int c2 = this.focus.getCol();
                //if i have to go to space bar
                if(this.focus.getRow() == (ROWS-2)  && (this.focus.getCol()>=4 && this.focus.getCol()<=6)) {
                    c2 = 3; //in col 3 row 4 there is space bar
                }
                newCell.setCol(c2);
                newCell.setRow(r2);
                break;
        }
        return newCell;
    }

    public boolean isNextFocusable(Cell newFocus, int direction){
        if(isFocusInRange(newFocus) && !(isInvalidKey(newFocus)) && !(isHiddenKey(newFocus))){
            if(direction == KeyEvent.KEYCODE_DPAD_LEFT || direction == KeyEvent.KEYCODE_DPAD_RIGHT){
                return focus.getRow() == newFocus.getRow();
            }else if (direction == KeyEvent.KEYCODE_DPAD_UP || direction == KeyEvent.KEYCODE_DPAD_DOWN) {
                if(newFocus.getRow()!= (ROWS-1))    //for space key not same column
                    return focus.getCol() == newFocus.getCol();
                else return true;
            }
        }
        return false;
    }

    /**
     * Check if the param "focus" is a valid focus in the keyboard
     * according to number of cols and rows
     * @param focus
     * @return if the param "focus" is valid
     */
    public boolean isFocusInRange(Cell focus) {
        return (focus.getCol() < COLS && focus.getRow() < ROWS && focus.isValidPosition());
    }

    /**
     * Check if a key in a specific cell is an hidden key.
     * An hidden key is a key not shown in the suggestion bar, so it is non accessible.
     * @param focus
     * @return if the key in cell focus is an hidden key.
     */
    public boolean isHiddenKey(Cell focus){
        Key key = null;
        if(focus.isValidPosition() && isFocusInRange(focus))
            key = keys.get(focus.getRow()).get(focus.getCol());
        return (key == null || key.getCode() == HIDDEN_KEY);
    }

    /**
     * Check if a key in a specific cell is an invalid key.
     * An Invalid key is a key without behaviour (no code, no label).
     * @param focus
     * @return if the key in cell focus is an invalid key.
     */
    private boolean isInvalidKey(Cell focus){
        Key key = keys.get(focus.getRow()).get(focus.getCol());
        return (key == null || key.getCode() == INVALID_KEY);
    }

    public Cell getFocus() {
        return focus;
    }

    public void setFocus(Cell newFocus) {
        moveFocusPosition(newFocus);
        this.focus = newFocus;
    }

    protected void moveFocusPosition(Cell newFocus){
        this.cursorSpaceView.moveCursor(this.keyboardView.getKeyViewAtCell(newFocus));
    }

    /*********************************OTHER*************************************/
    public ArrayList<Key> getKeysAtRow(int index){
        return keys.get(index);
    }

}
