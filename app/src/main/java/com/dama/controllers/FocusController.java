package com.dama.controllers;

import android.view.KeyEvent;

import com.dama.utils.Cell;

public class FocusController {
    private Cell currentFocus;
    private Cell previousFocus;

    public FocusController() {
        currentFocus = new Cell(0,0);
        previousFocus = new Cell(0,0);
    }

    public Cell calculateNewFocus(int arrowCode){
        Cell newCell = new Cell(0,0);
        int cols = 0;

        switch (arrowCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
                newCell.setRow(currentFocus.getRow());
                newCell.setCol(currentFocus.getCol()-1);

                if(newCell.getRow() == Controller.ROWS-1)
                    cols = Controller.COLS-4;
                else cols = Controller.COLS-1;
                if(newCell.getCol()<0){
                    newCell.setCol(cols);
                }

                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                newCell.setRow(currentFocus.getRow());
                newCell.setCol(currentFocus.getCol()+1);

                if(newCell.getRow() == Controller.ROWS-1)
                    cols = Controller.COLS-4;
                else cols = Controller.COLS-1;
                if(newCell.getCol()>cols){
                    newCell.setCol(0);
                }

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                newCell.setRow(currentFocus.getRow()+1);
                newCell.setCol(currentFocus.getCol());
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                newCell.setRow(currentFocus.getRow()-1);
                newCell.setCol(currentFocus.getCol());
                break;
        }
        return newCell;
    }

    public boolean isFocusInRange(Cell newFocus) {
        int cols = Controller.COLS;
        if(newFocus.getRow() == Controller.ROWS-1){
            cols = Controller.COLS-3;
        }
        return (newFocus.getCol() < cols && newFocus.getRow() < Controller.ROWS && newFocus.isValidPosition());
    }

    /*******************GETTERS/SETTERS********************/

    public Cell getCurrentFocus() {
        return currentFocus;
    }

    public void setCurrentFocus(Cell currentFocus) {
        this.currentFocus = currentFocus;
    }

    public Cell getPreviousFocus() {
        return previousFocus;
    }

    public void setPreviousFocus(Cell previousFocus) {
        this.previousFocus = previousFocus;
    }
}
