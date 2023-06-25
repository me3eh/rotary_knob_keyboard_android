package com.dama.controllers;

import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;

import com.dama.utils.Cell;
import com.dama.utils.Key;

import java.util.ArrayList;
import java.util.HashMap;

public class KeysController {
    private HashMap<Integer, ArrayList<Key>> keys;
    private HashMap<Character, Cell> keysPosition;

    public KeysController(Keyboard keyboard) {
        keys = new HashMap<>();
        keysPosition = new HashMap<>();
        loadKeys(keyboard);
    }


    /*******************ADD********************/

    private void loadKeys(Keyboard keyboard){
        if(keys.size()==0){
            int cols = Controller.COLS;
            for(int i=0, w=0; i<Controller.ROWS; i++) {
                ArrayList<Key> rowKeys = new ArrayList<>();
                if (i == (Controller.ROWS - 1)) cols = Controller.COLS - 3; //todo mod was 7
                for (int j = 0; j < cols; j++) {
                        Keyboard.Key k = keyboard.getKeys().get(getKeyIndex(new Cell(w, j)));
                        Key key = new Key(k.codes[0], k.label.toString(), k.icon);
                        //Log.d("mmmm","k: "+key+" - size cell: "+new Cell(w, m));
                        if(key.getLabel().length()>0)
                            keysPosition.put(key.getLabel().charAt(0), new Cell(i, j));
                        rowKeys.add(key);
                }
                w++;
                this.keys.put(i, rowKeys);
            }
        }
    }

    private int getKeyIndex(Cell cell){
        return ((cell.getRow()*Controller.COLS))+cell.getCol();
    }

    public void addKeyPosition(char character, Cell position){
        keysPosition.put(character, position);
    }

    /*******************GET KEY********************/

    public Cell getCharPosition(char character){
        return keysPosition.get(character);
    }

    public Key getCharToKey(char character){
        return getKeyAtPosition(getCharPosition(character));
    }

    public Key getKeyAtPosition(Cell position){
        return keys.get(position.getRow()).get(position.getCol());
    }

    public ArrayList<Key> getKeysAtRow(int row){
        return keys.get(row);
    }

    /*******************MODIFY KEYS********************/

    public void modifyKeyAtPosition(Cell position, int code, String label){
        if(position.isValidPosition()){
            getKeyAtPosition(position).setCode(code);
            getKeyAtPosition(position).setLabel(label);
        }
    }

    public void modifyIconKeyAtPosition(Cell position, Drawable icon){
        if(position.isValidPosition()){
            getKeyAtPosition(position).setIcon(icon);
        }
    }


    /*******************KEYS CHECK********************/

    public boolean isInvalidKey(Cell newFocus){
        Key key = getKeyAtPosition(newFocus);
        return (key == null || key.getCode() == Controller.INVALID_KEY);
    }

    public boolean isHiddenKey(Cell newFocus){
        Key key = getKeyAtPosition(newFocus);
        return (key == null || key.getCode() == Controller.HIDDEN_KEY);
    }

    /*******************GETTERS********************/

    public HashMap<Integer, ArrayList<Key>> getAllKeys(){
        return keys;
    }

}
