package com.dama.keyboardbase;

import com.dama.database.DatabaseHelper;
import com.dama.database.DatabaseManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.InputMethodService;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;

import com.dama.controllers.Controller;
import com.dama.customkeyboardbase.R;

import com.dama.log.TemaImeLogger;
import com.dama.utils.Cell;
import com.dama.utils.Key;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KeyboardImeService extends InputMethodService {
    private boolean previousKeyEnter = false;
    private int KEYBOARD_VERSION = 4;
    private Controller controller;
    private DictionarySuggestions dictionarySuggestions;
    private boolean keyboardShown;
    private InputConnection ic;
    private FrameLayout rootView;
    private TemaImeLogger temaImeLogger;
    private boolean longPress = false;
    boolean flag = false;

    boolean flag2 = false;
    private JavaServer js;
    Thread thread;
    Thread threadLongPress;
    List<String> suggestions = new ArrayList<String>();
    private int page = 1;
    private String wholeWord = "";
    private SQLiteDatabase db;
//    Thread changeColor;

    private String previousString = "";
    private String[] keys3Line = {"q", "w", "e", "r", "t", "y", "u", "i"," o", "p"};
    private String[] keys4Line = {"q", "s", "c", "t", "h", "m", "p"};
    private int codeGlobal = 0;
    @Override
    public View onCreateInputView() {
        copyDatabaseFromAssetsToDevice();

        DatabaseManager.initializeDatabase(this);

        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        try {
            js = new JavaServer(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
        controller.drawKeyboard();
        temaImeLogger = new TemaImeLogger(getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (thread != null)
            if (thread.isAlive())
                thread.interrupt();
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d("ruszyla maszyna", "less go");
                    js.run_method();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();

        return rootView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        previousKeyEnter = false;
        keyboardShown = true;
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        keyboardShown = false;
        ic = null;
        wholeWord = "";
        previousString = "";
        page = 1;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && keyboardShown) {
            Log.d("Test", "Long press!");
            flag = false;
            flag2 = true;
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && keyboardShown) {
            event.startTracking();
            if (flag) {
                Log.d("Test", "Short");
                typeOnKeyboard(keyCode, event);
            }
            flag = true;
            flag2 = false;
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && keyboardShown) {
            event.startTracking();
            if (flag2 == true) {
                flag = false;
            } else {
                flag = true;
                flag2 = false;
            }

            return true;
        }
        if(keyboardShown) {
            steerOnKeyboard(keyCode, event);
            return true;
        }
        if (keyboardShown && keyCode == KeyEvent.KEYCODE_BACK){
            hideKeyboard();
            return true;
        }
        Log.d("steering", String.valueOf(keyCode));
        Log.d("steering", "keycode: " + String.valueOf(keyCode));
        Log.d("steering", "event: " + String.valueOf(event));
        return super.onKeyDown(keyCode, event);
    }

    public boolean steerOnKeyboard(int keyCode, KeyEvent event){
        if(keyboardShown){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ic = getCurrentInputConnection();

            if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                if(previousKeyEnter)
                    hideKeyboard();
            }
            switch (keyCode){
                case KeyEvent.KEYCODE_1:
                    wholeWord = "";
                    previousString = "";
                    Log.d("MOD 1 QWERTY","selected");
                    KEYBOARD_VERSION = 4;
                    rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
                    controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
                    controller.drawKeyboard();
                    setInputView(rootView);
                    break;
                case KeyEvent.KEYCODE_2:
                    Log.d("MOD 2 ABC","selected");
                    wholeWord = "";
                    previousString = "";
                    KEYBOARD_VERSION = 3;
                    rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
                    controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
                    controller.drawKeyboard();
                    setInputView(rootView);

                    break;
                case KeyEvent.KEYCODE_BACK:
                    hideKeyboard();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    temaImeLogger.writeToLog("LEFT",false);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    temaImeLogger.writeToLog("RIGHT",false);
                case KeyEvent.KEYCODE_DPAD_UP:
                    temaImeLogger.writeToLog("UP",false);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
                        temaImeLogger.writeToLog("DOWN",false);
                    }
                    Cell focus = controller.getFocusController_().getCurrentFocus();
                    Cell newCell = controller.findNewFocus(keyCode);
                    Log.d("aktualna pozycja", String.valueOf(focus));
                    Log.d("nastepna pozycja", String.valueOf(newCell));

                    if (controller.isNextFocusable(newCell)){
                        //update focus
                        controller.getFocusController_().setCurrentFocus(newCell);
                        controller.moveFocusOnKeyboard(newCell);
                    }
                    if(suggestions.size() > (Controller.COLS * page) &&
                            focus.getRow() == 0 &&
                            focus.getCol() == Controller.COLS - 1 &&
                            keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        page += 1;
                        updateSuggestions(suggestions, page);
                        Cell beginningOfHints = new Cell(0, 0);
                        controller.getFocusController_().setCurrentFocus(beginningOfHints);
                        controller.moveFocusOnKeyboard(beginningOfHints);
                    }
                    else if(page > 1 && focus.getRow() == 0 &&
                            focus.getCol() == 0 &&
                            keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                        page -= 1;
                        updateSuggestions(suggestions, page);
                        Cell endOfHints = new Cell(0, Controller.COLS - 1);
                        controller.getFocusController_().setCurrentFocus(endOfHints);
                        controller.moveFocusOnKeyboard(endOfHints);
                    }
                    break;
            }
        }
        return true;
    }

    public boolean typeOnKeyboard(int keyCode, KeyEvent event) {
        Log.d("dalej", String.valueOf(keyboardShown));
        if (keyboardShown){
            if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
                Cell focus = controller.getFocusController_().getCurrentFocus();
                Key key = controller.getKeysController().getKeyAtPosition(focus);

                String btn;
                if(key.getCode() == Controller.ENTER_KEY)
                    btn = "INVIO";
                else
                    btn = key.getLabel();
                temaImeLogger.writeToLog("CENTER: "+btn,false);

                int code = key.getCode();
                if(code!=Controller.FAKE_KEY){
                    handleText(code, ic);
                }
            }
        }
        return true;
    }

    private void handleText(int code, InputConnection ic){
        Log.d("dalej", String.valueOf(code));

        switch (code){
            case Controller.DEL_KEY:
                ic.deleteSurroundingText(1, 0);
                break;
            case Controller.ENTER_KEY:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            default: //user press letter, number, symbol
                onText(code);
        }
    }


    public void onText(int code) {
        if(code >= 4000 && code <= 4014){
            Cell hint = new Cell(0, code - 3999);
            String value = controller.getLabelAtPosition(hint);
            commitSuggestion(value);
            Cell newCell = new Cell(1, 0);
            controller.getFocusController_().setCurrentFocus(newCell);
            controller.moveFocusOnKeyboard(newCell);

            wholeWord = "";
            previousString = "";
            page = 1;
            emptySuggestions();
            return;
        }
        if(code == 3000 || code == 3001){
            Cell newCell = new Cell(code - 3000, 0);
            controller.getFocusController_().setCurrentFocus(newCell);
            controller.moveFocusOnKeyboard(newCell);
            return;
        }
        if (code == 1002){
            wholeWord = removeLastChar(wholeWord);
            previousString = removeLastChar(previousString);

            if(Objects.equals(wholeWord, "")) {
                ic.finishComposingText();
                ic.deleteSurroundingText(1, 0);
                emptySuggestions();
            }
            else{
                suggestionQuery();
                updateSuggestions(suggestions, 1);
            }
            page = 1;
            return;
        }
        if (code == 1003){
            wholeWord = "";
            previousString = "";
            previousKeyEnter = true;
            return;
        }
        if (code == 1001){
            Cell firstHint = new Cell(0, 1);
            String value = controller.getLabelAtPosition(firstHint);
            commitSuggestion(value);
            wholeWord = "";
            page = 1;
            emptySuggestions();

            return;
        }

        wholeWord += code;
        codeGlobal = code;
        String[] line = keys4Line;
        if(KEYBOARD_VERSION == 3) {
            line = keys3Line;
        }
        previousString += line[codeGlobal];
        suggestionQuery();
        updateSuggestions(suggestions, 1);
    }

    public void suggestionQuery(){
        Cursor cursor;
        Log.d("wyraz", wholeWord);
        String column = "fourth_column";
        if(KEYBOARD_VERSION == 3)
            column = "three_column";

        if(wholeWord.length() <= 4) {
            String whereClauseWithLength = column + " LIKE ? " + " AND LENGTH(" + column + ") = " + wholeWord.length();
            Log.d("wyraz", whereClauseWithLength);
            cursor = db.query("dictionary", null, whereClauseWithLength, new String[]{wholeWord + "%"}, null, null, "LENGTH(name) ASC");
            Log.d("wyraz", "querka 4");
        }
        else {
            String whereClause = column + " LIKE ?";
            cursor = db.query("dictionary", null, whereClause, new String[]{wholeWord + "%"}, null, null, "LENGTH(name) ASC");
            Log.d("wyraz", "querka 3");
        }
        suggestions.clear();
        Log.d("wyraz", "wielkosc: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String value = cursor.getString(cursor.getColumnIndex("name"));
                suggestions.add(value);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void emptySuggestions(){
        int max = Controller.COLS;

        for(int i = 1; i < max; i++) {
            Cell hintCell = new Cell(0, i);
            controller.modifyKeyContent(hintCell, "");
        }
    }

    public void updateSuggestions(List<String> suggestions, int page) {
        int max = Controller.COLS;
        int maxWordInRow = Controller.COLS - 1;
        int previousSuggestionRange = (page - 1) * maxWordInRow;
        int suggestionRange = page * maxWordInRow;

        if (suggestions.size() < suggestionRange && suggestions.size() >= previousSuggestionRange)
            max = suggestions.size() - previousSuggestionRange + 1;
        Log.d("czasami", String.valueOf(max));
        if(max > 1) {
            previousString = suggestions.get(0);
            Log.d("czasami", previousString);
        }
        if (max > 1){
            for (int i = 1; i < max; i++) {
                Cell hintCell = new Cell(0, i);
                controller.modifyKeyContent(hintCell, suggestions.get(i + previousSuggestionRange - 1));
            }
            for (int i = max; i <= maxWordInRow; ++i) {
                Cell hintCell = new Cell(0, i);
                controller.modifyKeyContent(hintCell, "");
            }
        }
        else if(max == 1){
            emptySuggestions();
            Log.d("czasami", "weszlo");

            Cell hintCell = new Cell(0, 1);
            controller.modifyKeyContent(hintCell, previousString);
        }
        ic.setComposingText(previousString, previousString.length());
    }

    private void commitSuggestion(String suggestion) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
//            inputConnection.deleteSurroundingText(wholeWord.length(), 0);
//            wholeWord = "";
            inputConnection.commitText(suggestion, 1);
            inputConnection.commitText(" ", 1);
        }
    }

//    private void emptySuggestions(){
//        controller.modifyKeyContent(hint1, "");
//        controller.modifyKeyContent(hint2, "");
//        controller.modifyKeyContent(hint3, "");
//    }
    private String removeLastChar(String str) {
        if(str != null && !str.trim().isEmpty())
            return str.substring(0, str.length() - 1);

        return "";
    }
    private void hideKeyboard(){
        requestHideSelf(0); //calls onFinishInputView
    }
    private void copyDatabaseFromAssetsToDevice(){
        String dbName = "sample.db";
        InputStream inputStream;
        try {
            inputStream = getApplicationContext().getAssets().open(dbName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File dbFile = new File(getApplicationContext().getApplicationInfo().dataDir + "/databases/", dbName);
        try {
            OutputStream outputStream = new FileOutputStream(dbFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


