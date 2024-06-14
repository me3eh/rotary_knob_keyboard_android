package com.dama.keyboardbase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.content.res.Configuration;

import com.dama.controllers.Controller;
import com.dama.customkeyboardbase.R;

import com.dama.log.TemaImeLogger;
import com.dama.utils.Cell;
import com.dama.utils.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.net.*;
import java.util.Scanner;
public class KeyboardImeService extends InputMethodService {
    private boolean previousKeyEnter = false;
    private int KEYBOARD_VERSION = 4;
    private int SPACE_INDEX = 11;
//    private int SPACE_INDEX_3 = 13;
    private Controller controller;
    private DictionarySuggestions dictionarySuggestions;
    private boolean keyboardShown;
    private InputConnection ic;
    private FrameLayout rootView;
    private TemaImeLogger temaImeLogger;
    private boolean longPress = false;
//    private Cell hint1 = new Cell(0, 0);
//    private Cell hint2 = new Cell(0, 1);
//    private Cell hint3 = new Cell(0, 2);
//    private Cell space_button = new Cell(0, SPACE_INDEX);
//    private boolean
    boolean flag = false;

    boolean flag2 = false;
    private JavaServer js;
    Thread thread;
    Thread threadLongPress;
    List<String> suggestions = new ArrayList<String>();
    private int page = 0;
    private String wholeWord = "";

//    Thread changeColor;

    @Override
    public View onCreateInputView() {
        try {
            js = new JavaServer(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
        controller.drawKeyboard();
//        Color myWhite = new Color(255, 255, 254);
        temaImeLogger = new TemaImeLogger(getApplicationContext());
        dictionarySuggestions = new DictionarySuggestions(getResources());
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
            Cell focus = controller.getFocusController_().getCurrentFocus();
            int row = 0;
            if(focus.getRow() == 0)
                row = 1;
            Cell newCell = new Cell(row, 0);

            controller.getFocusController_().setCurrentFocus(newCell);
            controller.moveFocusOnKeyboard(newCell);
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
                    Log.d("MOD 1 QWERTY","selected");
                    KEYBOARD_VERSION = 4;
                    rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
                    controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
                    controller.drawKeyboard();
                    setInputView(rootView);
                    break;
                case KeyEvent.KEYCODE_2:
                    Log.d("MOD 2 ABC","selected");
                    KEYBOARD_VERSION = 3;
                    rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
                    controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
                    controller.drawKeyboard();
                    setInputView(rootView);

                    break;
                case KeyEvent.KEYCODE_BACK:

                    if(KEYBOARD_VERSION == 3){
                        KEYBOARD_VERSION = 4;
                    }
                    else{
                        KEYBOARD_VERSION = 3;
                    }
                    rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
                    controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);

                    controller.drawKeyboard();
                    setInputView(rootView);
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
                Log.d("dalej", "nacisnieto");
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
            Cell hint = new Cell(0, code - 4000);
            String value = controller.getLabelAtPosition(hint);
            commitSuggestion(value);
            Cell newCell = new Cell(1, 0);
            controller.getFocusController_().setCurrentFocus(newCell);
            controller.moveFocusOnKeyboard(newCell);

            wholeWord = "";
            page = 1;
            emptySuggestions();
            return;
        }
        if (code == 1002){
            wholeWord = removeLastChar(wholeWord);
            suggestions = dictionarySuggestions.getWords(wholeWord, KEYBOARD_VERSION);
            updateSuggestions(suggestions, 1);
            page = 1;
            return;
        }
        if (code == 1003){
            previousKeyEnter = true;
            return;
        }
        if (code == 1001){
            ic.commitText(String.valueOf(' '),1);
            wholeWord = "";
            page = 1;
            return;
        }

        wholeWord += code;
        suggestions = dictionarySuggestions.getWords(wholeWord, KEYBOARD_VERSION);
        updateSuggestions(suggestions, 1);
    }

    public void emptySuggestions(){
        int max = 12;
        if(KEYBOARD_VERSION ==4){
            max = 10;
        }

        for(int i = 0; i < max; i++) {
            Cell hintCell = new Cell(0, i);
            controller.modifyKeyContent(hintCell, "");
        }
    }

    public void updateSuggestions(List<String> suggestions, int page){
        int max = Controller.COLS;
        int maxWordInRow = Controller.COLS;
        int previousSuggestionRange = (page - 1) * maxWordInRow;
        int suggestionRange = page * maxWordInRow;
//        max = page * maxWordInRow;
        Log.d("miedzy", previousSuggestionRange + " " + suggestionRange);
        if(suggestions.size() < suggestionRange && suggestions.size() >= previousSuggestionRange)
            max = suggestions.size() - previousSuggestionRange;
        Log.d("miedzy max", String.valueOf(max));

        for(int i = 0; i < max; i++) {
            Cell hintCell = new Cell(0, i);
            controller.modifyKeyContent(hintCell, suggestions.get(i + previousSuggestionRange));
        }
        for(int i = max; i < maxWordInRow; ++i){
            Cell hintCell = new Cell(0, i);
            controller.modifyKeyContent(hintCell, "");
        }
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
    }}

