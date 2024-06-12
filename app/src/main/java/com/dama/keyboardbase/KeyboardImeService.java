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
    private Cell hint1 = new Cell(0, 0);
    private Cell hint2 = new Cell(0, 1);
    private Cell hint3 = new Cell(0, 2);
    private Cell space_button = new Cell(0, SPACE_INDEX);
    private JavaServer js;
    Thread thread;
    Thread changeColor;

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
        Log.d("poszlo", "start");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("poszlo", "nowy_thread");
        Log.d("poszlo", String.valueOf(thread));

//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
//        WifiManager.MulticastLock lock = wifiManager.createMulticastLock("Log_Tag");
//        lock.acquire();

//        Log.d("poszlo", "ip_address" + ipAddress);
        return rootView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        previousKeyEnter = false;
        keyboardShown = true;
        controller.modifyKeyBackgroundColor(space_button, Color.BLACK);

        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d("poszlo", "less go");
                    js.run_method();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        keyboardShown = false;
        ic = null;
        thread.interrupt();
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyboardShown){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            DatagramPacket sendPacket;

            ic = getCurrentInputConnection();
            if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                if(previousKeyEnter)
                    hideKeyboard();
            }
            switch (keyCode){
                case KeyEvent.KEYCODE_1:
                    Log.d("MOD 1 QWERTY","selected");
                    break;
                case KeyEvent.KEYCODE_2:
                    Log.d("MOD 2 ABC","selected");
                    break;
                case KeyEvent.KEYCODE_BACK:
                    hideKeyboard();
                    if(KEYBOARD_VERSION == 3) {
                        KEYBOARD_VERSION = 4;
                        space_button = new Cell(0, 11);
                    }
                    else {
                        KEYBOARD_VERSION = 3;
                        space_button = new Cell(0, 13);
                    }
//                    KEYBOARD_VERSION = KEYBOARD_VERSION == 3 ? 4 : 3;
//                    SPACE_INDEX = KEYBOARD_VERSION == 3 ? 13 : 11;
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
                    Cell newCell = controller.findNewFocus(keyCode);
                    if (controller.isNextFocusable(newCell)){
                        //update focus
                        controller.getFocusController_().setCurrentFocus(newCell);
                        controller.moveFocusOnKeyboard(newCell);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
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
                    break;
            }
            return true;
        }
        return false;
    }

    private void handleText(int code, InputConnection ic){
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
    final char[][] threeRowKeboard = {
            {'q', 'a', 'z'},
            {'w', 's', 'x'},
            {'e', 'd', 'c'},
            {'r', 'f', 'v'},
            {'t', 'g', 'b'},
            {'y', 'h', 'n'},
            {'u', 'j', 'm'},
            {'i', 'k', ','},
            {'o', 'l', '.'},
            {'p', ';', '/'},
    };
    final char[][] fourRowKeboard = {
            {'q', 'a', 'z', 'w'},
            {'s', 'x', 'e', 'd'},
            {'c', 'r', 'f', 'v'},
            {'t', 'g', 'b', 'y'},
            {'h', 'n', 'u', 'j'},
            {'m', 'i', 'k', 'o'},
            {'p', 'l', ',', '.'},
            {';', '/', '?', '!'},
    };

    String wholeWord = "";
    int howManyTimesStroked = 0;

    int valueOfpreviousKey = 0;
    int timesPreviousKey = -1;
    public int msElapsed;
    public boolean isRunning = false;
    long base_time = 0;
    int difference_in_time = 0;
    public void onText(int code) {
//        InputConnection inputConnection = getCurrentInputConnection();
        if (ic == null){
            return;
        }
//        int indexOfKeyboardKey = code;
        if (code == 1002){
            ic.deleteSurroundingText(1, 0);
            wholeWord = removeLastChar(wholeWord);
            setSpaceButtonToBlack();
//            updateSuggestions(wholeWord);
            return;
        }
        if (code == 1003){
            setSpaceButtonToBlack();
            previousKeyEnter = true;
            return;
        }
        if (code == 1001){
            setSpaceButtonToBlack();
            ic.commitText(String.valueOf(' '),1);
            wholeWord = "";
            return;
        }
        if (code == 2001) {
            setSpaceButtonToBlack();
            String value = controller.getLabelAtPosition(hint1);
            commitSuggestion(value);
            emptySuggestions();
            return;
        }
        if (code == 2002) {
            setSpaceButtonToBlack();
            String value = controller.getLabelAtPosition(hint2);
            commitSuggestion(value);
            emptySuggestions();
            return;
        }
        if (code == 2003) {
            setSpaceButtonToBlack();
            String value = controller.getLabelAtPosition(hint3);
            commitSuggestion(value);
            emptySuggestions();
            return;
        }
        difference_in_time = (int)(SystemClock.elapsedRealtime() - base_time);
        base_time = SystemClock.elapsedRealtime() - msElapsed;
        if(code == valueOfpreviousKey && ((difference_in_time / 1000) < 2) ){
            timesPreviousKey += 1;
            timesPreviousKey = timesPreviousKey % KEYBOARD_VERSION;
            ic.deleteSurroundingText(1, 0);
            wholeWord = removeLastChar(wholeWord);
        }
        else{
            timesPreviousKey = 0;
            valueOfpreviousKey = code;
        }
        char key;
        if(KEYBOARD_VERSION == 3)
            key = threeRowKeboard[code][timesPreviousKey];
        else
            key = fourRowKeboard[code][timesPreviousKey];

        wholeWord += key;
        // Odczytaj wprowadzoną literę
//        char keyChar = charSequence.charAt(0);
        ic.commitText(String.valueOf(key),1);
        if(thread.isAlive())
            thread.interrupt();
        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    controller.modifyKeyBackgroundColor(space_button, Color.RED);
                    Thread.sleep(2 * 1000);
                    controller.modifyKeyBackgroundColor(space_button, Color.BLACK);
                }catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                    Log.d("poszlo", "zatrzymano proces");
                }
            }
        });
        thread.start();
        dictionarySuggestions.updateSuggestions(wholeWord);
        String[] suggestions = dictionarySuggestions.getSuggestions();
        controller.modifyKeyContent(hint1, suggestions[0]);
        controller.modifyKeyContent(hint2, suggestions[1]);
        controller.modifyKeyContent(hint3, suggestions[2]);
    }

    public void setSpaceButtonToBlack(){
        if(thread.isAlive())
            thread.interrupt();
        controller.modifyKeyBackgroundColor(space_button, Color.BLACK);
    }
    private void commitSuggestion(String suggestion) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.deleteSurroundingText(wholeWord.length(), 0);
            wholeWord = "";
            inputConnection.commitText(suggestion, 1);
            inputConnection.commitText(" ", 1);
        }
    }

    private void emptySuggestions(){
        controller.modifyKeyContent(hint1, "");
        controller.modifyKeyContent(hint2, "");
        controller.modifyKeyContent(hint3, "");
    }
    private String removeLastChar(String str) {
        if(str != null && !str.trim().isEmpty())
            return str.substring(0, str.length() - 1);

        return "";
    }
    private void hideKeyboard(){
        requestHideSelf(0); //calls onFinishInputView
    }}

