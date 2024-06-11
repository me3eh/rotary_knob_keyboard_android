package com.dama.keyboardbase;

import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
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
    private Controller controller;
    private DictionarySuggestions dictionarySuggestions;
    private boolean keyboardShown;
    private InputConnection ic;
    private FrameLayout rootView;
    private TemaImeLogger temaImeLogger;
    private Cell hint1 = new Cell(0, 0);
    private Cell hint2 = new Cell(0, 1);
    private Cell hint3 = new Cell(0, 2);
    private JavaServer js;



    @Override
    public View onCreateInputView() {
        try {
            js = new JavaServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rootView = (FrameLayout) this.getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        controller = new Controller(getApplicationContext(), rootView, KEYBOARD_VERSION);
        controller.drawKeyboard();

        temaImeLogger = new TemaImeLogger(getApplicationContext());
        dictionarySuggestions = new DictionarySuggestions(getResources());
        Log.d("poszlo", "start");
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.d("poszlo", "less go");
                    js.run_method();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
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
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode2, KeyEvent event) {
        int keyCode = keyCode2;
        Log.d("klawisz nacisniety", String.valueOf(keyCode2));
//        int keyCode = keyCode2;
        if(keyboardShown){
            Log.d("klawisz", String.valueOf(keyCode));
            if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
            else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
            }
            else if(keyCode == KeyEvent.KEYCODE_VOLUME_MUTE)
                keyCode = KeyEvent.KEYCODE_DPAD_CENTER;
            Log.d("zmieniony klawisz", String.valueOf(keyCode));
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            DatagramPacket sendPacket;
            byte[] sendData;
            // Create a Datagram Socket
            DatagramSocket clientSocket = null;
            try {
                clientSocket = new DatagramSocket();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            // Set client timeout to be 1 second
            try {
                clientSocket.setSoTimeout(1000);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            String cmd = "1";
            // If client types quit, close the socket and exit
            if (cmd.equals("QUIT")) {
                clientSocket.close();
                System.exit(1);
            }
            sendData = cmd.getBytes();
            try {
                sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), 5001);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            Log.d("sendPacket", sendPacket.toString());
            try {
                clientSocket.send(sendPacket);
                Log.d("sendPacket", cmd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ic = getCurrentInputConnection();
            if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
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
                    KEYBOARD_VERSION = KEYBOARD_VERSION == 3 ? 4 : 3;
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
//            updateSuggestions(wholeWord);
            return;
        }
        if (code == 1003){
            previousKeyEnter = true;
            return;
        }
        if (code == 1001){
            ic.commitText(String.valueOf(' '),1);
            wholeWord = "";
            return;
        }
        if (code == 2001) {
            String value = controller.getLabelAtPosition(hint1);
            commitSuggestion(value);
            emptySuggestions();
            return;
        }
        if (code == 2002) {
            String value = controller.getLabelAtPosition(hint2);
            commitSuggestion(value);
            emptySuggestions();
            return;
        }
        if (code == 2003) {
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
        dictionarySuggestions.updateSuggestions(wholeWord);
        String[] suggestions = dictionarySuggestions.getSuggestions();
        controller.modifyKeyContent(hint1, suggestions[0]);
        controller.modifyKeyContent(hint2, suggestions[1]);
        controller.modifyKeyContent(hint3, suggestions[2]);
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

